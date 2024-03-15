package com.sparklab.TAM.services;

import com.sparklab.TAM.exceptions.NotFoundException;
import com.sparklab.TAM.model.ApartmentOption;
import com.sparklab.TAM.model.SpecificApartmentOption;
import com.sparklab.TAM.repositories.SpecificApartmentOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecificApartmentOptionService {


    private final SpecificApartmentOptionRepository specificApartmentOptionRepository;

    public String saveOrUpdateSpecificApartmentOption(SpecificApartmentOption specificApartmentOption) {

        if (specificApartmentOption != null) {
            specificApartmentOptionRepository.save(specificApartmentOption);
            return "Specific ApartmentOption Saved Successfully.";

        } else

            return null;
    }

    public List<SpecificApartmentOption> getAllSpecificApartmentOptions() {
        return specificApartmentOptionRepository.findAll();
    }

    public SpecificApartmentOption getSpecificApartmentOptionById(Long id) {
        return specificApartmentOptionRepository.findById(id).orElseThrow(
                () -> new NotFoundException("The requested specific apartment option could not be found"));
    }

    public String deleteSpecificApartmentOption(Long id) {
        specificApartmentOptionRepository.deleteById(id);
        return "Specific apartment Option was deleted successfully";
    }

    public List<SpecificApartmentOption> getAllSpecificApartmentOptionsPerApartment(String apartmentId) {
        Long parseApartmentId=Long.parseLong(apartmentId);
        return specificApartmentOptionRepository.findByApartmentId(parseApartmentId);
    }
}
