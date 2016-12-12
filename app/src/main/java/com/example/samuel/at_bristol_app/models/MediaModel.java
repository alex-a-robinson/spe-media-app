package com.example.samuel.at_bristol_app.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.samuel.at_bristol_app.activities.MediaListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

/**
 * used to store a single piece of media in a group
 * will be displayed when the user is inside a group
 */

public class MediaModel{
    private String path;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private StorageMetadata metaData;
    private MediaListActivity context;
    private File localFile;

    public MediaModel(final String path, final MediaListActivity context) {
        this.path = path;
        this.context = context;
        this.localFile = new File(context.getFilesDir(),path);
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.storageReference = firebaseStorage.getReferenceFromUrl("gs://spe-elabs.appspot.com/" + path);
        storageReference.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
            @Override
            public void onComplete(@NonNull Task<StorageMetadata> task) {
                if (task.isSuccessful()) {
                    metaData = task.getResult();
                }
                context.mediaCallback();
            }
        });
    }

    public StorageMetadata getMetaData(){
        return metaData;
    }

    public void download(){
        if (!localFile.getParentFile().exists()){
            try {
                localFile.getParentFile().mkdirs();
            } catch (SecurityException ex){
                System.out.print(ex.getMessage());
            }
        }
        storageReference.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context,"File Downloaded",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"File Failed To Download",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void deleteLocalFile(){
        if (localFile.delete()){
            Toast.makeText(context,"File Deleted",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context,"Error: file not found",Toast.LENGTH_SHORT).show();
        }
    }
    public boolean isDownloaded(){
        return (localFile.exists());
    }

    public String getPath() {
        return path;
    }

    public File getLocalFile(){
        return localFile;
    }
}
