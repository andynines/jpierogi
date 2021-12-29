package com.andrewsenin.pierogi.datatypes;

import com.andrewsenin.pierogi.io.IoManager;

import java.util.List;
import java.util.function.BiFunction;

public class BuiltinFunction extends NativeFunction {

    private final int arity;
    private final BiFunction<List<NativeData>, IoManager, NativeData> definition;

    public BuiltinFunction(int arity, BiFunction<List<NativeData>, IoManager, NativeData> definition) {
        this.arity = arity;
        this.definition = definition;
    }

    @Override
    public NativeData call(List<NativeData> arguments, IoManager ioManager) {
        if (arguments.size() != arity) {
            // TODO: inject an iomanager that knows where we are in the code and throw an error from it here
        }
        return definition.apply(arguments, ioManager);
    }

    @Override
    public String makePrintRepresentation() {
        return "<builtin function>";
    }
}
