package com.sparklab.TAM.dto.minStay;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class MinStayDTO {
 private List<String> dates;
 private int minStay;
 private int apartmentId;
}
