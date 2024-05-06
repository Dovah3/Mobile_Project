package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

public class Products extends AppCompatActivity {
    Button home, cart;
    SQLiteDatabase database;
    ListView listView;
    String userName, userEmail, userPhoneNumber, userCountry, userStreet;
    ProductAdapter adapter;
    List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        ConstraintLayout constraintLayout = findViewById(R.id.listOrders);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();

        home = findViewById(R.id.home_product);
        cart = findViewById(R.id.cart_home);
        listView = findViewById(R.id.listview);
        Intent intent = getIntent();
        userCountry = intent.getStringExtra("country");
        userPhoneNumber = intent.getStringExtra("num");
        userStreet = intent.getStringExtra("street");
        userName = intent.getStringExtra("name");
        userEmail = intent.getStringExtra("email");

        home.setOnClickListener(v -> {
            Intent i2 = new Intent(Products.this, Home.class);
            i2.putExtra("name", userName);
            i2.putExtra("email", userEmail);
            i2.putExtra("num", userPhoneNumber);
            i2.putExtra("street", userStreet);
            i2.putExtra("country", userCountry);
            startActivity(i2);
            finish();
        });

        Cursor cursor = database.rawQuery("select * from products WHERE Quant > 0", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                productList.add(new Product(cursor.getString(cursor.getColumnIndex("Name")), cursor.getString(cursor.getColumnIndex("Price")), cursor.getString(cursor.getColumnIndex("Image"))));
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter = new ProductAdapter(this, productList);
        listView.setAdapter(adapter);

        cart.setOnClickListener(v -> {
            Intent i2 = new Intent(Products.this, Cart.class);
            i2.putExtra("name", userName);
            i2.putExtra("email", userEmail);
            i2.putExtra("num", userPhoneNumber);
            i2.putExtra("street", userStreet);
            i2.putExtra("country", userCountry);
            startActivity(i2);
            finish();
        });
    }

    class ProductAdapter extends ArrayAdapter<Product> {
        private Context context;
        private List<Product> products;

        public ProductAdapter(Context context, List<Product> products) {
            super(context, 0, products);
            this.context = context;
            this.products = products;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_product, parent, false);
            }
            TextView productName = convertView.findViewById(R.id.product_name);
            TextView productPrice = convertView.findViewById(R.id.product_price);
            ImageView productImage = convertView.findViewById(R.id.product_image);
            TextView quantityText = convertView.findViewById(R.id.quantity);
            Button decreaseButton = convertView.findViewById(R.id.decrease_quantity);
            Button increaseButton = convertView.findViewById(R.id.increase_quantity);
            Button addToCartButton = convertView.findViewById(R.id.add_to_cart);

            Product product = getItem(position);
            productName.setText(product.getName());
            productPrice.setText("$" + product.getPrice());
            productImage.setImageResource(context.getResources().getIdentifier(product.getImageResource(), "drawable", context.getPackageName()));

            decreaseButton.setOnClickListener(v -> {
                int quantity = Integer.parseInt(quantityText.getText().toString());
                if (quantity > 1) {
                    quantity--;
                    quantityText.setText(String.valueOf(quantity));
                }
            });

            increaseButton.setOnClickListener(v -> {
                int quantity = Integer.parseInt(quantityText.getText().toString());
                int availableQuantity = getAvailableQuantity(productName.getText().toString());
                if (quantity < availableQuantity) {
                    quantity++;
                    quantityText.setText(String.valueOf(quantity));
                } else {
                    Toast.makeText(context, "Cannot increase quantity. Exceeds available quantity.", Toast.LENGTH_SHORT).show();
                }
            });

            addToCartButton.setOnClickListener(v -> {
                int purchaseQuantity = Integer.parseInt(quantityText.getText().toString());
                if (purchaseQuantity > 0) {
                    Cursor existingItemCursor = database.rawQuery("SELECT * FROM current WHERE item = ?", new String[]{productName.getText().toString()});
                    if (existingItemCursor.getCount() > 0) {
                        existingItemCursor.moveToFirst();
                        ContentValues updateValues = new ContentValues();
                        updateValues.put("Quant", purchaseQuantity);
                        database.update("current", updateValues, "item = ?", new String[]{productName.getText().toString()});
                        Toast.makeText(context, "Quantity updated in cart", Toast.LENGTH_SHORT).show();
                    } else {
                        ContentValues values = new ContentValues();
                        values.put("item", productName.getText().toString());
                        values.put("Quant", quantityText.getText().toString());
                        values.put("price", productPrice.getText().toString().substring(1));
                        database.insert("current", null, values);
                        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
                    }
                    existingItemCursor.close();
                } else {
                    Toast.makeText(context, "Please add at least one quantity", Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }

        private int getAvailableQuantity(String productName) {
            Cursor cursor = database.rawQuery("SELECT Quant FROM products WHERE Name = ?", new String[]{productName});
            int availableQuantity = 0;
            if (cursor != null && cursor.moveToFirst()) {
                availableQuantity = cursor.getInt(cursor.getColumnIndex("Quant"));
                cursor.close();
            }
            return availableQuantity;
        }
    }
}
