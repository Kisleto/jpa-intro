package com.codecool.jpaintro.entity;

import com.sun.javafx.beans.IDProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Address {

    @Id
    @GeneratedValue
    private Long id;

    private String country;
    private String city;
    private String address;
    private Integer zipCode;

    @OneToOne(mappedBy = "address")
    @EqualsAndHashCode.Exclude
    private Student student;
}
