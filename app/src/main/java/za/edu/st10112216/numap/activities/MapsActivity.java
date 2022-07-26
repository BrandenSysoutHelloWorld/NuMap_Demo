package za.edu.st10112216.numap.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
//import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import android.Manifest.permission;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import za.edu.st10112216.numap.R;
import za.edu.st10112216.numap.classes.FavouriteLandmarks;
import za.edu.st10112216.numap.classes.UserDetailsClass;
import za.edu.st10112216.numap.dataprocessers.FetchData;
import za.edu.st10112216.numap.dataprocessers.FetchDataFavLandmark;
import za.edu.st10112216.numap.dataprocessers.FetchDetailData;
import za.edu.st10112216.numap.dataprocessers.PolylineData;
import za.edu.st10112216.numap.displays.DisplayPlaceDetail;

import com.google.maps.android.SphericalUtil;
import com.google.maps.model.TravelMode;


public class MapsActivity extends FragmentActivity
        implements
        OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener {

    private static final String TAG = "Automated LOG-ENTRY";

    // Popup Window
    Button closePopup;
    Button moreInfo;
    PopupWindow popupWindow;
    PopupWindow popupWindowSearched;
    PopupWindow popupWindowShareOption;

    // Layouts
    LinearLayout ol;
    RelativeLayout tv;
    LinearLayout fav;
    ImageButton ol_mode;

    // Firebase
    DatabaseReference mDatabaseRef;
    FirebaseDatabase mDatabase;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // User Details Class
    private UserDetailsClass userDetailsClass;

    // Buttons
    ImageButton currentLocationImgBtn;
    ImageButton sendLocationImgBtn;
    ImageButton landmarkImgBtn;
    ImageButton fav_landmark;
    Button clearMap;
    Button directions;
    Button start_nav;
    Button end_nav;
    Button find_landmark;

    boolean ttmp = false;
    Spinner spin;

    // Landmark Overlay Buttons
    ImageButton returnBtn;
    ImageButton returnFav_Btn;
    ImageButton fireBtn;
    ImageButton medicalBtn;
    ImageButton policeBtn;
    ImageButton cafeBtn;
    ImageButton restaurantBtn;
    ImageButton museumBtn;
    ImageButton theatreBtn;
    ImageButton mallBtn;

    // Map Variables
    private GoogleMap mMap;
    private double lat, lng;
    private  double elat, elng;
    private Marker currentLocationMarker;
    private GeoApiContext mGeoApiContext = null;
    private ArrayList<PolylineData> mPolylinesData = new ArrayList<>();
    Double distance;
    boolean tmp = false;
    boolean tmpStart = false;
    private TextView destInfo;
    double tmpDuration = 0;

    // Markers
    private Marker mSelectedMarker = null;
    String markerName;
    String markerSnippet;
    private final ArrayList<Marker> mTripMarker = new ArrayList<>();

    // Location Variables
    LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;

    // Permissions
    boolean isPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // Navigation bar
    BottomNavigationView bottomNavigationView;

    @SuppressWarnings("deprecation")
    @SuppressLint("VisibleForTests")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        tmp = false;
        tmpStart = false;

        spin = findViewById(R.id.fav_spin);

        // Layouts
        ol = findViewById(R.id.overlay_landmark);
        tv = findViewById(R.id.main);
        fav = findViewById(R.id.overlay_fav_landmark);
        fav.setVisibility(View.GONE);
        ol.setVisibility(View.GONE);

        // User Details Class
        userDetailsClass = new UserDetailsClass();

        // Firebase
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference("usersDetails");

        // Navigation Bar
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onOptionsItemSelected);

        // Buttons
        currentLocationImgBtn = findViewById(R.id.btn_current_location);
        sendLocationImgBtn = findViewById(R.id.btn_send_location);
        landmarkImgBtn = findViewById(R.id.btn_landmarks);
        clearMap = findViewById(R.id.btn_clearMap);
        directions = findViewById(R.id.btn_directions);
        fav_landmark = findViewById(R.id.btn_favorite);
        start_nav = findViewById(R.id.btn_start_nav);
        end_nav = findViewById(R.id.btn_end_nav);
        returnFav_Btn = findViewById(R.id.landmarkFav_btn_return);
        find_landmark = findViewById(R.id.fav_btn_mark);
        ol_mode = findViewById(R.id.btn_walk_mode);

        // Landmark Overlay Buttons
        returnBtn = findViewById(R.id.landmark_btn_return);
        fireBtn = findViewById(R.id.btn_landmarks2);
        medicalBtn = findViewById(R.id.btn_landmarks3);
        policeBtn = findViewById(R.id.btn_landmarks4);
        cafeBtn = findViewById(R.id.btn_landmarks5);
        restaurantBtn = findViewById(R.id.btn_landmarks6);
        museumBtn = findViewById(R.id.btn_landmarks7);
        theatreBtn = findViewById(R.id.btn_landmarks8);
        mallBtn = findViewById(R.id.btn_landmarks9);

        // Location Variables
        fusedLocationProviderClient = new FusedLocationProviderClient(this);
        destInfo = findViewById(R.id.dest_info);

        // Checks Location Permissions
        checkPermissions();

        // Checks Play Services Exist
        if (checkGooglePlayServices()) {

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            assert mapFragment != null;
            mapFragment.getMapAsync(this);

            if (mGeoApiContext == null){
                mGeoApiContext = new GeoApiContext.Builder()
                        .apiKey(getString(R.string.map_key))
                        .build();

            }

            if (isPermissionGranted) {
                // Checks That GPS is Enabled
                CheckGps();
            }

        } else {
            Toast.makeText(MapsActivity.this, "Play Services Not Available", Toast.LENGTH_SHORT).show();
        }

    }

    private void removeTripMarkers(){
        for (Marker marker: mTripMarker){
            marker.remove();
        }
    }

    private void resetSelectedMarker(){
        if (mSelectedMarker != null){
            mSelectedMarker.setVisible(true);
            mSelectedMarker = null;
            removeTripMarkers();
        }
    }

    // Adds lines on the map for routes
    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(() -> {
            Log.d(TAG, "run: result routes: " + result.routes.length);
            if (mPolylinesData.size() > 0 && !tmp){
                for (PolylineData polylineData: mPolylinesData){
                    polylineData.getPolyline().remove();
                }
                mPolylinesData.clear();
                mPolylinesData = new ArrayList<>();
            }

            double duration = 99999;
            for(DirectionsRoute route: result.routes){
                Log.d(TAG, "run: leg: " + route.legs[0].toString());
                List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                List<LatLng> newDecodedPath = new ArrayList<>();

                // This loops through all the LatLng coordinates of ONE polyline.
                for(com.google.maps.model.LatLng latLng: decodedPath){
                    newDecodedPath.add(new LatLng(
                            latLng.lat,
                            latLng.lng
                    ));
                }

                mMap.addPolyline(new PolylineOptions().zIndex(1));
                Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                polyline.setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray));
                polyline.setClickable(true);
                mPolylinesData.add(new PolylineData(polyline, route.legs[0]));

                tmpDuration = route.legs[0].duration.inSeconds;
                if (tmpDuration < duration) {
                    duration = tmpDuration;
                    onPolylineClick(polyline);
                    zoomRoute(polyline.getPoints());
                }
            }
            mSelectedMarker.setVisible(false);
        });
    }

    // Zooms onto polylines
    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    // Calculates Directions
    private void calculateDirections(Marker marker){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
        directions.alternatives(true);

        directions.origin(
                new com.google.maps.model.LatLng(
                        lat,
                        lng
                ));

        Log.d(TAG, "calculateDirections: destination: " + destination);


            if (ttmp) {
                directions.destination(destination).mode(TravelMode.WALKING).setCallback(new PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(DirectionsResult result) {
                        Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                        Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                        Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                        Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                        addPolylinesToMap(result);

                    }
                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

                    }
                });

            } else {
                directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(DirectionsResult result) {
                        Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                        Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                        Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                        Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                        addPolylinesToMap(result);

                    }
                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

                    }
                });
            }



    }

   // Checks If Google Play Services Exist
    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApiAvailability.isUserResolvableError(result)) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, result, 201, dialogInterface -> Toast.makeText(MapsActivity.this, "User Canceled Dialog", Toast.LENGTH_SHORT).show());
            assert dialog != null;
            dialog.show();
        }
        return false;
    }

    // Checks Location Permissions and Prompts if required
    private void checkPermissions() {

        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        isPermissionGranted = true;

    }

    // Gets Current Location
    @SuppressLint("MissingPermission")
    private void getCurrentLoc() {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    lat = location.getLatitude();
                    lng = location.getLongitude();

                    // Sets Camera on Current Location
                    gotoLocation(location.getLatitude(), location.getLongitude());
                    userDetailsClass.setLat(lat);
                    userDetailsClass.setLng(lng);
                    writeLocation(userDetailsClass.getLat(), userDetailsClass.getLng());
                }
            });

    }

    @SuppressLint({"MissingPermission", "RtlHardcoded"})
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnPolylineClickListener(this);
        getCurrentLoc();
        directions.setVisibility(Button.GONE);
        start_nav.setVisibility(Button.GONE);
        end_nav.setVisibility(View.GONE);
        destInfo.setVisibility(View.GONE);
        ol_mode.setVisibility(View.GONE);

        //Initialize SDK
        String apikey = getString(R.string.map_key);

        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(), apikey);

        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment1);

        // Specify the types of place data to return.
        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        LatLng latLng1 = new LatLng(lat, lng);
        LatLng latLng2 = new LatLng(elat,lng);
        LatLngBounds latLng3 = new LatLngBounds(latLng1, latLng2);

        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                latLng3));

        // Select a country for relatable searches
        autocompleteFragment.setCountries("ZA");

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                //double lat, lng;
                elat = Objects.requireNonNull(place.getLatLng()).latitude;
                elng = place.getLatLng().longitude;

                gotoLocation(Objects.requireNonNull(place.getLatLng()).latitude, place.getLatLng().longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng latLng = new LatLng(elat, elng);
                markerOptions.position(latLng);
                markerOptions.title(place.getName());
                markerOptions.snippet(place.getAddress());

                markerName = place.getId();
                markerSnippet = place.getAddress();
                mMap.addMarker(markerOptions);

                if (directions.getVisibility() == Button.GONE) {
                    directions.setVisibility(Button.VISIBLE);
                }

                if (end_nav.getVisibility() == Button.VISIBLE) {
                    end_nav.setVisibility(Button.GONE);
                }

                mMap.setOnMarkerClickListener(marker -> {
                    marker.setVisible(false);
                    mSelectedMarker = marker;
                    markerSnippet = place.getName();
                    markerName = place.getId();

                    if (popupWindowSearched != null) {
                        popupWindowSearched.dismiss();
                    }

                    if (markerSnippet != null) {
                        resetSelectedMarker();
                        //instantiate the popup.xml layout file
                        LayoutInflater layoutInflater = (LayoutInflater) MapsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        @SuppressLint("InflateParams") View customView = layoutInflater.inflate(R.layout.pop_up, null);
                        TextView popupText = customView.findViewById(R.id.popup_text);
                        closePopup = customView.findViewById(R.id.popup_navigate);
                        moreInfo = customView.findViewById(R.id.popup_moreInfo);
                        mSelectedMarker = marker;

                        //instantiate popup window
                        popupWindowSearched = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        //display the popup window
                        popupWindowSearched.showAtLocation(tv, Gravity.TOP | Gravity.LEFT, 0, 0);
                        popupText.setText(markerSnippet);

                    }

                    moreInfo.setOnClickListener(view -> {
                        String urlDetail = "https://maps.googleapis.com/maps/api/place/details/json?" +
                                "fields=icon%2Cname%2Crating%2Cformatted_phone_number%2Copening_hours%2Cwebsite" +
                                "&place_id=" + markerName +
                                "&key=" + getResources().getString(R.string.map_key);
                        Log.d("Link StringBuilder Data", urlDetail);

                        Object[] dataFetchDetail = new Object[2];
                        dataFetchDetail[0] = mMap;
                        dataFetchDetail[1] = urlDetail;

                        FetchDetailData fetchDataDetail = new FetchDetailData();
                        fetchDataDetail.execute(dataFetchDetail);
                        Intent intent;
                        intent = new Intent(MapsActivity.this, DisplayPlaceDetail.class);
                        startActivity(intent);

                    });

                    closePopup.setOnClickListener(view -> {

                        if (directions.getVisibility() == Button.VISIBLE) {
                            directions.setVisibility(Button.GONE);
                        }
                        popupWindowSearched.dismiss();

                    });

                    directions.setOnClickListener(v -> {
                        if (start_nav.getVisibility() == Button.GONE) {
                            start_nav.setVisibility(Button.VISIBLE);

                        }

                        if (ol_mode.getVisibility() == Button.GONE) {
                            ol_mode.setVisibility(Button.VISIBLE);

                        }

                        if (directions.getVisibility() == Button.VISIBLE) {
                            directions.setVisibility(Button.GONE);
                        }
                        getDistance();
                        calculateDirections(marker);
                        popupWindowSearched.dismiss();

                        ol_mode.setOnClickListener(v2 -> {
                            ttmp = true;
                            calculateDirections(mSelectedMarker);
                            getDistance();
                            ol_mode.setVisibility(View.GONE);

                            Toast.makeText(MapsActivity.this, "Showing Walking Directions", Toast.LENGTH_SHORT).show();


                        });
                    });
                    return false;
                });
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        sendLocationImgBtn.setOnClickListener(view -> {
            if (popupWindowShareOption != null) {
                popupWindowShareOption.dismiss();
            }
            String textView = "Your Current Location is :" + "\n" + lat + "," + lng;

            LayoutInflater layoutInflater = (LayoutInflater) MapsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View customView = layoutInflater.inflate(R.layout.pop_up, null);
            TextView popupText = customView.findViewById(R.id.popup_text);
            closePopup = customView.findViewById(R.id.popup_navigate);
            moreInfo = customView.findViewById(R.id.popup_moreInfo);
            moreInfo.setText(R.string.share_copy);
            closePopup.setText(R.string.share_fb);

            //instantiate popup window
            popupWindowShareOption = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //display the popup window
            popupWindowShareOption.showAtLocation(tv, Gravity.TOP | Gravity.LEFT, 0, 0);
            popupText.setText(textView);

            moreInfo.setOnClickListener(v -> {
                popupWindowShareOption.dismiss();

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("CurrentLocation", "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MapsActivity.this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();

            });

            closePopup.setOnClickListener(v -> {
                popupWindowShareOption.dismiss();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent .setType("text/plain");
                intent .setPackage("com.facebook.katana");
                intent .putExtra(Intent.EXTRA_TEXT, "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng);
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MapsActivity.this, "Facebook have not been installed.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Button Click Listener for favourite landmarks
        fav_landmark.setOnClickListener(v -> {
            tv.setVisibility(View.GONE);
            fav.setVisibility(View.VISIBLE);
            ArrayList<String> arrayList = new ArrayList<>();
            @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") final ArrayList<String> arrayList1 = new ArrayList<>();
            final String[] placeName1 = new String[1];
            final Object[] placeID = new Object[1];

            mDatabaseRef.orderByChild("userID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    mDatabaseRef.child(user.getUid()).child("FavouriteLandmarks").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot pulledUser: snapshot.getChildren()) {
                                FavouriteLandmarks favouriteLandmarks = pulledUser.getValue(FavouriteLandmarks.class);
                                assert favouriteLandmarks != null;
                                arrayList.add(favouriteLandmarks.getID());
                                arrayList1.add(favouriteLandmarks.getName());


                            }

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MapsActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item, arrayList);
                            spin.setAdapter(arrayAdapter);

                            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    placeName1[0] = parent.getItemAtPosition(position).toString();

                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    Toast.makeText(MapsActivity.this, "Select an Item", Toast.LENGTH_SHORT).show();

                                }

                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MapsActivity.this, "Database Error: " + error.getCode(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MapsActivity.this, "Database Error: " + error.getCode(), Toast.LENGTH_SHORT).show();

                }
            });

            find_landmark.setOnClickListener(v1 -> {
                for (String s: arrayList) {
                    if (placeName1[0] != null) {
                        if (placeName1[0].equals(s)) {
                            mDatabaseRef.child(user.getUid()).child("FavouriteLandmarks").child(s).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot pulledUser: snapshot.getChildren()) {
                                        Object id = pulledUser.getValue();
                                        placeID[0] = id;
                                    }
                                    Log.d(TAG, "::" + placeID[0]);
                                    String urlDetail = "https://maps.googleapis.com/maps/api/place/details/json?" +
                                            "&place_id=" + placeID[0] +
                                            "&key=" + getResources().getString(R.string.map_key);
                                    Log.d("Link StringBuilder Data", urlDetail);

                                    Object[] dataFetchDetail = new Object[2];
                                    dataFetchDetail[0] = mMap;
                                    dataFetchDetail[1] = urlDetail;

                                    FetchDataFavLandmark fetchDataFavLandmark = new FetchDataFavLandmark();
                                    fetchDataFavLandmark.execute(dataFetchDetail);
                                    setLayoutInvisible();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(MapsActivity.this, "Database Error: " + error.getCode(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            });

            //ttmp = true; Walk directions enable.

            returnFav_Btn.setOnClickListener(v1 -> {
                tv.setVisibility(View.VISIBLE);
                fav.setVisibility(View.GONE);
            });
        });

        // Button Click Listeners for Landmark filter overlay
        currentLocationImgBtn.setOnClickListener(view -> {
            getCurrentLoc();

            if(tmp) {
                tmpStart = true;

            }
        });

        landmarkImgBtn.setOnClickListener(view -> {
            setLayoutVisible();
            mMap.clear();

        });

        returnBtn.setOnClickListener(view -> setLayoutInvisible());

        fireBtn.setOnClickListener(view -> {
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + lat + "," + lng +
                    "&radius=1000" +
                    "&type=fire_station" +
                    "&sensor=true" +
                    "&key=" + getResources().getString(R.string.map_key);
            Log.d("Link StringBuilder", url);

            Object[] dataFetch = new Object[2];
            dataFetch[0] = mMap;
            dataFetch[1] = url;

            FetchData fetchData = new FetchData();
            fetchData.execute(dataFetch);
            setLayoutInvisible();

            Toast.makeText(MapsActivity.this, "FireStations Near Me", Toast.LENGTH_SHORT).show();

        });

        medicalBtn.setOnClickListener(view -> {
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + lat + "," + lng +
                    "&radius=1000" +
                    "&type=hospital" +
                    "&sensor=true" +
                    "&key=" + getResources().getString(R.string.map_key);
            Log.d("Link StringBuilder", url);

            Object[] dataFetch = new Object[2];
            dataFetch[0] = mMap;
            dataFetch[1] = url;

            FetchData fetchData = new FetchData();
            fetchData.execute(dataFetch);
            setLayoutInvisible();
            Toast.makeText(MapsActivity.this, "Hospitals Near Me", Toast.LENGTH_SHORT).show();
        });

        policeBtn.setOnClickListener(view -> {
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + lat + "," + lng +
                    "&radius=1000" +
                    "&type=police" +
                    "&sensor=true" +
                    "&key=" + getResources().getString(R.string.map_key);
            Log.d("Link StringBuilder", url);

            Object[] dataFetch = new Object[2];
            dataFetch[0] = mMap;
            dataFetch[1] = url;

            FetchData fetchData = new FetchData();
            fetchData.execute(dataFetch);
            setLayoutInvisible();
            Toast.makeText(MapsActivity.this, "PoliceStations Near Me", Toast.LENGTH_SHORT).show();
        });

        cafeBtn.setOnClickListener(view -> {
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + lat + "," + lng +
                    "&radius=1000" +
                    "&type=cafe" +
                    "&sensor=true" +
                    "&key=" + getResources().getString(R.string.map_key);
            Log.d("Link StringBuilder", url);

            Object[] dataFetch = new Object[2];
            dataFetch[0] = mMap;
            dataFetch[1] = url;

            FetchData fetchData = new FetchData();
            fetchData.execute(dataFetch);
            setLayoutInvisible();
            Toast.makeText(MapsActivity.this, "Cafe's Near Me", Toast.LENGTH_SHORT).show();

        });

        restaurantBtn.setOnClickListener(view -> {
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + lat + "," + lng +
                    "&radius=1000" +
                    "&type=restaurant" +
                    "&sensor=true" +
                        "&key=" + getResources().getString(R.string.map_key);
            Log.d("Link StringBuilder", url);

            Object[] dataFetch = new Object[2];
            dataFetch[0] = mMap;
            dataFetch[1] = url;

            FetchData fetchData = new FetchData();
            fetchData.execute(dataFetch);
            setLayoutInvisible();
            Toast.makeText(MapsActivity.this, "Restaurants Near Me", Toast.LENGTH_SHORT).show();
        });

        museumBtn.setOnClickListener(view -> {
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + lat + "," + lng +
                    "&radius=1000" +
                    "&type=museum" +
                    "&sensor=true" +
                    "&key=" + getResources().getString(R.string.map_key);
            Log.d("Link StringBuilder", url);

            Object[] dataFetch = new Object[2];
            dataFetch[0] = mMap;
            dataFetch[1] = url;

            FetchData fetchData = new FetchData();
            fetchData.execute(dataFetch);
            setLayoutInvisible();
            Toast.makeText(MapsActivity.this, "Museum's Near Me", Toast.LENGTH_SHORT).show();
        });

        theatreBtn.setOnClickListener(view -> {
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + lat + "," + lng +
                    "&radius=1000" +
                    "&type=tourist_attraction" +
                    "&sensor=true" +
                    "&key=" + getResources().getString(R.string.map_key);
            Log.d("Link StringBuilder", url);

            Object[] dataFetch = new Object[2];
            dataFetch[0] = mMap;
            dataFetch[1] = url;

            FetchData fetchData = new FetchData();
            fetchData.execute(dataFetch);
            setLayoutInvisible();
            Toast.makeText(MapsActivity.this, "Theaters", Toast.LENGTH_SHORT).show();
        });

        mallBtn.setOnClickListener(view -> {
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + lat + "," + lng +
                    "&radius=1000" +
                    "&type=convenience_store" +
                    "&sensor=true" +
                    "&key=" + getResources().getString(R.string.map_key);
            Log.d("Link StringBuilder", url);

            FetchData fetchData = new FetchData();
            Object[] dataFetch = new Object[2];
            dataFetch[0] = mMap;
            dataFetch[1] = url;
            fetchData.execute(dataFetch);
            setLayoutInvisible();
            Toast.makeText(MapsActivity.this, "Mall/Shops Near Me", Toast.LENGTH_SHORT).show();

        });

        clearMap.setOnClickListener(view -> resetMap());

        // Marker Click Listener
        mMap.setOnMarkerClickListener(marker -> {
            elat = marker.getPosition().latitude;
            elng = marker.getPosition().longitude;

            marker.setVisible(false);
            markerSnippet = marker.getSnippet();
            markerName = marker.getTitle();

            if (popupWindow != null) {
                popupWindow.dismiss();
            }

            if (markerSnippet != null) {
                resetSelectedMarker();

                if (directions.getVisibility() == Button.GONE) {
                    directions.setVisibility(Button.VISIBLE);

                }

                //instantiate the popup.xml layout file
                LayoutInflater layoutInflater = (LayoutInflater) MapsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams") View customView = layoutInflater.inflate(R.layout.pop_up, null);
                TextView popupText = customView.findViewById(R.id.popup_text);
                closePopup = customView.findViewById(R.id.popup_navigate);
                moreInfo = customView.findViewById(R.id.popup_moreInfo);
                mSelectedMarker = marker;

                //instantiate popup window
                popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //display the popup window
                popupWindow.showAtLocation(tv, Gravity.TOP | Gravity.LEFT, 0, 0);
                popupText.setText(markerSnippet);


                moreInfo.setOnClickListener(view -> {
                    String urlDetail = "https://maps.googleapis.com/maps/api/place/details/json?" +
                            "fields=icon%2Cname%2Crating%2Cformatted_phone_number%2Copening_hours%2Cwebsite%2Cplace_id" +
                            "&place_id=" + markerName +
                            "&key=" + getResources().getString(R.string.map_key);
                    Log.d("Link StringBuilder Data", urlDetail);

                    Object[] dataFetchDetail = new Object[2];
                    dataFetchDetail[0] = mMap;
                    dataFetchDetail[1] = urlDetail;

                    FetchDetailData fetchDataDetail = new FetchDetailData();
                    fetchDataDetail.execute(dataFetchDetail);
                    Intent intent;
                    intent = new Intent(MapsActivity.this, DisplayPlaceDetail.class);
                    startActivity(intent);
                });

                closePopup.setOnClickListener(view -> {
                    popupWindow.dismiss();

                    if (directions.getVisibility() == Button.VISIBLE) {
                        directions.setVisibility(Button.GONE);
                    }

                });

                directions.setOnClickListener(v -> {
                    ol_mode.setVisibility(Button.VISIBLE);

                    popupWindow.dismiss();
                    if (start_nav.getVisibility() == Button.GONE) {
                            start_nav.setVisibility(Button.VISIBLE);
                    }

                    if (directions.getVisibility() == Button.VISIBLE) {
                            directions.setVisibility(Button.GONE);
                    }

                    ol_mode.setOnClickListener(v2 -> {
                        ol_mode.setVisibility(View.GONE);
                        ttmp = true;
                        calculateDirections(mSelectedMarker);
                        getDistance();

                        Toast.makeText(MapsActivity.this, "Showing Walking Directions", Toast.LENGTH_SHORT).show();


                    });

                    getDistance();
                    calculateDirections(marker);
                });
            }
            return false;
        });

        start_nav.setOnClickListener(v -> {
            tmpStart = true;
            tmp = true;
            start_nav.setVisibility(View.GONE);
            end_nav.setVisibility(View.VISIBLE);
            destInfo.setVisibility(View.VISIBLE);
            ol_mode.setVisibility(View.GONE);

            if (tmpStart) {
                for (PolylineData polylineData : mPolylinesData) {
                    if (polylineData.getPolyline().getZIndex() != 1) {
                        polylineData.getPolyline().remove();
                    }
                }
            }

        });


        end_nav.setOnClickListener(v -> {
            directions.setVisibility(View.GONE);
            ol_mode.setVisibility(View.GONE);
            end_nav.setVisibility(View.GONE);
            destInfo.setVisibility(View.GONE);
            resetMap();
            tmpStart = false;
            tmp = false;
        });

        // Close Popup on Map Click
        mMap.setOnMapClickListener(latLng -> {
            tmpStart = false;
            hideKeyboard(MapsActivity.this);

            if (popupWindowSearched != null) {
                popupWindowSearched.dismiss();
            }

            if (popupWindow != null) {
                popupWindow.dismiss();
            }

            if (popupWindowShareOption != null) {
                popupWindowShareOption.dismiss();
            }
        });

        mMap.setOnMapLongClickListener(latLng -> tmpStart = false);

    }

    // Checks GPS Permissions and Prompts if required
    private void CheckGps() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        Task<LocationSettingsResponse> locationSettingsResponseTask = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        locationSettingsResponseTask.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                Log.d(TAG, response.toString());
                getCurrentLocationUpdate();

            } catch (ApiException e) {
                if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(MapsActivity.this, 101);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }
                if (e.getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                    Toast.makeText(MapsActivity.this, "Settings Unavailable", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Updates Users current Location. Places a Marker.
    private void getCurrentLocationUpdate() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (null != currentLocationMarker) {
                    currentLocationMarker.remove();
                }

                lat = locationResult.getLastLocation().getLatitude();
                lng = locationResult.getLastLocation().getLongitude();

                LatLng latlng = new LatLng(lat, lng);
                currentLocationMarker = mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_person_pin_black_24dp))
                        .title("Current Location")
                        .position(latlng));

                Log.d("LOCATION : ", "User Location Updated ! - " + userDetailsClass.getLat() + "," + userDetailsClass.getLng());

                if (tmpStart) {
                    animateToLocationBearing(locationResult.getLastLocation());
                    getDistance();
                }
            }

        }, Looper.getMainLooper());
    }

    // Moves Camera to Current Location
    private void gotoLocation(double latitude, double longitude) {
        LatLng latlng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 15);
        mMap.moveCamera(cameraUpdate);

    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }

    // Bottom Menu Intents
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.nav_btn_home:
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                return true;

            case R.id.nav_btn_download:
                Toast.makeText(MapsActivity.this, "Viewing Map in Offline Mode", Toast.LENGTH_SHORT).show();
                fav_landmark.setVisibility(View.GONE);
                sendLocationImgBtn.setVisibility(View.GONE);
                clearMap.setText("Online Map");
                clearMap.setOnClickListener(v -> {
                    clearMap.setText("CLEAR MAP");
                    fav_landmark.setVisibility(View.VISIBLE);
                    sendLocationImgBtn.setVisibility(View.VISIBLE);
                });
                return true;

            case R.id.nav_btn_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    // Writes Current Location to User Class
    public void writeLocation(double lat, double lng) {

        userDetailsClass = new UserDetailsClass(lat, lng);
        mDatabaseRef.child(user.getUid()).child("lat").setValue(lat);
        mDatabaseRef.child(user.getUid()).child("lng").setValue(lng);

    }

    // Manages the Popupview visibility. Sets Popview visible.
    public void setLayoutInvisible() {
        ol.setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);

        if (fav.getVisibility() == View.VISIBLE) {
            tv.setVisibility(View.VISIBLE);
            fav.setVisibility(View.GONE);
        }
    }

    // Manages the Popupview visibility. Sets Popview invisible.
    public void setLayoutVisible() {
        ol.setVisibility(View.VISIBLE);
        tv.setVisibility(View.GONE);
    }

    // Checks which polyline is selected
    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {
        int index = 0;
        for (PolylineData polylineData : mPolylinesData) {
            index++;
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
            if (polyline.getId().equals(polylineData.getPolyline().getId())) {
                polylineData.getPolyline().setColor(ContextCompat.getColor(getApplicationContext(), com.google.android.libraries.places.R.color.quantum_googblue));
                polylineData.getPolyline().setZIndex(1);

                LatLng endLocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(endLocation)
                        .title("Trip: #" + index)
                        .snippet("Duration " + polylineData.getLeg().duration));

                Log.d(TAG, String.valueOf(polylineData.getPolyline().getZIndex()));

                assert marker != null;
                marker.showInfoWindow();

                mTripMarker.add(marker);

            } else {
                polylineData.getPolyline().setColor(ContextCompat.getColor(getApplicationContext(), com.google.android.libraries.places.R.color.quantum_grey));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }

    // Resets the Map and clears Vars
    private void resetMap(){
        getCurrentLoc();
        getCurrentLocationUpdate();
        tmp = false;
        tmpStart = false;
        ttmp = false;
        ol_mode.setVisibility(View.GONE);

        if (start_nav.getVisibility() == View.VISIBLE) {
            start_nav.setVisibility(View.GONE);
        }

        if (directions.getVisibility() == Button.VISIBLE){
            directions.setVisibility(Button.GONE);
        }

        LatLng latLng = new LatLng(lat, lng);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .tilt(0)
                .zoom(15)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), null);

        if(mMap != null) {
            mMap.clear();

            if(mPolylinesData.size() > 0){
                mPolylinesData.clear();
                mPolylinesData = new ArrayList<>();
            }
        }
    }

    // Method to hide keyboard
    public static void hideKeyboard(MapsActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(MapsActivity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // Calculates Distance between two points
    public void getDistance() {
        LatLng startLatLng = new LatLng(lat,lng);
        LatLng endLatLng = new LatLng(elat, elng);
        distance = SphericalUtil.computeDistanceBetween(startLatLng, endLatLng);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String uid = user.getUid();
        final boolean[] unitState = new boolean[1];

        // Gets Users Distance Unit Preference
        mDatabaseRef.orderByChild("userID").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot pulledUser : snapshot.getChildren()) {
                    UserDetailsClass userdetails = pulledUser.getValue(UserDetailsClass.class);
                    assert userdetails != null;

                    unitState[0] = userdetails.isUnitState();
                    if (!unitState[0]){
                        destInfo.setText(String.format("%.2f", distance / 1000) + "km | ETA(From Start): " + String.format("%.2f", (tmpDuration/60)/60) + "hrs");
                    }else {
                        double fdistance = distance/1000;
                        double aDistance = fdistance/2;
                        double bDistance = aDistance/4;
                        distance = aDistance + bDistance;
                        destInfo.setText(String.format("%.2f", distance) + "mi | ETA(From Start): " + String.format("%.2f", (tmpDuration/60)/60) + "hrs");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // Updates postion of camera to users bearing
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void animateToLocationBearing(Location position) {
        if (mMap == null || position == null)
            return;

            if (position.getBearingAccuracyDegrees() < 100) {
                Log.d(TAG, String.valueOf(position.getBearingAccuracyDegrees()));
                LatLng latLng = new LatLng(position.getLatitude(), position.getLongitude());
                float zoomLevel = mMap.getCameraPosition().zoom != mMap.getMinZoomLevel() ? mMap.getCameraPosition().zoom : 1F;
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        .bearing(position.getBearing())
                        .tilt(30)
                        .zoom(zoomLevel)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), null);

            } else {
                LatLng latLng = new LatLng(position.getLatitude(), position.getLongitude());
                float zoomLevel = mMap.getCameraPosition().zoom != mMap.getMinZoomLevel() ? mMap.getCameraPosition().zoom : 1F;
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(zoomLevel)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), null);

            }
    }

}



