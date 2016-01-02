package com.example.vincentmonot.rpgmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Locale;


public class DrawerActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();

    String title = "";
    public String[] navOptions;
    DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;

    protected ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#B79A00"));
        }
        /*
        switch(this.getClass().getSimpleName()) {
            case "CharacterSheetActivity":
                title = getResources().getStringArray(R.array.menu_items)[0];
                break;
            case "DiceActivity":
                title = getResources().getStringArray(R.array.menu_items)[1];
                break;
            case "OptionsActivity":
                title = getResources().getStringArray(R.array.menu_items)[2];
                break;
            default:
                title = "RPG Manager";
                break;
        }
        */
        if(getSupportActionBar() != null) {
            //getSupportActionBar().setTitle(title);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E5C100")));
        }
        updateLocale();

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume is called");
        mDrawerLayout.closeDrawers();
        updateLocale();


        navOptions = getResources().getStringArray(R.array.menu_items);

        mDrawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, navOptions));
        switch(this.getClass().getSimpleName()) {
            case "CharacterSheetActivity":
                title = navOptions[0];
                break;
            case "DiceActivity":
                title = navOptions[1];
                break;
            case "OptionsActivity":
                title = navOptions[2];
                break;
            default:
                title = "RPG Manager";
                break;
        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void setContentView(final int layoutResID) {
        DrawerLayout fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer, (ViewGroup) getCurrentFocus());
        RelativeLayout actContent = (RelativeLayout) fullLayout.findViewById(R.id.drawer_content);

        mDrawerLayout = (DrawerLayout) fullLayout.findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) fullLayout.findViewById(R.id.nav_drawer);

        navOptions = getResources().getStringArray(R.array.menu_items);

        mDrawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, navOptions));

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DrawerActivity.this.onItemSelection(parent, view, position, id);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                float moveFactor = DrawerActivity.this.mDrawerList.getWidth() * slideOffset;
                RelativeLayout contentLayout = (RelativeLayout) findViewById(R.id.content_layout);

                contentLayout.setTranslationX(moveFactor);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(fullLayout);
    }

    protected void onItemSelection(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(DrawerActivity.this, navOptions[position], Toast.LENGTH_SHORT).show();
    }

    protected void updateLocale() {
        SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String localePref = settings.getString("locale", "en_US");
        String[] split = localePref.split("_");
        Log.d(TAG, "SHARED_PREFERENCES Locale=" + localePref);

        Locale locale = new Locale(split[0]);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());

        Log.d(TAG, "Changed Locale=" + getResources().getConfiguration().locale);
    }
}