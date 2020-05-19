package com.blackcharm.discovery;

import java.io.Serializable;

public class DiscoveryImage implements Serializable {
    public DiscoveryImage(String refurl, String uri) {
        this.refurl = refurl;
        this.uri = uri;
    }

    public DiscoveryImage() {
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
