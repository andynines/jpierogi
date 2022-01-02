package com.andrewsenin.pierogi.datatypes;

import com.andrewsenin.pierogi.ast.Expression;
import com.andrewsenin.pierogi.interpreter.Environment;
import com.andrewsenin.pierogi.interpreter.Interpreter;
import com.andrewsenin.pierogi.io.IoManager;

import java.util.List;

public class UserFunction extends NativeFunction {

    private final List<String> parameters;
    private final List<Expression> definition;
    private final Environment environment;

    public UserFunction(List<String> parameters, List<Expression> definition, Environment environment) {
        this.parameters = parameters;
        this.definition = definition;
        this.environment = environment;
    }

    @Override
    public NativeData call(List<NativeData> arguments, IoManager ioManager) {
        environment.pushNewFrame();
        for (int i = 0; i < parameters.size(); i++) {
            environment.addBinding(parameters.get(i), arguments.get(i));
        }
        Interpreter interpreter = new Interpreter(ioManager, environment);
        List<NativeData> results = interpreter.interpret(definition);
        environment.popCurrentFrame();
        return results.get(results.size() - 1);
    }

    @Override
    public int getArity() {
        return parameters.size();
    }

    @Override
    public String makeValueRepresentation() {
        return "<function/" + getArity() + ">";
    }

    @Override
    public String makePrintRepresentation() {
        return makeValueRepresentation();
    }
}
