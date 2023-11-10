package com.kevin.ahorcado.models;

import java.io.Serializable;
import java.util.List;

public class Pokemon implements Serializable {

    String name;
    String ImageUrl;
    List<String> type;
    String region;
    String description;
    List<String> abilities;

    public Pokemon() {
    }

    public Pokemon(String name, String imageUrl, List<String> type, String region, String description, List<String> abilities) {
        this.name = name;
        ImageUrl = imageUrl;
        this.type = type;
        this.region = region;
        this.description = description;
        this.abilities = abilities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
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

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    @Override
    public String toString() {
        return "Pokemon{" +
                "name='" + name + '\'' +
                ", ImageUrl='" + ImageUrl + '\'' +
                ", type=" + type +
                ", region='" + region + '\'' +
                ", description='" + description + '\'' +
                ", abilities=" + abilities +
                '}';
    }
}
