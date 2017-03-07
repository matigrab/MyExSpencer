package com.matpaw.myexspencer.model;

public enum LimitImpactType {
    CONSUMES, NONE, OPTIONAL;

    public static int index(LimitImpactType limitImpactType) {
        int index = 0;
        for (LimitImpactType type : values()) {
            if(type.equals(limitImpactType)) {
                return index;
            }

            index++;
        }

        throw new IllegalStateException("Unknown type: " + limitImpactType);
    }
}
