package com.andrewsenin.pierogi.interpreter;

import com.andrewsenin.pierogi.datatypes.NativeData;

import java.util.HashMap;
import java.util.Map;

public class Frame {

    private final Frame parent;
    private final Map<String, NativeData> bindings;

    public Frame(Frame parent) {
        this(parent, new HashMap<>());
    }

    public Frame(Frame parent, Map<String, NativeData> bindings) {
        this.parent = parent;
        this.bindings = bindings;
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

    public Frame makeSnapshot() {
        Frame snapshotParent = null;
        if (parent != null) {
            snapshotParent = parent.makeSnapshot();
        }
        return new Frame(snapshotParent, new HashMap<>(bindings));
    }

    public void addBinding(String symbol, NativeData value) {
        bindings.put(symbol, value);
    }
}
