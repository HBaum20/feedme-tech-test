package io.stars.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Market extends Message
{
    private String eventId;
    private String marketId;

    @Builder
    private Market(final Header header, final String name, final boolean displayed, final boolean suspended,
                   final String eventId, final String marketId)
    {
        super(header, name, displayed, suspended);
        this.eventId = eventId;
        this.marketId = marketId;
    }
}
