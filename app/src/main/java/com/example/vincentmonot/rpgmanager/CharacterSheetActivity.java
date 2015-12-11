package com.example.vincentmonot.rpgmanager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

public class CharacterSheetActivity extends DrawerActivity {

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
        refreshData();
    }

    @Override
    protected void onItemSelection(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "In CharacterSheetActivity : "+navOptions[position], Toast.LENGTH_SHORT).show();
        Intent intent;
        switch (position) {
            case 0:
                refreshData();
                break;
            case 1:
                mDrawerLayout.closeDrawer(mDrawerList);
                intent = new Intent(this, DiceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case 2:
                mDrawerLayout.closeDrawer(mDrawerList);
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
        if (networkInfo != null && networkInfo.isConnected()) {
            Request req = new Request("http://uinelj.eu/misc/rpgm/action.php?a=get&id=foo", this);
            if(req.getValue("success").equals("true")) {
                Log.d("CharacterSheetActivity", "name="+req.getValue("name"));
                ((TextView) findViewById(R.id.textNickname)).setText(req.getValue("name"));
                Log.d("CharacterSheetActivity", "align="+req.getValue("align"));
                ((TextView) findViewById(R.id.textAlignment)).setText(req.getValue("align"));
                Log.d("CharacterSheetActivity", "race="+req.getValue("race"));
                ((TextView) findViewById(R.id.textRace)).setText(req.getValue("race"));
                Log.d("CharacterSheetActivity", "class="+req.getValue("class"));
                ((TextView) findViewById(R.id.textClass)).setText(req.getValue("class"));


                Log.d("CharacterSheetActivity", "str="+req.getValue("str"));
                ((TextView) findViewById(R.id.textStrength)).setText(req.getValue("str"));
                Log.d("CharacterSheetActivity", "dex="+req.getValue("dex"));
                ((TextView) findViewById(R.id.textDexterity)).setText(req.getValue("dex"));
                Log.d("CharacterSheetActivity", "con="+req.getValue("con"));
                ((TextView) findViewById(R.id.textConstitution)).setText(req.getValue("con"));
                Log.d("CharacterSheetActivity", "intel="+req.getValue("intel"));
                ((TextView) findViewById(R.id.textIntelligence)).setText(req.getValue("intel"));
                Log.d("CharacterSheetActivity", "wis="+req.getValue("wis"));
                ((TextView) findViewById(R.id.textWisdom)).setText(req.getValue("wis"));
                Log.d("CharacterSheetActivity", "cha="+req.getValue("cha"));
                ((TextView) findViewById(R.id.textCharisma)).setText(req.getValue("cha"));

                /*
                TextView nick = (TextView) findViewById(R.id.textNickname);
                nick.setText(req.getValue("name"));
                */
            }
            else {
                Log.d("CharacterSheetActivity", req.getValue("msg")+" ("+req.getValue("id")+")");
                Toast.makeText(CharacterSheetActivity.this, req.getValue("msg")+" ("+req.getValue("id")+")", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("CharacterSheetActivity", "LE NETWORK IL MARCHE PAS");
        }
    }
}
