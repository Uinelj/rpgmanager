package com.example.vincentmonot.rpgmanager;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by Vincent Monot on 06/12/2015.
 */
public class DrawerActivity extends AppCompatActivity {

    String title = "";
    public String[] navOptions;
    DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;

    protected ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_sheet);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#B79A00"));
        }

        switch(this.getClass().getSimpleName()) {
            case "CharacterSheetActivity":
                title = "Character Sheet";
                break;
            case "DiceActivity":
                title = "Roll the dice";
                break;
            case "OptionsActivity":
                title = "Options";
                break;
            default:
                title = "RPG Manager";
                break;
        }
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E5C100")));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mDrawerLayout.closeDrawers();
        /*
        if((! mDrawerLayout.isDrawerOpen(mDrawerList)) && (! getClass().getSimpleName().equals("CharacterSheetActivity"))) {
            mDrawerLayout.openDrawer(mDrawerList);
        }
        */
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

        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void setContentView(final int layoutResID) {
        DrawerLayout fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer, null);
        RelativeLayout actContent = (RelativeLayout) fullLayout.findViewById(R.id.drawer_content);

        mDrawerLayout = (DrawerLayout) fullLayout.findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) fullLayout.findViewById(R.id.nav_drawer);

        navOptions = getResources().getStringArray(R.array.menuItems);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navOptions));

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
        //mDrawerLayout.closeDrawer(mDrawerList);
    }
}