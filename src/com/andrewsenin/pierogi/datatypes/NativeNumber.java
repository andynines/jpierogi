package com.andrewsenin.pierogi.datatypes;

public class NativeNumber implements NativeType {

    private final double value;

    public NativeNumber(double value) {
        this.value = value;
    }

    public NativeNumber negate() {
        return new NativeNumber(-value);
    }

    public NativeNumber add(NativeNumber other) {
        return new NativeNumber(value + other.value);
    }

    public NativeNumber subtract(NativeNumber other) {
        return new NativeNumber(value - other.value);
    }

    public NativeNumber multiply(NativeNumber other) {
        return new NativeNumber(value * other.value);
    }

    public NativeNumber divide(NativeNumber other) {
        return new NativeNumber(value / other.value);
    }

    public NativeNumber exponentiate(NativeNumber other) {
        return new NativeNumber(Math.pow(value, other.value));
    }

    public NativeBool lessThan(NativeNumber other) {
        return new NativeBool(value < other.value);
    }

    public NativeBool greaterThan(NativeNumber other) {
        return new NativeBool(value > other.value);
    }

    public NativeBool lessEqual(NativeNumber other) {
        return new NativeBool(value <= other.value);
    }

    public NativeBool greaterEqual(NativeNumber other) {
        return new NativeBool(value >= other.value);
    }

    @Override
    public boolean equals(NativeType other) {
        if (!(other instanceof NativeNumber)) {
            return false;
        }
        return value == ((NativeNumber) other).value;
    }

    @Override
    public String makePrintRepresentation() {
        // TODO: don't output any numbers in scientific notation
        String text = Double.toString(value);
        if (text.endsWith(".0")) {
            text = text.substring(0, text.length() - 2);
        }
        return text;
    }
}
