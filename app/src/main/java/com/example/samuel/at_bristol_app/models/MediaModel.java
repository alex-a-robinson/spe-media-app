package com.example.samuel.at_bristol_app.models;

import java.io.File;
import java.sql.Time;
import java.util.Date;

/**
 * used to store a single piece of media in a group
 * will be displayed when the user is inside a group
 */

public class MediaModel {
    private String path;
    private int metaData;

    public MediaModel(String path) {
        this.path = path;
        this.metaData = setMetaData();
    }

    private int setMetaData(){
        //TODO: fetch the metaData
        return 1;
    }

    public int getMetaData() {
        return metaData;
    }

    public String getPath() {
        return path;
    }
}
