package com.collegeproject.onlinenetbanking;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.collegeproject.onlinenetbanking.Adapter.CustomerAdapter;
import com.collegeproject.onlinenetbanking.Adapter.TransactionAdapter;
import com.collegeproject.onlinenetbanking.Model.CustomerData;
import com.collegeproject.onlinenetbanking.Model.TransactionData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    List<TransactionData> transactionData;
    TransactionAdapter transactionAdapter;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();



        Intent intent = getIntent();

        if (intent.hasExtra("UserId")){
            UserId = intent.getStringExtra("UserId");
        }else {
            UserId = firebaseAuth.getCurrentUser().getUid();
        }

        recyclerView = findViewById(R.id.recycleView);

        transactionData = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(transactionAdapter);



        firebaseFirestore.collection("Customer").document(UserId).collection("Transaction")
                .orderBy("TimeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()){
                    TextView textView = findViewById(R.id.empty);
                    textView.setVisibility(View.GONE);
                }
                for (DocumentChange doc : value.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){

                        TransactionData mData = doc.getDocument().toObject(TransactionData.class);
                        transactionData.add(mData);
                        transactionAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }
}