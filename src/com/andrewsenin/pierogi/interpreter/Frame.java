package com.andrewsenin.pierogi.interpreter;

import com.andrewsenin.pierogi.datatypes.NativeData;

import java.util.HashMap;
import java.util.Map;

public class Frame {

    private final Map<String, NativeData> bindings = new HashMap<>();
    private final Frame parent;

    public Frame(Frame parent) {
        this.parent = parent;
    }

    public Frame getParent() {
        return parent;
    }

    public NativeData lookUpValueOf(String symbol) {
        if (bindings.containsKey(symbol)) {
            return bindings.get(symbol);
        }
        return null;
    }

    public void addBinding(String symbol, NativeData value) {
        bindings.put(symbol, value);
    }
}
