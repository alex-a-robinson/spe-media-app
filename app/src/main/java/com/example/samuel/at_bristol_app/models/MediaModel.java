package com.example.samuel.at_bristol_app.models;

import java.io.File;
import java.sql.Time;
import java.util.Date;

/**
 * used to store a single piece of media in a group
 * will be displayed when the user is inside a group
 */

public class MediaModel {
    private Date uploadDate;
    private String mediaType;
    private Object media;

    public MediaModel(Date uploadDate, String mediaType, Object media) {
        this.uploadDate = uploadDate;
        this.mediaType = mediaType;
        this.media = media;
    }

    Date getUploadDate() {return uploadDate;}
    String getMediaType() {return mediaType;}
    Object getMedia() {return media;}

    void setUploadDate(Date uploadDate) {this.uploadDate = uploadDate;}
    void setMedia(Object media) {this.media = media;}
    void setMediaType(String mediaType) {this.mediaType = mediaType;}



}
