package za.edu.st10112216.numap.activities;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import za.edu.st10112216.numap.R;
import za.edu.st10112216.numap.classes.UserDetailsClass;

public class RegisterActivity extends AppCompatActivity {

    //User Details Class
    private UserDetailsClass userdetails;

    // Firebase
    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    // Buttons
    Button loginbutton;
    Button registerbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //User Details Class
        userdetails = new UserDetailsClass();

        // Buttons
        loginbutton =  findViewById(R.id.btn_register_login);
        registerbutton =  findViewById(R.id.btn_register_register);

        // Inputs
        EditText username_et = findViewById(R.id.input_register_username);
        EditText email_et = findViewById(R.id.input_register_email);
        EditText cellphone_et = findViewById(R.id.input_register_cellphone);
        EditText password_et = findViewById(R.id.input_register_password);
        EditText confirm_password_et = findViewById(R.id.input_register_confirmpassword);

       // Register Button
       registerbutton.setOnClickListener(v -> {
           // Set Values and Validation

           // Username
           if (username_et.getText().toString().isEmpty()){
               username_et.setHint("Username Required !");
               username_et.setHintTextColor(getResources().getColor(R.color.red));
               Toast.makeText(RegisterActivity.this,"Username Required !" + userdetails.getUsername() + " !", Toast.LENGTH_SHORT).show();
           // Email
           }else if (email_et.getText().toString().isEmpty()) {
               email_et.setHint("Email Required !");
               email_et.setHintTextColor(getResources().getColor(R.color.red));
               Toast.makeText(RegisterActivity.this,"Email Required !" + userdetails.getUsername() + " !", Toast.LENGTH_SHORT).show();
           // Password
           }else if (password_et.getText().toString().isEmpty()){
               password_et.setHintTextColor(getResources().getColor(R.color.red));
               password_et.setHint("Password Required !");
               Toast.makeText(RegisterActivity.this,"Password Required !" + userdetails.getUsername() + " !", Toast.LENGTH_SHORT).show();
           // Confirm Password
           }else if (confirm_password_et.getText().toString().isEmpty()) {
               confirm_password_et.setHintTextColor(getResources().getColor(R.color.red));
               confirm_password_et.setHint("Confirm Password !");
               Toast.makeText(RegisterActivity.this,"Confirm Password ! " + userdetails.getUsername() + " !", Toast.LENGTH_SHORT).show();

           // Match Passwords is TRUE then Set Inputs
           }else if (password_et.getText().toString().equals(confirm_password_et.getText().toString())){

               userdetails.setUsername(username_et.getText().toString().trim());
               Log.d("username", userdetails.getUsername());

               String finalValue = cellphone_et.getText().toString();
               userdetails.setCellphone(finalValue);
               Log.d("cellphone", String.valueOf(userdetails.getCellphone()));


           // Posts Data to Authentication Database
           mAuth.createUserWithEmailAndPassword(email_et.getText().toString(),password_et.getText().toString()).addOnCompleteListener(task -> {
               // If the Data is posted to the Auth Database then the MapsActivity will open
               if (task.isSuccessful()){
                   Toast.makeText(RegisterActivity.this,"Welcome to NuMaps " + userdetails.getUsername() + " !", Toast.LENGTH_SHORT).show();

                   // Get and Set a unique ID of the user
                   FirebaseUser user = mAuth.getCurrentUser();
                   assert user != null;
                   userdetails.setUserID(user.getUid());

                    openMapsActivity();

                    // Decides which constructor to use
                   if (cellphone_et.getText().toString().isEmpty()){
                       writeNewUserMin(userdetails.getUserID(), userdetails.getUsername());

                   } else {
                       writeNewUserMinFull(userdetails.getUserID(), userdetails.getUsername(), userdetails.getCellphone());
                   }

               } else {

                   Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
               }
           });

           }else {
            // Passwords Don't Match
          Toast.makeText(RegisterActivity.this,"Passwords Don't Match !", Toast.LENGTH_SHORT).show();

          password_et.setText("");
          password_et.setHintTextColor(getResources().getColor(R.color.red));
          password_et.setHint("Password Required !");
          confirm_password_et.setText("");
          confirm_password_et.setHintTextColor(getResources().getColor(R.color.red));
          confirm_password_et.setHint("Confirm Password !");

           }
       });

       // Login Button
        loginbutton.setOnClickListener(v -> openLoginActivity());

    }

    // Writes Data to Realtime Database
    public void writeNewUserMin(String userId, String username) {

        userdetails = new UserDetailsClass(userId, username);

        mDatabase.child("usersDetails").child(userId).setValue(userdetails);

    }

    public void writeNewUserMinFull(String userId, String username, String cellphone) {

        userdetails = new UserDetailsClass(userId, username, cellphone);

        mDatabase.child("usersDetails").child(userId).setValue(userdetails);

    }

    // Open Login Activity
    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    // Open Maps Activity
    private void openMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}