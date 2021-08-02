package com.collegeproject.onlinenetbanking.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.collegeproject.onlinenetbanking.EditCustomerDataActivity;
import com.collegeproject.onlinenetbanking.Model.CustomerData;
import com.collegeproject.onlinenetbanking.R;
import com.collegeproject.onlinenetbanking.TransactionActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminCustomerAdapter extends RecyclerView.Adapter<AdminCustomerAdapter.ViewHolder> {

    List<CustomerData> customerData;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    Context context;
    public AdminCustomerAdapter(List<CustomerData> customerData) {
        this.customerData = customerData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_people_view,parent,false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String FullName = customerData.get(position).getFullName();
        String customerAccountBalance = customerData.get(position).getAccountBalance();
        String Mobile = customerData.get(position).getMobile();
        String ProfileImg = customerData.get(position).getProfileImgUrl();

        holder.mUserName.setText(FullName);
        if (!TextUtils.isEmpty(ProfileImg))
            Picasso.get().load(ProfileImg).into(holder.mProfileImg);



        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                notifyItemChanged(position);
                notifyDataSetChanged();
                OpenDialog(FullName,Mobile,ProfileImg,customerAccountBalance,position);

            }
        });
    }

    private void OpenDialog(String fullName, String mobile, String profileImg, String customerAccountBalance, int position) {

        notifyDataSetChanged();
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.customer_view_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        CircleImageView userImg = dialog.findViewById(R.id.userProfileImg);
        TextView userName = dialog.findViewById(R.id.userName);
        TextView Mobile = dialog.findViewById(R.id.mobile);
        TextView balance = dialog.findViewById(R.id.accountBalance);
        Button btnEdit = dialog.findViewById(R.id.btnViewDetails);
        Button btnTransaction = dialog.findViewById(R.id.btnTransaction);
        Button btnAddAmount = dialog.findViewById(R.id.btnAddAmount);


        userName.setText(fullName);
        Mobile.setText(mobile);
        balance.setText("Account Balance : ₹" + customerAccountBalance);
        Picasso.get().load(profileImg).into(userImg);

        //View customer transaction history
        btnTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) context;
                Intent intent = new Intent(context, TransactionActivity.class);
                intent.putExtra("UserId",customerData.get(position).getUserId());
                activity.startActivity(intent);
            }
        });

        //Edit customer data
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) context;
                Intent intent = new Intent(context, EditCustomerDataActivity.class);
                intent.putExtra("UserId",customerData.get(position).getUserId());
                activity.startActivity(intent);
            }
        });
        btnAddAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Dialog dialog1 = new Dialog(v.getContext());
                dialog1.setContentView(R.layout.add_money_dialog);
                dialog1.setCanceledOnTouchOutside(true);
                dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog1.show();

                CircleImageView userImg = dialog1.findViewById(R.id.userProfileImg);
                TextView userName = dialog1.findViewById(R.id.userName);
                TextView Mobile = dialog1.findViewById(R.id.mobile);
                EditText amount = dialog1.findViewById(R.id.amount);
                Button btnSend = dialog1.findViewById(R.id.btnSend);

                Picasso.get().load(profileImg).into(userImg);
                userName.setText(fullName);
                Mobile.setText(mobile);

                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long addAmount = Long.parseLong(amount.getText().toString());
                        long availableBalance = Long.parseLong(customerAccountBalance);

                        if (addAmount>0){

                            availableBalance += addAmount;

                            firebaseFirestore.collection("Customer").document(customerData.get(position).getUserId())
                                    .update("AccountBalance",String.valueOf(availableBalance)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(context, "Add amount " + addAmount, Toast.LENGTH_SHORT).show();
                                        HashMap<String,Object> map = new HashMap<>();
                                        map.put("Amount","+ ₹"+addAmount);
                                        map.put("TimeStamp",System.currentTimeMillis());
                                        map.put("UserId",firebaseAuth.getCurrentUser().getUid());
                                        map.put("CustomerId",customerData.get(position).CustId);
                                        map.put("CustomerName","Online Net Banking Manager");
                                        map.put("Payment","Credit");

                                        firebaseFirestore.collection("Customer").document(customerData.get(position).getUserId())
                                                .collection("Transaction").add(map);
                                        notifyItemChanged(position);
                                        dialog1.dismiss();
                                        dialog.dismiss();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Fail to debit amount", Toast.LENGTH_SHORT).show();
                                    dialog1.dismiss();
                                    dialog.dismiss();
                                }
                            });

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
