package com.sparklab.TAM.contollers;

import com.sparklab.TAM.model.SpecificApartmentOption;
import com.sparklab.TAM.services.SpecificApartmentOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("TAM/specificApartmentOption")
@RequiredArgsConstructor
public class SpecificApartmentOptionController {



    private final SpecificApartmentOptionService specificApartmentOptionService;


    @PostMapping("/saveOrUpdateSpecificApartmentOption")
    public ResponseEntity<?> saveOrUpdateSpecificApartmentOption(@RequestBody SpecificApartmentOption specificApartmentOption) {
        return new ResponseEntity<>(specificApartmentOptionService.saveOrUpdateSpecificApartmentOption(specificApartmentOption), HttpStatus.OK);

    }


    @GetMapping
    public ResponseEntity<?> getAllSpecificApartmentOptions() {
        return new ResponseEntity<>(specificApartmentOptionService.getAllSpecificApartmentOptions(), HttpStatus.OK);
    }


    @GetMapping("getAllSpecificApartmentOptionsByApartment/{apartmentId}")
    public ResponseEntity<?> getAllSpecificApartmentOptions(@PathVariable String apartmentId) {
        return new ResponseEntity<>(specificApartmentOptionService.getAllSpecificApartmentOptionsPerApartment(apartmentId), HttpStatus.OK);
    }


    @GetMapping("{id}")
    public ResponseEntity<?> getSpecificApartmentOptionById(@PathVariable Long id){
        return new ResponseEntity<>(specificApartmentOptionService.getSpecificApartmentOptionById(id),HttpStatus.OK );

    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteSpecificApartmentOptionById(@PathVariable Long id){
        return new ResponseEntity<>(specificApartmentOptionService.deleteSpecificApartmentOption(id),HttpStatus.OK );

    }





}
