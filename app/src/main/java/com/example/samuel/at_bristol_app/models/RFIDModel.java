package com.example.samuel.at_bristol_app.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * The model for a RFID list element, contains all the relevant functions and data
 */

public class RFIDModel {
    private String rfid;
    private List<MediaModel> mediaModelList;
    private String userID;

    public RFIDModel(String rfid) {
        this.rfid = rfid;
        generateChildren();
    }

    @SuppressWarnings("ConstantConditions")
    private void generateChildren() {
        mediaModelList = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference dbRef = firebaseDatabase.getReference("media/" + userID + "/" + rfid + "/");
        dbRef.orderByChild("file_name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    mediaModelList.add(new MediaModel("user-media/" + userID + "/" + child.child("file_name").getValue().toString()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public List<MediaModel> getMediaList() {
        return mediaModelList;
    }

    public MediaModel getMedia(int index) {
        return mediaModelList.get(index);
    }

    public int getMediaCount() {
        return mediaModelList.size();
    }

    public String getRfid() {
        return rfid;
    }
}
