package com.collegeproject.onlinenetbanking.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.collegeproject.onlinenetbanking.Model.CustomerData;
import com.collegeproject.onlinenetbanking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    List<CustomerData> customerData;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    long customerBalance;
    String UserId,AccountBalance,ProfileImg,customerAccountBalance,UserName;
    Context context;
    public CustomerAdapter(List<CustomerData> customerData) {
        this.customerData = customerData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_view,parent,false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        customerAccountBalance = customerData.get(position).getAccountBalance();
        holder.mUserName.setText(customerData.get(position).getFullName());
        ProfileImg = customerData.get(position).getProfileImgUrl();
        if (!TextUtils.isEmpty(ProfileImg))
            Picasso.get().load(ProfileImg).into(holder.mProfileImg);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                firebaseFirestore.collection("Customer").document(UserId)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            AccountBalance = task.getResult().getString("AccountBalance");
                            UserName = task.getResult().getString("FullName");
                        }
                    }
                });

                Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.transfer_money_dialog);
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                CircleImageView userImg = dialog.findViewById(R.id.userProfileImg);
                TextView userName = dialog.findViewById(R.id.userName);
                TextView mobile = dialog.findViewById(R.id.mobile);
                EditText amount = dialog.findViewById(R.id.amount);
                Button btnSend = dialog.findViewById(R.id.btnSend);

                if (!TextUtils.isEmpty(ProfileImg))
                Picasso.get().load(ProfileImg).into(userImg);

                userName.setText("Paying " +customerData.get(position).getFullName());
                mobile.setText(customerData.get(position).getMobile());

                if (!TextUtils.isEmpty(customerAccountBalance)){
                    customerBalance = Long.parseLong(customerAccountBalance);
                }else {
                    customerBalance = 0;
                }

                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long sendAmount = Long.parseLong(amount.getText().toString());
                        long availableBalance = Long.parseLong(AccountBalance);
                        if (sendAmount<=availableBalance){
                            customerBalance += sendAmount;
                            availableBalance -= sendAmount;
                            firebaseFirestore.collection("Customer").document(UserId)
                                    .update("AccountBalance",String.valueOf(availableBalance)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(context, "Debit amount " + sendAmount, Toast.LENGTH_SHORT).show();
                                        HashMap<String,Object> map = new HashMap<>();
                                        map.put("Amount","- ₹"+sendAmount);
                                        map.put("TimeStamp",System.currentTimeMillis());
                                        map.put("UserId",UserId);
                                        map.put("CustomerId",customerData.get(position).CustId);
                                        map.put("CustomerName",customerData.get(position).getFullName());
                                        map.put("Payment","Debit");

                                        firebaseFirestore.collection("Customer").document(UserId)
                                                .collection("Transaction").add(map);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Fail to debit amount", Toast.LENGTH_SHORT).show();
                                }
                            });

                            firebaseFirestore.collection("Customer").document(customerData.get(position).CustId)
                                    .update("AccountBalance",String.valueOf(customerBalance))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                HashMap<String,Object> map = new HashMap<>();
                                                map.put("Amount","+ ₹"+sendAmount);
                                                map.put("TimeStamp",System.currentTimeMillis());
                                                map.put("UserId",customerData.get(position).CustId);
                                                map.put("CustomerId",UserId);
                                                map.put("CustomerName",UserName);
                                                map.put("Payment","Credit");

                                                firebaseFirestore.collection("Customer").document(customerData.get(position).CustId)
                                                        .collection("Transaction").add(map);

                                                Toast.makeText(context, "Payment Successfully", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Payment Fail", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        }else {
                            Toast.makeText(context, "Insufficient balance", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
        });
    }

    @Override
    public int getItemCount() {
        return customerData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mUserName;
        CircleImageView mProfileImg;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mUserName = itemView.findViewById(R.id.userName);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            mProfileImg = itemView.findViewById(R.id.userProfileImg);

        }
    }
}
