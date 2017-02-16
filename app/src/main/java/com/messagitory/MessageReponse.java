package com.messagitory;

import java.io.Serializable;

/**
 * Created by admin on 2/8/2017.
 */

public class MessageReponse implements Serializable {
    private String message;

    private String id;

    private int viewType;

    private String reports;

    private String likes;

    private boolean report;

    private String created_at;

    private String device_id;

    private boolean favorite;

    private String is_cancelled;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReports() {
        return reports;
    }

    public void setReports(String reports) {
        this.reports = reports;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public boolean isReport() {
        return report;
    }

    public void setReport(boolean report) {
        this.report = report;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }


    public String getIs_cancelled() {
        return is_cancelled;
    }

    public void setIs_cancelled(String is_cancelled) {
        this.is_cancelled = is_cancelled;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public String toString() {
        return "ClassPojo [message = " + message + ", id = " + id + ", reports = " + reports + ", likes = " + likes + ", report = " + report + ", created_at = " + created_at + ", device_id = " + device_id + ", favorite = " + favorite + ", is_cancelled = " + is_cancelled + "]";
    }
}
