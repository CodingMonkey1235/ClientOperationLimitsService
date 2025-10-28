package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.clientsLimitsDto.HoldOperationAmountRequestDto;
import org.example.dto.clientsLimitsDto.HoldOperationAmountResponseDto;
import org.example.dto.clientsLimitsDto.AcceptOperationAmountRequestDto;
import org.example.dto.common.CommonErrorResponseDto;
import org.example.exception.NoSuchClientLimitException;
import org.example.exception.NoSuchPendingException;
import org.example.service.HoldOperationAmountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/day_limit")
public class DayLimitController {

    private static final Logger log = LoggerFactory.getLogger(DayLimitController.class);
    private final HoldOperationAmountService holdOperationAmountService;

    @PostMapping(value = "/hold/")
    public HoldOperationAmountResponseDto holdOperationAmount(HoldOperationAmountRequestDto requestDto) {
        return holdOperationAmountService.holdOperationAmount(requestDto);
    }

    @PostMapping(value = "/accept/")
    public HoldOperationAmountResponseDto acceptDayLimitOperation(AcceptOperationAmountRequestDto requestDto) {
        return holdOperationAmountService.acceptDayLimitOperation(requestDto);
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
