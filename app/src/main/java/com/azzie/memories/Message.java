package com.azzie.memories;

import java.io.Serializable;

/**
 * Created by Ajaj on 11/22/2016.
 */

public class Message implements Serializable {
    public String message, date, categoryname, categoryid;
    public boolean custome;
    public boolean favorite;

    public boolean isCustome() {
        return custome;
    }

    public void setCustome(boolean custome) {
        this.custome = custome;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {

        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", date='" + date + '\'' +
                ", categoryname='" + categoryname + '\'' +
                ", categoryid='" + categoryid + '\'' +
                ", favorite=" + favorite +
                '}';
    }
}
