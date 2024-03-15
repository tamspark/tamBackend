package com.sparklab.TAM.repositories;

import com.sparklab.TAM.model.ApartmentOptionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentOptionCategoriesRepository extends JpaRepository<ApartmentOptionCategory,Long>{

}
