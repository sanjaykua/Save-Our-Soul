package com.hash.sos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hash.sos.model.User;

import java.util.HashMap;
import java.util.Map;

public class profile_detail extends AppCompatActivity {

    TextView phone;
    EditText name;
    EditText email;
    EditText address;
    EditText message;
    Button submit;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        message = findViewById(R.id.message);
        submit = findViewById(R.id.submit);

        phone.setText(mAuth.getCurrentUser().getPhoneNumber());

        mDatabase.child("user").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    email.setText(user.email);
                    name.setText(user.name);
                    message.setText(user.message);
                    address.setText(user.address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().length() > 2) {
                    if (name.getText().length() > 2) {
                        if (message.getText().length() > 2) {
                            if (address.getText().length() > 4) {
                                User user = new User(mAuth.getUid(), mAuth.getCurrentUser().getPhoneNumber(),email.getText().toString(), name.getText().toString(),message.getText().toString(),address.getText().toString() );
                                Map<String, Object> uValues = user.toMap();

                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("/user/" + mAuth.getUid(), uValues);
                                mDatabase.updateChildren(childUpdates);
                                mAuth.getCurrentUser().updateEmail(email.getText().toString());
                                startActivity(new Intent(profile_detail.this, Home.class));
                                finish();
                            } else {
                                address.setError("Invalid");
                            }
                        } else {
                            message.setError("Invalid");
                        }
                    } else {
                        name.setError("Invalid");
                    }
                } else {
                    email.setError("Invalid");
                }
            }
        });
    }
}
