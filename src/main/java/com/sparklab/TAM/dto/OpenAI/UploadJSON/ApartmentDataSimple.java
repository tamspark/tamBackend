package com.sparklab.TAM.dto.OpenAI.UploadJSON;

import com.sparklab.TAM.model.ApartmentOption;
import com.sparklab.TAM.model.SpecificApartmentOption;
import lombok.Data;

import java.util.List;

@Data
public class ApartmentDataSimple {
    String name;
    Double price;
    int minLengthOfStay;
    int available;
    List<ApartmentOption> apartmentOptions;
    List<SpecificApartmentOption> specificApartmentOptions;
}
