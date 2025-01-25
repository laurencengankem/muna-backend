package com.example.kulvida.entity.cloth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "SIZES")
@NoArgsConstructor
@AllArgsConstructor
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "SIZE_ID")
    private Integer id;

    @Column(name = "SIZE_NAME")
    private String name;

    @OneToMany(mappedBy = "size", cascade = CascadeType.ALL)
    private List<ClothSize> clothSizes;

    public Size(String name){
        this.name=name;
    }

}
