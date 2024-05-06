package com.example.project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class orders extends AppCompatActivity {
    Button back;
    ListView listView;
    ArrayAdapter<String> adapter;
    SQLiteDatabase database;
    String userName, userEmail, userPhoneNumber, userCountry, userStreet;
    ListView ordersListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        ConstraintLayout constraintLayout = findViewById(R.id.main);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
        back = findViewById(R.id.back);
        ordersListView = findViewById(R.id.listOrders);
        Intent intent = getIntent();
        userCountry = intent.getStringExtra("country");
        userPhoneNumber = intent.getStringExtra("num");
        userStreet = intent.getStringExtra("street");
        userName = intent.getStringExtra("name");
        userEmail = intent.getStringExtra("email");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(orders.this, Home.class);
                intent.putExtra("name", userName);
                intent.putExtra("email", userEmail);
                intent.putExtra("num", userPhoneNumber);
                intent.putExtra("street", userStreet);
                intent.putExtra("country", userCountry);
                startActivity(intent);
                finish();
            }
        });

        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();

        ArrayList<String> pastOrders = loadPastOrders();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pastOrders);
        ordersListView.setAdapter(adapter);
    }

    private ArrayList<String> loadPastOrders() {
        ArrayList<String> pastOrders = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM Orders WHERE Email = ?", new String[]{userEmail});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String itemName = cursor.getString(cursor.getColumnIndex("item"));
                String quantity = cursor.getString(cursor.getColumnIndex("Quant"));
                String total = cursor.getString(cursor.getColumnIndex("total"));
                String orderDetails = quantity + "x " + itemName + " Total: $" + total;
                pastOrders.add(orderDetails);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return pastOrders;
    }
}
