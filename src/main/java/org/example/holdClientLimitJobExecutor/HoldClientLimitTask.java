package org.example.holdClientLimitJobExecutor;

import lombok.Getter;
import lombok.Setter;
import org.example.service.HoldOperationAmountService;

import java.util.UUID;

@Getter @Setter
public class HoldClientLimitTask implements Runnable {

    private UUID operationId;
    private long clientId;
    private Boolean isCancelled = false;
    private HoldOperationAmountService holdOperationAmountService;

    public HoldClientLimitTask(UUID operationId, long clientId, HoldOperationAmountService holdOperationAmountService) {
        this.operationId = operationId;
        this.clientId = clientId;
        this.holdOperationAmountService = holdOperationAmountService;
    }

    @Override
    public void run() {
        if (isCancelled || holdOperationAmountService == null) {
            return;
        }
        holdOperationAmountService.cancelPendingLimitAfterTimeout(clientId, operationId);
    }
}
