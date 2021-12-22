package com.andrewsenin.pierogi.datatypes;

public class NativeString implements NativeType {

    private final String value;

    public NativeString(String value) {
        this.value = value;
    }

    public NativeString concatenate(NativeString other) {
        return new NativeString(value + other.value);
    }

    @Override
    public String makePrintRepresentation() {
        return "\"" + value + "\"";
    }
}
