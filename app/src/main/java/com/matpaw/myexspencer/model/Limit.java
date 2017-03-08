package com.matpaw.myexspencer.model;

import android.support.annotation.NonNull;

import com.google.common.base.Joiner;
import com.matpaw.myexspencer.Constants;
import com.matpaw.myexspencer.utils.Dates;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static com.matpaw.myexspencer.Constants.DEFAULT_SCALE;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Limit limit = (Limit) o;
        return Objects.equals(id, limit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return Joiner.on(';').join(
                this.getId(),
                Dates.format(getDate()),
                this.getValue().setScale(DEFAULT_SCALE)
        );
    }
}
