package com.hash.sos;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hash.sos.model.Contact;
import com.hash.sos.viewholder.ContactViewHolder;

import java.util.HashMap;
import java.util.Map;

public class AddContect extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    private static final int PICK_CONTACT = 1;

    Uri contactData;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    RecyclerView contacts;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<Contact, ContactViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contect);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        contacts = findViewById(R.id.recycler_view);

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        contacts.setLayoutManager(mManager);
        contacts.setItemAnimator(new DefaultItemAnimator());
        contacts.setHasFixedSize(true);
        setUpMember();
    }

    public void setUpMember() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("contact").child(mAuth.getUid());

        FirebaseRecyclerOptions<Contact> options =
                new FirebaseRecyclerOptions.Builder<Contact>()
                        .setQuery(query, new SnapshotParser<Contact>() {
                            @NonNull
                            @Override
                            public Contact parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Contact(snapshot.child("cid").getValue().toString(),
                                        snapshot.child("name").getValue().toString(),
                                        snapshot.child("number").getValue().toString());
                            }
                        })
                        .build();

        mAdapter = new FirebaseRecyclerAdapter<Contact, ContactViewHolder>(options) {
            @Override
            public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);

                return new ContactViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ContactViewHolder holder, final int position, final Contact model) {
                holder.name.setText(model.name);
                holder.number.setText(model.number);

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference().child("contact").child(mAuth.getUid()).child(model.cid).removeValue();
                    }
                });
            }

        };
        contacts.setAdapter(mAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_pick_contact) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    contactData = data.getData();
                    Log.e("gfjdgjfgs", contactData.toString());
                    setPermissionsRequestReadContacts();
                }
                break;
        }
    }

    private void setPermissionsRequestReadContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            addPhoneNumber();
        }
    }

    private void addPhoneNumber() {
        Cursor c = getContentResolver().query(contactData, null, null, null, null);
        if (c.moveToFirst()) {
            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            String number = null;
            if (phones.moveToFirst()) {
                number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            phones.close();
            Log.e("Contact", name + "   " + number);
            Log.e("Contact", name);

            String key = mDatabase.child("contact").child(mAuth.getUid()).push().getKey();
            Contact contact = new Contact(key, mAuth.getUid(), name, number);
            Map<String, Object> cValues = contact.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/contact/" + mAuth.getUid() + "/" + key, cValues);
            mDatabase.updateChildren(childUpdates);

        }
        c.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addPhoneNumber();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Add failed, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

   /* private void sendSms() {
        SmsManager smsManager = SmsManager.getDefault();
        String message = "Help";
        for (Contact contact: dbHandler.getContacts()) {
            Toast.makeText(getApplicationContext(), "Sending to " + contact.number + "...",
                    Toast.LENGTH_SHORT).show();

            smsManager.sendTextMessage(contact.number, null, message, null, null);

            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        }
    }*/
}
