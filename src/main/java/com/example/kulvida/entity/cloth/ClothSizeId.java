package com.example.kulvida.entity.cloth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ClothSizeId implements Serializable {

    @Column(name = "CLOTH_ID")
    private Integer clothId;

    @Column(name = "SIZE_ID")
    private Integer sizeId;

}
