package com.example.task91p.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task91p.data.DatabaseHelper;
import com.example.task91p.adapter.ItemAdapter;
import com.example.task91p.R;

import java.util.Arrays;
import java.util.List;

public class ShowActivity extends AppCompatActivity implements ItemAdapter.ItemClickListener {

    // Declare recycler view and its adapter
    RecyclerView rvItems;
    ItemAdapter adapter;

    // Database helper used for reading item names
    DatabaseHelper db;

    // List of item names to display
    List<String> items;
    String item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialise list of item names
        db = new DatabaseHelper(this);
        items = Arrays.asList(db.fetchItemNames());

        // Initialise the recycler view with its adapter
        rvItems = findViewById(R.id.rvItems);
        adapter = new ItemAdapter(items, this);
        adapter.setClickListener(this);
        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
    }

    // On click of an item in the recycler view, goto info activity
    @Override
    public void onItemClick(View v, int position) {
        item = adapter.getItem(position);
        Intent intent = new Intent(ShowActivity.this, InfoActivity.class);
        intent.putExtra("ITEM_NAME", item);
        startActivity(intent);
        finish();
    }
}