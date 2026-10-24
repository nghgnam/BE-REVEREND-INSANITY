package com.cochannhan.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefineGuRequest {
    private String primaryGuCode;
    private String secondaryGuCode;
    private String materialName;
    private String environmentCondition;
}
