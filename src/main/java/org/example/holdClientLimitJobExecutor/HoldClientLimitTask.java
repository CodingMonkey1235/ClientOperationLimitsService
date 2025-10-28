package org.example.holdClientLimitJobExecutor;

import lombok.Getter;
import lombok.Setter;
import org.example.service.ClientsLimitsService;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter
public class HoldClientLimitTask implements Runnable {

    private UUID operationId;
    private long clientId;
    private Boolean isCancelled = false;
    private ClientsLimitsService clientsLimitsService;

    public HoldClientLimitTask(UUID operationId, long clientId, ClientsLimitsService clientsLimitsService) {
        this.operationId = operationId;
        this.clientId = clientId;
        this.clientsLimitsService = clientsLimitsService;
    }

    @Override
    public void run() {
        if (isCancelled || clientsLimitsService == null) {
            return;
        }
        clientsLimitsService.cancelPendingLimitAfterTimeout(clientId, operationId);
    }
}
