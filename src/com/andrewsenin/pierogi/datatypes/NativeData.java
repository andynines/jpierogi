package com.andrewsenin.pierogi.datatypes;

public interface NativeData {
    boolean equals(NativeData other);

    String makeValueRepresentation();

    String makePrintRepresentation();
}
