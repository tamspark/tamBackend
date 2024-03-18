package com.sparklab.TAM.services;

import com.sparklab.TAM.dto.minStay.MinStayDTO;
import com.sparklab.TAM.dto.minStay.MinStayRuleListDTO;
import com.sparklab.TAM.model.MinStayRule;
import com.sparklab.TAM.repositories.MinStayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MinStayService {

    private final MinStayRepository minStayRepository;

    public String saveMinStayRules(List<MinStayRule> minStayRuleList) {
        for (MinStayRule minStayRule : minStayRuleList) {
            if (minStayRule.getMinStay() <= minStayRule.getDay())
                minStayRepository.save(minStayRule);
        }
        return "Minimum stay rules saved successfully";
    }

    public List<MinStayRule> findAllMinStayRules() {
        Sort sortByDay = Sort.by("day").descending();
        return minStayRepository.findAll(sortByDay);
    }
}
