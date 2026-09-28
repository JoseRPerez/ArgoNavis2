package com.joserp.argonavis;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
//import com.github.chrisbanes.photoview.PhotoView;

public class object_image extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.object_super_image);

        String filepath = getIntent().getExtras().getString("filepath");

        //PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        //photoView.setImageURI(Uri.parse(filepath));
        SubsamplingScaleImageView imageView = (SubsamplingScaleImageView)findViewById(R.id.photo_view);
        imageView.setImage(ImageSource.uri(filepath));

    }
}