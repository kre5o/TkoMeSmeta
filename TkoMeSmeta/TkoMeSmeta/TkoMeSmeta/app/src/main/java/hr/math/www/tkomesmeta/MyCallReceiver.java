package hr.math.www.tkomesmeta;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.provider.ContactsContract;


public class MyCallReceiver extends BroadcastReceiver {
    Contact pretraga;

    @Override
    public void onReceive(Context context, Intent intent) {
        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        final String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        final Context ctx = context;
        if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            if (cursor == null) {
                return;
            }
            if (!cursor.moveToFirst()) {

                Handler nadji = new Handler() {

                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case 0:
                                Intent notificationIntent = new Intent(ctx, MainActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("number",number);
                                bundle.putString("name",pretraga._kontakt);
                                notificationIntent.putExtras(bundle);
                                PendingIntent contentIntent = PendingIntent.getActivity(ctx,
                                        0, notificationIntent,
                                        PendingIntent.FLAG_CANCEL_CURRENT);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                                        ctx)
                                        .setContentIntent(contentIntent)
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentTitle(ctx.getResources().getString(R.string.Number_to_find) + number)
                                        .setContentText(pretraga._kontakt);


                                NotificationManager notificationmanager = (NotificationManager) ctx
                                        .getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationmanager.notify(0, builder.build());
                                break;
                            case 1:
                                //Radi nesto dok završi funkcija findContact
                                break;
                        }
                    }
                };
                pretraga = new Contact(context,nadji);
                pretraga.findOnline(number);
            }

        }
    }
}