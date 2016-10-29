package com.example.samuel.at_bristol_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.samuel.at_bristol_app.models.MediaGroupModel;
import com.example.samuel.at_bristol_app.models.MediaModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    static Integer userID;
    Fragment fragments[] = new Fragment[3];

    Toolbar toolbar;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final String titles[] = {"Home","Media","My Account"};

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        //tab icon stuff
        //sets the tab icons and handles coloring them
        tabLayout.setupWithViewPager(mViewPager);
        int icons[] = {R.drawable.ic_home_black_24dp,
                R.drawable.ic_video_library_black_24dp,
                R.drawable.ic_account_circle_black_24dp};
        for (int i = 0; i<tabLayout.getTabCount();i++)
            tabLayout.getTabAt(i).setIcon(icons[i]);

        tabLayout.addOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    super.onTabSelected(tab);
                    int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.colorAccentGrey);
                    tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    toolbar.setTitle(titles[tab.getPosition()]);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    super.onTabUnselected(tab);
                    int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.tabUnselectedIconColor);
                    tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    super.onTabReselected(tab);
                }
            }
        );
        //doesnt count as being selected initially so this is a workaround
        int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.colorAccentGrey);
        tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

        //getting userID passed in through intent from loginActivity
        Intent intent = getIntent();
        userID = intent.getIntExtra("userID",-1);

        //declaring each tab's content fragment
        fragments = new Fragment[]{MediaFragment.newInstance(),
                MediaFragment.newInstance(),
                MediaFragment.newInstance()};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            intent.putExtra("userID",userID);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    // a media fragment containing a listView that will be populated with the groups of media
    public static class MediaFragment extends Fragment {

        ListView mediaGroupList;
        List<MediaGroupModel> mgms;

        //Returns a new instance of this fragment
        public static MediaFragment newInstance() {
            MediaFragment mediaFragment = new MediaFragment();
            Bundle args = new Bundle();
            mediaFragment.setArguments(args);
            return mediaFragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_media, container, false);

            //fetch media
            this.mgms = fetchMediaGroups(userID);

            mediaGroupList = (ListView) view.findViewById(R.id.lvList);
            MediaGroupAdapter adapter = new MediaGroupAdapter(view.getContext(), R.layout.media_group, mgms);
            mediaGroupList.setAdapter(adapter);

            mediaGroupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MediaGroupModel selectedMediaGroup = mgms.get(position); // get model of tapped group
                    //TODO: move to next activity

                }
            });
            return view;
        }

        static List<MediaGroupModel> fetchMediaGroups(Integer userID){

            //TODO: implement fetching the real list of media
            List<MediaModel> listMedia1 = Arrays.asList(
                    new MediaModel(new Date(200),"foo",null),
                    new MediaModel(new Date(35),"bar",null),
                    new MediaModel(new Date(1234),"FB",null));
            MediaGroupModel mgm1 = new MediaGroupModel("Group 1",listMedia1);

            List<MediaModel> listMedia2 = Arrays.asList(
                    new MediaModel(new Date(46),"barfoo",null),
                    new MediaModel(new Date(23),"foobar",null));

            MediaGroupModel mgm2 = new MediaGroupModel("Group 2",listMedia2);
            return Arrays.asList(mgm1,mgm2);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {return "";}
    }

    static class MediaGroupAdapter extends ArrayAdapter<MediaGroupModel> {

        private List<MediaGroupModel> mediaGroupModels;
        private int resource;
        private LayoutInflater inflater;

        MediaGroupAdapter(Context context, int resource, List<MediaGroupModel> mediaGroupModels) {
            super(context,resource,mediaGroupModels);
            this.mediaGroupModels = mediaGroupModels;
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            MediaGroupAdapter.ViewHolder holder;

            if(convertView == null){
                holder = new MediaGroupAdapter.ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
                holder.tvThumbNum = (TextView) convertView.findViewById(R.id.tvThumbNum);
                holder.tvGroupName = (TextView) convertView.findViewById(R.id.tvGroupName);
                holder.tvNumItems = (TextView) convertView.findViewById(R.id.tvNumItems);
                holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
                convertView.setTag(holder);
            } else {
                holder = (MediaGroupAdapter.ViewHolder) convertView.getTag();
            }

            //TODO: Handle Thumbnails

            holder.tvGroupName.setText(mediaGroupModels.get(position).getGroupName());

            DateFormat df = new SimpleDateFormat("dd-MM-yy", Locale.UK);
            holder.tvDate.setText(df.format(mediaGroupModels.get(position).getDate()));

            String numItemsText = "Items: " + mediaGroupModels.get(position).getGroupSize().toString();
            holder.tvNumItems.setText(numItemsText);
            String thumbNumItemsText = " " + mediaGroupModels.get(position).getGroupSize().toString() + " ";
            holder.tvThumbNum.setText(thumbNumItemsText);

            return convertView;
        }

        private class ViewHolder{
            private ImageView ivThumbnail;
            private TextView tvThumbNum;
            private TextView tvGroupName;
            private TextView tvNumItems;
            private TextView tvDate;

        }
    }
}


