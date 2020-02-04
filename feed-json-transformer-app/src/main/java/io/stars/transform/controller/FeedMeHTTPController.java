package io.stars.transform.controller;

import io.stars.transform.service.FeedMeService;
import io.stars.transform.data.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/feedme")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FeedMeHTTPController
{
    private final FeedMeService feedMeService;

    @GetMapping("/messages")
    public Mono<ResponseEntity<MessageResponse>> getMessages(@RequestParam("n") final Integer amountOfMessages)
    {
        return feedMeService.getMessages(amountOfMessages)
                            .collectList()
                            .map(messages -> MessageResponse.builder().messages(messages).build())
                            .map(ResponseEntity::ok);
    }

    @GetMapping("/store")
    public Mono<ResponseEntity<Object>> storeMessagesToMongoDB(@RequestParam(value = "n") final Integer amountOfMessages)
    {
        return Mono.when(feedMeService.getMessagesAndSendToMongoDB(amountOfMessages))
                   .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
