package com.epicdima.theatraxity.domain.common;

/**
 * @author EpicDima
 */
public abstract class Result<T> {

    private Result() {
        // only for nested subclasses
    }

    public abstract T getValue();

    public abstract boolean isSuccess();

    public abstract boolean isFailure();

    public abstract boolean isEmpty();

    public abstract Success<T> asSuccess();

    public abstract Failure asFailure();

    @SuppressWarnings("unchecked")
    public static <T> Result<T> empty() {
        return new Success<>((T) Success.Unit.UNIT);
    }

    @SuppressWarnings("unchecked")
    public static <T> Result<T> success(T data) {
        if (data == null) {
            return (Result<T>) new Failure(Failure.NULL_VALUE, Failure.NULL_VALUE);
        }
        return new Success<>(data);
    }

    @SuppressWarnings("unchecked")
    public static <T> Result<T> failure(int error, int code) {
        return (Result<T>) new Failure(error, code);
    }

    public static <T> Result<T> failure() {
        return failure(Failure.UNKNOWN_ERROR, Failure.UNKNOWN_ERROR);
    }

    public static <T, R> Result<R> failure(Result<T> result) {
        Failure failure = result.asFailure();
        return failure(failure.error, failure.code);
    }

    public static <T, R> Result<R> map(Result<T> result, Transformer<T, R> transformer) {
        if (result.isFailure()) {
            return failure(result);
        }
        return success(transformer.transform(result.getValue()));
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Result<R> identity(Result<T> result) {
        if (result.isFailure()) {
            return failure(result);
        }
        return (Result<R>) success(result.getValue());
    }


    public static final class Success<T> extends Result<T> {
        private final T data;

        private Success(T data) {
            this.data = data;
        }

        @Override
        public T getValue() {
            return data;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return Unit.UNIT.equals(data);
        }

        @Override
        public Success<T> asSuccess() {
            return this;
        }

        @Override
        public Failure asFailure() {
            throw new IllegalStateException("This Result is not a Failure instance");
        }


        private static final class Unit {
            private static final Unit UNIT = new Unit();

            private Unit() {}
        }
    }


    public static final class Failure extends Result<Integer> {
        public static final int UNKNOWN_ERROR = 0;
        public static final int NULL_VALUE = 1;

        private transient final int error;
        private final int code;

        private Failure(int error, int code) {
            this.error = error;
            this.code = code;
        }

        @Override
        public Integer getValue() {
            return error;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public boolean isEmpty() {
            throw new IllegalStateException("Failure instance cannot be empty or non empty");
        }

        @Override
        public Success<Integer> asSuccess() {
            throw new IllegalStateException("This Result is not a Success instance");
        }

        @Override
        public Failure asFailure() {
            return this;
        }

        public boolean isUnknown() {
            return UNKNOWN_ERROR == error;
        }

        public boolean isNull() {
            return NULL_VALUE == error;
        }
    }


    @FunctionalInterface
    public interface Transformer<T, R> {
        R transform(T value);
    }
}
