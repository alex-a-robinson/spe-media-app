package com.example.samuel.at_bristol_app.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.samuel.at_bristol_app.CustomViewPager;
import com.example.samuel.at_bristol_app.R;
import com.example.samuel.at_bristol_app.models.VisitModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    protected Fragment fragments[] = new Fragment[3];
    TabLayout tabLayout;
    Toolbar toolbar;
    static FirebaseUser currentUser;

    private static final String TAG = "MainActivity";
    FirebaseUser user;

    CustomViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //store current user
                    user = currentUser;

                    Log.d(TAG, "MAIN onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccentGrey));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final String titles[] = {"Home","Media","My Account"};

        tabLayout = (TabLayout) findViewById(R.id.tabs);

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
                mViewPager.setPagingEnabled(!(tab.getPosition()==0));

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
        //doesn't count as being selected initially so this is a workaround
        int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.colorAccentGrey);
        tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

        //declaring each tab's content fragment
        fragments = new Fragment[]{HomeFragment.newInstance(),
                MediaFragment.newInstance(),
                AccountFragment.newInstance()};

        if (getIntent().hasExtra("Tab")) {
            tabLayout.getTabAt((Integer) getIntent().getExtras().get("Tab")).select();
        }
    }

    @Override
    public void onBackPressed() {
        if (tabLayout.getSelectedTabPosition()==0){
            WebView wvHome = ((HomeFragment) fragments[0]).getWebView();
            if (wvHome.canGoBack())
                wvHome.goBack();
        } else {
            super.onBackPressed();
        }
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
            intent.putExtra("Tab",tabLayout.getSelectedTabPosition());
            startActivity(intent);
        } else if (id == R.id.action_tickets) {
            String url = getString(R.string.web_tickets);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public static class HomeFragment extends Fragment {

        private WebView wvHome;

        //Returns a new instance of this fragment
        public static HomeFragment newInstance() {
            HomeFragment homeFragment = new HomeFragment();
            Bundle args = new Bundle();
            homeFragment.setArguments(args);
            return homeFragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_home, container, false);
            wvHome = (WebView) view.findViewById(R.id.wvHome);
            //wvHome.loadUrl(getString(R.string.web_home));
            wvHome.setWebViewClient(new CustomWebViewClient(view));
            wvHome.getSettings().setJavaScriptEnabled(true);
            return view;
        }

        WebView getWebView(){
            return wvHome;
        }
    }

    static class CustomWebViewClient extends WebViewClient {
        private ProgressBar progressBar;
        CustomWebViewClient(View view){
            this.progressBar = new ProgressBar(view.getContext());
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.evaluateJavascript("document.getElementById('header-wrapper').parentNode.removeChild(document.getElementById('header-wrapper'));" +
                    "    document.getElementById('content-wrapper').removeAttribute('style');" +
                    "    document.getElementById('footer').parentNode.removeChild(document.getElementById('footer'));" +
                    "    document.getElementById('cookie-notification').parentNode.removeChild(document.getElementById('cookie-notification'));", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) { }
            });
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public static class MediaFragment extends Fragment {

        ListView visitListView;
        List<VisitModel> visitModelList;

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
            visitModelList = new ArrayList<>();
            createVisitModels(FirebaseAuth.getInstance().getCurrentUser().getUid());

            visitListView = (ListView) view.findViewById(R.id.lvVisits);
            VisitModelAdapter adapter = new VisitModelAdapter(view.getContext(), R.layout.visit_list_element, visitModelList);
            visitListView.setAdapter(adapter);

            visitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    VisitModel selectedVisit = visitModelList.get(position); // get model of tapped group
                    Intent intent = new Intent(getContext(),RFIDListActivity.class);
                    intent.putExtra("Model",selectedVisit);
                    startActivity(intent);
                }
            });
            return view;
        }

        private void createVisitModels(String userID){
            final Map<String,List<String>> rfidDateMap = new HashMap<>();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("rfids/");
            dbRef.orderByChild("uid").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Date date = new Date(child.child("date").getValue(Long.class));
                        String dateString = DateFormat.getDateInstance(DateFormat.SHORT).format(date);
                        if (rfidDateMap.containsKey(dateString)){
                            List<String> oldList = rfidDateMap.get(dateString);
                            oldList.add(child.getKey());
                            rfidDateMap.put(dateString, oldList);
                        } else {
                            rfidDateMap.put(dateString, new ArrayList<>(Collections.singletonList(child.getKey())));
                        }
                    }
                    for (Map.Entry<String, List<String>> entry : rfidDateMap.entrySet()) {
                        try {
                            visitModelList.add(new VisitModel(DateFormat.getDateInstance(DateFormat.SHORT).parse(entry.getKey()),entry.getValue()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    ((BaseAdapter) visitListView.getAdapter()).notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        static class VisitModelAdapter extends ArrayAdapter<VisitModel> {

            private List<VisitModel> visitModelList;
            private int resource;
            private LayoutInflater inflater;

            VisitModelAdapter(Context context, int resource, List<VisitModel> visitModelList) {
                super(context,resource,visitModelList);
                this.visitModelList = visitModelList;
                this.resource = resource;
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {

                VisitModelAdapter.ViewHolder holder;

                if(convertView == null){
                    holder = new VisitModelAdapter.ViewHolder();
                    convertView = inflater.inflate(resource, null);
                    holder.ivVisitThumb = (ImageView) convertView.findViewById(R.id.ivVisitThumb);
                    holder.tvVisitDate = (TextView) convertView.findViewById(R.id.tvVisitDate);
                    holder.tvVisitDetails = (TextView) convertView.findViewById(R.id.tvVisitDetails);
                    convertView.setTag(holder);
                } else {
                    holder = (VisitModelAdapter.ViewHolder) convertView.getTag();
                }

                //TODO: Handle Thumbnails

                String string = "Visit On " + DateFormat.getDateInstance(DateFormat.MEDIUM).format(visitModelList.get(position).getDate());
                holder.tvVisitDate.setText(string);

                string = "People: " + visitModelList.get(position).getRFIDCount() + " - Items: " + visitModelList.get(position).getItemCount();
                holder.tvVisitDetails.setText(string);

                return convertView;
            }

            private class ViewHolder{
                private ImageView ivVisitThumb;
                private  TextView tvVisitDate;
                private  TextView tvVisitDetails;

            }
        }

    }

    public static class AccountFragment extends Fragment {

        TextView tvAccountCircle, tvAccountName, tvAccountEmail;
        View view;
        Preference logout, changePassword, changeEmail, changeName;

        //Returns a new instance of this fragment
        public static AccountFragment newInstance() {
            AccountFragment accountFragment = new AccountFragment();
            Bundle args = new Bundle();
            accountFragment.setArguments(args);
            return accountFragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (view == null)
                view = inflater.inflate(R.layout.fragment_account, container, false);
            tvAccountCircle = (TextView) view.findViewById(R.id.tvAccountCircle);
            tvAccountName = (TextView) view.findViewById(R.id.tvAccountName);
            tvAccountEmail = (TextView) view.findViewById(R.id.tvAccountEmail);
            PreferenceFragment preferenceFragment = (PreferenceFragment) getActivity().getFragmentManager().findFragmentById(R.id.accountPrefFrag);
            logout = preferenceFragment.findPreference("setting_log_out");
            logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(view.getContext(),LoginActivity.class);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(view.getContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove("username").remove("password").apply();
                    startActivity(intent);
                    return true;
                }
            });
            changeEmail = preferenceFragment.findPreference("setting_account_email");
            changeEmail.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    LinearLayout layout = new LinearLayout(getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    final EditText editEmail = new EditText(getContext());
                    editEmail.setHint("new E-mail");
                    final EditText editPassword = new EditText(getContext());
                    editPassword.setHint("Password");
                    layout.addView(editEmail);
                    layout.addView(editPassword);
                    final AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Change E-mail")
                            .setView(layout)
                            .create();
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //if pw is right update
                        }
                    });
                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    return true;
                }
            });
            changeName = preferenceFragment.findPreference("setting_account_name");
            changeName.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    LinearLayout layout = new LinearLayout(getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    final EditText editName = new EditText(getContext());
                    editName.setHint("new Name");
                    final EditText editPassword = new EditText(getContext());
                    editPassword.setHint("Password");
                    layout.addView(editName);
                    layout.addView(editPassword);
                    final AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Change Name")
                            .setView(layout)
                            .create();
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //if pw is right update
                        }
                    });
                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    return true;
                }
            });
            changePassword = preferenceFragment.findPreference("setting_account_password");
            changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    LinearLayout layout = new LinearLayout(getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    final EditText editNewPassword = new EditText(getContext());
                    editNewPassword.setHint("new password");
                    final EditText editPassword = new EditText(getContext());
                    editPassword.setHint("old password");
                    layout.addView(editNewPassword);
                    layout.addView(editPassword);
                    final AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Change Password")
                            .setView(layout)
                            .create();
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //if pw is right update and reauthenticate
                        }
                    });
                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    return true;
                }
            });

            //TODO: update all textViews with relevant info
            String name = currentUser.getDisplayName();
            tvAccountCircle.setText(String.valueOf(Character.toUpperCase(name.charAt(0))));
            tvAccountEmail.setText(String.valueOf(currentUser.getEmail()));
            tvAccountName.setText(String.valueOf(name));
            return view;
        }
    }

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




    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AccountPreferenceFragment extends PreferenceFragment {
        public AccountPreferenceFragment(){

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(false);
            addPreferencesFromResource(R.xml.pref_account);
        }
    }
}


