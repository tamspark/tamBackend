package com.sparklab.TAM.repositories;

import com.sparklab.TAM.model.ApartmentOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentOptionRepository extends JpaRepository<ApartmentOption,Long> {

    @Query(value="select * from apartment_option where id in:ids",nativeQuery=true)
    List<ApartmentOption> getAllApartmentOptionsByTheirIds(List<Long> ids);

}
