package com.sparklab.TAM.services;

import com.sparklab.TAM.model.ApartmentOptionCategory;
import com.sparklab.TAM.repositories.ApartmentOptionCategoriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentOptionCategoriesService {

    private final ApartmentOptionCategoriesRepository apartmentOptionCategoriesRepository;
    public List<ApartmentOptionCategory> getAllApartmentOptionCategories() {
        return apartmentOptionCategoriesRepository.findAll();

    }
}
