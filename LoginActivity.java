package com.example.switchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.switchat.R;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEdt,passwordEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_activity);

        Button loginBtn = findViewById(R.id.login_button);
        usernameEdt = findViewById(R.id.username);
        passwordEdt = findViewById(R.id.password);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", usernameEdt.getText()+"  "+passwordEdt.getText());

//                Todo: store user data in sharedpreference and direct to home page
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}