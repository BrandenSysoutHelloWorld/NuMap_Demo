package za.edu.st10112216.numap.dataprocessers;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("deprecation")
public class FetchData extends AsyncTask<Object, String, String> {

    GoogleMap mMap;
    String url;
    String googleNearByPlacesData;

    String lat;
    String lng;
    String name;
    String vin;
    String id;
    String status = "Information Not Available";
    String rating;

  @Override
  protected String doInBackground(Object... params) {
      try {
          Log.d("GetNearbyPlacesData", "doInBackground entered");
          mMap = (GoogleMap) params[0];
          url = (String) params[1];
          DownloadUrl downloadUrl = new DownloadUrl();
          googleNearByPlacesData = downloadUrl.retrieveUrl(url);
          Log.d("GooglePlacesReadTask", "doInBackground Exit");
      } catch (Exception e) {
          Log.d("GooglePlacesReadTask", e.toString());
      }
      return googleNearByPlacesData;
  }


    @Override
    protected void onPostExecute(String result) {
      Log.d("GooglePlacesReadTask", "onPostExecute Entered");

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                JSONObject getLocation = jsonObject1.getJSONObject("geometry")
                        .getJSONObject("location");

                lat = getLocation.getString("lat");
                lng = getLocation.getString("lng");

                JSONObject getName = jsonArray.getJSONObject(i);
                name = getName.getString("name");

                JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                if (jsonObject2.has("opening_hours") && !jsonObject2.isNull("opening_hours")) {
                    JSONObject getStatus = jsonObject2.getJSONObject("opening_hours");
                    boolean status1 = getStatus.getBoolean("open_now");
                    if (status1 = true) {
                        status = "Open";
                    } else {
                        status = "Closed";
                    }
                }else{
                    status = "No Data";
                }

                JSONObject getVin = jsonArray.getJSONObject(i);
                vin = getVin.getString("vicinity");

                JSONObject getId = jsonArray.getJSONObject(i);
                id = getId.getString("place_id");

                JSONObject getRating = jsonArray.getJSONObject(i);
                if (getRating.has("rating") && !getRating.isNull("rating")) {
                    rating = getRating.getString("rating");
                }else{
                    rating = "No Data";
                }

                LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.snippet('\n' + name + '\n' +
                        "Currently " + status + '\n' +
                        "Vicinity " + vin + '\n' +
                        "Rating " + rating + "/5");
                markerOptions.title(id);
                mMap.addMarker(markerOptions);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }


      } catch (JSONException e) {
          e.printStackTrace();
      }
  }
}
