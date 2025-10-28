package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.clientsLimitsDto.HoldClientLimitRequestDto;
import org.example.dto.clientsLimitsDto.HoldClientLimitResponseDto;
import org.example.dto.clientsLimitsDto.WriteoffClientLimitRequestDto;
import org.example.dto.common.CommonErrorResponseDto;
import org.example.exception.NoSuchClientLimitException;
import org.example.exception.NoSuchPendingException;
import org.example.repository.ClientsLimitsRepository;
import org.example.service.ClientsLimitsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/client/limit")
public class ClientsLimitsController {

    private static final Logger log = LoggerFactory.getLogger(ClientsLimitsController.class);
    private final ClientsLimitsService clientsLimitsService;

    @PostMapping(value = "/hold/")
    public HoldClientLimitResponseDto holdClientLimit(HoldClientLimitRequestDto requestDto) {
        return clientsLimitsService.holdClientLimit(requestDto);
    }

    @PostMapping(value = "/writeoff/")
    public HoldClientLimitResponseDto writeoffClientLimit(WriteoffClientLimitRequestDto requestDto) {
        return clientsLimitsService.writeoffClientLimit(requestDto);
    }

    @GetMapping(value = "/hold/reset/")
    @ResponseStatus(value = HttpStatus.OK)
    public void resetClientLimit() {
        clientsLimitsService.resetClientDailyLimit();
    }

    @ExceptionHandler(value = NoSuchClientLimitException.class)
    public CommonErrorResponseDto noSuchClientLimitException(NoSuchClientLimitException exception) {
        log.error("noSuchClientLimitException", exception);
        return new CommonErrorResponseDto("ERROR " + exception.getClass().getSimpleName(), exception.getMessage());
    }

    @ExceptionHandler(value = NoSuchPendingException.class)
    public CommonErrorResponseDto noSuchPendingException(NoSuchPendingException exception) {
        log.error("noSuchPendingException", exception);
        return new CommonErrorResponseDto("ERROR " + exception.getClass().getSimpleName(), exception.getMessage());
    }
}
