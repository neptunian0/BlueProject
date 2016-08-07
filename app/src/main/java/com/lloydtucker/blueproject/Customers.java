package com.lloydtucker.blueproject;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by lloydtucker on 05/08/2016.
 */
public class Customers implements Serializable{

    private static final long serialVersionUID = 1L;
    /**
     * System-generated Id that uniquely identifies a single Customer
     * (Required)
     *
     */
    //@SerializedName("id")
    //@Expose
    private String id;
    /**
     *
     * (Required)
     *
     */
    //@SerializedName("givenName")
    //@Expose
    private String givenName;
    /**
     *
     * (Required)
     *
     */
    //@SerializedName("familyName")
    //@Expose
    private String familyName;
    /**
     *
     * (Required)
     *
     */
    //@SerializedName("address1")
    //@Expose
    private String address1;
    /**
     *
     * (Required)
     *
     */
    //@SerializedName("address2")
    //@Expose
    private String address2;
    /**
     *
     * (Required)
     *
     */
    //@SerializedName("town")
    //@Expose
    private String town;
    /**
     *
     * (Required)
     *
     */
    //@SerializedName("county")
    //@Expose
    private String county;
    /**
     *
     * (Required)
     *
     */
    //@SerializedName("postCode")
    //@Expose
    private String postCode;
    //@SerializedName("mobilePhone")
    //@Expose
    private String mobilePhone;

    /**
     * System-generated Id that uniquely identifies a single Customer
     * (Required)
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     * System-generated Id that uniquely identifies a single Customer
     * (Required)
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * (Required)
     *
     * @return
     * The givenName
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     *
     * (Required)
     *
     * @param givenName
     * The givenName
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    /**
     *
     * (Required)
     *
     * @return
     * The familyName
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     *
     * (Required)
     *
     * @param familyName
     * The familyName
     */
    public void setFamilyName(String familyName) {
        Log.d("Set", familyName);
        this.familyName = familyName;
    }

    /**
     *
     * (Required)
     *
     * @return
     * The address1
     */
    public String getAddress1() {
        return address1;
    }

    /**
     *
     * (Required)
     *
     * @param address1
     * The address1
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     *
     * (Required)
     *
     * @return
     * The address2
     */
    public String getAddress2() {
        return address2;
    }

    /**
     *
     * (Required)
     *
     * @param address2
     * The address2
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     *
     * (Required)
     *
     * @return
     * The town
     */
    public String getTown() {
        return town;
    }

    /**
     *
     * (Required)
     *
     * @param town
     * The town
     */
    public void setTown(String town) {
        this.town = town;
    }

    /**
     *
     * (Required)
     *
     * @return
     * The county
     */
    public String getCounty() {
        return county;
    }

    /**
     *
     * (Required)
     *
     * @param county
     * The county
     */
    public void setCounty(String county) {
        this.county = county;
    }

    /**
     *
     * (Required)
     *
     * @return
     * The postCode
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     *
     * (Required)
     *
     * @param postCode
     * The postCode
     */
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    /**
     *
     * @return
     * The mobilePhone
     */
    public String getMobilePhone() {
        return mobilePhone;
    }

    /**
     *
     * @param mobilePhone
     * The mobilePhone
     */
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String toString(){
        return "Name: " + givenName + " " + familyName + "\n" +
                "Town: " + town + "\n" +
                "Post Code: " + postCode;
    }
}
