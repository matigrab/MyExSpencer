package com.matpaw.myexspencer.model;

import android.support.annotation.NonNull;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.matpaw.myexspencer.utils.Dates;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Expense implements Comparable<Expense> {
    private UUID id;
    private Date date;
    private String description;
    private ExpenseType expenseType;
    private String payer;
    private float valueInEuro;
    private float valueInPLN;
    private LimitImpactType limitImpactType;
    private boolean confirmedByBank;
    private PaymentType paymentType;
    private Date additionDate;

    public Expense(UUID id, Date date, Date additionDate, String description, ExpenseType expenseType, String payer, float valueInEuro, float valueInPLN, LimitImpactType limitImpactType, boolean confirmedByBank, PaymentType paymentType) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.expenseType = expenseType;
        this.payer = payer;
        this.valueInEuro = valueInEuro;
        this.valueInPLN = valueInPLN;
        this.limitImpactType = limitImpactType;
        this.confirmedByBank = confirmedByBank;
        this.paymentType = paymentType;
        this.additionDate = additionDate;
    }

    public LimitImpactType getLimitImpactType() {
        return limitImpactType;
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
        List<String> parts = Lists.newArrayList();
        parts.add(getId().toString());
        parts.add(Dates.format(getDate()));
        parts.add(Dates.format(getAdditionDate()));
        parts.add(getPayer());
        parts.add(getDescription());
        parts.add(getExpenseType().toString());
        parts.add(getPaymentType().toString());
        parts.add("" + getValueInEuro());
        parts.add("" + getValueInPLN());
        parts.add(getLimitImpactType().toString());
        parts.add("" + isConfirmedByBank());
        return Joiner.on(";").join(parts);
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
