package com.andrewsenin.pierogi.interpreter;

import com.andrewsenin.pierogi.datatypes.*;
import com.andrewsenin.pierogi.io.ErrorType;
import com.andrewsenin.pierogi.io.IoManager;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Supplier;

public class BuiltinsFrame extends Frame {

    public BuiltinsFrame() {
        super(null);
        defineBuiltins();
    }

    private void defineBuiltins() {
        addBinding("print", new BuiltinFunction(1, (arguments, ioManager) -> {
            ioManager.print(arguments.get(0).makePrintRepresentation());
            return new NativeNil();
        }));
        addBinding("println", new BuiltinFunction(1, (arguments, ioManager) -> {
            ioManager.print(arguments.get(0).makePrintRepresentation() + "\n");
            return new NativeNil();
        }));
        addBinding("input", new BuiltinFunction(0, (arguments, ioManager) -> new NativeString(ioManager.requestInput())));

        addBinding("time", new BuiltinFunction(0, (arguments, ioManager) -> new NativeNumber(System.currentTimeMillis() / 1000.0)));

        addBinding("pi", new NativeNumber(Math.PI));
        addBinding("exp", new BuiltinFunction(1, (arguments, ioManager) -> makeTypeSensitive(ioManager, () -> new NativeNumber(Math.exp(((NativeNumber) arguments.get(0)).getValue())))));
        addBinding("sin", new BuiltinFunction(1, (arguments, ioManager) -> makeTypeSensitive(ioManager, () -> new NativeNumber(Math.sin(((NativeNumber) arguments.get(0)).getValue())))));
        addBinding("cos", new BuiltinFunction(1, (arguments, ioManager) -> makeTypeSensitive(ioManager, () -> new NativeNumber(Math.cos(((NativeNumber) arguments.get(0)).getValue())))));

        addBinding("map", new BuiltinFunction(2, (arguments, ioManager) -> makeTypeSensitive(ioManager, () -> {
            NativeFunction function = (NativeFunction) arguments.get(0);
            if (function.getArity() != 1) {
                throw ioManager.reportRuntimeError(ErrorType.BUILTIN_FUNCTION_CONTRACT_VIOLATION);
            }
            NativeList items = (NativeList) arguments.get(1);
            Deque<NativeData> newItems = new LinkedList<>();
            for (NativeData item : items.getItems()) {
                newItems.addLast(function.call(Collections.singletonList(item), ioManager));
            }
            return new NativeList(newItems);
        })));

        addBinding("filter", new BuiltinFunction(2, (arguments, ioManager) -> makeTypeSensitive(ioManager, () -> {
            NativeFunction predicate = (NativeFunction) arguments.get(0);
            if (predicate.getArity() != 1) {
                throw ioManager.reportRuntimeError(ErrorType.BUILTIN_FUNCTION_CONTRACT_VIOLATION);
            }
            NativeList items = (NativeList) arguments.get(1);
            Deque<NativeData> newItems = new LinkedList<>();
            for (NativeData item : items.getItems()) {
                if (((NativeBool) predicate.call(Collections.singletonList(item), ioManager)).getValue()) {
                    newItems.addLast(item);
                }
            }
            return new NativeList(newItems);
        })));
    }

    private NativeData makeTypeSensitive(IoManager ioManager, Supplier<NativeData> callback) {
        try {
            return callback.get();
        } catch (ClassCastException e) {
            throw ioManager.reportRuntimeError(ErrorType.BUILTIN_FUNCTION_CONTRACT_VIOLATION);
        }
    }
}
