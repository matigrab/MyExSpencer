package com.matpaw.myexspencer.model;


public enum ExpenseType {
    DINNER, LUNCH, PUBLIC_TRANSPORT, TRAIN, SOUVENIRS, HOTEL, TAXI, OTHER;

    public static int index(ExpenseType expenseType) {
        int index = 0;
        for (ExpenseType type : values()) {
            if(type.equals(expenseType)) {
                return index;
            }

            index++;
        }

        throw new IllegalStateException("Unknown type: " + expenseType);
    }
}
