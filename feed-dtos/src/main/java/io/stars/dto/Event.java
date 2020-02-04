package io.stars.dto;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event extends Message
{
    private String eventId;
    private String category;
    private String subCategory;
    private Long startTime;

    @Builder
    private Event(final Header header, final String name, final boolean displayed, final boolean suspended,
                  final String eventId, final String category, final String subCategory, final Long startTime)
    {
        super(header, name, displayed, suspended);
        this.eventId = eventId;
        this.category = category;
        this.subCategory = subCategory;
        this.startTime = startTime;
    }
}
