package com.example.project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Sign_up extends AppCompatActivity {
    EditText email, pass, country, street, number, name;
    Button back, register;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ConstraintLayout constraintLayout = findViewById(R.id.listOrders);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        db = dbHelper.getWritableDatabase();
        name = findViewById(R.id.name_reg);
        back = findViewById(R.id.back);
        register = findViewById(R.id.register);
        email = findViewById(R.id.email_reg);
        pass = findViewById(R.id.pass_reg);
        country = findViewById(R.id.country_reg);
        street = findViewById(R.id.street_reg);
        number = findViewById(R.id.phone_reg);
        back.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        });
        register.setOnClickListener(v -> {
            String userName = name.getText().toString().trim();
            String userEmail = email.getText().toString().trim();
            String userPass = pass.getText().toString().trim();
            String userCountry = country.getText().toString().trim();
            String userStreet = street.getText().toString().trim();
            String userNumber = number.getText().toString().trim();

            if (userName.isEmpty() || userEmail.isEmpty() || userPass.isEmpty() || userCountry.isEmpty() || userStreet.isEmpty() || userNumber.isEmpty()) {
                Toast.makeText(getApplicationContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
            } else {
                Cursor cursor = db.rawQuery("SELECT * FROM account WHERE Email = ?", new String[]{userEmail});
                if (cursor != null && cursor.getCount() > 0) {
                    Toast.makeText(getApplicationContext(), "Email already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    db.execSQL("INSERT INTO account VALUES('" + userName + "', '" + userEmail + "','" + userPass + "', '" + userCountry + "', '" + userStreet + "', '" + userNumber + "')");
                    Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_SHORT).show();

                    email.setText("");
                    name.setText("");
                    pass.setText("");
                    country.setText("");
                    street.setText("");
                    number.setText("");
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        });
    }
}
