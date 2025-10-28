package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "day_limits")
@Getter @Setter
public class DayLimit {

    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "day_limit", precision = 10, scale = 2)
    private BigDecimal dayLimit;

    public DayLimit() {}

    @Override
    public String toString() {
        return "ClientLimit{" +
                "clientId=" + id +
                ", dayLimit=" + dayLimit +
                '}';
    }
}
