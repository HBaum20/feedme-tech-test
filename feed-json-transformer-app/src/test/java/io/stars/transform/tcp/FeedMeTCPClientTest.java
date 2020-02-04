package io.stars.transform.tcp;

import io.stars.transform.tcp.factory.BufferedReaderFactory;
import io.stars.transform.tcp.factory.SocketFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.test.StepVerifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FeedMeTCPClientTest
{
    @Mock
    private SocketFactory socketFactory;

    @Mock
    private BufferedReaderFactory readerFactory;

    @Mock
    private Socket socket;

    @Mock
    private BufferedReader reader;

    /*
        Mock Data
     */
    private String packet1 = "|1|create|event|1580488990623|ff3e7e4c-df91-4967-9ed9-1cca4b32aaa3|Football|Sky Bet League Two|\\|Colchester\\| vs \\|Coventry\\||1580489024113|0|1|";
    private String packet2 = "|2|create|market|1580488990625|ff3e7e4c-df91-4967-9ed9-1cca4b32aaa3|76b986b6-d263-4ae6-8b87-043b29b61995|Full Time Result|0|1|";
    private String packet3 = "|3|create|outcome|1580488990626|76b986b6-d263-4ae6-8b87-043b29b61995|b032f0f9-2290-4f84-8c7c-24a7c5da0bac|\\|Colchester\\||6/5|0|1|";
    private String packet4 = "|4|create|outcome|1580488990626|76b986b6-d263-4ae6-8b87-043b29b61995|a640c304-e62e-4eaf-81bb-e0dea873d154|Draw|1/500|0|1|";
    private String packet5 = "|5|create|outcome|1580488990626|76b986b6-d263-4ae6-8b87-043b29b61995|0494f3a8-434a-4ede-90f2-fb0f04dbf6b7|\\|Coventry\\||1/1|0|1|";

    @InjectMocks
    private FeedMeTCPClient feedMeTCPClient;

    @Before
    public void setUp() throws IOException {
        when(socketFactory.createSocket(anyString(), anyInt())).thenReturn(socket);
        when(readerFactory.createBufferedReaderFromSocket(socket)).thenReturn(reader);
        when(reader.lines()).thenReturn(Stream.of(
                packet1,
                packet2,
                packet3,
                packet4,
                packet5
        ));
    }

    @Test
    public void initialiseClient_shouldGetSocketInputStream() throws IOException {
        feedMeTCPClient.initialise("localhost", 6969);
        verify(socketFactory, times(1)).createSocket("localhost", 6969);
        verify(readerFactory, times(1)).createBufferedReaderFromSocket(socket);
    }

    @Test
    public void terminateClient_shouldCloseReaderAndSocket() throws IOException {
        feedMeTCPClient.initialise("localhost", 6969);
        feedMeTCPClient.terminate();
        verify(reader, times(1)).close();
        verify(socket, times(1)).close();
    }

    @Test
    public void getFeedMeDataStream_returnsFluxOfData() {
        feedMeTCPClient.initialise("localhost", 6969);
        StepVerifier.create(feedMeTCPClient.getDataStream())
                    .expectNext(packet1)
                    .expectNext(packet2)
                    .expectNext(packet3)
                    .expectNext(packet4)
                    .expectNext(packet5)
                    .verifyComplete();
        feedMeTCPClient.terminate();
    }
}