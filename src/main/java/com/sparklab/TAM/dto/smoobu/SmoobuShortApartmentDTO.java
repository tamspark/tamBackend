package com.sparklab.TAM.dto.smoobu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sparklab.TAM.model.ApartmentOption;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmoobuShortApartmentDTO {
    int id;
    String name;

}
