package za.edu.st10112216.numap.displays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import za.edu.st10112216.numap.activities.MapsActivity;
import za.edu.st10112216.numap.R;
import za.edu.st10112216.numap.classes.FavouriteLandmarks;
import za.edu.st10112216.numap.classes.PlaceDetailsClass;

public class DisplayPlaceDetail extends AppCompatActivity {

    private static final String TAG = "TAGGGGGGGGGG";
    PlaceDetailsClass placeDetailsClass = new PlaceDetailsClass();
    FavouriteLandmarks favouriteLandmarks = new FavouriteLandmarks();

    // Array and Adapter
    private ListView placeDetails;
    private TextView placeName;
    private  List<String> placeList;
    private ArrayAdapter<String> placeAdapter;

    // Firebase
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference placeRef = database.getReference("tmp");

    // Firebase
    private final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference mUserRef = mDatabase.getReference("usersDetails");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // Vars
    String ID;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_place_detail);
        placeDetailsClass.setUserId(user.getUid());

        // Buttons
        ImageButton back = findViewById(R.id.displayPlace_return);
        Button fav = findViewById(R.id.btn_add_fav);


        // Initialize Array
        placeList = new ArrayList<>();

        // Array Output Resource
        placeDetails = findViewById(R.id.displayPlace_scrollview);
        placeName = findViewById(R.id.displayPlace_title);


        // Firebase Interaction Place Details
        placeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot pulledUser : snapshot.getChildren()) {
                    PlaceDetailsClass placeDetailsClass = pulledUser.getValue(PlaceDetailsClass.class);
                    assert placeDetailsClass != null;
                    placeList.add(placeDetailsClass.toString());
                    placeName.setText(placeDetailsClass.getName());
                    name = placeDetailsClass.getName();
                    ID = placeDetailsClass.getPlaceID();
                    fav.setText(R.string.add_fav);
                    fav.setOnClickListener(v -> addLandmark());


                    // Adapter to View User Details
                    placeAdapter = new ArrayAdapter<>(DisplayPlaceDetail.this,
                            android.R.layout.simple_list_item_1, placeList);
                    placeDetails.setAdapter(placeAdapter);

                    mUserRef.child(user.getUid()).child("FavouriteLandmarks").child(name).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot pulledUser : snapshot.getChildren()) {
                                Log.d(TAG, Objects.requireNonNull(pulledUser.getValue()).toString());
                                if (pulledUser.getValue() == null) {
                                    fav.setText(R.string.add_fav);
                                    fav.setOnClickListener(v -> addLandmark());
                                } else {
                                    fav.setText(R.string.fav_remove);
                                    fav.setOnClickListener(v -> {
                                        mUserRef.child(user.getUid()).child("FavouriteLandmarks").child(name).removeValue();
                                        Toast.makeText(DisplayPlaceDetail.this, "Landmark Removed", Toast.LENGTH_SHORT).show();
                                        fav.setVisibility(View.GONE);

                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(DisplayPlaceDetail.this, "Database Error: " + error.getCode(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DisplayPlaceDetail.this, "Database Error: " + error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
        back.setOnClickListener(view -> {
            Intent intent;
            intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            placeRef.removeValue();
            placeList.clear();

        });
    }

    private void addLandmark() {
        favouriteLandmarks.setID(ID);
        favouriteLandmarks.setName(name);

        writeNewLandmark(favouriteLandmarks.getID(),favouriteLandmarks.getName());

        Toast.makeText(DisplayPlaceDetail.this, "Landmark Added", Toast.LENGTH_SHORT).show();
    }

    private void writeNewLandmark(String id, String name) {

        favouriteLandmarks = new FavouriteLandmarks(id, name);

        mUserRef.child(user.getUid()).child("FavouriteLandmarks").child(name).setValue(favouriteLandmarks);
    }
}

