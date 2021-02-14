package com.hajma.qalanews_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.hajma.qalanews_android.eventbus.DataEvent;
import com.hajma.qalanews_android.fragments.FragmentCategory;
import com.hajma.qalanews_android.fragments.FragmentHome;
import com.hajma.qalanews_android.fragments.FragmentProfile;
import com.hajma.qalanews_android.fragments.FragmentSaved;
import com.hajma.qalanews_android.fragments.LanguageDialogFragment;
import com.hajma.qalanews_android.services.NewsMediaPlayerService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HomeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    final Fragment homeFragment = new FragmentHome();
    final Fragment categoryFragment = new FragmentCategory();
    final Fragment profileFragment = new FragmentProfile();
    final Fragment savedFragment = new FragmentSaved();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = homeFragment;
    private boolean isLogined;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean playOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        String lang = LocaleHelper.getPersistedData(this, "az");
        LocaleHelper.setLocale(this, lang);
        setContentView(R.layout.activity_home);



        // Register Callback - Call this in your app start!
        CheckNetwork network = new CheckNetwork(getApplicationContext());
        network.registerNetworkCallback();



        //SharedPreferences options
        sharedPreferences = getSharedPreferences("usercontrol", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isLogined = sharedPreferences.getBoolean("logined", false);
        playOn = false;


        if(isLogined) {
            EventBus.getDefault().postSticky(new DataEvent.LoginInfo(isLogined));
        }

        //Navigation view options
        bottomNavigationView = findViewById(R.id.nav_bottom_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.nav_bottom_home);

        fm.beginTransaction().add(R.id.fragment_container, categoryFragment, "category").hide(categoryFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, profileFragment, "profile").hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container,savedFragment, "saved").hide(savedFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container,homeFragment, "home").commit();


        //Toolbar options
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    //options menu toolbar


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem item = menu.findItem(R.id.itemSearch);
        SearchView searchView = (SearchView)item.getActionView();
        EditText et = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        et.setBackgroundColor(Color.LTGRAY);
        et.setTextColor(Color.BLACK);
        MenuItem lg = menu.findItem(R.id.itemLanguage);
        lg.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                LanguageDialogFragment dialogFragment = new LanguageDialogFragment();
                dialogFragment.show(fm, "language");
                return true;
            }
        });


        //play radio button
        MenuItem itemRadio = menu.findItem(R.id.itemRadioPlay);
        ImageButton imgPlayRadio = (ImageButton) itemRadio.getActionView();
        imgPlayRadio.setBackground(null);
        if(playOn) {
            imgPlayRadio.setImageResource(R.drawable.ic_pause_black_24dp);
        }else {
            imgPlayRadio.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
        imgPlayRadio.setOnClickListener(new View.OnClickListener() {

            Intent intent = new Intent(getApplicationContext(), NewsMediaPlayerService.class);

            @Override
            public void onClick(View v) {
                if (playOn){
                    playOn=false;
                    imgPlayRadio.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    //stop service
                    stopService(intent);
                    editor.putBoolean("playOn", false);
                    editor.commit();
                }else{
                    playOn=true;
                    imgPlayRadio.setImageResource(R.drawable.ic_pause_black_24dp);
                    //start service
                    startService(intent);
                    editor.putBoolean("playOn", true);
                    editor.commit();
                }
            }
        });


        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        EventBus.getDefault().postSticky(new DataEvent.SearchQuery(query));
        Fragment frg = null;
        frg = fm.findFragmentByTag("home");
        final FragmentTransaction  ft = fm.beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        EventBus.getDefault().postSticky(new DataEvent.SearchQuery(newText));
        Fragment frg = null;
        frg = fm.findFragmentByTag("home");
        final FragmentTransaction  ft = fm.beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //Bottom navigation item listener
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.nav_bottom_home :
                    fm.beginTransaction().hide(active).show(homeFragment).commit();
                    active = homeFragment;
                    // Show ActionBar
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().show();
                    }
                    return true;

                case R.id.nav_bottom_dash :
                    fm.beginTransaction().hide(active).show(categoryFragment).commit();
                    active = categoryFragment;
                    // Hide ActionBar
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().hide();
                    }
                    return true;

                case R.id.nav_bottom_saved :
                    fm.beginTransaction().hide(active).show(savedFragment).commit();
                    active = savedFragment;
                    // Hide ActionBar
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().hide();
                    }
                    return true;

                case R.id.nav_bottom_profile :
                    fm.beginTransaction().hide(active).show(profileFragment).commit();
                    active = profileFragment;
                    // Hide ActionBar
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().hide();
                    }
                    return true;
            }
            return true;
        };
    };

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, NewsMediaPlayerService.class));
    }

    @Subscribe(sticky = true)
    public void onFragmentSavedProfileUpdate(DataEvent.CallSavedProfileFragmentUpdate event) {
        if(event.getResponse() == 1 && event.getFragmentID() == 0) {
            refreshFragmentSaved();
            refreshFragmentProfile();
        }

        if(event.getResponse() == 1 && event.getFragmentID() == DataEvent.FRAGMENT_PROFILE_ID) {
            refreshFragmentProfile();
        }
    }

    private void refreshFragmentSaved() {
        Fragment frg = null;
        frg = fm.findFragmentByTag("saved");
        final FragmentTransaction  ft = fm.beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    private void refreshFragmentProfile() {
        Fragment frg = null;
        frg = fm.findFragmentByTag("profile");
        final FragmentTransaction ft = fm.beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }
}
