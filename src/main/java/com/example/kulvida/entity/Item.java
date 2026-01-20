package com.example.kulvida.entity;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Entity
@Data
@Table(name = "items")
public class Item implements Comparable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

    @Column(name="price")
    private Double price;

    @Column(name="quantity")
    private Integer quantity;

    @Column(name = "discount")
    private Double discount;

    @Transient
    private Double discounted;

    @Transient
    private boolean newlyAdded=false;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar creationDate;

    @Column(name = "last_update")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar lastUpdate;


    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "item_id")
    private List<ItemPicture> pictures;

    @Column(name = "available")
    private Boolean available=true;


    @Column(name="category")
    private String category;



    public void setDiscounted(){
        if(discount!=null)
            this.discounted= this.price-(this.price*this.discount)/100 ;
        else
            this.discounted=this.price;
    }

    public void setDiscounted(Double discounted) {
        this.discounted = discounted;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return this.getName().compareTo(((Item)o).getName());
    }

    public void isnew(){
        Calendar now= new GregorianCalendar();
        now.set(Calendar.MINUTE,now.get(Calendar.MINUTE)-10);
        if (this.getCreationDate().after(now))
            this.setNewlyAdded(true);
    }

    public String getMainPictureUrl(){
        for(ItemPicture ip:this.pictures){
            if(ip.getTag()!=null && ip.getTag().equals("MAIN"))
                return ip.url;
        }
        String url= this.pictures.isEmpty()? "": this.pictures.get(0).getUrl();
        return url;
    }
}
