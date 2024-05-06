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

public class MainActivity extends AppCompatActivity {
    Button signButton, loginButton;
    EditText emailEditText, passEditText;
    SQLiteDatabase database;
    String name, email, phoneNumber, country, street;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout constraintLayout = findViewById(R.id.listOrders);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();

        signButton = findViewById(R.id.sign);
        loginButton = findViewById(R.id.login);
        emailEditText = findViewById(R.id.email);
        passEditText = findViewById(R.id.pass);

        signButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Sign_up.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            Cursor cursor = database.rawQuery("SELECT * FROM account", null);
            if (cursor.moveToFirst()) {
                do {
                    if (emailEditText.getText().toString().equals(cursor.getString(1))
                            && passEditText.getText().toString().equals(cursor.getString(2))) {
                        flag = 1;
                        name = cursor.getString(0);
                        email = cursor.getString(1);
                        phoneNumber = cursor.getString(5);
                        country = cursor.getString(3);
                        street = cursor.getString(4);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();

            if (flag == 1) {
                Intent intent = new Intent(this, Home.class);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("num", phoneNumber);
                intent.putExtra("street", street);
                intent.putExtra("country", country);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Error in UserId or Password", Toast.LENGTH_LONG).show();
            }
        });
    }
}
