package com.example.kulvida.entity.cloth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "CATEGORY")
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "CATEGORY_ID")
    private Integer id;

    @Column(name = "CATEGORY_NAME")
    private String name;

    public Category(String name){
        this.name=name;
    }


}
