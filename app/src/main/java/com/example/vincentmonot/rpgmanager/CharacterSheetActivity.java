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

/** TODO
 * Update les valeurs via une requête vers la page HTTP depuis : LayoutNumberPicker->setPositiveButton->onClick (ligne 216)
 */

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

        LinearLayout layoutStrength = (LinearLayout) findViewById(R.id.layoutStrength);
        layoutStrength.setOnClickListener(new LayoutNumberPicker(R.string.strength_picker, R.id.textStrength));

        LinearLayout layoutDexterity = (LinearLayout) findViewById(R.id.layoutDexterity);
        layoutDexterity.setOnClickListener(new LayoutNumberPicker(R.string.dexterity_picker, R.id.textDexterity));

        LinearLayout layoutConstitution = (LinearLayout) findViewById(R.id.layoutConstitution);
        layoutConstitution.setOnClickListener(new LayoutNumberPicker(R.string.constitution_picker, R.id.textConstitution));

        LinearLayout layoutIntelligence = (LinearLayout) findViewById(R.id.layoutIntelligence);
        layoutIntelligence.setOnClickListener(new LayoutNumberPicker(R.string.intelligence_picker, R.id.textIntelligence));

        LinearLayout layoutWisdom = (LinearLayout) findViewById(R.id.layoutWisdom);
        layoutWisdom.setOnClickListener(new LayoutNumberPicker(R.string.wisdom_picker, R.id.textWisdom));

        LinearLayout layoutCharisma = (LinearLayout) findViewById(R.id.layoutCharisma);
        layoutCharisma.setOnClickListener(new LayoutNumberPicker(R.string.charisma_picker, R.id.textCharisma));

        refreshData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    protected void onItemSelection(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (position) {
            case 0:
                super.updateLocale();
                mDrawerLayout.closeDrawers();
                refreshData();
                break;
            case 1:
                //mDrawerLayout.closeDrawer(mDrawerList);
                intent = new Intent(this, DiceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case 2:
                //mDrawerLayout.closeDrawer(mDrawerList);
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
            Log.d(TAG, "LOADING URL "+fullUrl);
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
                    pickerValues.put("strength", Integer.valueOf(req.getValue("str")));
                    ((TextView) findViewById(R.id.textDexterity)).setText(req.getValue("dex"));
                    pickerValues.put("dexterity", Integer.valueOf(req.getValue("dex")));
                    ((TextView) findViewById(R.id.textConstitution)).setText(req.getValue("con"));
                    pickerValues.put("constitution", Integer.valueOf(req.getValue("con")));
                    ((TextView) findViewById(R.id.textIntelligence)).setText(req.getValue("intel"));
                    pickerValues.put("intelligence", Integer.valueOf(req.getValue("intel")));
                    ((TextView) findViewById(R.id.textWisdom)).setText(req.getValue("wis"));
                    pickerValues.put("wisdom", Integer.valueOf(req.getValue("wis")));
                    ((TextView) findViewById(R.id.textCharisma)).setText(req.getValue("cha"));
                    pickerValues.put("charisma", Integer.valueOf(req.getValue("cha")));
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
                    pickerValues.put("strength", Integer.valueOf(r.getString(R.string.base_value)));
                    ((TextView) findViewById(R.id.textDexterity)).setText(r.getString(R.string.base_value));
                    pickerValues.put("dexterity", Integer.valueOf(r.getString(R.string.base_value)));
                    ((TextView) findViewById(R.id.textConstitution)).setText(r.getString(R.string.base_value));
                    pickerValues.put("constitution", Integer.valueOf(r.getString(R.string.base_value)));
                    ((TextView) findViewById(R.id.textIntelligence)).setText(r.getString(R.string.base_value));
                    pickerValues.put("intelligence", Integer.valueOf(r.getString(R.string.base_value)));
                    ((TextView) findViewById(R.id.textWisdom)).setText(r.getString(R.string.base_value));
                    pickerValues.put("wisdom", Integer.valueOf(r.getString(R.string.base_value)));
                    ((TextView) findViewById(R.id.textCharisma)).setText(r.getString(R.string.base_value));
                    pickerValues.put("charisma", Integer.valueOf(r.getString(R.string.base_value)));

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
        int idTitle = 0, idTextView = 0;

        public LayoutNumberPicker(int idTitle, int idTextView) {
            super();
            this.idTitle = idTitle;
            this.idTextView = idTextView;
        }

        @Override
        public void onClick(View v) {
            final NumberPicker numPicker = new NumberPicker(CharacterSheetActivity.this);
            numPicker.setMinValue(0);
            numPicker.setMaxValue(99);
            numPicker.setValue(pickerValues.get(getResources().getString(idTitle).toLowerCase()));
            numPicker.setWrapSelectorWheel(false);

            RelativeLayout pickerLayout = new RelativeLayout(CharacterSheetActivity.this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
            RelativeLayout.LayoutParams numPickerParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            numPickerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

            pickerLayout.setLayoutParams(params);
            pickerLayout.addView(numPicker, numPickerParams);

            AlertDialog.Builder builder = new AlertDialog.Builder(CharacterSheetActivity.this);
            builder.setTitle(getResources().getString(idTitle))
                    .setView(pickerLayout)
                    .setPositiveButton(getResources().getString(R.string.picker_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "new "+getResources().getString(idTitle).toLowerCase()+" = "+numPicker.getValue());
                                    ((TextView) findViewById(idTextView)).setText(String.valueOf(numPicker.getValue()));
                                }
                            })
                    .setNegativeButton(getResources().getString(R.string.picker_cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "Cancelling");
                                    dialog.cancel();
                                }
                            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
