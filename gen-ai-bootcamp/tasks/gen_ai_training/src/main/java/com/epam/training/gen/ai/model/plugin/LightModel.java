package com.epam.training.gen.ai.model.plugin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LightModel {
    private Integer id;
    private String description;
    private Boolean isOn;
}
