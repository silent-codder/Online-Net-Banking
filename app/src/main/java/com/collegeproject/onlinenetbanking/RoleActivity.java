package com.collegeproject.onlinenetbanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;

public class RoleActivity extends AppCompatActivity {

    AlertDialog.Builder builder;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        builder = new AlertDialog.Builder(this);
        progressDialog = new ProgressDialog(this);

        findViewById(R.id.btnAdmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RoleActivity.this,AdminLoginActivity.class));
                finish();
            }
        });

        findViewById(R.id.btnCustomer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RoleActivity.this,CustomerLoginActivity.class));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        ConnectivityManager ConnectionManager=(ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true )
        {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user!=null){

                progressDialog.setMessage("Fetching data..");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                findViewById(R.id.btnAdmin).setVisibility(View.GONE);
                findViewById(R.id.btnCustomer).setVisibility(View.GONE);
                findViewById(R.id.text).setVisibility(View.GONE);

                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                firebaseFirestore.collection("Customer").document(firebaseAuth.getCurrentUser().getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String flag = task.getResult().getString("Flag");
                            if (!TextUtils.isEmpty(flag)){
                                if (flag.equals("Customer")){
                                    startActivity(new Intent(RoleActivity.this,CustomerMainActivity.class));
                                    progressDialog.dismiss();
                                }
                            }
                        }
                    }
                });

                firebaseFirestore.collection("Admin").document(firebaseAuth.getCurrentUser().getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String flag = task.getResult().getString("Flag");
                            if (!TextUtils.isEmpty(flag)){
                                if (flag.equals("Admin")){
                                    startActivity(new Intent(RoleActivity.this,MainActivity.class));
                                    progressDialog.dismiss();
                                }
                            }

                        }
                    }
                });

            }


        }
        else
        {

            //Uncomment the below code to Set the message and title from the strings.xml file
            builder.setMessage("") .setTitle("No Internet");

            //Setting message manually and performing action on button click
            builder.setMessage("No internet connection !!")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            alert.show();

        }
    }
}