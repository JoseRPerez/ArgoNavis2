package com.joserp.argonavis;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ImportListTask extends AsyncTask<Boolean, Void, Boolean> {
    private Context context;
    private DBHelper db;

    private InputStream in;


    public ImportListTask(Context context, InputStream in) {
        this.context = context;
        this.in = in;
        db = DBHelper.getInstance(context);
    }

    @Override
    protected Boolean doInBackground(Boolean... nothing) {
        Boolean IsnewDSOCreados = false;
        // Lee el archivo CSV y realiza las inserciones
        //File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Argonavis", "Lista.csv");

        //File file = new File(String.valueOf(in));

        try {
            //BufferedReader csvReader = new BufferedReader(new FileReader(file));
            BufferedReader csvReader = new BufferedReader(new InputStreamReader( in ));

            //La primera fila es el nombre de la lista y si es secuencial
            String filaUno[] = csvReader.readLine().split(",");
            String ListName = filaUno[0];

            boolean existeListName = db.ComprobarSiExisteNombreLista(ListName);
            if (existeListName) {
                ListName += "_";
            }

            db.AddNewUserList(ListName, filaUno[1]);

            String row;
            while ((row = csvReader.readLine()) != null) {
                boolean ans = db.WriteImprotedList(row, ListName);
                IsnewDSOCreados = IsnewDSOCreados && ans;
/*                if (IsnewDSOCreados) {
                    Toast toast = Toast.makeText(context, "Nuevo Objeto Creado: " + data[0] + data[1], Toast.LENGTH_SHORT);
                    toast.show();
                }*/
            }
            csvReader.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return IsnewDSOCreados;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context, "La importación puede durar algún tiempo dependiendo del tamaño de la lista", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onPostExecute(Boolean IsnewDSOCreados) {
        super.onPostExecute(IsnewDSOCreados);
        String msg;
        if (IsnewDSOCreados) {
            msg = "La importación ha finalizado. Se han creado nuevos objetos en el catálogo";
        } else {
            msg = "La importación ha finalizado";
        }
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }



}
