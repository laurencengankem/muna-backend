package com.example.kulvida.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "user_address")
public class UserAddress {

    @Id
    private Integer id;

    private String country;
    private String city;
    private Integer zip;
    private String street;

}
