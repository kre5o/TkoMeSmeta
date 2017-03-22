package hr.math.www.tkomesmeta;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import android.os.Handler;


public class MainActivity extends AppCompatActivity {

    private String kontakt, number;

    Contact contact;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    // Radi nesto dok završi funkcija findOnline
                    kontakt=contact._kontakt;
                    break;
                case 1:
                    //Radi nesto dok završi funkcija findContact
                    kontakt=contact._kontakt;
                    break;
            }
            TextView tv = (TextView) findViewById(R.id.textView);
            tv.setText(kontakt);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CALL_LOG}, 1);
        }

        kontakt = "";
        contact = new Contact(this, handler);


    }

    public void clickTrazi(View view) {
        EditText et = (EditText) findViewById(R.id.editText);
        contact.findOnline(et.getText().toString());
    }

    public void clickKontakti(View view){
        EditText et = (EditText) findViewById(R.id.editText);
        contact.findContact(et.getText().toString());
    }

    public void clickLista(View view){
        Intent intent = new Intent(this, CallListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        Bundle b = getIntent().getExtras();
        if( b != null) {
            if (b.containsKey("number")) {
                number = b.getString("number");
                kontakt = b.getString("name");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                if (!kontakt.equals(getResources().getString(R.string.Not_found))) {
                    builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            contact.addContact(kontakt, number);

                        }
                    });
                }
                builder.setTitle(R.string.Add_contact)
                        .setMessage(getResources().getString(R.string.Number_to_add) + ": " + number + "\n" + getResources().getString(R.string.Name) + ": " + kontakt)
                        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

}