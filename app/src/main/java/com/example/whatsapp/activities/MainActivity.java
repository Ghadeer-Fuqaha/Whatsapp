package com.example.whatsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    TextInputLayout username, email, status,password;
    String nameValue, emailValue, statusValue, passwordValue;
    Button signUp;

    FirebaseAuth auth;
    DatabaseReference reference;

    TextView signInActivity;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (TextInputLayout) findViewById(R.id.TextInputNameLayout);
        email = (TextInputLayout) findViewById(R.id.TextInputEmailLayout);
        status = (TextInputLayout) findViewById(R.id.TextInputStatusLayout);
        password = (TextInputLayout) findViewById(R.id.TextInputPassword);
        signInActivity = (TextView) findViewById(R.id.signInActivity);

        signInActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        signUp = (Button) findViewById(R.id.signUpBtn);
        auth = FirebaseAuth.getInstance();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){

            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Retrieve Data from TextInputLayout

                nameValue = username.getEditText().getText().toString().trim();
                emailValue = email.getEditText().getText().toString().trim();
                statusValue = status.getEditText().getText().toString().trim();
                passwordValue = password.getEditText().getText().toString().trim();

                //validate all fields

                if(nameValue.isEmpty() || emailValue.isEmpty() || statusValue.isEmpty() || passwordValue.isEmpty()){

                    Toast.makeText(MainActivity.this, "All Fields are required, please check them again!", Toast.LENGTH_LONG).show();
                    return;
                }else{
                    register(nameValue,emailValue,statusValue,passwordValue);
                }
            }
        });
    }

    private void register(String nameValue, String emailValue, String statusValue, String passwordValue) {

        auth.createUserWithEmailAndPassword(emailValue,passwordValue).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    userId = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference("users").child(userId);

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("id",userId);
                    hashMap.put("username",nameValue);
                    hashMap.put("email",emailValue);
                    hashMap.put("status",statusValue);
                    hashMap.put("password",passwordValue);
                    hashMap.put("imgUrl","default");
                    hashMap.put("state","offline");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(MainActivity.this, "SignUp failed!!!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(MainActivity.this, "Authentication Failed!!! ID --> "+ userId, Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}