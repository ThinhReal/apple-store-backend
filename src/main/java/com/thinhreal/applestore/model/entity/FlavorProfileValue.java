package com.thinhreal.applestore.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FlavorProfileValue {

    private Integer sweetnessLevel;
    private Integer tartnessLevel;
    private String overallProfile;
    private List<String> dominantNotes = new ArrayList<>();
    private String tastingDescription;
}
