package io.flamingock.core.metadata.util;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

public class LoggerPreProcessor {

    private final String logPrefix = "Flamingock annotation processor: ";

    private final ProcessingEnvironment processingEnv;

    public LoggerPreProcessor(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    public void info(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, logPrefix + message);
    }

    public void warn(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, logPrefix + message);
    }

    public void error(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, logPrefix + message);
    }
}
