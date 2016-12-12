package com.example.samuel.at_bristol_app.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.samuel.at_bristol_app.R;
import com.example.samuel.at_bristol_app.models.MediaModel;
import com.example.samuel.at_bristol_app.models.RFIDModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MediaListActivity extends AppCompatActivity {

    private List<MediaModel> mediaModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);

        List<String> mediaPathList = getIntent().getStringArrayListExtra("pathList");
        for (String path : mediaPathList) {
            mediaModelList.add(new MediaModel(path,this));
        }

        setTitle("Wristband " + getIntent().getExtras().get("rfidNumber"));

        ListView lvRFIDList = (ListView) findViewById(R.id.lvMedia);
        lvRFIDList.setAdapter(new MediaModelAdapter(this,R.layout.media_list_element,mediaModelList));
        lvRFIDList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final MediaModel selectedMediaModel = mediaModelList.get(position); // get model of tapped group
                //TODO: if the media is downloaded, show it, else download it.
                if (selectedMediaModel.isDownloaded()){
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(parent.getContext());
                    dialogBuilder.setMessage("File Downloaded").setPositiveButton("Show Media", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Intent.ACTION_VIEW).setData(
                                    FileProvider.getUriForFile(getApplicationContext(),
                                            getApplicationContext().getPackageName()+".provider",
                                            selectedMediaModel.getLocalFile())).setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);
                        }
                    }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            selectedMediaModel.deleteLocalFile();
                        }
                    });
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                } else {
                    selectedMediaModel.download();
                }

            }
        });

    }

    static class MediaModelAdapter extends ArrayAdapter<MediaModel> {

        private List<MediaModel> mediaModelList;
        private int resource;
        private LayoutInflater inflater;

        MediaModelAdapter(Context context, int resource, List<MediaModel> mediaModelList) {
            super(context,resource,mediaModelList);
            this.mediaModelList = mediaModelList;
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            ViewHolder holder;

            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
                holder.tvThumbType = (TextView) convertView.findViewById(R.id.tvThumbType);
                holder.tvMediaName = (TextView) convertView.findViewById(R.id.tvMediaName);
                holder.tvExhibit  = (TextView) convertView.findViewById(R.id.tvExhibit);
                holder.tvFileSize  = (TextView) convertView.findViewById(R.id.tvFileSize);
                holder.tvDate      = (TextView) convertView.findViewById(R.id.tvDate);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //TODO:set media detail textviews
            MediaModel mediaModel = mediaModelList.get(position);
            holder.tvMediaName.setText(mediaModel.getLocalFile().getName());
            return convertView;
        }

        public static String readableSize(long bytes) {
            if (bytes < 1024) return bytes + " B";
            int exp = (int) (Math.log(bytes) / Math.log(1024));
            Character pre = "kMGTPE".charAt(exp-1);
            return String.format(Locale.getDefault(),"%.1f %sB", bytes / Math.pow(1024, exp), pre);
        }

        private class ViewHolder{
            private ImageView ivThumbnail;
            private  TextView tvThumbType;
            private  TextView tvMediaName;
            private  TextView tvExhibit;
            private  TextView tvFileSize;
            private  TextView tvDate;
        }
    }
}
