package com.sparklab.TAM.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MeetingLinkDTO {

    private Long reservationId;
    public String guestName;
}
