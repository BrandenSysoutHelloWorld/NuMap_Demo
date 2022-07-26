package za.edu.st10112216.numap.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import za.edu.st10112216.numap.R;
import za.edu.st10112216.numap.classes.UserDetailsClass;

public class AccountActivity extends AppCompatActivity {

    //private static final String TAG = "LOG: ";

    // Layouts
    RelativeLayout account;
    LinearLayout edit_account;
    LinearLayout update_password;
    LinearLayout update_email;
    LinearLayout update_cellphone;
    LinearLayout update_username;

    // Firebase
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference userRef = database.getReference("usersDetails");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // TextViews Account
    private TextView lstUserDetails;
    private TextView userName;

    // TextViews Edit
    EditText et_update_password;
    EditText et_update_confirm_password;

    EditText et_update_email;
    EditText et_update_email_confirm;

    EditText et_update_cellphone;
    EditText et_update_cellphone_confirm;

    EditText et_update_username;
    EditText et_update_username_confirm;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Layouts
        account = (RelativeLayout) findViewById(R.id.account);
        edit_account = (LinearLayout) findViewById(R.id.edit_account);
        update_password = (LinearLayout) findViewById(R.id.update_password);
        update_email = (LinearLayout)  findViewById(R.id.update_email);
        update_cellphone = (LinearLayout) findViewById(R.id.update_cellphone);
        update_username = (LinearLayout) findViewById(R.id.update_username);
        update_username.setVisibility(View.GONE);
        update_cellphone.setVisibility(View.GONE);
        edit_account.setVisibility(View.GONE);
        update_password.setVisibility(View.GONE);
        update_email.setVisibility(View.GONE);

        // Buttons
        ImageButton back = findViewById(R.id.account_btn_return);
        ImageButton update_rtn = findViewById(R.id.update_btn_return_password);
        ImageButton edit_back = findViewById(R.id.edit_btn_return);
        ImageButton update_rtn_email = findViewById(R.id.update_btn_return_email);
        ImageButton update_rtn_cellphone = findViewById(R.id.update_btn_return_cellphone);
        ImageButton update_rtn_username = findViewById(R.id.update_btn_return_username);

        Button signOut = findViewById(R.id.account_btn_logout);
        Button deleteUser = findViewById(R.id.account_btn_delete);
        Button editUser = findViewById(R.id.account_btn_edit);
        Button editPassword = findViewById(R.id.edit_btn_password);
        Button updatePassword = findViewById(R.id.update_btn_password);
        Button editUsername = findViewById(R.id.edit_btn_username);
        Button editEmail = findViewById(R.id.edit_btn_email);
        Button editCellphone = findViewById(R.id.edit_btn_cellphone);
        Button updateEmail = findViewById(R.id.update_btn_email);
        Button updateCellphone = findViewById(R.id.update_btn_cellphone);
        Button updateUsername = findViewById(R.id.update_btn_username);

        // TextViews Account
        lstUserDetails = findViewById(R.id.account_details);
        userName = findViewById(R.id.account_title1);

        // TextViews Edit
        et_update_password = findViewById(R.id.et_update_password);
        et_update_confirm_password = findViewById(R.id.et_update_password_confirm);

        et_update_email = findViewById(R.id.et_update_email);
        et_update_email_confirm = findViewById(R.id.et_update_email_confirm);

        et_update_cellphone = findViewById(R.id.et_update_cellphone);
        et_update_cellphone_confirm = findViewById(R.id.et_update_cellphone_confirm);

        et_update_username = findViewById(R.id.et_update_username);
        et_update_username_confirm = findViewById(R.id.et_update_username_confirm);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        // Name, email address
        final String[] name = new String[1];
        final String[] cellphone = new String[1];
        String email = user.getEmail();
        String uid = user.getUid();

