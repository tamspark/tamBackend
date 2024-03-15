package com.sparklab.TAM.contollers;

import com.sparklab.TAM.dto.apartment.ApartmentLinkedApartmentOption;
import com.sparklab.TAM.dto.minStay.MinStayRuleListDTO;
import com.sparklab.TAM.exceptions.ApiCallError;
import com.sparklab.TAM.model.MinStayRule;
import com.sparklab.TAM.repositories.MinStayRepository;
import com.sparklab.TAM.services.MinStayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("TAM/minStay")
@RequiredArgsConstructor
public class MinStayController {


    private final MinStayService minStayService;

    @PostMapping("saveOrUpdate")
    public ResponseEntity<?> saveMinStayRules(@RequestBody List<MinStayRule> minStayRuleList) {
        return new ResponseEntity<>(minStayService.saveMinStayRules(minStayRuleList), HttpStatus.OK);
    }


    @GetMapping("getAllMinStays")
    public ResponseEntity<?> getAllMinStayRules() {
        try {
            return new ResponseEntity<>(minStayService.findAllMinStayRules(), HttpStatus.OK);
        } catch (ApiCallError e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
        }
    }


}
