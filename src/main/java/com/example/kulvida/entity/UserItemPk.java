package com.example.kulvida.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserItemPk implements Serializable {

    @Column(name = "item_id")
    private Integer itemId;

    @Column(name="user_id")
    private Integer UserId;
}
