package com.example.vincentmonot.rpgmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class CharacterSheetActivity extends DrawerActivity {

    private final static String TAG = "CharacterSheetActivity";
    String url;

    Map<String, Integer> pickerValues = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specs);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        refreshData();

        Intent notifIntent = new Intent(getApplicationContext(), NotificationService.class);
        notifIntent.addCategory(NotificationService.TAG);
        stopService(notifIntent);
        startService(notifIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Resources r = getResources();

        // If we updated the locale, we need to reload the activity
        String dispLvl = ((TextView) findViewById(R.id.textViewLevel)).getText().toString();
        String rightLvl = r.getString(R.string.level);
        if(! dispLvl.equalsIgnoreCase(rightLvl)) {
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            this.finish();
            startActivity(intent);
        }

        // We refresh the data
        refreshData();

        // Make the layouts clickable to edit hte values
        RelativeLayout layoutHealth = (RelativeLayout) findViewById(R.id.layoutHealth);
        layoutHealth.setOnClickListener(new LayoutNumberPicker(
                r.getString(R.string.health_picker),
                R.id.textCurrentHealth, "hp;maxhp"));

        LinearLayout layoutLevel = (LinearLayout) findViewById(R.id.layoutLevel);
        layoutLevel.setOnClickListener(new LayoutNumberPicker(
                r.getString(R.string.level_picker),
                R.id.textLevel, "lvl;xp"));
        LinearLayout layoutStrength = (LinearLayout) findViewById(R.id.layoutStrength);
        layoutStrength.setOnClickListener(new LayoutNumberPicker(
                r.getString(R.string.strength_picker),
                R.id.textStrength, "str"));
        LinearLayout layoutDexterity = (LinearLayout) findViewById(R.id.layoutDexterity);
        layoutDexterity.setOnClickListener(new LayoutNumberPicker(
                r.getString(R.string.dexterity_picker),
                R.id.textDexterity, "dex"));
        LinearLayout layoutConstitution = (LinearLayout) findViewById(R.id.layoutConstitution);
        layoutConstitution.setOnClickListener(new LayoutNumberPicker(
                r.getString(R.string.constitution_picker),
                R.id.textConstitution, "con"));
        LinearLayout layoutIntelligence = (LinearLayout) findViewById(R.id.layoutIntelligence);
        layoutIntelligence.setOnClickListener(new LayoutNumberPicker(
                r.getString(R.string.intelligence_picker),
                R.id.textIntelligence, "intel"));
        LinearLayout layoutWisdom = (LinearLayout) findViewById(R.id.layoutWisdom);
        layoutWisdom.setOnClickListener(new LayoutNumberPicker(
                r.getString(R.string.wisdom_picker),
                R.id.textWisdom, "wis"));
        LinearLayout layoutCharisma = (LinearLayout) findViewById(R.id.layoutCharisma);
        layoutCharisma.setOnClickListener(new LayoutNumberPicker(
                r.getString(R.string.charisma_picker),
                R.id.textCharisma, "cha"));
    }

    @Override
    // Used when clicking a menu item
    protected void onItemSelection(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (position) {
            case 0:
                mDrawerLayout.closeDrawers();
                refreshData();
                break;
            case 1:
                intent = new Intent(this, DiceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(this, OptionsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    // Refresh the values of the app
    private void refreshData() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // We get the saved url, or a default one
        SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        url = settings.getString("url", "http://uinelj.eu/misc/rpgm/");

        // We update the values
        if (networkInfo != null && networkInfo.isConnected()) {
            String nickname = settings.getString("nickname", "foo");
            String fullUrl = url+"action.php?a=get&id="+nickname;
            Log.d(TAG, "Retrieving data from: "+fullUrl);
            Request req = new Request(fullUrl, this);
            if(req.getResult().containsKey("success")) {
                // If we were able to retrieve the values, we refresh the textviews
                if (req.getValue("success").equals("true")) {

                    SharedPreferences notifSettings = getSharedPreferences("notifications", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = notifSettings.edit();
                    editor.putInt("hp", Integer.valueOf(req.getValue("hp")));
                    editor.putInt("lvl", Integer.valueOf(req.getValue("lvl")));
                    editor.apply();

                    ((TextView) findViewById(R.id.textNickname)).setText(req.getValue("name"));
                    ((TextView) findViewById(R.id.textAlignment)).setText(req.getValue("align"));
                    ((TextView) findViewById(R.id.textRace)).setText(req.getValue("race"));
                    ((TextView) findViewById(R.id.textClass)).setText(req.getValue("class"));

                    ((TextView) findViewById(R.id.textCurrentHealth)).setText(req.getValue("hp"));
                    pickerValues.put("hp", Integer.valueOf(req.getValue("hp")));
                    String maxHealth = "/" + req.getValue("maxhp");
                    ((TextView) findViewById(R.id.textMaxHealth)).setText(maxHealth);
                    pickerValues.put("maxhp", Integer.valueOf(req.getValue("maxhp")));
                    ((TextView) findViewById(R.id.textDmg)).setText(req.getValue("dmg"));
                    ((TextView) findViewById(R.id.textDefense)).setText(req.getValue("armour"));

                    String level = req.getValue("lvl");
                    pickerValues.put("lvl", Integer.valueOf(level));
                    String xp = req.getValue("xp");
                    if (Integer.valueOf(xp) < 10) {
                        xp = "0" + xp;
                    }
                    level += "(" + xp + ")";
                    ((TextView) findViewById(R.id.textLevel)).setText(level);
                    pickerValues.put("xp", Integer.valueOf(xp));
                    ((TextView) findViewById(R.id.textStrength)).setText(req.getValue("str"));
                    pickerValues.put("str", Integer.valueOf(req.getValue("str")));
                    ((TextView) findViewById(R.id.textDexterity)).setText(req.getValue("dex"));
                    pickerValues.put("dex", Integer.valueOf(req.getValue("dex")));
                    ((TextView) findViewById(R.id.textConstitution)).setText(req.getValue("con"));
                    pickerValues.put("con", Integer.valueOf(req.getValue("con")));
                    ((TextView) findViewById(R.id.textIntelligence)).setText(req.getValue("intel"));
                    pickerValues.put("intel", Integer.valueOf(req.getValue("intel")));
                    ((TextView) findViewById(R.id.textWisdom)).setText(req.getValue("wis"));
                    pickerValues.put("wis", Integer.valueOf(req.getValue("wis")));
                    ((TextView) findViewById(R.id.textCharisma)).setText(req.getValue("cha"));
                    pickerValues.put("cha", Integer.valueOf(req.getValue("cha")));

                    ((TextView) findViewById(R.id.textBonds)).setText(req.getValue("bonds"));
                    ((TextView) findViewById(R.id.textGear)).setText(req.getValue("gear"));
                    ((TextView) findViewById(R.id.textMoves)).setText(req.getValue("moves"));

                }
                // If it didn't work, we put the default values
                else {
                    Resources r = getResources();
                    ((TextView) findViewById(R.id.textNickname)).setText(r.getString(R.string.textNickname));
                    ((TextView) findViewById(R.id.textAlignment)).setText(r.getString(R.string.textAlignment));
                    ((TextView) findViewById(R.id.textRace)).setText(r.getString(R.string.textRace));
                    ((TextView) findViewById(R.id.textClass)).setText(r.getString(R.string.textClass));

                    ((TextView) findViewById(R.id.textCurrentHealth)).setText(r.getString(R.string.base_value));
                    ((TextView) findViewById(R.id.textMaxHealth)).setText(r.getString(R.string.base_health));
                    ((TextView) findViewById(R.id.textDmg)).setText(r.getString(R.string.base_dmg));
                    ((TextView) findViewById(R.id.textDefense)).setText(r.getString(R.string.base_value));

                    ((TextView) findViewById(R.id.textLevel)).setText(r.getString(R.string.base_level));
                    ((TextView) findViewById(R.id.textStrength)).setText(r.getString(R.string.base_value));
                    pickerValues.put("str", Integer.valueOf(r.getString(R.string.base_value)));
                    ((TextView) findViewById(R.id.textDexterity)).setText(r.getString(R.string.base_value));
                    pickerValues.put("dex", Integer.valueOf(r.getString(R.string.base_value)));
                    ((TextView) findViewById(R.id.textConstitution)).setText(r.getString(R.string.base_value));
                    pickerValues.put("con", Integer.valueOf(r.getString(R.string.base_value)));
                    ((TextView) findViewById(R.id.textIntelligence)).setText(r.getString(R.string.base_value));
                    pickerValues.put("intel", Integer.valueOf(r.getString(R.string.base_value)));
                    ((TextView) findViewById(R.id.textWisdom)).setText(r.getString(R.string.base_value));
                    pickerValues.put("wis", Integer.valueOf(r.getString(R.string.base_value)));
                    ((TextView) findViewById(R.id.textCharisma)).setText(r.getString(R.string.base_value));
                    pickerValues.put("cha", Integer.valueOf(r.getString(R.string.base_value)));

                    ((TextView) findViewById(R.id.textBonds)).setText(r.getString(R.string.textBonds));
                    ((TextView) findViewById(R.id.textGear)).setText(r.getString(R.string.textGear));
                    ((TextView) findViewById(R.id.textMoves)).setText(r.getString(R.string.textMoves));

                    String message = req.getValue("msg") + " (" + req.getValue("id") + ")";
                    Toast.makeText(CharacterSheetActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Log.d(TAG, "Unable to retrieve data");
                Toast.makeText(CharacterSheetActivity.this, R.string.toast_no_data, Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d(TAG, "Network isn't working");
            Toast.makeText(CharacterSheetActivity.this, R.string.toast_no_network, Toast.LENGTH_SHORT).show();
        }
    }

    // Class which responds to clicks on layouts (to update their values)
    private class LayoutNumberPicker implements View.OnClickListener {
        int idTextView = 0;
        String title = "", field = "";
        String[] split;

        public LayoutNumberPicker(String title, int idTextView, String field) {
            super();
            this.title = title;
            this.idTextView = idTextView;
            this.field = field;

            split = field.split(";");
        }

        @Override
        public void onClick(View v) {
            // We create a set of values to display in the NumberPicker
            String values[] = new String[100];
            for(int i=0 ; i<100 ; i++) {
                if(i>=90) {
                    values[i] = "0"+Integer.toString(99-i);
                }
                else {
                    values[i] = Integer.toString(99 - i);
                }
            }
            // We set up the NumberPicker and its layout
            final NumberPicker numPicker = new NumberPicker(CharacterSheetActivity.this);
            numPicker.setMinValue(0);
            numPicker.setMaxValue(99);
            numPicker.setWrapSelectorWheel(false);
            numPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            numPicker.setDisplayedValues(values);

            LinearLayout pickerLayout = new LinearLayout(CharacterSheetActivity.this);
            pickerLayout.setOrientation(LinearLayout.HORIZONTAL);
            pickerLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);
            LinearLayout.LayoutParams numPickerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            pickerLayout.setLayoutParams(params);

            NumberPicker numPicker2 = new NumberPicker(CharacterSheetActivity.this);
            // If we have multiple NumberPickers to display at the same time (for health and level/xp),
            // we set up another NumberPicker
            switch (split[0]) {
                case "lvl":
                    String values2[] = new String[1000];
                    for (int i = 0; i < 1000; i++) {
                        if (i >= 990) {
                            values2[i] = "00" + Integer.toString(999 - i);
                        } else if (i >= 900) {
                            values2[i] = "0" + Integer.toString(999 - i);
                        } else {
                            values2[i] = Integer.toString(999 - i);
                        }
                    }
                    numPicker.setValue(99 - pickerValues.get(split[0]));

                    numPicker2.setMinValue(0);
                    numPicker2.setMaxValue(999);
                    numPicker2.setValue(999 - pickerValues.get(split[1]));
                    numPicker2.setWrapSelectorWheel(false);
                    numPicker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                    numPicker2.setDisplayedValues(values2);

                    numPickerParams.setMargins(50, 0, 50, 0);
                    pickerLayout.addView(numPicker, numPickerParams);
                    pickerLayout.addView(numPicker2, numPickerParams);
                    break;
                case "hp":
                    numPicker.setValue(99 - pickerValues.get(split[0]));

                    numPicker2.setMinValue(0);
                    numPicker2.setMaxValue(99);
                    numPicker2.setValue(99 - pickerValues.get(split[1]));
                    numPicker2.setWrapSelectorWheel(false);
                    numPicker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                    numPicker2.setDisplayedValues(values);

                    numPickerParams.setMargins(50, 0, 50, 0);
                    pickerLayout.addView(numPicker, numPickerParams);
                    pickerLayout.addView(numPicker2, numPickerParams);
                    break;
                default:
                    numPicker.setValue(99 - pickerValues.get(field));
                    pickerLayout.addView(numPicker, numPickerParams);
                    break;
            }

            // We build the alert dialog to display the number picker
            AlertDialog.Builder builder = new AlertDialog.Builder(CharacterSheetActivity.this);
            final NumberPicker finalNumPicker2 = numPicker2;
            builder.setTitle(title)
                    .setView(pickerLayout)
                    .setPositiveButton(getResources().getString(R.string.picker_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Checking if there were multiple number pickers at the same time
                                    switch (split[0]) {
                                        case "lvl":
                                            // Updating the value online
                                            updateValue(split[0], String.valueOf(99 - numPicker.getValue()));
                                            updateValue(split[1], String.valueOf(999 - finalNumPicker2.getValue()));

                                            // Update the display of the value
                                            String level = pickerValues.get("lvl") + "(" + pickerValues.get("xp") + ")";
                                            ((TextView) findViewById(R.id.textLevel)).setText(level);
                                            break;
                                        case "hp": {
                                            String newValue = String.valueOf(99 - numPicker.getValue());
                                            updateValue(split[0], newValue);
                                            ((TextView) findViewById(R.id.textCurrentHealth)).setText(newValue);

                                            newValue = String.valueOf(99 - finalNumPicker2.getValue());
                                            updateValue(split[1], newValue);
                                            String maxhp = "/" + pickerValues.get("maxhp");
                                            ((TextView) findViewById(R.id.textMaxHealth)).setText(maxhp);
                                            break;
                                        }
                                        default: {
                                            String newValue = String.valueOf(99 - numPicker.getValue());
                                            updateValue(split[0], newValue);
                                            ((TextView) findViewById(idTextView)).setText(newValue);
                                            break;
                                        }
                                    }
                                }
                            })
                    .setNegativeButton(getResources().getString(R.string.picker_cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        // Update value online
        private void updateValue(String field, String value) {
            // Updates the value in the HashMap
            pickerValues.put(field, Integer.valueOf(value));

            // Get the preferences
            SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
            String urlSettings = settings.getString("url", "http://uinelj.eu/misc/rpgm/");
            String playerId = settings.getString("nickname", "foo");

            // Create the url
            String fullUrl = urlSettings + "action.php?a=update&id=" + playerId
                    + "&field=" + field + "&value=" + value;

            // Make the request online
            Request req = new Request(fullUrl, CharacterSheetActivity.this);
            if(req.getResult().containsKey("success")) {
                if(req.getValue("success").equals("true")) {
                    Toast.makeText(CharacterSheetActivity.this,
                            R.string.toast_chg_data_ok,
                            Toast.LENGTH_SHORT).show();
                }
                else if(req.getValue("success").equals("false")){
                    Toast.makeText(CharacterSheetActivity.this,
                            R.string.toast_chg_data_err,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
