package com.andrewsenin.pierogi.interpreter;

import com.andrewsenin.pierogi.datatypes.BuiltinFunction;
import com.andrewsenin.pierogi.datatypes.NativeNumber;
import com.andrewsenin.pierogi.datatypes.NativeString;

public class BuiltinsFrame extends Frame {

    public BuiltinsFrame() {
        super(null);
        defineBuiltins();
    }

    private void defineBuiltins() {
        addBinding("pi", new NativeNumber(Math.PI));
        addBinding("time", new BuiltinFunction(0, (arguments, ioManager) -> new NativeNumber(System.currentTimeMillis() / 1000.0)));
        addBinding("input", new BuiltinFunction(0, (arguments, ioManager) -> new NativeString(ioManager.requestInput())));
    }
}
