package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "pending_clients_limits")
@Getter @Setter
public class PendingClientLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operation_id")
    private UUID operationId;

    @Column(name = "client_id")
    private long clientId;

    @Column(name = "pending_limit")
    private BigDecimal pendingLimit;

    @Column(name = "need_daily_reset", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean needDailyReset;

    @Override
    public String toString() {
        return "PendingClientLimit{" +
                "operationId=" + operationId +
                ", clientId='" + clientId + '\'' +
                ", pendingLimit=" + pendingLimit +
                ", needDailyReset=" + needDailyReset +
                '}';
    }
}
