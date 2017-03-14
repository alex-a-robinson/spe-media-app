package com.example.samuel.at_bristol_app.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.samuel.at_bristol_app.R;
import com.example.samuel.at_bristol_app.models.MediaModel;
import com.example.samuel.at_bristol_app.models.RFIDModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MediaListActivity extends AppCompatActivity {

    private List<MediaModel> mediaModelList = new ArrayList<>();
    int callbackCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);

        List<String> mediaPathList = getIntent().getStringArrayListExtra("pathList");
        for (String path : mediaPathList) {
            mediaModelList.add(new MediaModel(path,this));
        }

        setTitle("Wristband " + getIntent().getExtras().get("rfidNumber"));

        ProgressBar progressSpinner = (ProgressBar) findViewById(R.id.pbMediaList);
        progressSpinner.setVisibility(View.VISIBLE);
    }

    public void mediaCallback(){
        callbackCount++;
        if (callbackCount == mediaModelList.size()){
            showList();
        }
    }

    private void showList(){
        ProgressBar progressSpinner = (ProgressBar) findViewById(R.id.pbMediaList);
        progressSpinner.setVisibility(View.GONE);

        ListView lvRFIDList = (ListView) findViewById(R.id.lvMedia);
        lvRFIDList.setAdapter(new MediaModelAdapter(this,R.layout.media_list_element,mediaModelList));
        lvRFIDList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final MediaModel selectedMediaModel = mediaModelList.get(position);
                if (selectedMediaModel.isDownloaded()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(
                            FileProvider.getUriForFile(getApplicationContext(),
                                    getApplicationContext().getPackageName() + ".provider",
                                    selectedMediaModel.getLocalFile()), selectedMediaModel.getMetaData().getContentType()).setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    selectedMediaModel.download(view);
                }

            }
        });
        lvRFIDList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                final MediaModel selectedMediaModel = mediaModelList.get(position); // get model of tapped group
                if (selectedMediaModel.isDownloaded()){
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(parent.getContext());
                    dialogBuilder.setMessage("File Downloaded").setPositiveButton("Show Media", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(
                                    FileProvider.getUriForFile(getApplicationContext(),
                                            getApplicationContext().getPackageName()+".provider",
                                            selectedMediaModel.getLocalFile()),selectedMediaModel.getMetaData().getContentType()).setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);
                        }
                    }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            selectedMediaModel.deleteLocalFile(view);
                        }
                    });
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                } else {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(parent.getContext());
                    dialogBuilder.setMessage("File Not Downloaded").setPositiveButton("Download", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            selectedMediaModel.download(view);
                        }
                    });
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }
                return true;
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
                holder.ivIcon      = (ImageView) convertView.findViewById(R.id.ivMedia);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //TODO:set media detail textviews
            MediaModel mediaModel = mediaModelList.get(position);
            holder.tvMediaName.setText(mediaModel.getLocalFile().getName().split("(\\.)")[0]);
            String fileSize = "Size: " + readableSize(mediaModel.getMetaData().getSizeBytes());
            holder.tvFileSize.setText(fileSize);
            String date = "Date: " + DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date(mediaModel.getMetaData().getUpdatedTimeMillis()));
            holder.tvDate.setText(date);
            String pathArray[] = mediaModel.getPath().split("(\\.)");
            String fileType = pathArray[pathArray.length-1];
            holder.tvThumbType.setText(fileType);
            if (mediaModel.isDownloaded()){
                holder.ivIcon.setImageResource(R.drawable.ic_check_black_24dp);
            }
            holder.ivIcon.setColorFilter(0xffef3e42, PorterDuff.Mode.SRC_IN);
            return convertView;
        }

        private static String readableSize(long bytes) {
            if (bytes < 1024) return bytes + " B";
            int exp = (int) (Math.log(bytes) / Math.log(1024));
            Character pre = "kMGTPE".charAt(exp-1);
            return String.format(Locale.getDefault(),"%.1f %sB", bytes / Math.pow(1024, exp), pre);
        }

        private class ViewHolder{
            private ImageView ivThumbnail;
            private ImageView ivIcon;
            private  TextView tvThumbType;
            private  TextView tvMediaName;
            private  TextView tvExhibit;
            private  TextView tvFileSize;
            private  TextView tvDate;

        }
    }
}
