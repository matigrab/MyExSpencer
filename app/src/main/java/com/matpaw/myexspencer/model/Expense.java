package com.matpaw.myexspencer.model;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.UUID;

public class Expense implements Comparable {
    private UUID id;
    private Date date;
    private String description;
    private ExpenseType expenseType;
    private String payer;
    private float value;
    private LimitConsumptionType limitConsumptionType;

    public Expense(UUID id, Date date, String description, ExpenseType expenseType, String payer, float value, LimitConsumptionType limitConsumptionType) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.expenseType = expenseType;
        this.payer = payer;
        this.value = value;
        this.limitConsumptionType = limitConsumptionType;
    }

    public LimitConsumptionType getLimitConsumptionType() {
        return limitConsumptionType;
    }

    public float getValue() {
        return value;
    }

    public String getPayer() {
        return payer;
    }

    public ExpenseType getExpenseType() {
        return expenseType;
    }

    public Date getDate() {
        return date;
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if(o instanceof Expense) {
            return (((Expense) o).getDate()).compareTo(getDate());
        }
        throw new IllegalArgumentException("Not Expense!");
    }
}
