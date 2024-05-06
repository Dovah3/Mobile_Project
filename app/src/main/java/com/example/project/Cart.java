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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class Cart extends AppCompatActivity {
    String name, email, num, country, street;
    Button homeButton, prodButton, confirmButton;
    SQLiteDatabase orderDatabase, productDatabase;
    ListView listView;
    ArrayList<String> productsList = new ArrayList<>();
    static ListViewAdapter adapter;
    TextView totalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ConstraintLayout constraintLayout = findViewById(R.id.listOrders);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        homeButton = findViewById(R.id.home_product);
        prodButton = findViewById(R.id.products_home);
        confirmButton = findViewById(R.id.confirm);
        listView = findViewById(R.id.cartList);
        totalTextView = findViewById(R.id.total);

        int total = 0;
        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        productDatabase = dbHelper.getWritableDatabase();

        Intent intent = getIntent();
        country = intent.getStringExtra("country");
        num = intent.getStringExtra("num");
        street = intent.getStringExtra("street");
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");

        orderDatabase = dbHelper.getWritableDatabase();

        prodButton.setOnClickListener(v -> {
            Intent i2 = new Intent(this, Products.class);
            i2.putExtra("name", name);
            i2.putExtra("email", email);
            i2.putExtra("num", num);
            i2.putExtra("street", street);
            i2.putExtra("country", country);
            startActivity(i2);
        });

        homeButton.setOnClickListener(v -> {
            Intent i2 = new Intent(this, Home.class);
            i2.putExtra("name", name);
            i2.putExtra("email", email);
            i2.putExtra("num", num);
            i2.putExtra("street", street);
            i2.putExtra("country", country);
            startActivity(i2);
        });

        Cursor cursor = productDatabase.rawQuery("select * from current", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String productName = cursor.getString(cursor.getColumnIndex("item"));
                String productQuant = cursor.getString(cursor.getColumnIndex("Quant"));
                String productPrice = cursor.getString(cursor.getColumnIndex("price"));
                int price = Integer.parseInt(productPrice);
                int quant = Integer.parseInt(productQuant);
                int totalItem = price * quant;
                productsList.add(quant + "x " + productName + " " + totalItem + "$");
                total += totalItem;
            } while (cursor.moveToNext());
        }
        adapter = new ListViewAdapter(getApplicationContext(), productsList);
        listView.setAdapter(adapter);
        totalTextView.setText("Total: " + total + "$");

        int finalTotal = total;
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = productDatabase.rawQuery("select * from current", null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String productName = cursor.getString(cursor.getColumnIndex("item"));
                        String productQuant = cursor.getString(cursor.getColumnIndex("Quant"));
                        int orderedQuantity = Integer.parseInt(productQuant);

                        Cursor productCursor = productDatabase.rawQuery("select * from products where Name = ?", new String[]{productName});
                        if (productCursor != null && productCursor.moveToFirst()) {
                            int currentQuantity = productCursor.getInt(productCursor.getColumnIndex("Quant"));
                            int newQuantity = currentQuantity - orderedQuantity;
                            newQuantity = Math.max(0, newQuantity);

                            ContentValues updateValues = new ContentValues();
                            updateValues.put("Quant", newQuantity);
                            int rowsAffected = productDatabase.update("products", updateValues, "Name = ?", new String[]{productName});
                            if (rowsAffected > 0) {
                                // Quantity updated successfully
                            }
                        }
                        if (productCursor != null) {
                            productCursor.close();
                        }

                        ContentValues orderValues = new ContentValues();
                        orderValues.put("item", productName);
                        orderValues.put("Quant", productQuant);
                        orderValues.put("total", finalTotal);
                        orderValues.put("Email", email);
                        productDatabase.insert("Orders", null, orderValues);
                    } while (cursor.moveToNext());
                }
                Toast.makeText(getApplicationContext(), "Order Saved Thank you", Toast.LENGTH_SHORT).show();
                productDatabase.execSQL("DELETE FROM current");
                productsList.clear();
                adapter.notifyDataSetChanged();
                totalTextView.setText("Total: 0$");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedItem = adapter.getItem(position);
                productsList.remove(selectedItem);
                String[] parts = selectedItem.split("\\s+");
                String productName = parts[1];
                adapter.notifyDataSetChanged();
                productDatabase.delete("current", "item=?", new String[]{productName});
                Toast.makeText(getApplicationContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                updateTotalAmount();
            }
        });
    }

    private void updateTotalAmount() {
        int total = 0;
        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM current", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int quantity = cursor.getInt(cursor.getColumnIndex("Quant"));
                int price = cursor.getInt(cursor.getColumnIndex("price"));
                total += quantity * price;
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        totalTextView.setText("Total: $" + total);
    }

    public class ListViewAdapter extends ArrayAdapter<String> {
        private ArrayList<String> itemList;
        private Context context;
        private SQLiteDatabase database;
        private TextView itemNumber;
        private TextView itemName;
        private ImageView productImageView;

        public ListViewAdapter(Context context, ArrayList<String> items) {
            super(context, R.layout.list_row, items);
            this.context = context;
            itemList = items;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_row, null);
                DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(context);
                database = dbHelper.getWritableDatabase();
                itemNumber = convertView.findViewById(R.id.number);
                itemName = convertView.findViewById(R.id.product_name);
                productImageView = convertView.findViewById(R.id.product_image);
            }
            itemNumber.setText(position + 1 + ".");
            itemNumber.setTextSize(13);
            itemName.setText(itemList.get(position));
            itemName.setTextSize(13);
            String imageName = getImageNameFromDatabase(itemList.get(position));
            productImageView.setImageResource(context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()));
            return convertView;
        }

        private String getImageNameFromDatabase(String itemName) {
            String imageName = "";
            String[] parts = itemName.split("\\s+");
            String query = "SELECT Image FROM products WHERE Name = ?";
            itemName = parts[1];
            if (parts[1].equals("Hand")){
                itemName = itemName +" "+ parts[2];
                Cursor cursor = database.rawQuery(query, new String[]{itemName});
                if (cursor != null && cursor.moveToFirst()) {
                    imageName = cursor.getString(cursor.getColumnIndex("Image"));
                    cursor.close();
                }
                return imageName;
            }
            else {
                Cursor cursor = database.rawQuery(query, new String[]{itemName});
                if (cursor != null && cursor.moveToFirst()) {
                    imageName = cursor.getString(cursor.getColumnIndex("Image"));
                    cursor.close();
                }
                return imageName;
            }
        }
    }
}
