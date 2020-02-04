package io.stars.transform.error;

import static java.lang.String.format;

public class TCPClientInitException extends RuntimeException
{
    public TCPClientInitException(final String message)
    {
        super(format("Failed to initialize TCP Client: %s", message));
    }
}
