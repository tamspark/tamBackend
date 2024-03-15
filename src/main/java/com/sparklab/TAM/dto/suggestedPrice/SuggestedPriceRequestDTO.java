package com.sparklab.TAM.dto.suggestedPrice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SuggestedPriceRequestDTO {
    @JsonProperty("Price_per_night")
    private int pricePerNight;

    @JsonProperty("stay_duration")
    private int stayDuration;

    @JsonProperty("days_until_checkin")
    private int daysUntilCheckin;

    @JsonProperty("Checkin_date_zero")
    private int checkinDateZero;

    @JsonProperty("Checkout_date_zero")
    private int checkoutDateZero;

    @JsonProperty("Request_date_zero")
    private int requestDateZero;

    @JsonProperty("Room_Type_Category_private_room")
    private int roomTypeCategoryPrivateRoom = 0;
}
