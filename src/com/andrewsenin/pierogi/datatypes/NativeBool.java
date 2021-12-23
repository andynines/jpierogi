package com.andrewsenin.pierogi.datatypes;

public class NativeBool implements NativeType {

    private final boolean value;

    public NativeBool(boolean value) {
        this.value = value;
    }

    public boolean isTrue() {
        return value;
    }

    public NativeBool negate() {
        return new NativeBool(!value);
    }

    public NativeBool and(NativeBool other) {
        return new NativeBool(value && other.value);
    }

    public NativeBool or(NativeBool other) {
        return new NativeBool(value || other.value);
    }

    @Override
    public boolean equals(NativeType other) {
        if (!(other instanceof NativeBool)) {
            return false;
        }
        return value == ((NativeBool) other).value;
    }

    @Override
    public String makePrintRepresentation() {
        return Boolean.toString(value);
    }
}
