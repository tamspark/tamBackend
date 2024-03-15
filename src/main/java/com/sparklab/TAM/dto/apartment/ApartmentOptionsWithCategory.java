package com.sparklab.TAM.dto.apartment;

import com.sparklab.TAM.model.ApartmentOptionCategory;
import lombok.Data;

import java.util.List;

@Data
public class ApartmentOptionsWithCategory {
    private ApartmentOptionCategory category;
    private List<ApartmentOptionswithStatuses> apartmentOptions;
}
