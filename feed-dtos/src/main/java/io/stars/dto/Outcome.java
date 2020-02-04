package io.stars.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Outcome extends Message
{
    private String marketId;
    private String outcomeId;
    private String price;

    @Builder
    private Outcome(final Header header, final String name, final boolean displayed, final boolean suspended,
                    final String marketId, final String outcomeId, final String price)
    {
        super(header, name, displayed, suspended);
        this.marketId = marketId;
        this.outcomeId = outcomeId;
        this.price = price;
    }
}
