package com.example.vincentmonot.rpgmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class CharacterSheetActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_sheet);
    }

    @Override
    protected void onItemSelection(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "In CharacterSheetActivity : "+navOptions[position], Toast.LENGTH_SHORT).show();
        Intent intent;
        switch (position) {
            case 0:
                // ICI ON RAFRAICHIT LES INFOS DU JOUEUR
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
}
