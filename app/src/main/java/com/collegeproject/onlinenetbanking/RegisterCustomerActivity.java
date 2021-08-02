package com.collegeproject.onlinenetbanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class RegisterCustomerActivity extends AppCompatActivity {

    EditText mFullName,mBirthDate,mOccupation,mAddress,mCity,mState,mEmail,mMobile,mPassword;
    RadioGroup mGender,mMarital;
    Button mBtnSubmit;
    ProgressBar progressBar;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    Calendar calendar;
    String Marital,Gender;
    int day,month,year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);

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
        mPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.loader);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        // Birth date picker
        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        mBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterCustomerActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                String Password = mPassword.getText().toString();

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
                }else if (TextUtils.isEmpty(Password)){
                    mPassword.setError("Empty field");
                }else {
                    AddCustomer(FullName,Email,BirthDate,Mobile,Address,City,State,Gender,Marital,Occupation,Password);
                }
            }
        });

    }

    private void AddCustomer(String fullName, String email, String birthDate, String mobile, String address, String city, String state, String gender, String marital, String occupation, String password) {

        progressBar.setVisibility(View.VISIBLE);
        mBtnSubmit.setVisibility(View.GONE);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("FullName",fullName);
                            map.put("Email",email);
                            map.put("BirthDate",birthDate);
                            map.put("Mobile","+91"+mobile);
                            map.put("Address",address);
                            map.put("City",city);
                            map.put("State",state);
                            map.put("Occupation",occupation);
                            map.put("Flag","Customer");
                            map.put("TimeStamp",System.currentTimeMillis());
                            map.put("Gender",gender);
                            map.put("Marital",marital);
                            map.put("Password",password);
                            map.put("UserId",firebaseAuth.getCurrentUser().getUid());

                            firebaseFirestore.collection("Customer").document(firebaseAuth.getCurrentUser().getUid()).set(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                mFullName.setText("");
                                                mBirthDate.setText("");
                                                mOccupation.setText("");
                                                mAddress.setText("");
                                                mCity.setText("");
                                                mState.setText("");
                                                mEmail.setText("");
                                                mMobile.setText("");
                                                mPassword.setText("");
                                                progressBar.setVisibility(View.GONE);
                                                mBtnSubmit.setVisibility(View.VISIBLE);

                                                startActivity(new Intent(RegisterCustomerActivity.this,CustomerMainActivity.class));
                                                Toast.makeText(RegisterCustomerActivity.this, "Customer Add Successfully..", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterCustomerActivity.this, "Server error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                mBtnSubmit.setVisibility(View.VISIBLE);
                Log.d(TAG, "onFailure: " + e.getMessage());
                Toast.makeText(RegisterCustomerActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        firebaseAuth.signInWithEmailAndPassword(email,password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()){
//                            HashMap<String,Object> map = new HashMap<>();
//                            map.put("FullName",fullName);
//                            map.put("Email",email);
//                            map.put("BirthDate",birthDate);
//                            map.put("Mobile","+91"+mobile);
//                            map.put("Address",address);
//                            map.put("City",city);
//                            map.put("State",state);
//                            map.put("Occupation",occupation);
//                            map.put("TimeStamp",System.currentTimeMillis());
//                            map.put("Gender",gender);
//                            map.put("Marital",marital);
//                            map.put("Password",password);
//
//                            firebaseFirestore.collection("Customer").document(firebaseAuth.getCurrentUser().getUid()).set(map)
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()){
//                                                mFullName.setText("");
//                                                mBirthDate.setText("");
//                                                mOccupation.setText("");
//                                                mAddress.setText("");
//                                                mCity.setText("");
//                                                mState.setText("");
//                                                mEmail.setText("");
//                                                mMobile.setText("");
//                                                mPassword.setText("");
//                                                progressBar.setVisibility(View.GONE);
//                                                mBtnSubmit.setVisibility(View.VISIBLE);
//
//                                                Toast.makeText(RegisterCustomerActivity.this, "Customer Add Successfully..", Toast.LENGTH_LONG).show();
//                                            }
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(RegisterCustomerActivity.this, "Server error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                progressBar.setVisibility(View.GONE);
//                mBtnSubmit.setVisibility(View.VISIBLE);
//                Toast.makeText(RegisterCustomerActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });



    }

}