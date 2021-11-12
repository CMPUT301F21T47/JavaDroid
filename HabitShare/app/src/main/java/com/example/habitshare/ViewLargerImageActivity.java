package com.example.habitshare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewLargerImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image);

        ImageView imageView = findViewById(R.id.view_habit_event_image_large);
        Intent intent = getIntent();
        Uri imageURI = Uri.parse(intent.getStringExtra("image_uri"));
        imageView.setImageURI(imageURI);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}