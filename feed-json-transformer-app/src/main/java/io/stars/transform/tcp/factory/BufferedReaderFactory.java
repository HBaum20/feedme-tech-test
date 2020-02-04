package io.stars.transform.tcp.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@Slf4j
@Component
public class BufferedReaderFactory
{
    public BufferedReader createBufferedReaderFromSocket(final Socket socket) throws IOException
    {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
}
