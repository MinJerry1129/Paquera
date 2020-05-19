package com.blackcharm.discovery;

import java.io.Serializable;
import java.util.ArrayList;

public class Discovery  implements Serializable {
    String discovery_id;

    String isDraft;

    ArrayList<DiscoveryImage> images;

    public ArrayList<DiscoveryImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<DiscoveryImage> images) {
        this.images = images;
    }

    public Discovery(String discovery_id, ArrayList<DiscoveryImage> images, String title, String subtitle, String location, String price, String includes, String durations, String minage, String groupsize, String email, String website, String phone, String instagram, String description, String userid, String isDraft) {
        this.discovery_id = discovery_id;
        this.images = images;
        this.title = title;
        this.subtitle = subtitle;
        this.location = location;
        this.price = price;
        this.includes = includes;
        this.durations = durations;
        this.minage = minage;
        this.groupsize = groupsize;
        this.email = email;
        this.website = website;
        this.phone = phone;
        this.instagram = instagram;
        this.description = description;
        this.userid = userid;
        this.isDraft = isDraft;
    }

    public Discovery() {
        this.setUserid("");
        this.setDiscovery_id("");
        this.setDescription("");
        this.setPhone("");
        this.setWebsite("");
        this.setEmail("");
        this.setGroupsize("");
        this.setMinage("");
        this.setDurations("");
        this.setIncludes("");
        this.setLocation("");
        this.setSubtitle("");
        this.setTitle("");
        this.setInstagram("");
        this.setPrice("");
        this.setDraft("true");
        images=new ArrayList<DiscoveryImage>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getIncludes() {
        return includes;
    }

    public void setIncludes(String includes) {
        this.includes = includes;
    }

    public String getDurations() {
        return durations;
    }

    public void setDurations(String durations) {
        this.durations = durations;
    }

    public String getMinage() {
        return minage;
    }

    public void setMinage(String minage) {
        this.minage = minage;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
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

    String title;
    String subtitle;
    String location;
    String price;
    String includes;
    String durations;
    String minage;

    public String getGroupsize() {
        return groupsize;
    }

    public void setGroupsize(String groupsize) {
        this.groupsize = groupsize;
    }

    String groupsize;
    String email;
    String website;
    String phone;
    String instagram;
    String description;
    String userid;

    public String getDiscovery_id() {
        return discovery_id;
    }

    public void setDiscovery_id(String discovery_id) {
        this.discovery_id = discovery_id;
    }

    public String getDraft() {
        return isDraft;
    }

    public void setDraft(String draft) {
        isDraft = draft;
    }
}
