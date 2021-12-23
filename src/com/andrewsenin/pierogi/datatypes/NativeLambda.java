package com.andrewsenin.pierogi.datatypes;

public class NativeLambda implements NativeType {
    @Override
    public boolean equals(NativeType other) {
        // TODO: define me
        return false;
    }

    @Override
    public String makePrintRepresentation() {
        return "<lambda>";
    }
}
