package com.kevin.ahorcado.models;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "POKEMON")
public class Pokemon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "IMAGEURL")
    private String imageUrl;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "REGION")
    private String region;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ABILITY")
    private String ability;

    public Pokemon() {
    }

	public Pokemon(Long id, String name, String imageUrl, String type, String region, String description, String ability) {
		this.id = id;
		this.name = name;
		this.imageUrl = imageUrl;
		this.type = type;
		this.region = region;
		this.description = description;
		this.ability = ability;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAbility() {
		return ability;
	}

	public void setAbility(String ability) {
		this.ability = ability;
	}

	@Override
	public String toString() {
		return "Pokemon [Id=" + id + ", name=" + name + ", ImageUrl=" + imageUrl + ", Type=" + type + ", region="
				+ region + ", description=" + description + ", ability=" + ability + "]";
	}

}
