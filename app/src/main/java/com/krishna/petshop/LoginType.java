package com.krishna.petshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class LoginType extends AppCompatActivity
{

    Button btnBuyer, btnSeller;
    Preferences preferences;
    TextView txtName, txtEmail, txtMobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_type);

        preferences = new Preferences(LoginType.this);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtMobile = findViewById(R.id.txtMobile);

        txtName.setText("UserName : "+preferences.getUserName());
        txtEmail.setText("Email Id : "+preferences.getUserEmail());
        txtMobile.setText("Mobile : "+preferences.getUserMobile());

        btnBuyer = findViewById(R.id.btnBuyer);
        btnSeller = findViewById(R.id.btnSeller);

        btnBuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginType.this, Home.class);
                preferences.setUserType("Buyer");
                startActivity(intent);
            }
        });

        btnSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginType.this, Home.class);
                preferences.setUserType("Seller");
                startActivity(intent);
            }
        });
    }
}
