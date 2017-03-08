package com.matpaw.myexspencer.model;

import android.support.annotation.NonNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class Limit implements Comparable<Limit> {
    private UUID id;
    private Date date;
    private BigDecimal value;

    public Limit(UUID id, Date date, BigDecimal value) {
        this.id = id;
        this.date = date;
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public UUID getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public int compareTo(@NonNull Limit o) {
        return o.getDate().compareTo(getDate());
    }
}
