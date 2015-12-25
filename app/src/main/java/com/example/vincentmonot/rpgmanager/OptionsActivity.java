package com.example.vincentmonot.rpgmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class OptionsActivity extends DrawerActivity {

    private static final String TAG = "OptionsActivity";
    Button testCon;
    Spinner langSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        testCon = (Button) findViewById(R.id.buttonCon);
        testCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString = ((EditText) findViewById(R.id.editTextURL)).getText().toString();

                if(! (urlString.startsWith("http://") || urlString.startsWith("https://"))) {
                    urlString = "http://"+urlString;
                }

                try {
                    URL url = new URL(urlString);
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setRequestMethod("HEAD");
                    urlc.setConnectTimeout(5 * 1000);
                    urlc.setReadTimeout(5 * 1000);
                    urlc.connect();

                    if(urlc.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Log.d(TAG, "Reachable destination");
                        Toast.makeText(OptionsActivity.this, "Reachable destination", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    Log.d(TAG, "Unreachable destination");
                    Toast.makeText(OptionsActivity.this, "Unreachable destination", Toast.LENGTH_SHORT).show();
                }
            }
        });

        langSelect = (Spinner) findViewById(R.id.spinnerLanguages);
        langSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getSelectedItem().toString();
                String lang;
                switch (selected) {
                    case "Français":
                        lang = "fr_FR";
                        break;
                    case "English":
                    default:
                        lang = "en_US";
                        break;
                }

                Log.d(TAG, "SELECTED lang=" + lang);

                SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("locale", lang);
                editor.apply();

                Locale locale = new Locale(lang);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                //Log.d(TAG, "CONFIG " + config.locale.getLanguage());
                onConfigurationChanged(config);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Put the selected locale as default on spinner
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        String lang = settings.getString("locale", "en_US");
        String language;
        switch(lang) {
            case "fr_FR":
                language = "Français";
                break;
            case "en_US":
            default:
                language = "English";
                break;
        }
        langSelect.setSelection(((ArrayAdapter) langSelect.getAdapter()).getPosition(language));
    }

    /*
    @Override
    protected void onResume() {
        super.onResume();

        File cfgFile = new File(getFilesDir(), "config.ini");
        try {
            BufferedReader br = new BufferedReader(new FileReader(cfgFile));
            String line;
            String lang = "en_US";

            while((line = br.readLine()) != null) {
                String[] split = line.split("::");
                switch(split[0]) {
                    case "url":
                        ((EditText) findViewById(R.id.editTextURL)).setText(split[1]);
                        break;
                    case "language":
                        Log.d(TAG, Locale.getDefault().toString());
                        switch (split[1]) {
                            case "English":
                                lang = "en_US";
                                ((Spinner) findViewById(R.id.spinnerLanguages)).setSelection(0);
                                break;
                            case "Français":
                                lang = "fr_FR";
                                ((Spinner) findViewById(R.id.spinnerLanguages)).setSelection(1);
                                break;
                            default:
                                lang = "en_US";
                                ((Spinner) findViewById(R.id.spinnerLanguages)).setSelection(0);
                                break;
                        }


                        break;
                    default:
                        break;
                }
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
    /*
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        //setContentView(R.layout.activity_options);
        Log.d(TAG, "newConfig.locale = "+newConfig.locale);
    }
    */
    @Override
    protected void onItemSelection(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "In DiceActivity : " + navOptions[position], Toast.LENGTH_SHORT).show();
        Intent intent;
        switch (position) {
            case 0:
                //mDrawerLayout.closeDrawer(mDrawerList);
                intent = new Intent(this, CharacterSheetActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case 1:
                //mDrawerLayout.closeDrawer(mDrawerList);
                intent = new Intent(this, DiceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case 2:
                mDrawerLayout.closeDrawers();
                break;
            default:
                break;
        }
    }
}
