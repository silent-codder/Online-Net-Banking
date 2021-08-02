package com.collegeproject.onlinenetbanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterAdminActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    EditText mFullName,mEmail,mPassword,mMobile,mAddress,mCity,mState;
    Button mBtnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_admin);

        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mMobile = findViewById(R.id.mobile);
        mAddress = findViewById(R.id.address);
        mCity = findViewById(R.id.city);
        mState = findViewById(R.id.state);
        mBtnSubmit = findViewById(R.id.btnSubmit);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FullName = mFullName.getText().toString();
                String Email = mEmail.getText().toString();
                String Password = mPassword.getText().toString();
                String Mobile = mMobile.getText().toString();
                String Address = mAddress.getText().toString();
                String City = mCity.getText().toString();
                String State = mState.getText().toString();

                if (TextUtils.isEmpty(FullName)){
                    mFullName.setError("Empty field");
                }else if (TextUtils.isEmpty(Email)){
                    mEmail.setError("Empty field");
                }else if (TextUtils.isEmpty(Password)){
                    mPassword.setError("Empty field");
                }else if (TextUtils.isEmpty(Mobile)){
                    mMobile.setError("Empty field");
                }else if (TextUtils.isEmpty(Address)){
                    mAddress.setError("Empty field");
                }else if (TextUtils.isEmpty(City)){
                    mCity.setError("Empty field");
                }else if (TextUtils.isEmpty(State)){
                    mState.setError("Empty field");
                }else {
                    Register(FullName,Email,Password,Mobile,Address,City,State);
                }
            }
        });
    }

    private void Register(String fullName, String email, String password, String mobile, String address, String city, String state) {

        ProgressBar progressDialog = findViewById(R.id.loader);
        progressDialog.setVisibility(View.VISIBLE);
        mBtnSubmit.setVisibility(View.GONE);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("FullName",fullName);
                            map.put("Email",email);
                            map.put("Password",password);
                            map.put("Mobile",mobile);
                            map.put("Address",address);
                            map.put("City",city);
                            map.put("State",state);
                            map.put("Flag","Admin");
                            map.put("TimeStamp",System.currentTimeMillis());

                            firebaseFirestore.collection("Admin").document(firebaseAuth.getCurrentUser().getUid())
                                    .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterAdminActivity.this, "Registration Successfully..", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterAdminActivity.this,MainActivity.class));
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterAdminActivity.this, "Form Submit error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterAdminActivity.this, "Register Fail error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}