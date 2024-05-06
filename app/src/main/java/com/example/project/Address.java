package com.example.project;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Address extends AppCompatActivity {
    String name, email, num, country, street;
    Button backButton, saveButton;
    EditText cityEditText, streetEditText, numberEditText;

    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address2);

        ConstraintLayout constraintLayout = findViewById(R.id.listOrders);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        Intent intent = getIntent();
        country = intent.getStringExtra("country");
        num = intent.getStringExtra("num");
        street = intent.getStringExtra("street");
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");

        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();

        backButton = findViewById(R.id.back);
        saveButton = findViewById(R.id.save);
        cityEditText = findViewById(R.id.city);
        streetEditText = findViewById(R.id.street);
        numberEditText = findViewById(R.id.Phone);

        cityEditText.setText(country);
        streetEditText.setText(street);
        numberEditText.setText(num);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Address.this, Home.class);
                intent2.putExtra("name", name);
                intent2.putExtra("email", email);
                intent2.putExtra("num", num);
                intent2.putExtra("street", street);
                intent2.putExtra("country", country);
                startActivity(intent2);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCity = cityEditText.getText().toString();
                String newStreet = streetEditText.getText().toString();
                String newNumber = numberEditText.getText().toString();

                if (!newCity.equals(country) || !newStreet.equals(street) || !newNumber.equals(num)) {
                    ContentValues values = new ContentValues();
                    values.put("Country", newCity);
                    values.put("Street", newStreet);
                    values.put("Number", newNumber);

                    country = newCity;
                    num = newNumber;
                    street = newStreet;

                    int rowsAffected = database.update("account", values, "email = ?", new String[]{email});

                    if (rowsAffected > 0) {
                        Toast.makeText(getApplicationContext(), "Address updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to update address", Toast.LENGTH_SHORT).show();
                    }
                    database.close();
                } else {
                    Toast.makeText(getApplicationContext(), "No changes were made", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
