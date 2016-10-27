package com.example.samuel.at_bristol_app.models;

import android.content.Intent;

import java.util.Date;
import java.util.List;

/**
 * Used to store the Groups of media that will be displayed on the Media tab
 */

public class MediaGroupModel {
    private String groupName;
    private List<MediaModel> mediaModelList;
    private Date date;
    private Integer groupSize;

    public MediaGroupModel(String groupName, List<MediaModel> mediaModelList){
        this.groupName = groupName;
        this.mediaModelList = mediaModelList;
        this.date = getRecentDate(mediaModelList);
        this.groupSize = mediaModelList.size();
    }

    private Date getRecentDate(List<MediaModel> mediaModelList){
        Date returnDate = new Date(Long.MIN_VALUE);
        Date testDate;
        for (MediaModel mediaModel : mediaModelList){
            testDate = mediaModel.getUploadDate();
            if (returnDate.compareTo(testDate)>0){
                returnDate =  testDate;
            }
        }
        return returnDate;
    }

    public String getGroupName() {return groupName;}
    public Date getDate() {return date;}
    public List<MediaModel> getMediaModelList() {return mediaModelList;}
    public Integer getGroupSize() {return groupSize;}

    public void setGroupName(String groupName) {this.groupName = groupName;}
    public void setDate(Date date) {this.date = date;}
    public void setMediaModelList(List<MediaModel> mediaModelList) {this.mediaModelList = mediaModelList;}
    public void setGroupSize(Integer groupSize) {this.groupSize = groupSize;}
}
