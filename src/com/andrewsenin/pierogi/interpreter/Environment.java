package com.andrewsenin.pierogi.interpreter;

import com.andrewsenin.pierogi.datatypes.NativeType;

public class Environment {

    private Frame currentFrame = new GlobalFrame();

    public boolean hasDefinitionFor(String symbol) {
        return lookUpValueOf(symbol) != null;
    }

    public NativeType lookUpValueOf(String symbol) {
        Frame searchFrame = currentFrame;
        do {
            NativeType lookupResult = searchFrame.lookUpValueOf(symbol);
            if (lookupResult != null) {
                return lookupResult;
            }
            searchFrame = searchFrame.getParent();
        } while (searchFrame != null);
        return null;
    }

    public void addBinding(String symbol, NativeType value) {
        currentFrame.addBinding(symbol, value);
    }

    public void pushNewFrame() {
        currentFrame = new Frame(currentFrame);
    }

    public void popCurrentFrame() {
        currentFrame = currentFrame.getParent();
    }
}
