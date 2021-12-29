package com.andrewsenin.pierogi.datatypes;

public class NativeBool implements NativeData {

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

    @Override
    public boolean equals(NativeData other) {
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
