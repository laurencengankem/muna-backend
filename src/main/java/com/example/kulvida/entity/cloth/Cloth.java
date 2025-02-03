package com.example.kulvida.entity.cloth;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

@Data
@Entity
@Table(name = "CLOTHES")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Cloth implements Comparable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "CLOTH_ID")
    private Integer clothId;

    private String name;
    private String description;

    private Integer discount=0;
    private Sex sex;
    private String brand;
    private String color;

    @Column(name = "CODE",unique = true, nullable = true)
    private String code;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @OneToMany(mappedBy = "cloth", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClothSize> clothSizes;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "CLOTH_ID")
    private List<ClothPicture> pictures;

    @Column(name = "available")
    private Boolean available=true;


    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar creationDate;

    @Column(name = "last_update")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar lastUpdate;


    public String getMainPictureUrl(){
        for(ClothPicture ip:this.pictures){
            if(ip.getTag()!=null && ip.getTag().equals("MAIN"))
                return ip.url;
        }
        String url= this.pictures.isEmpty()? "": this.pictures.get(0).getUrl();
        return url;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return this.getName().compareTo(((Cloth)o).getName());
    }

}
