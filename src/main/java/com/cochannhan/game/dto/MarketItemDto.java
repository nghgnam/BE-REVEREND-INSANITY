package com.cochannhan.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketItemDto {
    private String guCode;
    private String name;
    private int rank;
    private String type;
    private String pathName;
    private String icon;
    private int stoneCost;
    private int contributionCost;
    private String description;
}
