package za.edu.st10112216.numap.classes;

import androidx.annotation.NonNull;

public class UserDetailsClass {
    String userID;
    String username;
    String email;
    String cellphone;
    double lat;
    double lng;
    boolean unitState;

    //Default Constructor
    public UserDetailsClass() {

    }

    // User's Current Location. Constructor.
    public UserDetailsClass(double lat, double lng){
        this.lat = lat;
        this.lng = lng;

    }

    // Minimum Entries for Users. Constructor.
    public UserDetailsClass(String userID, String username){
        this.userID = userID;
        this.username = username;

    }

    // Full Entries for Users. Constructor.
    public UserDetailsClass(String userID, String username, String cellphone) {
        this.userID = userID;
        this.username = username;
        this.cellphone = cellphone;

    }

    //Getters and Setters

    public boolean isUnitState() {
        return unitState;
    }

    public void setUnitState(boolean unitState) {
        this.unitState = unitState;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @NonNull
    @Override
    public String toString() {
        return  "Email Address: " + '\n'+ email+ '\n' + '\n' +
                "Cellphone Number: " + '\n' + cellphone + '\n' + '\n' +
                "Password: ********* " + '\n' + '\n' +
                "My ID: " + '\n' + userID + '\n' + '\n';

    }

}
