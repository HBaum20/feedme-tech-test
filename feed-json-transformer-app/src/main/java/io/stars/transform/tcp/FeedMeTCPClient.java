package io.stars.transform.tcp;

import io.stars.transform.error.TCPClientInitException;
import io.stars.transform.tcp.factory.BufferedReaderFactory;
import io.stars.transform.tcp.factory.SocketFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedMeTCPClient
{
    private final SocketFactory socketFactory;
    private final BufferedReaderFactory readerFactory;

    private BufferedReader reader;
    private Socket socket;

    public void initialise(final String host, final Integer port)
    {
        try {
            this.socket = socketFactory.createSocket(host, port);
            this.reader = readerFactory.createBufferedReaderFromSocket(socket);
            log.info("FeedMe TCP Client has been initialised");
        } catch(IOException ex) {
            log.error("Failed to initialise the FeedMe TCP Client: {}", ex.getMessage());
            throw new TCPClientInitException(format("Failed to initialise the FeedMe TCP Client: %s", ex.getMessage()));
        }
    }

    public void terminate()
    {
        try {
            reader.close();
            socket.close();
            log.info("FeedMe TCP Client has been terminated");
        } catch(IOException ex) {
            log.error("Failed to terminate the TCP Client: {}", ex.getMessage());
        }
    }

    public Flux<String> getDataStream() {
        return Optional.ofNullable(reader)
                       .map(bufReader -> Flux.fromStream(bufReader.lines()))
                       .orElseGet(() -> {
                           log.warn("TCP Client is not connected to FeedMe provider");
                           throw new TCPClientInitException("TCP Client is not connected to FeedMe Client");
                       });
    }
}
