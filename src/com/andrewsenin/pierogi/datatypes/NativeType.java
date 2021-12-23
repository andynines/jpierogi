package com.andrewsenin.pierogi.datatypes;

public interface NativeType {
    boolean equals(NativeType other);

    String makePrintRepresentation();
}
