package com.sparklab.TAM.dto.minStay;

import com.sparklab.TAM.model.MinStayRule;
import lombok.Data;

import java.util.List;


@Data
public class MinStayRuleListDTO {

    List<MinStayRule> minStayDTOList;
}
