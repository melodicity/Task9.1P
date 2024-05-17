package com.example.task91p.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.task91p.data.DatabaseHelper;
import com.example.task91p.model.Item;
import com.example.task91p.R;

public class InfoActivity extends AppCompatActivity {

    // Declare views
    Button btnRemove;
    TextView tvName, tvDate, tvLocation, tvDescription, tvPhone;

    // Database helper used for reading item data
    DatabaseHelper db;

    // The selected item, and its name
    Item item;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialise views
        btnRemove = findViewById(R.id.btnRemove);
        tvName = findViewById(R.id.tvName);
        tvDate = findViewById(R.id.tvDate);
        tvLocation = findViewById(R.id.tvLocation);
        tvDescription = findViewById(R.id.tvDescription);
        tvPhone = findViewById(R.id.tvPhone);

        // Get the selected item name from the intent
        Intent intent = getIntent();
        name = intent.getStringExtra("ITEM_NAME");

        // Get the matching item from the database
        db = new DatabaseHelper(this);
        item = db.fetchItem(name);

        // Set views to show item data
        tvName.setText(item.getName());
        tvDate.setText(item.getDate());
        tvLocation.setText(item.getLocation().toString());
        tvDescription.setText(item.getDescription());
        tvPhone.setText(item.getPhone());

        // On "remove" click, remove the item from the DB and go back
        btnRemove.setOnClickListener(v -> {
            if (!db.deleteItem(name)) {
                // Delete failed, send a toast
                Toast.makeText(InfoActivity.this, "Error deleting item from database", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent1 = new Intent(InfoActivity.this, ShowActivity.class);
            startActivity(intent1);
            finish(); // closes this activity
        });
    }
}