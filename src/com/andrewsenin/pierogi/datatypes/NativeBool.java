package com.andrewsenin.pierogi.datatypes;

public class NativeBool implements NativeType {

    private final boolean value;

    public NativeBool(boolean value) {
        this.value = value;
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
    public String makePrintRepresentation() {
        return Boolean.toString(value);
    }
}
