package hr.math.www.tkomesmeta;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Contact{
    private Context _context;
    private Handler _handler;
    /* handler treba biti oblika
    private Handler handler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                // Radi nesto dok završi funkcija findOnline
                break;
            case 1:
                //Radi nesto dok završi funkcija findContact
                break;
        }
    }
    }; */
    public String _kontakt;
    Cursor cursor;

    public Contact(Context context, Handler handler){
        _context = context;
        _handler = handler;
        _kontakt = "";
    }

    public void findOnline(String broj){
        if (broj.contains("+")) broj = "0" + broj.substring(4, broj.length());
        final String br = broj;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("http://www.imenik.hr/imenik/trazi/1/"+br+".html").get();
                    Elements ele = doc.select("div.telefon > a");

                    if(ele.isEmpty()) {
                        _kontakt=_context.getResources().getString(R.string.Not_found);
                    }
                    else {
                        _kontakt=ele.first().attr("title").toString();
                    }

                } catch (Exception e) {
                    _kontakt=_context.getResources().getString(R.string.Not_found);
                }
                _handler.sendEmptyMessage(0);
            }
        }).start();
    }

    public void findContact(String broj){
        final String br = broj;
        if(!br.equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(br));

                    final String[] projection = {ContactsContract.Contacts.DISPLAY_NAME};
                    ContentResolver contentResolver = _context.getContentResolver();

                    cursor = contentResolver.query(CONTENT_URI,
                            projection,
                            null,
                            null,
                            null);
                    String name = _context.getResources().getString(R.string.Not_found);
                    if (cursor.moveToFirst()) {
                        name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                    }

                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                    _kontakt = name;
                    _handler.sendEmptyMessage(1);
                }
            }).start();
        } else {
            _kontakt = _context.getResources().getString(R.string.Not_found);
            _handler.sendEmptyMessage(1);
        }
    }

    public void addContact(String name, String phone) {
        ContentValues values = new ContentValues();
        values.put(Contacts.People.NUMBER, phone);
        values.put(Contacts.People.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
        values.put(Contacts.People.LABEL, name);
        values.put(Contacts.People.NAME, name);
        Uri dataUri = _context.getContentResolver().insert(Contacts.People.CONTENT_URI, values);
        Uri updateUri = Uri.withAppendedPath(dataUri, Contacts.People.Phones.CONTENT_DIRECTORY);
        values.clear();
        values.put(Contacts.People.Phones.TYPE, Contacts.People.TYPE_MOBILE);
        values.put(Contacts.People.NUMBER, phone);
        updateUri = _context.getContentResolver().insert(updateUri, values);
    }
}