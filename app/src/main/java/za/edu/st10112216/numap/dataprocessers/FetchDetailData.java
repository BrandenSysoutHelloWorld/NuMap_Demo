package za.edu.st10112216.numap.dataprocessers;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import za.edu.st10112216.numap.classes.PlaceDetailsClass;


@SuppressWarnings("deprecation")
public class FetchDetailData extends AsyncTask<Object, String, String> {

    GoogleMap mMap;
    String urlData;
    String googleNearByPlacesData;

    private DatabaseReference mDatabase;

    PlaceDetailsClass placeDetailsClass = new PlaceDetailsClass();

    String name;
    boolean open = false;
    String openCheck;
    double rating;
    String website;
    String cellphone;
    String placeID;

    @Override
  protected String doInBackground(Object... params) {
      try {
          Log.d("GetPlacesData", "doInBackground entered");
          mMap = (GoogleMap) params[0];
          urlData = (String) params[1];
          DownloadUrlData downloadUrl = new DownloadUrlData();
          googleNearByPlacesData = downloadUrl.retrieveUrl(urlData);
          Log.d("GooglePlacesReadData", "doInBackground Exit");
      } catch (Exception e) {
          Log.d("GooglePlacesReadData", e.toString());
      }
      return googleNearByPlacesData;
  }

    @Override
    protected void onPostExecute(String result) {
      Log.d("GooglePlacesReadData", "onPostExecute Entered");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        try {

          JSONObject jsonObject = new JSONObject(result);

          JSONObject getName = jsonObject.getJSONObject("result");
          name = getName.getString("name").replace(".",",");


          JSONObject getNumber = jsonObject.getJSONObject("result");
            if (getNumber.has("formatted_phone_number") && !getNumber.isNull("formatted_phone_number")){
                getNumber = jsonObject.getJSONObject("result");
                cellphone = getNumber.getString("formatted_phone_number");
            }else{
                cellphone = "No Data";
            }

            JSONObject getStatus = jsonObject.getJSONObject("result");
            if (getStatus.has("opening_hours") && !getStatus.isNull("opening_hours")) {
                getStatus = jsonObject.getJSONObject("result").getJSONObject("opening_hours");
                 open = getStatus.getBoolean("open_now");
                if (open = true) {
                    openCheck = "Open";

                } else {
                    openCheck = "Closed";

                }
            }else {
                openCheck = "No Data";
            }

            JSONObject getRating = jsonObject.getJSONObject("result");
            if (getRating.has("rating") && !getRating.isNull("rating")) {
                getRating = jsonObject.getJSONObject("result");
                rating = getRating.getDouble("rating");
            }else{
                rating = -1;
            }

            JSONObject getWebsite = jsonObject.getJSONObject("result");
            if (getWebsite.has("website") && !getWebsite.isNull("website")) {
                getWebsite = jsonObject.getJSONObject("result");
                website = getWebsite.getString("website");
            }else{
                website = "No Data";
            }

            JSONObject getID = jsonObject.getJSONObject("result");
            placeID = getID.getString("place_id");

            placeDetailsClass.setName(name);
            placeDetailsClass.setOpenCheck(openCheck);
            placeDetailsClass.setRatings(rating);
            placeDetailsClass.setCellphone(cellphone);
            placeDetailsClass.setWebsite(website);
            placeDetailsClass.setPlaceID(placeID);

          writeLocationDetailFull(placeDetailsClass.getName(),placeDetailsClass.getOpenCheck(),placeDetailsClass.getRatings(),placeDetailsClass.getCellphone(),placeDetailsClass.getWebsite(),placeDetailsClass.getPlaceID());

            Log.d("GooglePlacesReadData", placeDetailsClass.toString());
      } catch (JSONException e) {
          e.printStackTrace();
      }
  }

    // Writes Current Location to Detail Class
    public void writeLocationDetailFull(String name, String openCheck, double ratings, String cellphone, String website, String placeID) {

        placeDetailsClass = new PlaceDetailsClass(name,openCheck,ratings,cellphone,website,placeID);

        mDatabase.child("tmp").child(name).setValue(placeDetailsClass);
    }

}
