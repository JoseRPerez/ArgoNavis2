package com.joserp.argonavis;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.joserp.argonavis.AuxFun.Common_functions;

public class MemorySettingsActivity extends AppCompatActivity {

    private DBHelper db;
    private Context context = this;
    private EditText place, bortle, telescope, eyepiece;
    private ImageButton ibBack, ibSavePlace, ibSaveTelescope, ibSaveEyepiece;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memory_settings_layout);

        //prueba para ocultar barra navegacion android
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Obtener referencias a las vistas
        initializeViews();

        db = DBHelper.getInstance(this);
    }

    private void initializeViews() {
        place = findViewById(R.id.et_place);
        bortle = findViewById(R.id.et_place_bortle);
        telescope = findViewById(R.id.et_telescope);
        eyepiece = findViewById(R.id.et_eyepiece);

        ibBack = findViewById(R.id.ibback);
        ibSavePlace = findViewById(R.id.btn_place);
        ibSaveTelescope = findViewById(R.id.btn_telescope);
        ibSaveEyepiece = findViewById(R.id.btn_eyepiece);


    ibBack.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    });

    ibSavePlace.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            savePlace();
        }
    });

    ibSaveTelescope.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveTelescope();
        }
    });

    ibSaveEyepiece.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveEyepiece();
        }
    });

    }


    private void savePlace() {
        String placeText = place.getText().toString().trim();
        String bortleText = bortle.getText().toString().trim();
        if (placeText.isEmpty() || bortleText.isEmpty()) {
            Common_functions.makeToast(context, "Por favor, complete al menos el campo Nombre");
        } else {
            db.insertPlace(placeText, bortleText);
            Common_functions.makeToast(context, "Lugar guardado");
        }
    }

    private void saveTelescope() {
        String telescopeText = telescope.getText().toString().trim();

        if (telescopeText.isEmpty()) {
            Common_functions.makeToast(context, "Por favor, complete el campo Nombre");
        } else {
            db.insertTelescope(telescopeText);
            Common_functions.makeToast(context, "Telescopio guardado");
        }
    }

    private void saveEyepiece() {
        String eyepieceText = eyepiece.getText().toString().trim();

        if (eyepieceText.isEmpty()) {
            Common_functions.makeToast(context, "Por favor, complete el campo Nombre");
        } else {
            db.insertEyepiece(eyepieceText);
            Common_functions.makeToast(context, "Ocular guardado");
        }

    }




}
