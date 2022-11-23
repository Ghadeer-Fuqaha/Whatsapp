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

public class LoginActivity extends AppCompatActivity {

    TextInputLayout Email,Password;
    Button SignInButton;

    FirebaseAuth auth;

    TextView createNewAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email = (TextInputLayout) findViewById(R.id.EmailLogin);
        Password = (TextInputLayout) findViewById(R.id.PasswordLogin);

        createNewAccount = (TextView) findViewById(R.id.signUpActivity);

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }
        });
        SignInButton = (Button) findViewById(R.id.signInBtn);

        auth = FirebaseAuth.getInstance();

        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Email.getEditText().getText().toString().trim();
                String pass = Password.getEditText().getText().toString().trim();

                CheckUser(email,pass);
            }
        });
    }

    private void CheckUser(String email, String pass) {

        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, "Invalid Email or Password!!!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}