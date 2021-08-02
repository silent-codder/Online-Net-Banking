package com.collegeproject.onlinenetbanking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerProfileActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String UserId;
    EditText mFullName,mMobile,mEmail,mPassword;
    Button mBtnUpdate;
    CircleImageView mProfileImg;
    ImageView mEditProfile;

    ProgressDialog progressDialog;
    ProgressBar progressBar;
    Uri profileImgUri;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();

        mFullName = findViewById(R.id.fullName);
        mMobile = findViewById(R.id.mobile);
        mBtnUpdate = findViewById(R.id.btnUpdate);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mProfileImg = findViewById(R.id.userProfileImg);
        mEditProfile = findViewById(R.id.editProfile);

        progressBar = findViewById(R.id.loader);
        progressDialog = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();

        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImg();
            }
        });

        firebaseFirestore.collection("Customer").document(UserId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            mFullName.setText(task.getResult().getString("FullName"));
                            mMobile.setText(task.getResult().getString("Mobile"));
                            mEmail.setText(task.getResult().getString("Email"));
                            mPassword.setText(task.getResult().getString("Password"));
                            String ProfileImg = task.getResult().getString("ProfileImgUrl");
                            if (!TextUtils.isEmpty(ProfileImg))
                                Picasso.get().load(ProfileImg).into(mProfileImg);
                        }
                    }
                });

        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(CustomerProfileActivity.this,WelcomeActivity.class));
                finish();
                Toast.makeText(CustomerProfileActivity.this, "Logout successfully..", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FullName = mFullName.getText().toString();
                String Mobile = mMobile.getText().toString();

                if (TextUtils.isEmpty(FullName)){
                    mFullName.setError("Empty field");
                }else if (TextUtils.isEmpty(Mobile)){
                    mMobile.setError("Empty field");
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    mBtnUpdate.setVisibility(View.GONE);
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("FullName",FullName);
                    map.put("Mobile",Mobile);

                    firebaseFirestore.collection("Customer").document(UserId)
                            .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                mBtnUpdate.setVisibility(View.VISIBLE);
                                startActivity(new Intent(CustomerProfileActivity.this,CustomerMainActivity.class));
                                Toast.makeText(CustomerProfileActivity.this, "Personal details updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            mBtnUpdate.setVisibility(View.VISIBLE);
                            Toast.makeText(CustomerProfileActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void UploadImg() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setOutputCompressQuality(40)
                .setAspectRatio(1,1)
                .start(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profileImgUri = result.getUri();
                mProfileImg.setImageURI(profileImgUri);
                progressDialog.dismiss();
                AddImg();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void AddImg() {

        progressDialog.setMessage("Uploading Image...");
        progressDialog.show();
        StorageReference profileImgPath = storageReference.child("Profile").child(System.currentTimeMillis() + ".jpg");

        profileImgPath.putFile(profileImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profileImgPath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        String ProfileUri = task.getResult().toString();
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("ProfileImgUrl" , ProfileUri);

                        firebaseFirestore.collection("Customer").document(UserId).update(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(CustomerProfileActivity.this,CustomerMainActivity.class));
                                        Toast.makeText(getApplicationContext(), "Upload Successfully..", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Storage error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}