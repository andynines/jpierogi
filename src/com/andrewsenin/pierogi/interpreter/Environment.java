package com.andrewsenin.pierogi.interpreter;

import com.andrewsenin.pierogi.datatypes.NativeType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Environment {

    private final LinkedList<Map<String, NativeType>> frames;

    public Environment() {
        frames = new LinkedList<>();
        pushNewFrame();
    }

    public void pushNewFrame() {
        frames.addFirst(new HashMap<>());
    }

    public void popCurrentFrame() {
        frames.pop();
    }

    public boolean hasDefinitionFor(String symbol) {
        for (Map<String, NativeType> frame : frames) {
            if (frame.containsKey(symbol)) {
                return true;
            }
        }
        return false;
    }

    public void addBinding(String symbol, NativeType value) {
        frames.getFirst().put(symbol, value);
    }

    public NativeType lookUpValueOf(String symbol) {
        // TODO: make sure name makes sense, throw error when not found, make sure symbols in more specific scopes can't be found in higher scopes
        for (Map<String, NativeType> frame : frames) {
            if (frame.containsKey(symbol)) {
                return frame.get(symbol);
            }
        }
        return null;
    }
}
