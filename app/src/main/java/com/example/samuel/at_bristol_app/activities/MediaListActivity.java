package com.example.samuel.at_bristol_app.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.samuel.at_bristol_app.R;
import com.example.samuel.at_bristol_app.models.MediaModel;
import com.example.samuel.at_bristol_app.models.RFIDModel;

import java.util.List;

public class MediaListActivity extends AppCompatActivity {

    private RFIDModel rfidModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);
        rfidModel = (RFIDModel) getIntent().getExtras().get("Model");

        setTitle("Wristband " + getIntent().getExtras().get("rfidNumber"));

        ListView lvRFIDList = (ListView) findViewById(R.id.lvMedia);
        lvRFIDList.setAdapter(new MediaModelAdapter(this,R.layout.media_list_element,rfidModel.getMediaList()));
        lvRFIDList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaModel selectedRFID = rfidModel.getMediaList().get(position); // get model of tapped group
                //TODO: create intent for relevent media and launch correct app
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

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            return convertView;
        }

        private class ViewHolder{
            private  TextView tvRFIDNumber;
            private  TextView tvRFIDDetails;

        }
    }
}
