package com.collegeproject.onlinenetbanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

public class EditCustomerDataActivity extends AppCompatActivity {

    EditText mFullName,mBirthDate,mOccupation,mAddress,mCity,mState,mEmail,mMobile;
    RadioGroup mGender,mMarital;
    Button mBtnSubmit;
    ProgressBar progressBar;
    ProgressDialog progressDialog;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    Calendar calendar;
    String Marital,Gender,UserId;
    int day,month,year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer_data);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("fetching data...");
        progressDialog.show();

        Intent intent = getIntent();

        if (intent!=null){
            UserId = intent.getStringExtra("UserId");
        }else {
            UserId = firebaseAuth.getCurrentUser().getUid();
        }


        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.email);
        mOccupation = findViewById(R.id.occupation);
        mBirthDate = findViewById(R.id.birthDate);
        mMobile = findViewById(R.id.mobile);
        mAddress = findViewById(R.id.address);
        mCity = findViewById(R.id.city);
        mState = findViewById(R.id.state);
        mBtnSubmit = findViewById(R.id.btnSubmit);
        mGender = findViewById(R.id.gender);
        mMarital = findViewById(R.id.marital);
        progressBar = findViewById(R.id.loader);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Customer").document(UserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    mFullName.setText(task.getResult().getString("FullName"));
                    mEmail.setText(task.getResult().getString("Email"));
                    mOccupation.setText(task.getResult().getString("Occupation"));
                    mBirthDate.setText(task.getResult().getString("BirthDate"));
                    mMobile.setText(task.getResult().getString("Mobile"));
                    mAddress.setText(task.getResult().getString("Address"));
                    mCity.setText(task.getResult().getString("City"));
                    mState.setText(task.getResult().getString("State"));
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditCustomerDataActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }
        });


        // Birth date picker
        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        mBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditCustomerDataActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        month += 1;
                        mBirthDate.setText(day + "-" + month + "-" + year);

                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        mGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId=group.getCheckedRadioButtonId();
                RadioButton radioButton =(RadioButton)findViewById(selectedId);
                Gender = radioButton.getText().toString();
            }
        });

        mMarital.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId=group.getCheckedRadioButtonId();
                RadioButton radioButton =(RadioButton)findViewById(selectedId);
                Marital = radioButton.getText().toString();
            }
        });

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FullName = mFullName.getText().toString();
                String Email = mEmail.getText().toString();
                String BirthDate = mBirthDate.getText().toString();
                String Mobile = mMobile.getText().toString();
                String Address = mAddress.getText().toString();
                String City = mCity.getText().toString();
                String State = mState.getText().toString();
                String Occupation = mOccupation.getText().toString();

                if (TextUtils.isEmpty(FullName)){
                    mFullName.setError("Empty field");
                }else if (TextUtils.isEmpty(Email)){
                    mEmail.setError("Empty field");
                }else if (TextUtils.isEmpty(BirthDate)){
                    mBirthDate.setError("Empty field");
                }else if (TextUtils.isEmpty(Occupation)){
                    mOccupation.setError("Empty field");
                } else if (TextUtils.isEmpty(Mobile)){
                    mMobile.setError("Empty field");
                }else if (TextUtils.isEmpty(Address)){
                    mAddress.setError("Empty field");
                }else if (TextUtils.isEmpty(City)){
                    mCity.setError("Empty field");
                }else if (TextUtils.isEmpty(State)){
                    mState.setError("Empty field");
                }else {
                    AddCustomer(FullName,Email,BirthDate,Mobile,Address,City,State,Gender,Marital,Occupation);
                }
            }
        });

    }

    private void AddCustomer(String fullName, String email, String birthDate, String mobile, String address, String city, String state, String gender, String marital, String occupation) {

        progressBar.setVisibility(View.VISIBLE);
        mBtnSubmit.setVisibility(View.GONE);

        HashMap<String,Object> map = new HashMap<>();
        map.put("FullName",fullName);
        map.put("Email",email);
        map.put("BirthDate",birthDate);
        map.put("Mobile","+91"+mobile);
        map.put("Address",address);
        map.put("City",city);
        map.put("State",state);
        map.put("Occupation",occupation);
        map.put("TimeStamp",System.currentTimeMillis());
        map.put("Gender",gender);
        map.put("Marital",marital);

        firebaseFirestore.collection("Customer").document(UserId).update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            mBtnSubmit.setVisibility(View.VISIBLE);

                            Toast.makeText(getApplicationContext(), "Update details Successfully..", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Server error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}