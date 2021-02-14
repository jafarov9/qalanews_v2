package com.hajma.qalanews_android.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hajma.qalanews_android.DetailedNewsActivity;
import com.hajma.qalanews_android.MainActivity;
import com.hajma.qalanews_android.MySingleton;
import com.hajma.qalanews_android.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class MyFirabaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("qwewqe", "Message");
        showNotification(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("qwewqe", "Message");
    }

    private void showNotification(RemoteMessage remoteMessage) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.hajma.qalanews_android";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("DEV");
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        if(remoteMessage.getData() != null) {

            if(remoteMessage.getData().size() > 0) {

                String title = remoteMessage.getData().get("title");
                String message = remoteMessage.getData().get("message");
                String image_url = remoteMessage.getData().get("img_url");
                Log.e("qwewqe", "Title: "+remoteMessage.getData().get("title"));
                Log.e("qwewqe", "Message: "+remoteMessage.getData().get("message"));
                Log.e("qwewqe", "newsId: "+remoteMessage.getData().get("news_id"));
                int newsID = Integer.parseInt(remoteMessage.getData().get("news_id"));

                notiBuilder
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.qalalogo)
                        .setContentTitle(title)
                        .setContentText(message);

                ImageRequest imageRequest = new ImageRequest(image_url, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Log.e("qwewqe", "Onresponse");
                        notiBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(response));
                        notiBuilder.setContentIntent(getPendingIntent(newsID));
                        notificationManager.notify(new Random().nextInt(), notiBuilder.build());
                    }
                }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("qwewqe", error.getMessage());
                    }
                });

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(imageRequest);

            }
        }

    }

    private PendingIntent getPendingIntent(int newsID) {
        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, DetailedNewsActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        resultIntent.putExtra("newsID", newsID);


        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;

    }

}
