package com.example.samuel.at_bristol_app.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RFIDModel{
    private String rfid;
    private List<String> mediaPathList;

    public RFIDModel(String rfid) {
        this.rfid = rfid;
        generatePathList();
    }

    @SuppressWarnings("ConstantConditions")
    private void generatePathList() {
        mediaPathList = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final String userID = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference dbRef = firebaseDatabase.getReference("media/" + userID + "/" + rfid + "/");
        dbRef.orderByChild("file_name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    mediaPathList.add("user-media/" + userID + "/" +rfid + "/" + child.child("file_name").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public List<String> getMediaList() {
        return mediaPathList;
    }

    public int getMediaCount() {
        return mediaPathList.size();
    }

}
