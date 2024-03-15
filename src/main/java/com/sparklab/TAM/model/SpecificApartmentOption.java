package com.sparklab.TAM.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;

@Entity
@Getter
@Setter
public class SpecificApartmentOption extends BaseEntity{

    @Column(unique=true)
    private String optionName;
//    private String description;
    private Long apartmentId;
    private URL link;
}
