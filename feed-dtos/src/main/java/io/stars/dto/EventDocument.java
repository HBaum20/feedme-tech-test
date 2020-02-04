package io.stars.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "events")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDocument {
    private Long msgId;

    private String operation;

    private String type;

    private Long timestamp;

    @Id
    private String eventId;

    private String category;

    private String subCategory;

    private String name;

    private Long startTime;

    private boolean displayed;

    private boolean suspended;

    @Builder.Default
    private List<MarketDocument> markets = new ArrayList<>();
}
