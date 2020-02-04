package io.stars.transform.tcp.factory;

import io.stars.transform.error.TCPClientInitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Socket;

import static java.lang.String.format;

@Slf4j
@Component
public class SocketFactory
{
    public Socket createSocket(final String host, final Integer port)
    {
        try {
            return new Socket(host, port);
        } catch (IOException e) {
            log.error("Failed to create socket: {}", e.getMessage());
            throw new TCPClientInitException(format("Failed to create socket: %s", e.getMessage()));
        }
    }
}
