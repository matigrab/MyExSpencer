package com.matpaw.myexspencer.model;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.UUID;

public class Expense implements Comparable<Expense> {
    private UUID id;
    private Date date;
    private String description;
    private ExpenseType expenseType;
    private String payer;
    private float valueInEuro;
    private float valueInPLN;
    private LimitImpactType limitConsumptionType;
    private boolean confirmedByBank;
    private PaymentType paymentType;
    private Date additionDate;

    public Expense(UUID id, Date date, String description, ExpenseType expenseType, String payer, float valueInEuro, float valueInPLN, LimitImpactType limitConsumptionType, boolean confirmedByBank, PaymentType paymentType) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.expenseType = expenseType;
        this.payer = payer;
        this.valueInEuro = valueInEuro;
        this.valueInPLN = valueInPLN;
        this.limitConsumptionType = limitConsumptionType;
        this.confirmedByBank = confirmedByBank;
        this.paymentType = paymentType;
        this.additionDate = new Date();
    }

    public LimitImpactType getLimitImpactType() {
        return limitConsumptionType;
    }

    public float getValueInEuro() {
        return valueInEuro;
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

    public float getValueInPLN() {
        return valueInPLN;
    }

    public boolean isConfirmedByBank() {
        return confirmedByBank;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public Date getAdditionDate() {
        return additionDate;
    }

    @Override
    public String toString() {
        return getId() + ";" + getDescription();
    }

    @Override
    public int compareTo(@NonNull Expense o) {
        int compareTo = o.getDate().compareTo(getDate());
        if(compareTo == 0) {
            return o.getAdditionDate().compareTo(getAdditionDate());
        }
        return compareTo;
    }
}
