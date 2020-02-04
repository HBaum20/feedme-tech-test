package io.stars.store.error;

public class MalformedEventException extends RuntimeException
{
    public MalformedEventException(final String message)
    {
        super(message);
    }
}
