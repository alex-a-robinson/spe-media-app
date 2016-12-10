package com.example.samuel.at_bristol_app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The model for a Visit list element, contains all the relevant functions and data
 */

public class VisitModel implements Serializable{
    private Date date;
    private List<RFIDModel> rfidModelList;

    public VisitModel(Date date, List<String> rfidStringList){
        this.date = date;
        generateChildren(rfidStringList);
    }
    private VisitModel(Parcel parcel){
        this.date = new Date(parcel.readLong());
        parcel.readList(rfidModelList,List.class.getClassLoader());
    }

    private void generateChildren(List<String> rfidStringList){
        rfidModelList = new ArrayList<>();
        for (String rfidString : rfidStringList) {
            rfidModelList.add(new RFIDModel(rfidString));
        }
    }

    public List<RFIDModel> getRFIDList(){
        return rfidModelList;
    }

    public RFIDModel getRFID(int index){
        return rfidModelList.get(index);
    }

    public int getRFIDCount(){
        return rfidModelList.size();
    }

    public int getItemCount(){
        int count = 0;
        for (RFIDModel child : rfidModelList) {
            count += child.getMediaCount();
        }
        return count;
    }

    public Date getDate(){
        return date;
    }
}
