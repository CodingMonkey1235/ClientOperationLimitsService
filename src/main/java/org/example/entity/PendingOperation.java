package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "pending_operations")
@Getter @Setter
public class PendingOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private UUID id;

    @Column(name = "day_limit_id")
    private long dayLimitId;

    @Column(name = "operation_amount", precision = 10, scale = 2)
    private BigDecimal operationAmount;

    @Column(name = "date_created")
    private ZonedDateTime dateCreated;

    @Override
    public String toString() {
        return "PendingClientLimit{" +
                "operationId=" + id +
                ", clientId='" + dayLimitId + '\'' +
                ", operationAmount=" + operationAmount +
                ", dateCreated=" + dateCreated +
                '}';
    }
}
