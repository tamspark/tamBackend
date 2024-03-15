package com.sparklab.TAM.contollers;

import com.sparklab.TAM.exceptions.ApiCallError;
import com.sparklab.TAM.model.ApartmentOption;
import com.sparklab.TAM.services.ApartmentOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("TAM/apartmentOption")
@RequiredArgsConstructor
public class ApartmentOptionController {

    private final ApartmentOptionService apartmentOptionService;


    @PostMapping("/saveOrUpdateApartmentOption")
    public ResponseEntity<?> saveOrUpdateApartmentOption(@RequestBody ApartmentOption apartmentOption) {
        return new ResponseEntity<>(apartmentOptionService.saveOrUpdateApartmentOption(apartmentOption), HttpStatus.OK);

    }


    @GetMapping
    public ResponseEntity<?> getAllApartmentOptions() {
            return new ResponseEntity<>(apartmentOptionService.getAllApartmentOptions(), HttpStatus.OK);
    }



    @GetMapping("{id}")
    public ResponseEntity<?> getApartmentOptionById(@PathVariable Long id){
            return new ResponseEntity<>(apartmentOptionService.getApartmentOptionById(id),HttpStatus.OK );

    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteApartmentOptionById(@PathVariable Long id){
        return new ResponseEntity<>(apartmentOptionService.deleteApartmentOption(id),HttpStatus.OK );

    }

}
