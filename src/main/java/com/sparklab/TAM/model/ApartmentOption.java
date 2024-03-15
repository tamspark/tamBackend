package com.sparklab.TAM.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApartmentOption extends BaseEntity {

//    @Column(unique = true)
    private String description;
    private String details;
    @ManyToOne
    @JoinColumn(name="apartmentOptionCategoryId",referencedColumnName = "id")
    private ApartmentOptionCategory apartmentOptionCategory;

}
