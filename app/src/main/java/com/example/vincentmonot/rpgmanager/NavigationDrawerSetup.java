package com.example.vincentmonot.rpgmanager;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by Vincent Monot on 06/12/2015.
 */
public class NavigationDrawerSetup {

    private ListView mDrawerList;
    private ArrayAdapter<String> mArrayAdapter;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private Activity currentActivity;

    public NavigationDrawerSetup(Activity currentActivity) {
        this.currentActivity = currentActivity;
        //mDrawerList = (ListView) currentActivity.findViewById(R.id.nav_list);
        mDrawerLayout = (DrawerLayout) currentActivity.findViewById(R.id.drawer_layout);
    }

    public void configureDrawer() {
        final String[] itemsArray = currentActivity.getResources().getStringArray(R.array.menuItems);
        mArrayAdapter = new ArrayAdapter<String>(currentActivity, android.R.layout.simple_list_item_1, itemsArray);
        mDrawerList.setAdapter(mArrayAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                String curActivityName = currentActivity.getClass().getSimpleName();
                Log.d("NavigationDrawerSetup", "In " + curActivityName);
                switch (position) {
                    case 0:
                        if (curActivityName.equals("SpecsActivity")) {
                            Log.d("NavigationDrawerSetup", "Calling myself");
                            Toast.makeText(currentActivity, "This is me", Toast.LENGTH_SHORT).show();
                            // ICI IL FAUT RAFRAICHIR LA PAGE
                        } else {
                            Log.d("NavigationDrawerSetup", "Starting SpecsActivity");
                            /*
                            intent = new Intent(currentActivity, SpecsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            currentActivity.startActivity(intent);
                            */
                        }
                        break;
                    case 1:
                        /*
                        if (curActivityName.equals("SpecsActivity")) {
                            Log.d("NavigationDrawerSetup", "Starting DiceActivity");
                            intent = new Intent(currentActivity, DiceActivity.class);
                            currentActivity.startActivity(intent);
                        } else if (curActivityName.equals("DiceActivity")) {

                        } else {

                        }
                        */
                        if (curActivityName.equals("DiceActivity")) {
                            Log.d("NavigationDrawerSetup", "Calling myself");
                            Toast.makeText(currentActivity, "This is me", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("NavigationDrawerSetup", "Starting DiceActivity");
                            intent = new Intent(currentActivity, DiceActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            currentActivity.startActivity(intent);
                        }
                        break;
                    case 2:
                        if (curActivityName.equals("OptionsActivity")) {
                            Log.d("NavigationDrawerSetup", "Calling myself");
                            Toast.makeText(currentActivity, "This is me", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("NavigationDrawerSetup", "Starting OptionsActivity");
                            intent = new Intent(currentActivity, OptionsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            currentActivity.startActivity(intent);
                        }
                        break;
                    default:
                        break;
                }

            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(currentActivity, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //currentActivity.getSupportActionBar().setTitle(R.string.menuName);
                currentActivity.invalidateOptionsMenu();
            }
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //currentActivity.getSupportActionBar().setTitle(activityTitle);
                currentActivity.invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                float moveFactor = mDrawerList.getWidth() * slideOffset;
                RelativeLayout contentLayout = (RelativeLayout) currentActivity.findViewById(R.id.content_layout);

                contentLayout.setTranslationX(moveFactor);
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }
}
