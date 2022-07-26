package za.edu.st10112216.numap.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import za.edu.st10112216.numap.R;
import za.edu.st10112216.numap.classes.UserDetailsClass;


public class LoginActivity extends AppCompatActivity{
    // Buttons
    Button regbutton;
    Button login;

    // Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            // Buttons
            regbutton = findViewById(R.id.btn_login_register);
            login = findViewById(R.id.btn_login_login);

            // Inputs
            EditText email_et = findViewById(R.id.input_login_email_login);
            EditText password_et = findViewById(R.id.input_login_password_login);

            // Firebase
            mAuth = FirebaseAuth.getInstance();

            // Button listener for MapsActivity
            login.setOnClickListener(v -> {
                String email = email_et.getText().toString().trim();
                String password = password_et.getText().toString().trim();

                // Validates Input. Validates Users Credentials.
                if (email.isEmpty()) {
                    email_et.setHint("Email Required !");
                    email_et.setHintTextColor(getResources().getColor(R.color.red));

                } else if (password.isEmpty()) {
                    password_et.setHintTextColor(getResources().getColor(R.color.red));
                    password_et.setHint("Password Required !");

                } else {

                    // Validates User's Credentials
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(this, MapsActivity.class);
                            startActivity(intent);

                            Toast.makeText(LoginActivity.this, "Welcome Back !", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(LoginActivity.this, "Error : Check Login Credentials", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        // Button listener for RegisterActivity
        regbutton.setOnClickListener(v -> {
            // Opens RegisterActivity
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);

        });
    }
}