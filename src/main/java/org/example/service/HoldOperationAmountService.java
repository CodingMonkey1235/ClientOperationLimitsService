package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.DayLimitsConfiguration;
import org.example.dto.clientsLimitsDto.HoldOperationAmountRequestDto;
import org.example.dto.clientsLimitsDto.HoldOperationAmountResponseDto;
import org.example.dto.clientsLimitsDto.AcceptOperationAmountRequestDto;
import org.example.entity.DayLimit;
import org.example.entity.PendingOperation;
import org.example.exception.NoSuchClientLimitException;
import org.example.exception.NoSuchPendingException;
import org.example.holdClientLimitJobExecutor.HoldClientLimitTask;
import org.example.holdClientLimitJobExecutor.HoldClientLimitJobExecutor;
import org.example.repository.DayLimitsRepository;
import org.example.repository.PendingOperationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class HoldOperationAmountService {

    private static final Logger log = LoggerFactory.getLogger(HoldOperationAmountService.class);
    private final DayLimitsRepository clientsLimitsRepository;
    private final DayLimitsConfiguration dayLimitsConfiguration;
    private final HoldClientLimitJobExecutor holdClientLimitJobExecutor;
    private final PendingOperationRepository pendingClientLimitRepository;

    public HoldOperationAmountResponseDto holdOperationAmount(HoldOperationAmountRequestDto requestDto) {
        long clientId = requestDto.clientId();
        BigDecimal holdAmount = requestDto.holdAmount();
        BigDecimal maxLimit = dayLimitsConfiguration.getDefaultClientLimit();
        DayLimit clientLimit = clientsLimitsRepository.findById(clientId).orElse(createNewDayLimit(requestDto));
        BigDecimal currentLimit = clientLimit.getDayLimit();
        if (holdAmount.compareTo(currentLimit) > 0) {
            String errorDesc = "Сумма операции первышает оставштйся дневной лимит. Ваш суточный лимит: " + maxLimit
                    + ". Оставшийся лимит: " + currentLimit
                    + " сумма операции: " + holdAmount + ".";
            return new HoldOperationAmountResponseDto(null, "ERROR", errorDesc);
        } else {
            BigDecimal newLimit = currentLimit.subtract(holdAmount);
            clientLimit.setDayLimit(newLimit);

            PendingOperation pendingClientLimit = new PendingOperation();
            pendingClientLimit.setOperationAmount(holdAmount);
            pendingClientLimit.setDayLimitId(clientId);
            pendingClientLimit.setDateCreated(ZonedDateTime.now());
            PendingOperation saved = pendingClientLimitRepository.save(pendingClientLimit);

            UUID uuid = saved.getId();

            HoldClientLimitTask holdClientLimitJob = new HoldClientLimitTask(uuid, clientId, this);
            holdClientLimitJobExecutor.addTask(uuid, holdClientLimitJob);

            return new HoldOperationAmountResponseDto(uuid.toString(), "SUCCESS", null);
        }
    }

    private DayLimit createNewDayLimit(HoldOperationAmountRequestDto requestDto) {
        DayLimit clientLimit = new DayLimit();
        clientLimit.setId(requestDto.clientId());
        clientLimit.setDayLimit(dayLimitsConfiguration.getDefaultClientLimit());
        return clientLimit;
    }

    public void cancelHoldClientLimitAfterTimeout(long clientId, BigDecimal clientLimit) {
        DayLimit clientLimitEntity = clientsLimitsRepository.findById(clientId).orElseThrow(NoSuchElementException::new);
        clientLimitEntity.setDayLimit(clientLimit);
    }

    public void cancelPendingLimitAfterTimeout(long clientId, UUID operationId) {
        PendingOperation pendingClientLimit = pendingClientLimitRepository.findById(operationId).orElseThrow(NoSuchClientLimitException::new);
        DayLimit clientLimitEntity = clientsLimitsRepository.findById(clientId).orElseThrow(NoSuchPendingException::new);
        BigDecimal recoveredLimit = clientLimitEntity.getDayLimit().add(pendingClientLimit.getOperationAmount());
        BigDecimal defaultClientLimit = dayLimitsConfiguration.getDefaultClientLimit();

        // для случаев если дневной лимит уже обновился во время резерва и при отмене операции
        // лимит не может быть больше 100_000, если подтверждение лимита пришло после 00:00 списывает лимит нового дня
        if (defaultClientLimit.compareTo(recoveredLimit) < 0) {
            clientLimitEntity.setDayLimit(defaultClientLimit);
        } else {
            clientLimitEntity.setDayLimit(recoveredLimit);
        }
    }

    public HoldOperationAmountResponseDto acceptDayLimitOperation(AcceptOperationAmountRequestDto requestDto) {
        UUID uuid = UUID.fromString(requestDto.operationId());
        holdClientLimitJobExecutor.cancelTask(uuid);
        pendingClientLimitRepository.findById(uuid).ifPresent(pendingClientLimitRepository::delete);
        return new HoldOperationAmountResponseDto(uuid.toString(), "SUCCESS", null);
    }

    // каждый провежуток времени очищаем не подтвержденные списания лимита
    @Scheduled(fixedDelay = 1000)
    public void cleanNotAcceptedOperations() {
        int pageSize = dayLimitsConfiguration.getPageSize();
        int currentPage = 0;
        long rowsCount = clientsLimitsRepository.count();
        long pagesCount = Math.round((double) rowsCount / pageSize) - 1;

        while (currentPage < pagesCount) {
            PageRequest pageRequest = PageRequest.of(currentPage, pageSize);
            Page<PendingOperation> nextPage = pendingClientLimitRepository.findAll(pageRequest);
            for (PendingOperation operation : nextPage.getContent()) {
                System.out.println(operation.toString());

                long deltaMinutes = Duration.between(operation.getDateCreated(), ZonedDateTime.now()).toMinutes();
                if (deltaMinutes > dayLimitsConfiguration.getCancelLimitDelay()) {
                    pendingClientLimitRepository.delete(operation);
                }
            }
            currentPage++;
        }
    }

    // в 00:00:00 обновляем дневные лимиты
    // выбрана логика учитываем лимиты по факту обновления
    @Scheduled(cron = "0 0 * * *")
    public void resetClientDailyLimit() {
        int pageSize = dayLimitsConfiguration.getPageSize();
        int currentPage = 0;
        long rowsCount = clientsLimitsRepository.count();
        long pagesCount = Math.round((double) rowsCount / pageSize) - 1;

        while (currentPage < pagesCount) {
            PageRequest pageRequest = PageRequest.of(currentPage, pageSize);
            Page<DayLimit> nextPage = clientsLimitsRepository.findAll(pageRequest);
            for (DayLimit clientLimit : nextPage.getContent()) {
                long clientId = clientLimit.getId();
                clientLimit.setDayLimit(dayLimitsConfiguration.getDefaultClientLimit());
            }
            currentPage++;
        }
    }
}
