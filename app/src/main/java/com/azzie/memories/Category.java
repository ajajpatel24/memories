package com.azzie.memories;

import java.io.Serializable;

/**
 * Created by Ajaj on 11/22/2016.
 */

public class Category implements Serializable {
    public String name, date;
    public boolean favorite, custom;
    public int image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", favorite=" + favorite +
                ", custom=" + custom +
                '}';
    }
}
