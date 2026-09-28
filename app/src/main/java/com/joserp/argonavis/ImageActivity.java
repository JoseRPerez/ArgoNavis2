package com.joserp.argonavis;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;


public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.object_super_image);

        String filepath = getIntent().getExtras().getString("filepath");

        SubsamplingScaleImageView imageView = findViewById(R.id.photo_view);
        imageView.setImage(ImageSource.uri(filepath));
    }
}