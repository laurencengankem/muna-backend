package com.example.kulvida.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="itemPictures")
@Data
public class ItemPicture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(name = "url")
    public String url;

    @Column(name = "tag")
    public String tag;

}