        userRef.orderByChild("userID").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Loop through Users
                for (DataSnapshot pulledUser : snapshot.getChildren()){
                    UserDetailsClass userdetails = pulledUser.getValue(UserDetailsClass.class);
                    assert userdetails != null;
                    name[0] = userdetails.getUsername();
                    cellphone[0] = userdetails.getCellphone();
                    userName.setText("You are Logged in as: " + '\n' + name[0]);
                    lstUserDetails.setText("Email " + '\n' + email + '\n' +
                            "Cellphone " + '\n' + cellphone[0] + '\n' +
                            "Password " + '\n' + "************" + '\n' +
                            "User ID " + '\n' + uid);

                    editUser.setOnClickListener(v -> {
                        setLayoutInvisible();

                        editUsername.setOnClickListener(v1 -> {
                            update_username.setVisibility(View.VISIBLE);
                            edit_account.setVisibility(View.GONE);

                            update_rtn_username.setOnClickListener(v2 -> {
                                update_username.setVisibility(View.GONE);
                                setLayoutInvisible();
                            });

                            updateUsername.setOnClickListener(v2 -> {
                                if (et_update_username.getText().toString().equals(et_update_username_confirm.getText().toString())){
                                    updateUsername();
                                }else{
                                    Toast.makeText(AccountActivity.this, "Usernames don't match !", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });

                        editEmail.setOnClickListener(v1 -> {
                            update_email.setVisibility(View.VISIBLE);
                            edit_account.setVisibility(View.GONE);

                            update_rtn_email.setOnClickListener(v2 -> {
                                update_email.setVisibility(View.GONE);
                                setLayoutInvisible();
                            });

                            updateEmail.setOnClickListener(v2 -> {
                                if (et_update_email.getText().toString().equals(et_update_email_confirm.getText().toString())){
                                    updateEmail();
                                }else{
                                    Toast.makeText(AccountActivity.this, "Emails don't match !", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });

                        editCellphone.setOnClickListener(v1 -> {
                            update_cellphone.setVisibility(View.VISIBLE);
                            edit_account.setVisibility(View.GONE);

                            update_rtn_cellphone.setOnClickListener(v2 -> {
                                update_cellphone.setVisibility(View.GONE);
                                setLayoutInvisible();
                            });

                            updateCellphone.setOnClickListener(v2 -> {
                                if (et_update_cellphone.getText().toString().equals(et_update_cellphone_confirm.getText().toString())){
                                    updateCellphone();
                                }else{
                                    Toast.makeText(AccountActivity.this, "Cellphone numbers don't match !", Toast.LENGTH_SHORT).show();
                                }
                            });

                        });

                        editPassword.setOnClickListener(v1 -> {
                            update_password.setVisibility(View.VISIBLE);
                            edit_account.setVisibility(View.GONE);

                            update_rtn.setOnClickListener(v2 -> {
                                update_password.setVisibility(View.GONE);
                                setLayoutInvisible();
                            });

                            updatePassword.setOnClickListener(v2 -> {
                                update_password.setVisibility(View.VISIBLE);
                                if (et_update_password.getText().toString().equals(et_update_confirm_password.getText().toString())){
                                    updatePassword();
                                }else{
                                    Toast.makeText(AccountActivity.this, "Passwords don't match !", Toast.LENGTH_SHORT).show();
                                }
                            });

                        });

                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccountActivity.this, "Database Error: " + error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        // Button listener for Deleting a User/Account
        deleteUser.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AccountActivity.this, "User Deleted", Toast.LENGTH_SHORT).show();
                }
            });

            userRef.removeValue();
        });

        edit_back.setOnClickListener(v -> setLayoutVisible());

        // Button listener for Return
        back.setOnClickListener(v -> returnSettingsActivity());

        // Button listener for Signing User Out
        signOut.setOnClickListener(v -> signOut());
    }

    // Returns to Settings
    private void returnSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Manages the edit layout visibility. Sets edit visible.
    public void setLayoutInvisible() {
        account.setVisibility(View.GONE);
        edit_account.setVisibility(View.VISIBLE);
    }

    // Manages the edit layout visibility. Sets edit invisible.
    public void setLayoutVisible() {
        account.setVisibility(View.VISIBLE);
        edit_account.setVisibility(View.GONE);
    }

    private void updateUsername() {
        String finalValue = et_update_username.getText().toString();
        userRef.child(user.getUid()).child("username").setValue(finalValue);
        Toast.makeText(AccountActivity.this, "Username Updated !", Toast.LENGTH_SHORT).show();
    }

    public void updatePassword(){
        user.updatePassword(et_update_password.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AccountActivity.this, "Password Updated!", Toast.LENGTH_SHORT).show();
                        hideKeyboard(AccountActivity.this);
                    }
                });
    }

    private void updateEmail() {
        user.updateEmail(et_update_email.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AccountActivity.this, "Email Updated !", Toast.LENGTH_SHORT).show();
                        hideKeyboard(AccountActivity.this);
                    }
                });
    }

    private void updateCellphone() {
        String finalValue = et_update_cellphone.getText().toString();
        userRef.child(user.getUid()).child("cellphone").setValue(finalValue);
        Toast.makeText(AccountActivity.this, "Cellphone Updated !", Toast.LENGTH_SHORT).show();

    }

    public static void hideKeyboard(AccountActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(MapsActivity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
