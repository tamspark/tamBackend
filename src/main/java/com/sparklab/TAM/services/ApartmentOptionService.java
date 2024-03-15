package com.sparklab.TAM.services;

import com.sparklab.TAM.dto.apartment.Apartment;
import com.sparklab.TAM.exceptions.NotFoundException;
import com.sparklab.TAM.model.ApartmentOption;
import com.sparklab.TAM.repositories.ApartmentOptionRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentOptionService {

    private final ApartmentOptionRepository apartmentOptionRepository;


    public String saveOrUpdateApartmentOption(ApartmentOption apartmentOption) {
        if (apartmentOption.getId() != null) {
            ApartmentOption savedApartmentOption = apartmentOptionRepository.findById(apartmentOption.getId()).get();
            savedApartmentOption.setDescription(apartmentOption.getDescription());
            savedApartmentOption.setDetails(apartmentOption.getDetails());
            apartmentOptionRepository.save(savedApartmentOption);
        }
        apartmentOptionRepository.save(apartmentOption);
        return "Apartment Option Saved Successfully";
    }


    public List<ApartmentOption> getAllApartmentOptions() {
        return apartmentOptionRepository.findAll();
    }


    public ApartmentOption getApartmentOptionById(Long id) {
        return apartmentOptionRepository.findById(id).orElseThrow(() -> new NotFoundException("No apartment option exist with this id"));
    }

    public String deleteApartmentOption(Long id){
        apartmentOptionRepository.deleteById(id);
        return "Apartment option deleted successfully";
    }




}
