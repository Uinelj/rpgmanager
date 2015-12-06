package com.example.vincentmonot.rpgmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class DiceActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice);
    }

    @Override
    protected void onItemSelection(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "In DiceActivity : " + navOptions[position], Toast.LENGTH_SHORT).show();
        Intent intent;
        switch (position) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            default:
                break;
        }
    }
}