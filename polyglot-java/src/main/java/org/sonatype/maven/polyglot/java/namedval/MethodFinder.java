package org.sonatype.maven.polyglot.java.namedval;

import static java.util.Arrays.asList;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

public interface MethodFinder extends Serializable {
    default SerializedLambda serialized() {
        try {
            Method replaceMethod = getClass().getDeclaredMethod("writeReplace");
            replaceMethod.setAccessible(true);
            return (SerializedLambda) replaceMethod.invoke(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default Class<?> getContainingClass() {
        try {
            String className = serialized().getImplClass().replaceAll("/", ".");
            return Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default Method method() {
        SerializedLambda lambda = serialized();
        Class<?> containingClass = getContainingClass();
        return asList(containingClass.getDeclaredMethods()).stream()
                .filter(method -> Objects.equals(method.getName(), lambda.getImplMethodName()))
                .findFirst()
                .orElseThrow(UnableToGuessMethodException::new);
    }

    default Parameter parameter(int n) {
        return method().getParameters()[n];
    }

    default Object defaultValueForParameter(int n) {
        return DefaultValue.ofType(parameter(n).getType());
    }

    class UnableToGuessMethodException extends RuntimeException {}
}
