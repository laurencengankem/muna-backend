package com.example.kulvida.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.kulvida.domain.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String username;
    private String password;
    private String phone;
    private String firstName;
    private String lastName;
    private String status;
    private Date createdOn;
    private Date updatedOn;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<UserAddress> addresses;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Set<GrantedAuthority> getAuthorities(){
        Set<GrantedAuthority> authorities=new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(this.role.getValue()));
        return authorities;
    }

	public User(String username, String password, UserRole role) {
		super();
		this.username = username;
		this.password = password;
		this.role = role;
	}
}
