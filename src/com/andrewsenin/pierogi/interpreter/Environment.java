package com.andrewsenin.pierogi.interpreter;

import com.andrewsenin.pierogi.datatypes.NativeData;

public class Environment {

    private Frame currentFrame = new Frame(new BuiltinsFrame());

    public boolean hasDefinitionFor(String symbol) {
        return lookUpValueOf(symbol) != null;
    }

    public NativeData lookUpValueOf(String symbol) {
        Frame searchFrame = currentFrame;
        do {
            NativeData lookupResult = searchFrame.lookUpValueOf(symbol);
            if (lookupResult != null) {
                return lookupResult;
            }
            searchFrame = searchFrame.getParent();
        } while (searchFrame != null);
        return null;
    }

    public void addBinding(String symbol, NativeData value) {
        currentFrame.addBinding(symbol, value);
    }

    public void pushNewFrame() {
        currentFrame = new Frame(currentFrame);
    }

    public void popCurrentFrame() {
        currentFrame = currentFrame.getParent();
    }
}
