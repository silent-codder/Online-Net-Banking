package com.collegeproject.onlinenetbanking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.collegeproject.onlinenetbanking.Adapter.CustomerAdapter;
import com.collegeproject.onlinenetbanking.Adapter.TemporaryCustomerAdapter;
import com.collegeproject.onlinenetbanking.Model.CustomerData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerMainActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    CircleImageView mProfileImg;
    TextView mAccountBalance,mTransaction;
    String UserId;
    RecyclerView recyclerView;

    List<CustomerData> customerData;
    CustomerAdapter customerAdapter;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        mProfileImg = findViewById(R.id.userProfileImg);
        recyclerView = findViewById(R.id.recycleView);
        mAccountBalance = findViewById(R.id.btnViewBalance);
        mTransaction = findViewById(R.id.btnTransaction);

        builder = new AlertDialog.Builder(this);

        firebaseFirestore.collection("Customer").document(UserId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String ProfileImg = task.getResult().getString("ProfileImgUrl");
                            String FullName =task.getResult().getString("FullName");
                            TextView textView = findViewById(R.id.userName);
                            textView.setText("Welcome, " + FullName );
                            if (!TextUtils.isEmpty(ProfileImg))
                                Picasso.get().load(ProfileImg).into(mProfileImg);
                        }
                    }
                });

        findViewById(R.id.userProfileImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerMainActivity.this,CustomerProfileActivity.class));
            }
        });

        findViewById(R.id.transactionHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerMainActivity.this,TransactionActivity.class));
            }
        });

        customerData = new ArrayList<>();
        customerAdapter = new CustomerAdapter(customerData);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),4));
        recyclerView.setAdapter(customerAdapter);

        firebaseFirestore.collection("Customer").whereNotEqualTo("UserId",UserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()){
                    TextView textView = findViewById(R.id.empty);
                    textView.setVisibility(View.GONE);
                }
                for (DocumentChange doc : value.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        String Id = doc.getDocument().getId();
                        CustomerData mData = doc.getDocument().toObject(CustomerData.class).withId(Id);
                        customerData.add(mData);
                        customerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        mAccountBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Customer").document(UserId)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){

                            String AccountBalance = task.getResult().getString("AccountBalance");
                            String FullName = task.getResult().getString("FullName");
                            //Uncomment the below code to Set the message and title from the strings.xml file
                            builder.setTitle(FullName + "\n" +"Your Account Balance");

                            //Setting message manually and performing action on button click
                            if (!TextUtils.isEmpty(AccountBalance)){
                                builder.setMessage(Html.fromHtml("<font color='#689F38' style='bold'>₹ "+AccountBalance+"</font>"))
                                        .setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                AlertDialog alert = builder.create();
                                                alert.cancel();
                                            }
                                        });
                                //Creating dialog box
                                AlertDialog alert = builder.create();
                                alert.show();
                            }else {
                                builder.setMessage(Html.fromHtml("<font color='#D32F2F' style='bold'>₹ 0.00</font>"))
                                        .setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                AlertDialog alert = builder.create();
                                                alert.cancel();
                                            }
                                        });
                                //Creating dialog box
                                AlertDialog alert = builder.create();
                                alert.show();
                            }

                        }
                    }
                });
            }
        });
    }
}