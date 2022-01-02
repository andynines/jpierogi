package com.andrewsenin.pierogi.datatypes;

public class NativeNumber implements NativeData {

    private final double value;

    public NativeNumber(double value) {
        this.value = value;
    }

    // TODO: add other detectors to defend against creating Infinity by 999999^999999999999 for instance
    public boolean isInvalid() {
        return value == Double.POSITIVE_INFINITY || value == Double.NEGATIVE_INFINITY || Double.isNaN(value);
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(NativeData other) {
        if (!(other instanceof NativeNumber)) {
            return false;
        }
        return value == ((NativeNumber) other).value;
    }

    @Override
    public String makeValueRepresentation() {
        String text = Double.toString(value);
        if (text.endsWith(".0")) {
            return text.substring(0, text.length() - 2);
        } else if (text.contains("E")) {
            return text.toLowerCase();
        }
        return text;
    }

    @Override
    public String makePrintRepresentation() {
        return makeValueRepresentation();
    }
}
