package com.blackcharm.events;

import java.io.Serializable;

public class EventsImage implements Serializable {
    public EventsImage(String refurl, String uri) {
        this.refurl = refurl;
        this.uri = uri;
    }

    public EventsImage() {
        this.refurl = "";
        this.uri = "";
    }

    public String getRefurl() {
        return refurl;
    }

    public void setRefurl(String refurl) {
        this.refurl = refurl;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    String refurl;
    String uri;
}
