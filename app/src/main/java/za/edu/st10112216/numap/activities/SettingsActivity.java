package za.edu.st10112216.numap.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import za.edu.st10112216.numap.R;
import za.edu.st10112216.numap.classes.UserDetailsClass;

public class SettingsActivity extends AppCompatActivity {

    // Firebase
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference userRef = database.getReference("usersDetails");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    DatabaseReference mData;
    UserDetailsClass userDetailsClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mData = FirebaseDatabase.getInstance().getReference();
        userDetailsClass = new UserDetailsClass();

        // Buttons
        ImageButton account = findViewById(R.id.settings_btn_account);
        ImageButton back = findViewById(R.id.settings_btn_return);

        // Button listeners
        account.setOnClickListener(v -> openAccountActivity());
        back.setOnClickListener(v -> returnAccountActivity());


        assert user != null;
        String uid = user.getUid();
        userDetailsClass.setUserID(user.getUid());

        // Switch
        Switch unitState = findViewById(R.id.settings_swtch_units);
        final boolean[] state = new boolean[1];

        //unitState.setChecked(true);

        userRef.orderByChild("userID").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot pulledUser : snapshot.getChildren()) {
                    UserDetailsClass userdetails = pulledUser.getValue(UserDetailsClass.class);
                    assert userdetails != null;
                    state[0] = userdetails.isUnitState();
                    unitState.setChecked(state[0]);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Switch Listener
        unitState.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Distance set to Miles (Metric)", Toast.LENGTH_SHORT).show();

                userDetailsClass.setUnitState(true);
                userRef.child(user.getUid()).child("unitState").setValue(userDetailsClass.isUnitState());

            } else {
                Toast.makeText(this, "Distance set to Imperial (Imperial)", Toast.LENGTH_SHORT).show();

                userDetailsClass.setUnitState(false);
                userRef.child(user.getUid()).child("unitState").setValue(userDetailsClass.isUnitState());
            }
        });
    }

    // Return to Map
    private void returnAccountActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    // Opens Account Activity
    private void openAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }



}
