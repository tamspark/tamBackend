package com.sparklab.TAM.dto.apartment;

import com.sparklab.TAM.model.ApartmentOptionCategory;
import lombok.Data;

@Data
public class ApartmentOptionswithStatuses {

   private Long optionId;
   private String description;
   private boolean checked;
   private String details;
   private ApartmentOptionCategory apartmentOptionCategory;
}
