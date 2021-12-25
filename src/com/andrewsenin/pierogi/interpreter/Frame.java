package com.andrewsenin.pierogi.interpreter;

import com.andrewsenin.pierogi.datatypes.NativeType;

import java.util.HashMap;
import java.util.Map;

public class Frame {

    private final Map<String, NativeType> bindings = new HashMap<>();
    private final Frame parent;

    public Frame(Frame parent) {
        this.parent = parent;
    }

    public Frame getParent() {
        return parent;
    }

    public NativeType lookUpValueOf(String symbol) {
        if (bindings.containsKey(symbol)) {
            return bindings.get(symbol);
        }
        return null;
    }

    public void addBinding(String symbol, NativeType value) {
        bindings.put(symbol, value);
    }
}
