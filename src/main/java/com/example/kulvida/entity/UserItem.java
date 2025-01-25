package com.example.kulvida.entity;

import com.example.kulvida.entity.cloth.Cloth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "user_items")
@AllArgsConstructor
@NoArgsConstructor
public class UserItem {

    @EmbeddedId
    private UserItemPk id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("itemId")
    @JoinColumn(name = "cloth_id")
    private Cloth item;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "size", nullable = false)
    private String size;


}
