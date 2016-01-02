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

        refreshData();

        LinearLayout layoutStrength = (LinearLayout) findViewById(R.id.layoutStrength);
        layoutStrength.setOnClickListener(new LayoutNumberPicker(r.getString(R.string.strength_picker), R.id.textStrength, "str"));
        LinearLayout layoutDexterity = (LinearLayout) findViewById(R.id.layoutDexterity);
        layoutDexterity.setOnClickListener(new LayoutNumberPicker(r.getString(R.string.dexterity_picker), R.id.textDexterity, "dex"));
        LinearLayout layoutConstitution = (LinearLayout) findViewById(R.id.layoutConstitution);
        layoutConstitution.setOnClickListener(new LayoutNumberPicker(r.getString(R.string.constitution_picker), R.id.textConstitution, "con"));
        LinearLayout layoutIntelligence = (LinearLayout) findViewById(R.id.layoutIntelligence);
        layoutIntelligence.setOnClickListener(new LayoutNumberPicker(r.getString(R.string.intelligence_picker), R.id.textIntelligence, "intel"));
        LinearLayout layoutWisdom = (LinearLayout) findViewById(R.id.layoutWisdom);
        layoutWisdom.setOnClickListener(new LayoutNumberPicker(r.getString(R.string.wisdom_picker), R.id.textWisdom, "wis"));
        LinearLayout layoutCharisma = (LinearLayout) findViewById(R.id.layoutCharisma);
        layoutCharisma.setOnClickListener(new LayoutNumberPicker(r.getString(R.string.charisma_picker), R.id.textCharisma, "cha"));
    }

    @Override
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

    private void refreshData() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        url = settings.getString("url", "http://uinelj.eu/misc/rpgm/");

        if (networkInfo != null && networkInfo.isConnected()) {
            String nickname = settings.getString("nickname", "foo");
            String fullUrl = url+"action.php?a=get&id="+nickname;
            Log.d(TAG, "Retrieving data from: "+fullUrl);
            Request req = new Request(fullUrl, this);
            if(req.getResult().containsKey("success")) {
                if (req.getValue("success").equals("true")) {
                    ((TextView) findViewById(R.id.textNickname)).setText(req.getValue("name"));
                    ((TextView) findViewById(R.id.textAlignment)).setText(req.getValue("align"));
                    ((TextView) findViewById(R.id.textRace)).setText(req.getValue("race"));
                    ((TextView) findViewById(R.id.textClass)).setText(req.getValue("class"));

                    ((TextView) findViewById(R.id.textCurrentHealth)).setText(req.getValue("hp"));
                    String maxHealth = "/" + req.getValue("maxhp");
                    ((TextView) findViewById(R.id.textMaxHealth)).setText(maxHealth);
                    ((TextView) findViewById(R.id.textDmg)).setText(req.getValue("dmg"));
                    ((TextView) findViewById(R.id.textDefense)).setText(req.getValue("armour"));

                    String level = req.getValue("lvl");
                    String xp = req.getValue("xp");
                    if (Integer.valueOf(xp) < 10) {
                        xp = "0" + xp;
                    }
                    level += "(" + xp + ")";
                    ((TextView) findViewById(R.id.textLevel)).setText(level);
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
                } else {
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

                    String message = req.getValue("msg") + " (" + req.getValue("id") + ")";
                    Log.d(TAG, message);
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

    private class LayoutNumberPicker implements View.OnClickListener {
        int idTextView = 0;
        String title = "", field = "";

        public LayoutNumberPicker(String title, int idTextView, String field) {
            super();
            this.title = title;
            this.idTextView = idTextView;
            this.field = field;
        }

        @Override
        public void onClick(View v) {
            String values[] = new String[100];
            for(int i=0 ; i<100 ; i++) {
                if(i>=90) {
                    values[i] = "0"+Integer.toString(99-i);
                }
                else {
                    values[i] = Integer.toString(99 - i);
                }
            }

            final NumberPicker numPicker = new NumberPicker(CharacterSheetActivity.this);
            numPicker.setMinValue(0);
            numPicker.setMaxValue(99);
            numPicker.setValue(99-pickerValues.get(field));
            numPicker.setWrapSelectorWheel(false);
            numPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            numPicker.setDisplayedValues(values);

            RelativeLayout pickerLayout = new RelativeLayout(CharacterSheetActivity.this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
            RelativeLayout.LayoutParams numPickerParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            numPickerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

            pickerLayout.setLayoutParams(params);
            pickerLayout.addView(numPicker, numPickerParams);

            AlertDialog.Builder builder = new AlertDialog.Builder(CharacterSheetActivity.this);
            builder.setTitle(title)
                    .setView(pickerLayout)
                    .setPositiveButton(getResources().getString(R.string.picker_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String newValue = String.valueOf(99-numPicker.getValue());
                                    ((TextView) findViewById(idTextView)).setText(newValue);
                                    pickerValues.put(field, Integer.valueOf(newValue));

                                    SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                                    String urlSettings = settings.getString("url", "http://uinelj.eu/misc/rpgm/");
                                    String playerId = settings.getString("nickname", "foo");

                                    String fullUrl = urlSettings + "action.php?a=update&id=" + playerId
                                            + "&field=" + field + "&value=" + newValue;

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
    }
}
