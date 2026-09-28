package com.joserp.argonavis;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.joserp.argonavis.AuxFun.Common_functions;
import com.joserp.argonavis.AuxFun.VibrationHelper;
import com.joserp.argonavis.Defs.DSO;

import java.util.List;

public class EditDSO extends AppCompatActivity {

    private DBHelper db;
    private EditText et_name, et_size, et_mag, et_refencia, et_other;
    private EditText etAR, etDEC;

    private DSO dso;
    private Spinner sp_constelaciones, sp_clasificacion;

    private String Catalogo, Referencia, Other, Magnitud, Size, Memoria;

    private String Clasificacion;
    private String ID, Name, Info, Carta, Constelacion, Finalidad;
    private List constelaciones, Catalogos;
    private List<String> Tipos, clasificaciones;
    private AutoCompleteTextView AC_CAT;
    private Button Btn_delete, Btn_download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_item_layout);

        //prueba para ocultar barra navegacion android
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        VibrationHelper.initialize(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = DBHelper.getInstance(this);

        Finalidad = getIntent().getExtras().getString("Finalidad de la funcion");

        Btn_delete = findViewById(R.id.bt_remove);
        Btn_download = findViewById(R.id.btn_download);

        AC_CAT = findViewById(R.id.AC_CAT);
        et_refencia = findViewById(R.id.et_referencia);
        et_other = findViewById(R.id.et_other);
        et_name = findViewById(R.id.et_name);
        et_size = findViewById(R.id.et_size);
        et_mag = findViewById(R.id.et_mag);
        etAR = findViewById(R.id.et_AR);
        etDEC = findViewById(R.id.et_DEC);

        sp_clasificacion = findViewById(R.id.sp_clasificacion);
        sp_constelaciones = findViewById(R.id.sp_constelaciones);


        constelaciones = db.getConstellationsArraylist();
        clasificaciones = db.getClasses();
        Catalogos = db.getCatalogListArrayList();


        //Cargamos el spiner
        ArrayAdapter<String> adapterCons = new ArrayAdapter<String>(this, R.layout.my_spiner2, constelaciones);
        ArrayAdapter<String> adapterClasif =  new ArrayAdapter<String>(this, R.layout.my_spiner2, clasificaciones);
        ArrayAdapter<String> adapterCat = new ArrayAdapter<String>(this, R.layout.textview_row, Catalogos);


        AC_CAT.setAdapter(adapterCat);
        sp_constelaciones.setAdapter(adapterCons);
        sp_clasificacion.setAdapter(adapterClasif);

        ImageButton backButton = findViewById(R.id.ib_Back);
        backButton.setOnClickListener(view -> {
            Common_functions.vibrar("corta");
            finish();
        });


        Btn_download.setOnClickListener(v -> {
            String cat = AC_CAT.getText().toString().trim();
            String ref = et_refencia.getText().toString().trim();

            if (ref.isEmpty()) {
                Toast.makeText(this, "Debe completar Catálogo y referencia", Toast.LENGTH_SHORT).show();

            }
            else {
                obtenerCoordenadas(cat+ref, new getCoordinatesFromWeb.SesameCallback() {
                    @Override
                    public void onResult(String ra, String dec) {
                        // Completamos los campos de RA y DEC
                        etAR.setText(ra);
                        etDEC.setText(dec);
                        Toast.makeText(EditDSO.this, "Coordenadas obtenidas", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(EditDSO.this, "Error al obtener coordenadas", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        if (Finalidad.equals("NuevoObjeto") ){
            Btn_delete.setVisibility(View.GONE);
            dso = new DSO();
        } else {
            dso = (DSO) getIntent().getExtras().getSerializable("DSO");
            ID = dso.getId();
            Catalogo = dso.getCat();
            Referencia = dso.getRef();
            Other = dso.getDesig();
            Name = dso.getNombre();
            Constelacion = dso.getCons();
            Clasificacion = dso.getClasif();
            Magnitud = dso.getMag();
            Size = dso.getSize();

            AC_CAT.setText(Catalogo);
            et_refencia.setText(Referencia);

            et_other.setText(Other);
            et_name.setText(Name);
            et_size.setText(Size);
            et_mag.setText(Magnitud);
            etAR.setText(String.valueOf(dso.getAR()));
            etDEC.setText(String.valueOf(dso.getDEC()));

            sp_constelaciones.setSelection(constelaciones.indexOf(dso.getCons())); //la posicion 0 del spinner corresponde al _id = 1 de la BD
            sp_clasificacion.setSelection(clasificaciones.indexOf(dso.getClasif()));
        }
    }

    private void obtenerCoordenadas(String objeto, getCoordinatesFromWeb.SesameCallback callback) {
        new getCoordinatesFromWeb(callback).execute(objeto);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú en la Toolbar
        getMenuInflater().inflate(R.menu.menu_edit_dso, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Common_functions.vibrar("corta");
        switch (item.getItemId()) {
            case R.id.btn_Save:
                SaveChanges();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void SaveChanges(){
        String Cat = AC_CAT.getText().toString();
        String cons = sp_constelaciones.getSelectedItem().toString();
        String clasif = sp_clasificacion.getSelectedItem().toString();
        String type = db.getTypeFromClasification(clasif);

        if (Finalidad.equals("NuevoObjeto")) {
            boolean result = db.NewItemToDB(Cat, et_refencia.getText().toString(), et_other.getText().toString(),
                    et_name.getText().toString(), cons, type, clasif, et_mag.getText().toString(),
                    et_size.getText().toString(), etAR.getText().toString(), etDEC.getText().toString());
            if (result) {
                Common_functions.makeToast(this, "Objeto guardado correctamente");
            } else {
                Common_functions.makeToast(this, "Error al guardar el objeto");
            }
        } else {
            db.UpdateInfoItem(ID, Cat, et_refencia.getText().toString(), et_other.getText().toString(),
                    et_name.getText().toString(), cons, type, clasif, et_mag.getText().toString(),
                    et_size.getText().toString(), etAR.getText().toString(), etDEC.getText().toString());
            Log.d("DEBUG", etAR.getText().toString());

            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();

        }
    }

    public void DeleteItem(View v) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("¿Eliminar objeto?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteItem(ID);
                Toast.makeText(EditDSO.this, "Objeto eliminado", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(v.getContext(), MainActivity.class);
                startActivity(i);
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }


    public void Back(View v){
        finish();
    }

}
