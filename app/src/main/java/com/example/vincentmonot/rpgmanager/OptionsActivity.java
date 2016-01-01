package com.example.vincentmonot.rpgmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class OptionsActivity extends DrawerActivity {

    private static final String TAG = "OptionsActivity";
    Button testCon, acceptNick, loadLocale;
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

                if (!(urlString.startsWith("http://") || urlString.startsWith("https://"))) {
                    urlString = "http://" + urlString;
                }

                try {
                    URL url = new URL(urlString);
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    //urlc.setRequestMethod("HEAD");
                    //urlc.setConnectTimeout(5 * 1000);
                    //urlc.setReadTimeout(5 * 1000);
                    urlc.connect();

                    if (urlc.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Log.d(TAG, "Reachable destination");
                        Toast.makeText(OptionsActivity.this, R.string.toast_r_dest, Toast.LENGTH_SHORT).show();

                        if (!urlString.endsWith("/")) {
                            urlString += "/";
                        }
                        Log.d(TAG, "Saving URL " + urlString);
                        SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("url", urlString);
                        editor.apply();
                    }

                } catch (IOException e) {
                    Log.d(TAG, "Unreachable destination");
                    Toast.makeText(OptionsActivity.this, R.string.toast_unr_dest, Toast.LENGTH_SHORT).show();
                }
            }
        });

        acceptNick = (Button) findViewById(R.id.ok_button);
        acceptNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = ((EditText) findViewById(R.id.editTextNick)).getText().toString();
                SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("nickname", nickname);
                editor.apply();

                Toast.makeText(
                        OptionsActivity.this,
                        getResources().getString(R.string.toast_new_nick)+" : "+nickname,
                        Toast.LENGTH_SHORT).show();
            }
        });

        loadLocale = (Button) findViewById(R.id.buttonLocale);
        loadLocale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = OptionsActivity.this.getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                OptionsActivity.this.finish();
                startActivity(intent);
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

                // Updates the preferences
                SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("locale", lang);
                editor.apply();

                OptionsActivity.super.updateLocale();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        // Put the default URL in the EditText
        String url = settings.getString("url", "http://uinelj.eu/misc/rpgm/");
        ((EditText) findViewById(R.id.editTextURL)).setText(url);

        // Put the default nickname in the EditText
        String nickname = settings.getString("nickname", "foo");
        ((EditText) findViewById(R.id.editTextNick)).setText(nickname);

        // Put the selected locale as default on spinner
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
        String[] array = {"English", "Français"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.activity_options, array);
        langSelect.setSelection(adapter.getPosition(language));
    }

    @Override
    protected void onItemSelection(AdapterView<?> parent, View view, int position, long id) {
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
                super.updateLocale();
                SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                Map<String, ?> keys = settings.getAll();
                for(Map.Entry<String, ?> entry : keys.entrySet()) {
                    Log.d(TAG, "Values = "+entry.getKey()+":"+entry.getValue());
                }
                mDrawerLayout.closeDrawers();
                break;
            default:
                break;
        }
    }
}
