package com.sparklab.TAM.repositories;


import com.sparklab.TAM.model.SpecificApartmentOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecificApartmentOptionRepository extends JpaRepository<SpecificApartmentOption,Long> {


    List<SpecificApartmentOption> findByApartmentId(Long parseApartmentId);
}
