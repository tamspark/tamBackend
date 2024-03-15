package com.sparklab.TAM.repositories;

import com.sparklab.TAM.model.ApartmentLinkedapartmentOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentLinkedApartmentOptionRepository extends JpaRepository<ApartmentLinkedapartmentOptions,Long> {


    void deleteByApartmentIdAndApartmentOptionId(Long apartmentId, Long id);

    List<ApartmentLinkedapartmentOptions> findByApartmentId(Long apartmentId);
    @Query(value = "select apartment_option_id from apartment_linkedapartment_options where apartment_id=:apartmentId",nativeQuery = true)
    List<Long> getOptionIdsByApartmentId(Long apartmentId);

}
