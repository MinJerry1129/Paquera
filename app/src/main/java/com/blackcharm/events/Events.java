package com.blackcharm.events;

import java.io.Serializable;

public class Events implements Serializable {

    String title;
    String datetime;
    String organise;
    String location;
    String time;
    String howtofindus;
    String link1;
    String link2;
    String description;
    String userid;
    String events_id;
    String isDraft;
    EventsImage image;



    public Events() {
        this.title = "";
        this.datetime = "";
        this.organise = "";
        this.location = "";
        this.time = "";
        this.howtofindus = "";
        this.link1 = "";
        this.link2 = "";
        this.description = "";
        this.userid = "";
        this.events_id = "";
        this.isDraft = "true";
        this.image = new EventsImage();
    }

    public Events(String title, String datetime, String organise, String location, String time, String howtofindus, String link1, String link2, String description, String userid, String events_id, EventsImage image, String isDraft) {
        this.title = title;
        this.datetime = datetime;
        this.organise = organise;
        this.location = location;
        this.time = time;
        this.howtofindus = howtofindus;
        this.link1 = link1;
        this.link2 = link2;
        this.description = description;
        this.userid = userid;
        this.events_id = events_id;
        this.image = image;
        this.isDraft = isDraft;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getOrganise() {
        return organise;
    }

    public void setOrganise(String organise) {
        this.organise = organise;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLink1() {
        return link1;
    }

    public void setLink1(String link1) {
        this.link1 = link1;
    }

    public String getLink2() {
        return link2;
    }

    public void setLink2(String link2) {
        this.link2 = link2;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEvents_id() {
        return events_id;
    }

    public void setEvents_id(String events_id) {
        this.events_id = events_id;
    }

    public String getHowtofindus() {
        return howtofindus;
    }

    public void setHowtofindus(String howtofindus) {
        this.howtofindus = howtofindus;
    }

    public EventsImage getImage() {
        return image;
    }

    public void setImage(EventsImage image) {
        this.image = image;
    }

    public String getDraft() {
        return isDraft;
    }

    public void setDraft(String draft) {
        isDraft = draft;
    }
}
