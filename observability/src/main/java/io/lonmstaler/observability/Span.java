package io.lonmstaler.observability;

public interface Span extends AutoCloseable {
    void setError(Throwable t);
    @Override
    void close();
}
