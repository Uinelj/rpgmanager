package com.example.vincentmonot.rpgmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class DiceActivity extends DrawerActivity {

    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;

    int diceValue = 20;
    ImageButton diceImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice);

        // Initialize the sensor manager (to detect the shaking of the device)
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
            Random r = new Random();

            public void onShake() {
                int number = r.nextInt(diceValue)+1;
                Toast message = Toast.makeText(DiceActivity.this, String.valueOf(number), Toast.LENGTH_LONG);
                message.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                ((TextView) ((ViewGroup) message.getView()).getChildAt(0)).setTextSize(40);
                message.show();

            }
        });

        // Initialize the dice image
        diceImage = (ImageButton) findViewById(R.id.diceImage);
        diceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When clicking the dice, a choice of dices appears
                AlertDialog.Builder builder = new AlertDialog.Builder(DiceActivity.this);
                builder.setTitle(getResources().getString(R.string.nb_faces))
                    .setItems(R.array.dices_array, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // When selecting a new dice, it updates the image and the max value for the random number
                            switch(which) {
                                case 0:
                                    DiceActivity.this.diceValue = 4;
                                    diceImage.setImageResource(R.drawable.d4);
                                    break;
                                case 1:
                                    DiceActivity.this.diceValue = 6;
                                    diceImage.setImageResource(R.drawable.d6);
                                    break;
                                case 2:
                                    DiceActivity.this.diceValue = 8;
                                    diceImage.setImageResource(R.drawable.d8);
                                    break;
                                case 3:
                                    DiceActivity.this.diceValue = 10;
                                    diceImage.setImageResource(R.drawable.d10);
                                    break;
                                case 4:
                                    DiceActivity.this.diceValue = 12;
                                    diceImage.setImageResource(R.drawable.d12);
                                    break;
                                case 5:
                                    DiceActivity.this.diceValue = 20;
                                    diceImage.setImageResource(R.drawable.d20);
                                    break;
                                case 6:
                                    DiceActivity.this.diceValue = 24;
                                    diceImage.setImageResource(R.drawable.d24);
                                    break;
                                case 7:
                                    DiceActivity.this.diceValue = 30;
                                    diceImage.setImageResource(R.drawable.d30);
                                    break;
                            }

                            // Save the dice in preferences when new one selected
                            SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt("diceFaces", DiceActivity.this.diceValue);
                            editor.apply();
                        }
                    });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start the detection of shaking
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);

        // Displays the last dice clicked (used when orientation changes among other possibilities)
        SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        diceValue = settings.getInt("diceFaces", 20);

        switch(diceValue) {
            case 4:
                DiceActivity.this.diceValue = 4;
                diceImage.setImageResource(R.drawable.d4);
                break;
            case 6:
                DiceActivity.this.diceValue = 6;
                diceImage.setImageResource(R.drawable.d6);
                break;
            case 8:
                DiceActivity.this.diceValue = 8;
                diceImage.setImageResource(R.drawable.d8);
                break;
            case 10:
                DiceActivity.this.diceValue = 10;
                diceImage.setImageResource(R.drawable.d10);
                break;
            case 12:
                DiceActivity.this.diceValue = 12;
                diceImage.setImageResource(R.drawable.d12);
                break;
            case 20:
                DiceActivity.this.diceValue = 20;
                diceImage.setImageResource(R.drawable.d20);
                break;
            case 24:
                DiceActivity.this.diceValue = 24;
                diceImage.setImageResource(R.drawable.d24);
                break;
            case 30:
                DiceActivity.this.diceValue = 30;
                diceImage.setImageResource(R.drawable.d30);
                break;
            default:
                DiceActivity.this.diceValue = 20;
                diceImage.setImageResource(R.drawable.d20);
                break;
        }

    }

    @Override
    protected void onPause() {
        // We stop registering the movement
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    // Used when clicking a menu item
    protected void onItemSelection(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(this, CharacterSheetActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case 1:
                super.updateLocale();
                mDrawerLayout.closeDrawers();
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