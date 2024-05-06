package com.example.project;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Home extends AppCompatActivity {
    TextView userName, userEmail;
    String name, email, phoneNumber, country, street;
    Button productsButton, cartButton, ordersButton, addressButton, signOutButton;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ConstraintLayout constraintLayout = findViewById(R.id.listOrders);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        userName = findViewById(R.id.name_home);
        userEmail = findViewById(R.id.email_home);
        productsButton = findViewById(R.id.products_home);
        cartButton = findViewById(R.id.cart_home);
        ordersButton = findViewById(R.id.order_home);
        addressButton = findViewById(R.id.address_home);
        signOutButton = findViewById(R.id.signout_home);

        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();

        Intent intent = getIntent();
        country = intent.getStringExtra("country");
        phoneNumber = intent.getStringExtra("num");
        street = intent.getStringExtra("street");
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");

        userName.setText(name);
        userEmail.setText(email);

        productsButton.setOnClickListener(v -> {
            Intent intent2 = new Intent(this, Products.class);
            intent2.putExtra("name", name);
            intent2.putExtra("email", email);
            intent2.putExtra("num", phoneNumber);
            intent2.putExtra("street", street);
            intent2.putExtra("country", country);
            startActivity(intent2);
            finish();
        });

        cartButton.setOnClickListener(v -> {
            Intent intent2 = new Intent(Home.this, Cart.class);
            intent2.putExtra("name", name);
            intent2.putExtra("email", email);
            intent2.putExtra("num", phoneNumber);
            intent2.putExtra("street", street);
            intent2.putExtra("country", country);
            startActivity(intent2);
            finish();
        });

        ordersButton.setOnClickListener(v -> {
            Intent intent2 = new Intent(Home.this, orders.class);
            intent2.putExtra("name", name);
            intent2.putExtra("email", email);
            intent2.putExtra("num", phoneNumber);
            intent2.putExtra("street", street);
            intent2.putExtra("country", country);
            startActivity(intent2);
        });

        addressButton.setOnClickListener(v -> {
            Intent intent2 = new Intent(Home.this, Address.class);
            intent2.putExtra("name", name);
            intent2.putExtra("email", email);
            intent2.putExtra("num", phoneNumber);
            intent2.putExtra("street", street);
            intent2.putExtra("country", country);
            startActivity(intent2);
            finish();
        });

        signOutButton.setOnClickListener(v -> {
            Intent intent2 = new Intent(Home.this, MainActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            database.delete("current", null, null);
            startActivity(intent2);
            finish();
        });
    }
}
