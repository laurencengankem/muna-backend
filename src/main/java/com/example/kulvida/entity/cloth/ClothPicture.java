package com.example.kulvida.entity.cloth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="CLOTH_PICTURES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClothPicture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(name = "url")
    public String url;

    @Column(name = "tag")
    public String tag;
}
