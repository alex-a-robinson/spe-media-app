package com.example.samuel.at_bristol_app.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The model for a Visit list element, contains all the relevant functions and data
 */

public class VisitModel {
    private Date date;
    private List<RFIDModel> rfidModelList;
    private List<String> rfidStringList;

    public VisitModel(Date date, List<String> rfidStringList){
        this.date = date;
        this.rfidStringList = rfidStringList;
        generateChildren();
    }

    private void generateChildren(){
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
