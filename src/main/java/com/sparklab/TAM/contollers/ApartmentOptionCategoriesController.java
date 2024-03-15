package com.sparklab.TAM.contollers;

import com.sparklab.TAM.services.ApartmentOptionCategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("TAM/apartmentOptionCategory")
public class ApartmentOptionCategoriesController {

    private final ApartmentOptionCategoriesService apartmentOptionCategoriesService;



    @GetMapping("getallApartmentOptions")
    public ResponseEntity<?> getAllApartmentsOptionCategories() {
        return new ResponseEntity<>(apartmentOptionCategoriesService.getAllApartmentOptionCategories(), HttpStatus.OK);
    }


}
