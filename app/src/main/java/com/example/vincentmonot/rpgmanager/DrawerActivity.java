package com.example.vincentmonot.rpgmanager;

import android.content.res.Configuration;
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

    public String[] navOptions;
    protected ActionBarDrawerToggle mDrawerToggle;

    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_sheet);
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

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void setContentView(final int layoutResID) {
        DrawerLayout fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer, null);
        RelativeLayout actContent = (RelativeLayout) fullLayout.findViewById(R.id.drawer_content);

        DrawerLayout mDrawerLayout = (DrawerLayout) fullLayout.findViewById(R.id.drawer_layout);
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
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(R.string.app_name);
            }
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                getSupportActionBar().setTitle(R.string.app_name);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                float moveFactor = DrawerActivity.this.mDrawerList.getWidth() * slideOffset;
                RelativeLayout contentLayout = (RelativeLayout) findViewById(R.id.content_layout);

                contentLayout.setTranslationX(moveFactor);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(fullLayout);
    }

    protected void onItemSelection(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(DrawerActivity.this, navOptions[position], Toast.LENGTH_SHORT).show();
    }

    /*
    protected void onCreateDrawer() {
        // R.id.drawer_layout should be in every activity with exactly the same id.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle((Activity) this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(R.string.app_name);
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(R.string.menuName);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        layers = getResources().getStringArray(R.array.menuItems);
        drawerList = (ListView) findViewById(R.id.navList);


        View header = getLayoutInflater().inflate(R.layout.drawer_list_header, null);
        drawerList.addHeaderView(header, null, false);

        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, layers));

        View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.drawer_list_footer, null, false);
        drawerList.addFooterView(footerView);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DrawerActivity.this, layers[position], Toast.LENGTH_SHORT).show();
            }
        });
    }
    */
}