package com.andrewsenin.pierogi.io;

public class FunctionIoManagerWrapper implements IoManager {

    private final IoManager ioManager;
    private final String functionRepresentation;
    private final int functionLineNumber;

    public FunctionIoManagerWrapper(IoManager ioManager, String functionRepresentation, int functionLineNumber) {
        this.ioManager = ioManager;
        this.functionRepresentation = functionRepresentation;
        this.functionLineNumber = functionLineNumber;
    }

    @Override
    public void print(String message) {
        ioManager.print(message);
    }

    @Override
    public String requestInput() {
        return ioManager.requestInput();
    }

    @Override
    public UnwindingException reportStaticError(ErrorType errorType, String near, int lineNumber) {
        ioManager.recordFunctionScope(functionRepresentation, functionLineNumber);
        return ioManager.reportStaticError(errorType, near, lineNumber);
    }

    @Override
    public UnwindingException reportRuntimeError(ErrorType errorType) {
        ioManager.recordFunctionScope(functionRepresentation, functionLineNumber);
        return ioManager.reportRuntimeError(errorType);
    }

    @Override
    public UnwindingException reportRuntimeError(ErrorType errorType, String near, int lineNumber) {
        ioManager.recordFunctionScope(functionRepresentation, functionLineNumber);
        return ioManager.reportRuntimeError(errorType, near, lineNumber);
    }

    @Override
    public void recordFunctionScope(String functionRepresentation, int lineNumber) {
        ioManager.recordFunctionScope(functionRepresentation, lineNumber);
    }
}
