package com.collegeproject.onlinenetbanking.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.collegeproject.onlinenetbanking.Model.TransactionData;
import com.collegeproject.onlinenetbanking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder>{

    List<TransactionData> transactionData;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    Context context;

    public TransactionAdapter(List<TransactionData> transactionData) {
        this.transactionData = transactionData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_history,parent,false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseFirestore.collection("Customer").document(transactionData.get(position).getCustomerId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                   Picasso.get().load(task.getResult().getString("ProfileImgUrl")).into(holder.mProfileImg);
                }
            }
        });

        holder.mUserName.setText(transactionData.get(position).getCustomerName());
        String payment = transactionData.get(position).getPayment();

        if (payment.equals("Debit")){
            holder.mAmount.setText(transactionData.get(position).getAmount());
            holder.mAmount.setTextColor(Color.RED);
        }
        holder.mAmount.setText(transactionData.get(position).getAmount());

        Date d = new Date(transactionData.get(position).getTimeStamp());
        DateFormat dateFormat1 = new SimpleDateFormat("MMM dd , hh : mm a");
        String Date = dateFormat1.format(d.getTime());
        holder.mDate.setText(Date);
    }

    @Override
    public int getItemCount() {
        return transactionData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mUserName,mDate,mAmount;
        CircleImageView mProfileImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mUserName = itemView.findViewById(R.id.userName);
            mDate = itemView.findViewById(R.id.date);
            mProfileImg = itemView.findViewById(R.id.userProfileImg);
            mAmount = itemView.findViewById(R.id.amount);

        }
    }

}
