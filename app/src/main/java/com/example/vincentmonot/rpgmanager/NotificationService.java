package com.example.vincentmonot.rpgmanager;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NotificationService extends IntentService {
    public static final String TAG = "NotificationServiceTag";
    int NOTIF_LEVEL_UP = 1337;
    int NOTIF_DEATH = 666;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean allowNotif = true;

        while(allowNotif) {
            try {
                SharedPreferences notifSettings = getSharedPreferences("notifications", Context.MODE_PRIVATE);
                allowNotif = notifSettings.getBoolean("allow", true);
                int oldLvl = notifSettings.getInt("lvl", 0);
                int oldHp = notifSettings.getInt("hp", 0);

                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                // We get the saved url, or a default one
                SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                String url = settings.getString("url", "http://uinelj.eu/misc/rpgm/");

                // We update the values
                if (networkInfo != null && networkInfo.isConnected()) {
                    String nickname = settings.getString("nickname", "foo");
                    String fullUrl = url + "action.php?a=get&id=" + nickname;
                    Request req = new Request(fullUrl, this);
                    if (req.getResult().containsKey("success")) {
                        // If we were able to retrieve the values
                        if (req.getValue("success").equals("true")) {

                            int hpValue = Integer.valueOf(req.getValue("hp"));
                            if (hpValue != oldHp) {
                                SharedPreferences.Editor editor = notifSettings.edit();
                                editor.putInt("hp", hpValue);
                                editor.apply();

                                if (hpValue <= 0) {

                                    sendNotification(NOTIF_DEATH,
                                            getResources().getString(R.string.app_name),
                                            "You died : "+req.getValue("hp")+"/"+req.getValue("maxhp"),
                                            CharacterSheetActivity.class);

                                }
                            }

                            int lvlValue = Integer.valueOf(req.getValue("lvl"));
                            if(lvlValue != oldLvl) {
                                SharedPreferences.Editor editor = notifSettings.edit();
                                editor.putInt("lvl", lvlValue);
                                editor.apply();

                                if(lvlValue > oldLvl) {
                                    sendNotification(NOTIF_LEVEL_UP,
                                            getResources().getString(R.string.app_name),
                                            "You leveled up : lvl. "+req.getValue("lvl"),
                                            CharacterSheetActivity.class);
                                }
                            }
                        }
                    }
                }
                Thread.sleep(30000);
                allowNotif = notifSettings.getBoolean("allow", true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void sendNotification(int id, String title, String content, Class intentClass) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle(title)
                .setContentText(content);
        builder.setAutoCancel(true);

        Intent resultIntent = new Intent(this, intentClass);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(intentClass);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(id, builder.build());
    }
}
