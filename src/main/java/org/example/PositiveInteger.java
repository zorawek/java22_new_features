package org.example;

import java.math.BigInteger;

public class PositiveInteger extends BigInteger {
    private int value;

    public PositiveInteger(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must be positive");
        }
        super(String.valueOf(value));
    }

    public static PositiveInteger valueOf(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must be positive");
        }
        return new PositiveInteger(value);
    }
}
