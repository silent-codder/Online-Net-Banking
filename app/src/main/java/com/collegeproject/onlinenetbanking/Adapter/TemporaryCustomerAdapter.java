package com.collegeproject.onlinenetbanking.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.collegeproject.onlinenetbanking.Model.CustomerData;
import com.collegeproject.onlinenetbanking.R;
import com.collegeproject.onlinenetbanking.TransactionActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TemporaryCustomerAdapter extends RecyclerView.Adapter<TemporaryCustomerAdapter.ViewHolder> {

    List<CustomerData> customerData;
    Context context;
    public TemporaryCustomerAdapter(List<CustomerData> customerData) {
        this.customerData = customerData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_view,parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mUserName.setText(customerData.get(position).getFullName());
        holder.mAccountBalance.setText("Account Balance : ₹ " +customerData.get(position).getAccountBalance());
        String ProfileImg = customerData.get(position).getProfileImgUrl();
        if (!TextUtils.isEmpty(ProfileImg))
            Picasso.get().load(ProfileImg).into(holder.mProfileImg);
        holder.mBtnViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.mBtnTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Intent intent = new Intent(context, TransactionActivity.class);
                intent.putExtra("UserId",customerData.get(position).getUserId());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return customerData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mUserName,mAccountBalance,mBtnViewDetails;
        Button mBtnEdit,mBtnDelete,mBtnTransaction;
        CircleImageView mProfileImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mUserName = itemView.findViewById(R.id.userName);
            mAccountBalance = itemView.findViewById(R.id.accountBalance);
            mBtnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            mBtnEdit = itemView.findViewById(R.id.btnEdit);
            mBtnDelete = itemView.findViewById(R.id.btnDelete);
            mBtnTransaction = itemView.findViewById(R.id.btnTransaction);
            mProfileImg = itemView.findViewById(R.id.userProfileImg);

        }
    }
}
