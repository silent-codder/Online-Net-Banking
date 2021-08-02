package com.collegeproject.onlinenetbanking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.collegeproject.onlinenetbanking.Adapter.AdminCustomerAdapter;
import com.collegeproject.onlinenetbanking.Model.CustomerData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
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

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    CircleImageView mProfileImg;
    String UserId;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    List<CustomerData> customerData;
    AdminCustomerAdapter adminCustomerAdapter;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        mProfileImg = findViewById(R.id.userProfileImg);
        recyclerView = findViewById(R.id.recycleView);
        swipeRefreshLayout = findViewById(R.id.refresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                customerData = new ArrayList<>();
                adminCustomerAdapter = new AdminCustomerAdapter(customerData);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),4));
                recyclerView.setAdapter(adminCustomerAdapter);

                firebaseFirestore.collection("Customer").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                adminCustomerAdapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    }
                });
            }
        });

        builder = new AlertDialog.Builder(this);

        firebaseFirestore.collection("Admin").document(UserId).get()
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
                startActivity(new Intent(MainActivity.this,AdminProfileActivity.class));
            }
        });

        customerData = new ArrayList<>();
        adminCustomerAdapter = new AdminCustomerAdapter(customerData);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),4));
        recyclerView.setAdapter(adminCustomerAdapter);

        firebaseFirestore.collection("Customer").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        adminCustomerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }

}