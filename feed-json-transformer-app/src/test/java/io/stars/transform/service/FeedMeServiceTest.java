package io.stars.transform.service;

import io.stars.dto.*;
import io.stars.transform.connector.DataStoreConnector;
import io.stars.transform.error.TCPClientInitException;
import io.stars.transform.service.transform.JsonObjectCreator;
import io.stars.transform.tcp.FeedMeTCPClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FeedMeServiceTest
{
    @Mock
    private FeedMeTCPClient feedMeTCPClient;

    @Mock
    private JsonObjectCreator objectCreator;

    @Mock
    private DataStoreConnector connector;

    @InjectMocks
    private FeedMeService feedMeService;

    private List<String> proprietaryData = generateProprietaryData();
    private List<Message> messages = generateMessages();

    @Before
    public void setUp()
    {
        when(feedMeTCPClient.getDataStream()).thenReturn(Flux.fromIterable(proprietaryData));
        when(objectCreator.createMessage(proprietaryData.get(0))).thenReturn(messages.get(0));
        when(objectCreator.createMessage(proprietaryData.get(1))).thenReturn(messages.get(1));
        when(objectCreator.createMessage(proprietaryData.get(2))).thenReturn(messages.get(2));
        when(feedMeTCPClient.getDataStream()).thenReturn(Flux.fromIterable(proprietaryData));

        final Event event = (Event)messages.get(0);
        when(connector.saveEventToDatabase(event)).thenReturn(Mono.just(generateEventDocument()));

        final Market market = (Market)messages.get(1);
        when(connector.saveMarketToDatabase(market)).thenReturn(Mono.just(generateMarketDocument()));

        final Outcome outcome = (Outcome)messages.get(2);
        when(connector.saveOutcomeToDatabase(outcome)).thenReturn(Mono.just(generateOutcomeDocument()));
    }

    @Test
    public void getMessages_returnsMessagesToUser()
    {
        StepVerifier.create(feedMeService.getMessages(3))
                    .expectNext(messages.get(0))
                    .expectNext(messages.get(1))
                    .expectNext(messages.get(2))
                    .verifyComplete();
    }

    @Test
    public void getMessages_withAmount_returnsQuantityAsRequested()
    {
        StepVerifier.create(feedMeService.getMessages(2))
                    .expectNextCount(2)
                    .verifyComplete();
    }

    @Test
    public void getMessages_terminatesUponCompletion()
    {
        StepVerifier.create(feedMeService.getMessages(3))
                    .expectNextCount(3)
                    .verifyComplete();

        verify(feedMeTCPClient).terminate();
    }

    @Test(expected = TCPClientInitException.class)
    public void getMessages_whenNotConnectedToProvider_throwsException()
    {
        when(feedMeTCPClient.getDataStream()).thenThrow(TCPClientInitException.class);
        feedMeService.getMessages(3).blockLast();
    }

    @Test
    public void getMessagesAndSaveToMongo_sendsMessagesToDataStore()
    {
        final Event event = (Event)messages.get(0);
        final Market market = (Market)messages.get(1);
        final Outcome outcome = (Outcome)messages.get(2);

        StepVerifier.create(feedMeService.getMessagesAndSendToMongoDB(3))
                    .expectNext(messages.get(0))
                    .expectNext(messages.get(1))
                    .expectNext(messages.get(2))
                    .verifyComplete();

        verify(connector, times(1)).saveEventToDatabase(event);
        verify(connector, times(1)).saveMarketToDatabase(market);
        verify(connector, times(1)).saveOutcomeToDatabase(outcome);
    }

    private static List<String> generateProprietaryData()
    {
        return Arrays.asList(
                "|1|create|event|1580488990623|ff3e7e4c-df91-4967-9ed9-1cca4b32aaa3|Football|Sky Bet League Two|\\|Colchester\\| vs \\|Coventry\\||1580489024113|0|1|",
                "|2|create|market|1580488990625|ff3e7e4c-df91-4967-9ed9-1cca4b32aaa3|76b986b6-d263-4ae6-8b87-043b29b61995|Full Time Result|0|1|",
                "|3|create|outcome|1580488990626|76b986b6-d263-4ae6-8b87-043b29b61995|b032f0f9-2290-4f84-8c7c-24a7c5da0bac|\\|Colchester\\||6/5|0|1|"
        );
    }

    private EventDocument generateEventDocument()
    {
        return EventDocument.builder()
                            .msgId(1L)
                            .operation("create")
                            .type("event")
                            .timestamp(1580488990623L)
                            .eventId("ff3e7e4c-df91-4967-9ed9-1cca4b32aaa3")
                            .category("Football")
                            .subCategory("Sky Bet League Two")
                            .name("|Colchester| vs |Coventry|")
                            .startTime(1580489024113L)
                            .displayed(false)
                            .suspended(true)
                            .build();
    }

    private MarketDocument generateMarketDocument()
    {
        return MarketDocument.builder()
                             .msgId(2L)
                             .operation("create")
                             .type("market")
                             .timestamp(1580488990625L)
                             .eventId("ff3e7e4c-df91-4967-9ed9-1cca4b32aaa3")
                             .marketId("76b986b6-d263-4ae6-8b87-043b29b61995")
                             .name("Full Time Result")
                             .displayed(false)
                             .suspended(true)
                             .build();
    }

    private OutcomeDocument generateOutcomeDocument()
    {
        return OutcomeDocument.builder()
                              .msgId(3L)
                              .operation("create")
                              .type("outcome")
                              .timestamp(1580488990626L)
                              .marketId("76b986b6-d263-4ae6-8b87-043b29b61995")
                              .outcomeId("b032f0f9-2290-4f84-8c7c-24a7c5da0bac")
                              .name("|Colchester|")
                              .price("6/5")
                              .displayed(false)
                              .suspended(true)
                              .build();
    }

    private static List<Message> generateMessages()
    {
        return Arrays.asList(
                Event.builder()
                     .header(Header.builder()
                                   .msgId(1L)
                                   .operation("create")
                                   .type("event")
                                   .timestamp(1580488990623L)
                                   .build())
                     .eventId("ff3e7e4c-df91-4967-9ed9-1cca4b32aaa3")
                     .category("Football")
                     .subCategory("Sky Bet League Two")
                     .name("|Colchester| vs |Coventry|")
                     .startTime(1580489024113L)
                     .suspended(true)
                     .displayed(false)
                     .build(),
                Market.builder()
                      .header(Header.builder()
                                    .msgId(2L)
                                    .operation("create")
                                    .type("market")
                                    .timestamp(1580488990625L)
                                    .build())
                      .eventId("ff3e7e4c-df91-4967-9ed9-1cca4b32aaa3")
                      .marketId("76b986b6-d263-4ae6-8b87-043b29b61995")
                      .name("Full Time Result")
                      .displayed(false)
                      .suspended(true)
                      .build(),
                Outcome.builder()
                       .header(Header.builder()
                                     .msgId(3L)
                                     .operation("create")
                                     .type("outcome")
                                     .timestamp(1580488990626L)
                                     .build())
                       .marketId("76b986b6-d263-4ae6-8b87-043b29b61995")
                       .outcomeId("b032f0f9-2290-4f84-8c7c-24a7c5da0bac")
                       .name("|Colchester|")
                       .price("6/5")
                       .displayed(false)
                       .suspended(true)
                       .build()
        );
    }
}