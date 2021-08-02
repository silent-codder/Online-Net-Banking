package com.collegeproject.onlinenetbanking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class CustomerLoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    EditText mEmail,mPassword;
    Button mBtnLogin,mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        //Initialize firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Find ids
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mBtnLogin = findViewById(R.id.btnLogin);
        mBtnRegister = findViewById(R.id.btnRegister);

        //get action login btn
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = mEmail.getText().toString();
                String Password = mPassword.getText().toString();

                // check admin already register or not
                firebaseFirestore.collection("Customer").whereEqualTo("Email",Email)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (!value.isEmpty()){

                                    //if email address found in database then login fuc call
                                    Login(Email,Password);
                                }else {
                                    Toast.makeText(CustomerLoginActivity.this, "This email address not register", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerLoginActivity.this,RegisterCustomerActivity.class));
            }
        });

    }

    private void Login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(CustomerLoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CustomerLoginActivity.this,CustomerMainActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CustomerLoginActivity.this, "Login Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}