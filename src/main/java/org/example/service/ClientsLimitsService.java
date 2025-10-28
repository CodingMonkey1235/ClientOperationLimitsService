package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.ClientsLimitsConfiguration;
import org.example.dto.clientsLimitsDto.HoldClientLimitRequestDto;
import org.example.dto.clientsLimitsDto.HoldClientLimitResponseDto;
import org.example.dto.clientsLimitsDto.WriteoffClientLimitRequestDto;
import org.example.entity.ClientLimit;
import org.example.entity.PendingClientLimit;
import org.example.exception.NoSuchClientLimitException;
import org.example.exception.NoSuchPendingException;
import org.example.holdClientLimitJobExecutor.HoldClientLimitTask;
import org.example.holdClientLimitJobExecutor.HoldClientLimitJobExecutor;
import org.example.repository.ClientsLimitsRepository;
import org.example.repository.PendingClientLimitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ClientsLimitsService {

    private static final Logger log = LoggerFactory.getLogger(ClientsLimitsService.class);
    private final ClientsLimitsRepository clientsLimitsRepository;
    private final ClientsLimitsConfiguration clientsLimitsConfiguration;
    private final HoldClientLimitJobExecutor holdClientLimitJobExecutor;
    private final PendingClientLimitRepository pendingClientLimitRepository;

    public HoldClientLimitResponseDto holdClientLimit(HoldClientLimitRequestDto requestDto) {
        long clientId = requestDto.clientId();
        BigDecimal holdAmount = requestDto.holdAmount();
        BigDecimal maxLimit = clientsLimitsConfiguration.getDefaultClientLimit();
        ClientLimit clientLimit = clientsLimitsRepository.findById(clientId).orElse(createClientLimit(requestDto));
        BigDecimal currentLimit = clientLimit.getClientLimit();
        if (holdAmount.compareTo(currentLimit) > 0) {
            String errorDesc = "Сумма операции первышает оставштйся дневной лимит. Ваш суточный лимит: " + maxLimit
                    + ". Оставшийся лимит: " + currentLimit
                    + " сумма операции: " + holdAmount + ".";
            return new HoldClientLimitResponseDto(null, "ERROR", errorDesc);
        } else {
            BigDecimal newLimit = currentLimit.subtract(holdAmount);
            clientLimit.setClientLimit(newLimit);

            PendingClientLimit pendingClientLimit = new PendingClientLimit();
            pendingClientLimit.setPendingLimit(holdAmount);
            pendingClientLimit.setClientId(clientId);
            PendingClientLimit saved = pendingClientLimitRepository.save(pendingClientLimit);

            UUID uuid = saved.getOperationId();

            HoldClientLimitTask holdClientLimitJob = new HoldClientLimitTask(uuid, clientId, this);
            holdClientLimitJobExecutor.addTask(uuid, holdClientLimitJob);

            return new HoldClientLimitResponseDto(uuid.toString(), "SUCCESS", null);
        }
    }

    private ClientLimit createClientLimit(HoldClientLimitRequestDto requestDto) {
        ClientLimit clientLimit = new ClientLimit();
        clientLimit.setClientId(requestDto.clientId());
        clientLimit.setClientLimit(clientsLimitsConfiguration.getDefaultClientLimit());
        return clientLimit;
    }

    public void cancelHoldClientLimitAfterTimeout(long clientId, BigDecimal clientLimit) {
        ClientLimit clientLimitEntity = clientsLimitsRepository.findById(clientId).orElseThrow(NoSuchElementException::new);
        clientLimitEntity.setClientLimit(clientLimit);
    }

    public void cancelPendingLimitAfterTimeout(long clientId, UUID operationId) {
        PendingClientLimit pendingClientLimit = pendingClientLimitRepository.findById(operationId).orElseThrow(NoSuchClientLimitException::new);
        ClientLimit clientLimitEntity = clientsLimitsRepository.findById(clientId).orElseThrow(NoSuchPendingException::new);
        BigDecimal recoveredLimit = clientLimitEntity.getClientLimit().add(pendingClientLimit.getPendingLimit());
        BigDecimal defaultClientLimit = clientsLimitsConfiguration.getDefaultClientLimit();

        // для случаев если дневной лимит уже обновился во время резерва и при отмене операции
        // лимит не может быть больше 100_000, если подтверждение лимита пришло после 00:00 списывает лимит нового дня
        if (defaultClientLimit.compareTo(recoveredLimit) < 0) {
            clientLimitEntity.setClientLimit(defaultClientLimit);
        } else {
            clientLimitEntity.setClientLimit(recoveredLimit);
        }
    }

    public HoldClientLimitResponseDto writeoffClientLimit(WriteoffClientLimitRequestDto requestDto) {
        UUID uuid = UUID.fromString(requestDto.operationId());
        holdClientLimitJobExecutor.cancelTask(uuid);
        pendingClientLimitRepository.findById(uuid).ifPresent(pendingClientLimitRepository::delete);
        return new HoldClientLimitResponseDto(uuid.toString(), "SUCCESS", null);
    }

    // в 00:00:00 обновляем лневные лимиты
    // выбрана логика учитываем лимиты по факту обновления
    @Scheduled(cron = "0 0 * * *")
    public void resetClientDailyLimit() {
        int pageSize = clientsLimitsConfiguration.getPageSize();
        int currentPage = 0;
        long rowsCount = clientsLimitsRepository.count();
        long pagesCount = Math.round((double) rowsCount / pageSize) - 1;

        while (currentPage <= pagesCount) {
            PageRequest pageRequest = PageRequest.of(currentPage, pageSize);
            Page<ClientLimit> nextPage = clientsLimitsRepository.findAll(pageRequest);
            for (ClientLimit clientLimit : nextPage.getContent()) {
                System.out.println(clientLimit.toString());
                long clientId = clientLimit.getClientId();
                clientLimit.setClientLimit(clientsLimitsConfiguration.getDefaultClientLimit());
            }
            currentPage++;
        }
    }
}
