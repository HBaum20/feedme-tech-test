package io.stars.transform.service;

import io.stars.dto.Event;
import io.stars.dto.Market;
import io.stars.dto.Message;
import io.stars.dto.Outcome;
import io.stars.transform.connector.DataStoreConnector;
import io.stars.transform.service.transform.JsonObjectCreator;
import io.stars.transform.tcp.FeedMeTCPClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FeedMeService
{
    private final FeedMeTCPClient feedMeTCPClient;
    private final JsonObjectCreator objectCreator;
    private final DataStoreConnector dataStoreConnector;
    private final String host;
    private final Integer clientPort;

    public Flux<Message> getMessages(final Integer amountOfMessages)
    {
        feedMeTCPClient.initialise(host, clientPort);
        return feedMeTCPClient.getDataStream()
                              .take(amountOfMessages)
                              .map(objectCreator::createMessage)
                              .doOnComplete(feedMeTCPClient::terminate);
    }

    public Flux<Message> getMessagesAndSendToMongoDB(final Integer amountOfMessages)
    {
        feedMeTCPClient.initialise(host, clientPort);
        return feedMeTCPClient.getDataStream()
                              .take(amountOfMessages)
                              .flatMap(message -> Mono.just(objectCreator.createMessage(message)))
                              .map(messageDocument -> sendToDatabase(messageDocument).block())
                              .doOnComplete(feedMeTCPClient::terminate);
    }

    private Mono<Message> sendToDatabase(final Message message)
    {
        if(message instanceof Event) {
            return Mono.when(dataStoreConnector.saveEventToDatabase((Event)message))
                       .then(Mono.just(message));
        } else if(message instanceof Market) {
            return Mono.when(dataStoreConnector.saveMarketToDatabase((Market)message))
                       .then(Mono.just(message));
        } else if(message instanceof Outcome) {
            return Mono.when(dataStoreConnector.saveOutcomeToDatabase((Outcome)message))
                       .then(Mono.just(message));
        } else {
            return Mono.empty();
        }
    }
}
