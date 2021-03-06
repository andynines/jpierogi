package com.andrewsenin.pierogi.datatypes;

import com.andrewsenin.pierogi.io.IoManager;

import java.util.List;

public abstract class NativeFunction implements NativeData {

    public abstract NativeData call(List<NativeData> arguments, IoManager ioManager);

    public abstract int getArity();

    @Override
    public boolean equals(NativeData other) {
        return this == other;
    }
}
