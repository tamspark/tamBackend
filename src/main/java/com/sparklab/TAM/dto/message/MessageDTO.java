package com.sparklab.TAM.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDTO {
    private int id;
    private String subject;
    private String message;
    private String htmlMessage;
    private int type;
}
