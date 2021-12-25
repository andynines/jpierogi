package com.andrewsenin.pierogi.interpreter;

import com.andrewsenin.pierogi.datatypes.NativeNumber;

public class GlobalFrame extends Frame {

    public GlobalFrame() {
        super(null);
        defineBuiltins();
    }

    private void defineBuiltins() {
        addBinding("pi", new NativeNumber(Math.PI));
    }
}
