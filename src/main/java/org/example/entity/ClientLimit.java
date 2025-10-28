package org.example.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "clients_limits")
public class ClientLimit {

    @Id
    @Column(name = "client_id")
    private long clientId;

    @Column(name = "client_limit")
    private BigDecimal clientLimit;

    public ClientLimit() {}

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public BigDecimal getClientLimit() {
        return clientLimit;
    }

    public void setClientLimit(BigDecimal clientLimit) {
        this.clientLimit = clientLimit;
    }

    @Override
    public String toString() {
        return "ClientLimit{" +
                "clientId=" + clientId +
                ", limit=" + clientLimit +
                '}';
    }
}
