package hr.math.www.tkomesmeta;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

public class CallListActivity extends AppCompatActivity {

    ListView lv;
    ArrayList<String> contactList;
    private int MY_PERM = 1;


    String kontakt;
    String numberG;
    Contact contact;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    // Radi nesto dok završi funkcija findOnline
                    kontakt=contact._kontakt;
                    AlertDialog.Builder builder = new AlertDialog.Builder(CallListActivity.this);
                    if(!contact._kontakt.equals(getResources().getString(R.string.Not_found))) {
                        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                contact.addContact(kontakt, numberG);
                            }
                        });
                    }
                    builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder.setMessage(getResources().getString(R.string.Number_to_add) + ": "+numberG+"\n"+getResources().getString(R.string.Name)+": " + kontakt)
                            .setTitle(R.string.Add_contact);


                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();

                    //show dialog
                    dialog.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_list);

        //permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    MY_PERM);
        }

        //initializing variables
        lv = (ListView) findViewById(R.id.list);
        contactList = new ArrayList<>();
        contact = new Contact(this, handler);
        kontakt = "";

        //get log of calls
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                null);

        final int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);

        while (cursor.moveToNext()) {
            String phNumber = cursor.getString(number);
            String callType = cursor.getString(type);
            String callDate = cursor.getString(date);
            String ctName = cursor.getString(name);
            Date callDayTime = new Date(Long.valueOf(callDate));
            int dircode = Integer.parseInt(callType);
            if ((dircode == CallLog.Calls.INCOMING_TYPE || dircode == CallLog.Calls.MISSED_TYPE)
                    && ctName == null)
                contactList.add(getResources().getString(R.string.Date) + ": " + callDayTime + "\n" + getResources().getString(R.string.Number) + ": " + phNumber);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_item, R.id.textView2, contactList);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Do whatever you want with the list data
                int location = contactList.get(position).lastIndexOf(" ");
                String number = contactList.get(position).substring(location+1, contactList.get(position).length());
                //if (number.contains("+")) number = "0" + number.substring(5, number.length());
                numberG = number;
                contact.findOnline(number);
            }
        });
    }
}
