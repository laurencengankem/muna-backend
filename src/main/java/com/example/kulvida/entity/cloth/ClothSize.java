package com.example.kulvida.entity.cloth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "CLOTH_SIZES")
@NoArgsConstructor
@AllArgsConstructor
public class ClothSize implements Serializable {

    @EmbeddedId
    private ClothSizeId id = new ClothSizeId();

    @ManyToOne
    @MapsId("clothId")
    @JoinColumn(name = "CLOTH_ID")
    private Cloth cloth;

    @ManyToOne
    @MapsId("sizeId")
    @JoinColumn(name = "SIZE_ID")
    private Size size;

    // Additional columns in the join table (quantity and price)
    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = true)
    private Integer magasin;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = true)
    private String location;
}
