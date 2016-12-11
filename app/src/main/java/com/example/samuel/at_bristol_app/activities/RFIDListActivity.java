package com.example.samuel.at_bristol_app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.samuel.at_bristol_app.R;
import com.example.samuel.at_bristol_app.models.RFIDModel;
import com.example.samuel.at_bristol_app.models.VisitModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RFIDListActivity extends AppCompatActivity {
    private List<RFIDModel> rfidModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfidlist);

        final List<String> rfidStringList = getIntent().getStringArrayListExtra("rfidList");
        for (String rfid : rfidStringList) {
            rfidModelList.add(new RFIDModel(rfid));
        }
        Date date = (Date) getIntent().getExtras().get("date");

        setTitle(DateFormat.getDateInstance(DateFormat.MEDIUM).format(date));

        ListView lvRFIDList = (ListView) findViewById(R.id.lvRfid);
        lvRFIDList.setAdapter(new RFIDModelAdapter(this,R.layout.rfid_list_element,rfidModelList));
        lvRFIDList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RFIDModel selectedRFID = rfidModelList.get(position); // get model of tapped group
                Intent intent = new Intent(getApplicationContext(),MediaListActivity.class);
                intent.putStringArrayListExtra("pathList", (ArrayList<String>) selectedRFID.getMediaList());
                intent.putExtra("rfidNumber", (position+1));
                startActivity(intent);
            }
        });
    }

    static class RFIDModelAdapter extends ArrayAdapter<RFIDModel> {

        private List<RFIDModel> rfidModelList;
        private int resource;
        private LayoutInflater inflater;

        RFIDModelAdapter(Context context, int resource, List<RFIDModel> rfidModelList) {
            super(context,resource,rfidModelList);
            this.rfidModelList = rfidModelList;
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            RFIDModelAdapter.ViewHolder holder;

            if(convertView == null){
                holder = new RFIDModelAdapter.ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.tvRFIDNumber = (TextView) convertView.findViewById(R.id.tvRFIDNumber);
                holder.tvRFIDDetails = (TextView) convertView.findViewById(R.id.tvRFIDDetails);
                convertView.setTag(holder);
            } else {
                holder = (RFIDModelAdapter.ViewHolder) convertView.getTag();
            }

            String string = "Wristband " + (position+1);
            holder.tvRFIDNumber.setText(string);

            string = " Items: " + rfidModelList.get(position).getMediaCount();//TODO: this is being called before the media list is populated therefore is 0
            holder.tvRFIDDetails.setText(string);

            return convertView;
        }

        private class ViewHolder{
            private  TextView tvRFIDNumber;
            private  TextView tvRFIDDetails;

        }
    }
}
