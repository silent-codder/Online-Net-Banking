package com.collegeproject.onlinenetbanking.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class CustId {
    @Exclude
    public String CustId;
    public <T extends CustId> T withId(@NonNull final String id){
        this.CustId = id;
        return (T)this;
    }
}
