package com.collegeproject.onlinenetbanking.Model;

public class CustomerData extends CustId{
    String Address;
    String BirthDate;
    String City;
    String State;
    String Email;
    String FullName;
    String Gender;
    String Marital;
    String Mobile;
    String Occupation;
    String ProfileImgUrl;
    Long TimeStamp;
    String AccountBalance;
    String UserId;

    public CustomerData() {
    }

    public CustomerData(String address, String birthDate, String city, String state, String email, String fullName, String gender, String marital, String mobile, String occupation, String profileImgUrl, Long timeStamp, String accountBalance, String userId) {
        Address = address;
        BirthDate = birthDate;
        City = city;
        State = state;
        Email = email;
        FullName = fullName;
        Gender = gender;
        Marital = marital;
        Mobile = mobile;
        Occupation = occupation;
        ProfileImgUrl = profileImgUrl;
        TimeStamp = timeStamp;
        AccountBalance = accountBalance;
        UserId = userId;
    }

    public String getAccountBalance() {
        return AccountBalance;
    }

    public void setAccountBalance(String accountBalance) {
        AccountBalance = accountBalance;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String birthDate) {
        BirthDate = birthDate;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getMarital() {
        return Marital;
    }

    public void setMarital(String marital) {
        Marital = marital;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getOccupation() {
        return Occupation;
    }

    public void setOccupation(String occupation) {
        Occupation = occupation;
    }

    public String getProfileImgUrl() {
        return ProfileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        ProfileImgUrl = profileImgUrl;
    }

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        TimeStamp = timeStamp;
    }
}
