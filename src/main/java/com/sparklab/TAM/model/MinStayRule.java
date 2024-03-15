package com.sparklab.TAM.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MinStayRule extends BaseEntity {

    @Column(unique = true)
    private int day;
    private int minStay;
    private Long userId;
}
