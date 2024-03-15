package com.sparklab.TAM.model;

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
public class ApartmentLinkedapartmentOptions extends BaseEntity {

    private Long apartmentId;

    @ManyToOne
    @JoinColumn(name="apartmentOptionId",referencedColumnName = "id")
    private ApartmentOption apartmentOption;

}
