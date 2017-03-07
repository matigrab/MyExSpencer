package com.matpaw.myexspencer.model;

public enum PaymentType {
    CASH, CREDIT_CARD, DEBIT_CARD;

    public static int index(PaymentType paymentType) {
        int index = 0;
        for (PaymentType type : values()) {
            if(type.equals(paymentType)) {
                return index;
            }

            index++;
        }

        throw new IllegalStateException("Unknown type: " + paymentType);
    }
}
