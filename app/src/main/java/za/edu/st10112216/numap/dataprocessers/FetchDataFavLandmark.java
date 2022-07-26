package za.edu.st10112216.numap.dataprocessers;

import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("deprecation")
public class FetchDataFavLandmark  extends AsyncTask<Object, String, String> {

    GoogleMap mMap;
    String url;
    String googleNearByPlacesData;

    String lat;
    String lng;
    double elat;
    double elng;
    String name;
    String id;
    String status = "Information Not Available";
    boolean open = false;

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
            JSONObject getLocation = jsonObject.getJSONObject("result").getJSONObject("geometry")
                    .getJSONObject("location");

                lat = getLocation.getString("lat");
                lng = getLocation.getString("lng");


            JSONObject getName = jsonObject.getJSONObject("result");
            name = getName.getString("name").replace(".",",");

            JSONObject getStatus = jsonObject.getJSONObject("result");
            if (getStatus.has("opening_hours") && !getStatus.isNull("opening_hours")) {
                getStatus = jsonObject.getJSONObject("result").getJSONObject("opening_hours");
                open = getStatus.getBoolean("open_now");
                if (open = true) {
                    status = "Open";

                } else {
                    status = "Closed";

                }
            }else {
                status = "No Data";
            }

            JSONObject getID = jsonObject.getJSONObject("result");
            id = getID.getString("place_id");

            LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.snippet('\n' + name + '\n' +
                    "Currently " + status);
            markerOptions.title(id);
            mMap.addMarker(markerOptions);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            elat = Double.parseDouble(lat);
            elng = Double.parseDouble(lng);

            LatLng latlng = new LatLng(elat, elng);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 15);
            mMap.moveCamera(cameraUpdate);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
