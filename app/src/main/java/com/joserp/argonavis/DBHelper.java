package com.joserp.argonavis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.joserp.argonavis.Defs.DSO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jose on 09/04/2017.


public class DBHelper extends SQLiteOpenHelper {
//public class DBHelper extends SQLiteAssetHelper{
    private static String DB_PATH = "Argonavis";
    private static final String DB_NAME = "argonavisdb.sqlite";
    private static final int DB_VER = 3;
    public static final String DB_TABLE = "Catalog";
    //public static final String DB_COLUMN = "TaskName";


    //Forma nueva con singleton, solo una instancia por activity
    public DBHelper(Context context) {
        super(context, Environment.getExternalStorageDirectory().getPath()
                + File.separator + DB_PATH
                + File.separator + DB_NAME, null, DB_VER);
    }

 */

public class DBHelper extends SQLiteOpenHelper {

    // Cambiamos el nombre del archivo para coincidir con MainActivity
    private static final String DB_NAME = "argonavisdb.sqlite";
    private static final int DB_VER = 3;
    public static final String DB_TABLE = "Catalog";

    // Constructor de DBHelper
    public DBHelper(Context context) {
        // Al pasar solo DB_NAME, Android busca automáticamente en el almacenamiento interno privado de la App
        super(context, DB_NAME, null, DB_VER);
    }


    //Prueba de instancia Singleton de DBHelper para evitar que se abran más instancias de la misma
    //Variable estática privada para almacenar la instancia única de DBHelper
    //En la version antigua la siguiente linea no estaba
    private static DBHelper instance;

    //El siguiente modulo no estaba en la version antigua. Con singleton es necesario que este
    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tu código actual de onCreate...
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Tu código actual de onUpgrade...
    }

/*

public Boolean getAppData(String Column){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT " + Column + " FROM appData", null);
        cur.moveToFirst();
        String item = cur.getString(0);
        cur.close();
        return Boolean.valueOf(item);
    }

    public List<List<String>> getNeighMaps(String mapName){
        SQLiteDatabase db = this.getReadableDatabase();
        List<List<String>> resultado = new ArrayList<>();

        //Usando ? para evitar SQL Injection
        //Cursor cur = db.rawQuery("SELECT L, R, U, D FROM Cartas Where Name = ?", new String[]{mapName});
        //Buscamos cartas ignorando si esta en mayusculas o no para ser menos restrictivos
        Cursor cur = db.rawQuery("SELECT L, R, U, D FROM Cartas Where LOWER(Name) = LOWER(?)", new String[]{mapName});

        if (cur.moveToFirst()){
            String L = cur.getString(0);
            String R = cur.getString(1);
            String U = cur.getString(2);
            String D = cur.getString(3);

            // Procesar cada columna y agregarla a la lista de resultados
            resultado.add(Arrays.asList(L.split(","))); // Dividir L por comas
            resultado.add(Arrays.asList(R.split(","))); // Dividir R por comas
            resultado.add(Arrays.asList(U.split(","))); // Dividir U por comas
            resultado.add(Arrays.asList(D.split(","))); // Dividir D por comas
        }
        cur.close();

        return resultado;
    }

    public Cursor getObjectInSequenceList(String ID_List) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ltrim(CAT || ' '|| REF) as CAT, NAME, TYPE, CLASS, TOVIEW, VISTO, RATE, _id FROM Catalog WHERE _id in (SELECT Id_Object FROM UserLists WHERE ListKey = ?)";
        return db.rawQuery(query, new String[] {ID_List});
    }


    public Map<String, List<String>> getPlacesData() {
        Map<String, List<String>> result = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM Places";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                if (!cursor.isNull(0)) result.put(cursor.getString(1), new String[] {cursor.getString(3), cursor.getString(4)});
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }


    public String getMapsDeepSearch_original(String dsoID) {
        // Esta funcion no contempla los casos en los que la carta atravies ra = 0h
        SQLiteDatabase db = this.getReadableDatabase();
        String maps = "";

        String query = "SELECT GROUP_CONCAT(Cartas.Name, ',') AS Names FROM Cartas JOIN Catalog " +
                "ON Catalog._id = ? WHERE Catalog.ra BETWEEN Cartas.ramin AND Cartas.ramax  AND " +
                "Catalog.dec BETWEEN Cartas.decmin AND Cartas.decmax";

        Cursor cursor = db.rawQuery(query, new String[] {dsoID});

        //Verificamos si el cursor tiene datos para evitar excepciones
        if (cursor != null && cursor.moveToFirst()) {
            maps = cursor.getString(0);
            if (maps == null) {
                maps = "";
            }
            cursor.close();
        }
        //Se devuelven todas las cartas concatatenadas en una sola cadena y separadas por ","
        return maps;
    }


    public String getMapsDeepSearch_mejorada(String dsoID) {
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder maps = new StringBuilder();
        Set<String> uniqueMaps = new HashSet<>(); // Para evitar duplicados

        // Consulta única que maneja todos los casos (normal, cruce RA=0h, y RA>24h)
        String query = "SELECT Cartas.Name FROM Cartas JOIN Catalog " +
                "ON Catalog._id = ? WHERE " +
                "(" +
                // Caso normal (ramin <= ramax)
                "(Cartas.ramin <= Cartas.ramax AND Catalog.ra BETWEEN Cartas.ramin AND Cartas.ramax) " +
                "OR " +
                // Caso cruce RA=0h (ramin > ramax)
                "(Cartas.ramin > Cartas.ramax AND (Catalog.ra >= Cartas.ramin OR Catalog.ra <= Cartas.ramax)) " +
                ") " +
                "AND Catalog.dec BETWEEN Cartas.decmin AND Cartas.decmax";

        Cursor cursor = db.rawQuery(query, new String[]{dsoID});

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String mapName = cursor.getString(0);
                    if (mapName != null) {
                        uniqueMaps.add(mapName);
                    }
                }
            } finally {
                cursor.close();
            }
        }

        // Convertir el Set a String concatenado
        for (String map : uniqueMaps) {
            if (maps.length() > 0) {
                maps.append(",");
            }
            maps.append(map);
        }

        return maps.toString();
    }

    public float[] getPlaceCoordinates(String placeName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = null;

        try {
            String query;
            String[] selectionArgs;

            if (placeName == null) {
                query = "SELECT lat, lon FROM Places LIMIT 1";
                selectionArgs = null;
            } else {
                query = "SELECT lat, lon FROM Places WHERE name = ?";
                selectionArgs = new String[]{placeName};
            }

            cur = db.rawQuery(query, selectionArgs);

            if (cur != null && cur.moveToFirst()) {
                // Verificar si alguno de los valores es NULL
                if (cur.isNull(0) || cur.isNull(1)) {
                    return null;
                }

                return new float[]{cur.getFloat(0), cur.getFloat(1)};
            }
            return null;
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }
 */


    public float[] getMapData(String mapName){
        SQLiteDatabase db = this.getReadableDatabase();
        float[] resultado = new float[4];
        Cursor cur = db.rawQuery("SELECT ramin, ramax, decmin, decmax FROM Cartas Where Name = ?", new String[]{mapName});
        if (cur.moveToFirst()){
            resultado[0] = cur.getFloat(0);
            resultado[1] = cur.getFloat(1);
            resultado[2] = cur.getFloat(2);
            resultado[3] = cur.getFloat(3);
        }
        cur.close();
        return resultado;
    }


    public List<String> getNeighMapsDirection(String mapName, String direction){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        String query = "SELECT " + direction + " FROM Cartas Where LOWER(Name) = LOWER(?)";

        try {
            cursor = db.rawQuery(query, new String[]{mapName});

            if (cursor != null && cursor.moveToFirst()) {
                String item = cursor.getString(0);
                return Arrays.asList(item.split(","));
            } else {
                return new ArrayList<>();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
    }


    public Cursor getDSObyRef(String text) {
        SQLiteDatabase db = this.getReadableDatabase();

        /*String query = "SELECT _id, " +
                "ltrim(CAT || ' ' || REF || " +
                "CASE WHEN NAME IS NULL OR NAME = '' THEN '' ELSE ' (' || NAME || ')' END) as NAME, " +
                "TYPE, CLASS " +
                "FROM Catalog " +
                "WHERE REF LIKE ? " +
                "LIMIT 6";

        return db.rawQuery(query, new String[]{"%" + ref + "%"});
        */

        String query =
                "SELECT _id, " +
                        "ltrim(CAT || ' ' || REF || " +
                        "CASE WHEN NAME IS NULL OR NAME = '' THEN '' ELSE ' (' || NAME || ')' END) AS NAME, " +
                        "TYPE, CLASS " +
                        "FROM Catalog " +
                        "WHERE CAST(REF AS TEXT) LIKE ? OR COALESCE(NAME, '') LIKE ? " +
                        "LIMIT 8";

        String pattern = "%" + text + "%";
        return db.rawQuery(query, new String[]{ pattern, pattern });
    }


    public void updateDSOField(String dsoID, String Field, String Info){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Field, Info);

        db.update("Catalog", values, "_id=?", new String[] {dsoID});
        //db.close();
    }



    public boolean NewItemToDB(String cat, String ref, String other, String name, String con,
                               String tipo, String clas, String mag, String size, String AR,
                               String DEC) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("CAT", cat);
        values.put("REF", ref);
        values.put("OTHER", other);
        values.put("NAME", name);
        values.put("CON", con);
        values.put("TYPE", tipo);
        values.put("CARTA", "");
        values.put("CLASS", clas);
        values.put("MAG", mag);
        values.put("SIZE", size);
        values.put("ra", AR);
        values.put("dec", DEC);

        long result = db.insert("Catalog", null, values);
        return result != -1;
    }


    public boolean NewItemToDB2(String cat, String ref, String other, String name, String con,
                                String tipo, String clas, String AR, String DEC, String Mag, String Size) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("CAT", cat);
        values.put("REF", ref);
        values.put("OTHER", other);
        values.put("NAME", name);
        values.put("CON", con);
        values.put("TYPE", tipo);
        values.put("CLASS", clas);
        values.put("MAG", Mag);
        values.put("SIZE", Size);
        values.put("ra", AR);
        values.put("dec", DEC);

        long result = db.insert("Catalog", null, values);
        return result != -1;
    }

    public long NewEasyItemToDB3(String cat, String ref, String other, String name, String con,
                                String tipo, String clas, String mag, String size, String AR,
                                String DEC, String rate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("CAT", cat);
        values.put("REF", ref);
        values.put("OTHER", other);
        values.put("NAME", name);
        values.put("CON", con);
        values.put("TYPE", tipo);
        values.put("CARTA", "");
        values.put("CLASS", clas);
        values.put("MAG", mag);
        values.put("SIZE", size);
        values.put("ra", AR);
        values.put("dec", DEC);
        values.put("VISTO", "true");
        values.put("RATE", rate);

        long result = db.insert("Catalog", null, values);
        return result;
    }



    public void UpdateInfoItem(String id, String cat, String ref, String other, String name,
                               String con, String tipo, String clas, String mag, String size,
                               String AR, String DEC) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("CAT", cat);
        values.put("REF", ref);
        values.put("OTHER", other);
        values.put("NAME", name);
        values.put("CON", con);
        values.put("TYPE", tipo);
        values.put("CLASS", clas);
        values.put("MAG", mag);
        values.put("SIZE", size);


        if (DEC.trim().isEmpty() || AR.trim().isEmpty()) {
            Log.d("DEBUG", "AR o DEC vacios");
            values.putNull("RA");
            values.putNull("DEC");
        } else {
            values.put("RA", AR);
            values.put("DEC", DEC);
        }

        db.update("Catalog", values, "_id=?", new String[]{id});
    }


    public void EditSeqField(String idList, boolean isSeq) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isSEQ", String.valueOf(isSeq));
        db.update("ULists", values, "_id=?", new String[]{idList});
    }


    public void RenameList(String List_ID, String NewName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", NewName);
        db.update("ULists", values, "_id=?", new String[]{List_ID});
    }


    public void UpdateRateItem(String id, String Rate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("RATE", Rate);
        db.update("Catalog", values, "_id=?", new String[]{id});
    }

    public void UpdateConstellationCharts(String ConsName, String Charts){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("CHARTS", Charts);
        db.update("Cons", values, "NAME = ?", new String[]{ConsName});
    }



    public void AddItem2UserList(String id, String UserList){
        //UserList es el nombre de la lista
        //id es el ID del objeto DSO

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur;

        //Obtenemos los elementos que hay en ULists
        cur = db.query("ULists", new String[]{"dsos"}, "name=?", new String[] {UserList}, null, null, null);
        cur.moveToFirst();

        String dsos = cur.getString(0);
        String[] lista_dsos = dsos.split(",");

        //Comprobamos que el elemento no esté en la lista
        if (!Arrays.asList(lista_dsos).contains(id)){
            dsos += "," + id;
            ContentValues values = new ContentValues();
            values.put("dsos", dsos);
            db.update("ULists", values, "name=?", new String[] {UserList});

        }
        cur.close();
    }


    public void RemoveItemFromUserList(String objectID, String listID){
        SQLiteDatabase db = this.getWritableDatabase();

        String[] objectIds = getDSOsIDinUserList(listID);

        // Convertir el array a un ArrayList
        List<String> updatedList = new ArrayList<>(Arrays.asList(objectIds));

        // Eliminar el objetoID de la lista
        updatedList.remove(objectID);

        String updateDsos = String.join(",", updatedList);

        ContentValues values = new ContentValues();
        values.put("dsos", updateDsos);
        //db.execSQL( "UPDATE ULists SET dsos = ? WHERE _id = ? ", new String[] {updateDsos, listID} );
        db.update("ULists", values, "_id = ?", new String[]{listID});
    }


    public String[] getDSOsIDinUserList(String ID_List) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("ULists", new String[]{"dsos"}, "_id=?", new String[] {ID_List}, null, null, null);
        cursor.moveToFirst();
        String dsos = cursor.getString(0);
        cursor.close();
        return dsos.split(",");
    }


    public Map<String, List<String>> getAllMemoryExtraData() {
        Map<String, List<String>> result = new HashMap<>();
        result.put("places", new ArrayList<>());
        result.put("telescopes", new ArrayList<>());
        result.put("eyepieces", new ArrayList<>());

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT DISTINCT place FROM Memories";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                if (!cursor.isNull(0)) result.get("places").add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        query = "SELECT DISTINCT telescope FROM Memories";
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                if (!cursor.isNull(0)) result.get("telescopes").add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        query = "SELECT DISTINCT eyepiece FROM Memories";
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                if (!cursor.isNull(0)) result.get("eyepieces").add(cursor.getString(0));
            } while (cursor.moveToNext());
        }


        cursor.close();
        db.close();
        return result;
    }


    public String getMapsDeepSearch(String dsoID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String maps = "";

        String query = "SELECT GROUP_CONCAT(Name, ',') AS Names FROM (" +
                "SELECT DISTINCT Cartas.Name FROM Cartas JOIN Catalog " +
                "ON Catalog._id = ? WHERE " +
                "(" +
                "(Cartas.ramin <= Cartas.ramax AND Catalog.ra BETWEEN Cartas.ramin AND Cartas.ramax) OR " +
                "(Cartas.ramin > Cartas.ramax AND (Catalog.ra >= Cartas.ramin OR Catalog.ra <= Cartas.ramax))" +
                ") " +
                "AND Catalog.dec BETWEEN Cartas.decmin AND Cartas.decmax" +
                ")";

        Cursor cursor = db.rawQuery(query, new String[]{dsoID});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                maps = cursor.getString(0);
                if (maps == null) {
                    maps = "";
                }
            }
            cursor.close();
        }

        return maps;
    }


    public Cursor getTenRandom(String dsoID) {
        SQLiteDatabase db = this.getReadableDatabase();

        float dsoAR;
        float dsoDEC;

        String query = "SELECT ra, dec FROM Catalog WHERE _id = ?";
        Cursor cursor = db.rawQuery(query, new String[] {dsoID} );

        if (cursor.moveToFirst()) {
            // Obtener los valores de ra y dec
            String raValue = cursor.getString(0);
            String decValue = cursor.getString(1);

            // Verificar si los valores no son nulos ni vacíos
            if (raValue != null && decValue != null) {
                try {
                    dsoAR = Float.parseFloat(raValue.trim());
                    dsoDEC = Float.parseFloat(decValue.trim());

                    float deltaAR = 1.5f;  //area de 3 horas de AR
                    float deltaDEC = 17f;  //area de 34 grados de DEC

                    String minAR = String.valueOf(dsoAR - deltaAR);
                    String maxAR = String.valueOf(dsoAR + deltaAR);
                    String minDEC = String.valueOf(dsoDEC - deltaDEC);
                    String maxDEC = String.valueOf(dsoDEC + deltaDEC);

                    cursor.close();

                    String query2 = "SELECT _id, ltrim(CAT || \" \" || REF || replace(\" (\" " +
                            "|| NAME || \")\", \"()\", \"\")) as NAME, TYPE, CLASS FROM Catalog WHERE " +
                            "Catalog.ra BETWEEN ? AND ? AND Catalog.dec BETWEEN ? AND ?  " +
                            "AND Catalog.ra != '' AND Catalog.dec != '' ORDER BY RANDOM() LIMIT 10";

                    return db.rawQuery(query2, new String[]{minAR, maxAR, minDEC, maxDEC});

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    cursor.close();
                    return null;
                }
            } else {
                cursor.close();
                return null;
            }
        } else {
            cursor.close();
            return  null;
        }
    }


    public String[] getIDsInUserList(String ID_List) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT dsos FROM ULists WHERE _id = ?";
        Cursor cursor = db.rawQuery(query, new String[] {ID_List});
        cursor.moveToFirst();
        String dsos = cursor.getString(0);
        return dsos.split(",");
    }


    public Cursor getDsosInListOrderByCon(String ListID) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] ids = getIDsInUserList(ListID); //Devuelve un array de ID's

        if (ids.length == 0) {
            return null; // Si no hay ID's en la lista retorna null
        } else {
            String placeholders = new String(new char[ids.length]).replace("\0", "?,"); //Prepara el string ?,?,?,...
            placeholders = placeholders.substring(0, placeholders.length() - 1); //elimina la ultima ,

            String query = "SELECT ltrim(CAT || ' ' || REF) as CAT, CON, NAME, TYPE, CLASS, TOVIEW, VISTO, RATE, _id FROM Catalog WHERE _id IN (" + placeholders + ") ORDER BY CON ASC";
            return db.rawQuery(query, ids); //le pasamos la consulta y los ids
        }
    }

    public Cursor getDsosInListNativeOrder(String listID) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] ids = getIDsInUserList(listID); // Devuelve un array de ID's

        if (ids.length == 0) {
            return null; // Si no hay ID's en la lista, retorna null
        }

        // Construir la parte de los placeholders para el IN
        String placeholders = TextUtils.join(",", Collections.nCopies(ids.length, "?"));

        // Construir la consulta SQL
        StringBuilder query = new StringBuilder();
        query.append("SELECT ltrim(CAT || ' ' || REF) as CAT, NAME, TYPE, CLASS, TOVIEW, VISTO, RATE, _id ");
        query.append("FROM Catalog ");
        query.append("WHERE _id IN (").append(placeholders).append(") ");

        // Añadir ORDER BY CASE para mantener el orden de la lista de IDs
        query.append("ORDER BY CASE _id ");
        for (int i = 0; i < ids.length; i++) {
            query.append("WHEN ? THEN ").append(i).append(" ");
        }
        query.append("END");

        // Preparar los parámetros para la consulta
        String[] queryParams = new String[ids.length * 2];
        System.arraycopy(ids, 0, queryParams, 0, ids.length); // Copiar IDs para el IN
        System.arraycopy(ids, 0, queryParams, ids.length, ids.length); // Copiar IDs para el ORDER BY

        // Ejecutar la consulta
        return db.rawQuery(query.toString(), queryParams);
    }


    public Cursor getToObserveList_cursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ltrim(CAT || ' ' || REF) as CAT, CON, NAME, TYPE, CLASS, TOVIEW, VISTO, RATE, _id FROM Catalog WHERE TOVIEW = 'true' ORDER BY CON ASC";
        return db.rawQuery(query, null);
    }

    public String getChartsInconstellation(String ConsName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("select CHARTS from Cons Where NAME = ?", new String[] {ConsName});
        cur.moveToFirst();
        String Mapas = cur.getString(0);
        cur.close();
        return Mapas;
    }

    public String getChartsInconstellationID(String ConsID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("select CHARTS from Cons Where _id = ?", new String[] {ConsID});
        cur.moveToFirst();
        String Mapas = cur.getString(0);
        cur.close();
        return Mapas;
    }



    public void recalcular() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Actualizamos el numero de objetos en cada constelacion
        db.execSQL( "UPDATE Cons SET dsoIn = (SELECT Count(*) Valor FROM Catalog WHERE Catalog.CON = Cons.NAME)" );
        //Actualizamos el numero de objetos vistos en cada constelacion
        db.execSQL( "UPDATE Cons SET viewed = (SELECT Count(*) Valor FROM Catalog WHERE Catalog.CON = Cons.NAME AND Catalog.VISTO == 'true')" );
        //db.close();
    }


    public void recalcularVistosInCons(String Cons){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " UPDATE Cons SET dsoIn = (SELECT Count(*) Valor FROM Catalog WHERE Catalog.CON = Cons.NAME AND Catalog.VISTO == 'true') WHERE Cons.NAME = ? ";
        db.rawQuery( query, new String[] { Cons } );
    }


    public boolean ComprobarSiExisteNombreLista(String ListName) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT EXISTS (SELECT 1 FROM ULists WHERE name = ?) AS resultado";
        Cursor cur = db.rawQuery(query, new String[] {ListName});
        cur.moveToFirst();
        return cur.getInt(0) == 1 ? true : false;
    }


    public void AddNewUserList(String Name, String Seq) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", Name);
        values.put("isSeq", Seq);
        db.insert("ULists", null, values);
    }



    public void Add2View(String id, String ToView) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TOVIEW", ToView);

        db.update("Catalog", values, "_id=?", new String[]{id});
        //db.close();
    }


    public void Add2Viewed(String id_dso, String Viewed) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Actualizamos el valor de le la columna VISTO de la tabla Catalog con el valor Viewed
        ContentValues values = new ContentValues();
        values.put("VISTO", Viewed);
        db.update("Catalog", values, "_id=?", new String[]{id_dso});

        //Sumamos o Restamos uno al valor de vistos que hay en la columna viewed de la tabla Cons
        Integer delta;

        // Obtener el nombre de la constelación a partir del ID
        String constellationName = getConstellationNameFromDSOID(id_dso);

        //Actualizamos el numero de objetos vistos en la constelacion
        if (Viewed.equals("true")){
            db.execSQL("UPDATE Cons SET Viewed = Viewed + 1 WHERE NAME = ?", new String[]{constellationName});
        } else {
            db.execSQL("UPDATE Cons SET Viewed = Viewed - 1 WHERE NAME = ?", new String[]{constellationName});
        }
    }



    public void deleteItem(String dsoID) {
        SQLiteDatabase db = this.getWritableDatabase();

        String cons = this.getIDConstellationForDSOid(dsoID);

        //this.recalcularTotalInCons(conID);    //Integrado en triggers
        this.recalcularVistosInCons(cons);

        //eliminamos el elemento de la base de Catalog
        db.delete("Catalog", "_id = ?", new String[]{dsoID});
        //db.close();
    }


    public String getIDConstellationForDSOid(String dsoID){
        SQLiteDatabase db = this.getReadableDatabase();
        String cons;
        Cursor cur = db.query("Catalog", new String[]{"CON"}, "_id=?", new String[]{dsoID},null, null, null);
        cur.moveToFirst();
        cons = cur.getString(0);
        cur.close();
        return cons;
    }


    public List<String> fromTypeGetClasses(String Type){
        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT Class FROM Catalog where type = ?", new String[]{Type});
        while (cursor.moveToNext()){
            lista.add(cursor.getString(0));
        }
        cursor.close();
        //db.close();
        return lista;
    }


    public List<String> getCatalogListArrayList() {
        List<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query("Catalog", new String[]{"CAT"}, "CAT !=''", null, "CAT", null, "CAT ASC");
        while (cur.moveToNext()) {
            int index = cur.getColumnIndex("CAT");
            lista.add(cur.getString(index));
        }
        cur.close();
        //db.close();
        //lista.add(0,"*Todos*");
        return lista;
    }


    public List<String> getSavedLists() {
        List<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query("ULists", new String[]{"name"}, null, null, null, null, null);

        while (cur.moveToNext()) {
            int index = cur.getColumnIndex("name");
            lista.add(cur.getString(index));
        }
        cur.close();
        return lista;
    }


    public Cursor getSavedLists_cursor(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT _id, name, dsos, isSeq FROM ULists";
        return db.rawQuery(query, null);
    }


    public boolean IsNameListInUserList(String Name){
        List<String> UserList = getSavedLists();

        return UserList.contains(Name);
    }


    public List<String> getConstellationsArraylist() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT NAME FROM CONS ORDER BY NAME ASC", new String[] {});
        while (cur.moveToNext()) {
            list.add(cur.getString(0));
        }
        cur.close();
        //db.close();
        return list;
    }


    public Cursor getConstellation(boolean showAll) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        if (showAll){
            query = "SELECT _id, NAME, viewed, dsoIn, visible FROM Cons ORDER BY NAME";
        } else {
            query = "SELECT _id, NAME, viewed, dsoIn, visible FROM Cons WHERE visible = 'true' ORDER BY NAME";
        }
        return db.rawQuery(query, null);
    }


    public String getConstellationNameFromDSOID(String ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT CON FROM Catalog WHERE _id = ?", new String[] {ID});
        cursor.moveToFirst();
        String Name = cursor.getString(0);
        cursor.close();
        return Name;
    }


    public void changeVisibleCons(String ID, Boolean visible) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //visible = true ? false: true;

        values.put("visible", visible.toString() );

        db.update("Cons", values, "_id=?", new String[]{ID});
    }


    public void updateField(String Field, String ID, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Field, value);
        db.update("Catalog",values, "_id=?", new String[]{ID});
    }


    public List<String> getTypes() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT DISTINCT Type FROM Catalog",null);
        while (cur.moveToNext()){
            list.add(cur.getString(0));
        }
        cur.close();
        return list;
    }


    public List<String> getClasses() {
        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT DISTINCT Class FROM Catalog", null);
        while (cur.moveToNext()) {
            lista.add(cur.getString(0));
        }

        cur.close();
        //db.close();
        return lista;
    }


    public Cursor getComplexDSOSearch(String TypeID, String ClassID, String Rating) {
         SQLiteDatabase db = this.getReadableDatabase();
         String campos = "_id, ltrim(CAT || ' ' || REF || replace(' (' || NAME || ')', '()', '')) as NAME, CAT, REF, TYPE, CLASS";
         String query = "SELECT " + campos + " FROM Catalog";
         ArrayList<String> selectionArgs = new ArrayList<>();
         StringBuilder whereClause = new StringBuilder();

         // Añadir la condición de rating si no está vacía
         if (!Rating.isEmpty()) {
             whereClause.append("RATE >= ?");
             selectionArgs.add(Rating);
         }

         // Añadir la condición de TypeID si no está vacía
         if (!TypeID.isEmpty()) {
             if (whereClause.length() > 0) whereClause.append(" AND ");
             whereClause.append("TYPE = ?");
             selectionArgs.add(TypeID);
         }

         // Añadir la condición de ClassID si no está vacía
         if (!ClassID.isEmpty()) {
             if (whereClause.length() > 0) whereClause.append(" AND ");
             whereClause.append("CLASS = ?");
             selectionArgs.add(ClassID);
         }
         return db.rawQuery(whereClause.length() > 0 ? query + " WHERE " + whereClause.toString() : query, selectionArgs.toArray(new String[0]));
    }


    public Cursor getComplexDSOSearchInCON(String Type, String Class, String Rating, String ConsName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String campos = "ltrim(CAT || ' ' || REF) as CAT, NAME,TYPE, CLASS, TOVIEW, VISTO, RATE, _id";
        String query = "SELECT " + campos + " FROM Catalog";
        ArrayList<String> selectionArgs = new ArrayList<>();
        StringBuilder whereClause = new StringBuilder();

        whereClause.append("CON = ?");
        selectionArgs.add(ConsName);

        // Añadir la condición de rating si no está vacía
        if (!Rating.isEmpty()) {
            whereClause.append(" AND RATE >= ?");
            selectionArgs.add(Rating);
        }

        // Añadir la condición de TypeID si no está vacía
        if (!Type.isEmpty()) {
            whereClause.append(" AND TYPE = ?");
            selectionArgs.add(Type);
        }

        // Añadir la condición de ClassID si no está vacía
        if (!Class.isEmpty()) {
            whereClause.append(" AND CLASS = ?");
            selectionArgs.add(Class);
        }

        String finalQuery = query + " WHERE " + whereClause.toString();

        return db.rawQuery(finalQuery, selectionArgs.toArray(new String[0]));
    }


    public String getTypeFromClasification(String Clasif) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT TYPE FROM Catalog WHERE CLASS = ? LIMIT 1", new String[]{Clasif});
        cursor.moveToFirst();
        String type = cursor.getString(0);
        cursor.close();
        return type;
    }


    public DSO getDSOInfo(String dsoID) {
        //ArrayList<String> ObjInfo = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        DSO dso = new DSO();

        String query = "SELECT * FROM Catalog WHERE _id = ?";

        Cursor cur = db.rawQuery(query, new String[] {dsoID} );

        cur.moveToFirst();
        
        dso.setId(cur.getString(0));
        dso.setCat(cur.getString(1));
        dso.setRef(cur.getString(2));
        dso.setDesig(cur.getString(3));
        dso.setNombre(cur.getString(4));
        dso.setCons(cur.getString(5));
        dso.setTipo(cur.getString(6));
        dso.setClasif(cur.getString(7));
        dso.setCarta(cur.getString(8));
        dso.setMag(cur.getString(9));
        dso.setSize(cur.getString(10));
        dso.setInfo(cur.getString(11));
        dso.setRate(cur.getInt(12));
        dso.setToView(Boolean.valueOf(cur.getString(13)));
        dso.setVisto(Boolean.valueOf(cur.getString(14)));

        if (cur.isNull(15) || cur.isNull(16)) {
            dso.setAR(-9999.0);
            dso.setDEC(-9999.0);
        } else {
            dso.setAR(cur.getDouble(15));
            dso.setDEC(cur.getDouble(16));
        }

        return dso;
    }


    public ArrayList<String> getDataListToExport(String ID_List) {
        ArrayList<String> dso_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor curIDs = null;
        Cursor curDSOs = null;
        try {
            String query = "SELECT dsos FROM ULists WHERE _id = ?";
            curIDs = db.rawQuery(query, new String[]{ID_List});
            if (curIDs.moveToFirst()) {
                String dsos = curIDs.getString(0);
                String[] ids = dsos.split(",");
                String query2 = "SElECT CAT || \",\" || REF || \",\"  || OTHER || \",\"  || NAME || " +
                        "\",\"  || CON || \",\"  || TYPE || \",\"  || CLASS || " +
                        "\",\" || ra || \",\" || dec || \",\" || MAG || \",\" || SIZE " +
                        " FROM Catalog WHERE _id = ?";
                for (String id : ids) {
                    curDSOs = db.rawQuery(query2, new String[]{id});
                    if(curDSOs.moveToFirst()) dso_list.add(curDSOs.getString(0));
                    curDSOs.close();
                }
            }
        } finally {
            if(curIDs != null) curIDs.close();
            if(curDSOs != null) curDSOs.close();
            //db.close();
        }
        return dso_list;
    }


    public boolean WriteImprotedList(String row, String UserListName) {
        //SQLiteDatabase db = this.getWritableDatabase();

        String[] data = row.split(",");
        boolean isCreateNewDSOs = false;

        //si el objeto de la lista no existe en la bbdd se inserta
        if (!existeDSOenBBDD(data)) {
            NewItemToDB2(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7],
                    data[8], data[9], data[10]);
            isCreateNewDSOs = true;
        }

        //Metemos el objeto en la lista
        AddItem2UserList(getIDofDSO(data[0], data[1]) , UserListName);


        return isCreateNewDSOs;
    }


    public boolean existeDSOenBBDD(String[] data) {
        //data tiene la estructura {CAT, REF, OTHER, NAME}
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT EXISTS (SELECT 1 FROM Catalog WHERE CAT = ? AND REF = ?) AS resultado";
        Cursor cur = db.rawQuery(query, new String[] {data[0], data[1]});
        cur.moveToFirst();
        return cur.getInt(0) == 1 ? true : false;
    }


    public String getIDofDSO(String Cat, String Ref){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT _id FROM Catalog WHERE CAT=? AND REF=?";
        Cursor cur = db.rawQuery(query, new String[] {Cat, Ref});

        cur.moveToFirst();

        return cur.getString(0);
    }


    public Cursor searchByText(String string) {
        string = "%" + string + "%";
        SQLiteDatabase db = this.getReadableDatabase();
        String campos = "_id, ltrim(CAT || ' ' || REF || replace(' (' || NAME || ')', '()', '')) as NAME, CAT, REF, TYPE, CLASS";
        String query = "SELECT " + campos + " FROM Catalog WHERE CAT LIKE ? OR REF LIKE ? OR OTHER LIKE ? OR NAME LIKE ?";

        return db.rawQuery(query, new String[] {string, string, string, string});
    }


    public void saveNewMemory(String DSOid, String todayDate, String MemoryText, String place,
                              String telescope, String eyepiece) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("memo", MemoryText);
        values.put("date", todayDate);
        values.put("place", place);
        values.put("telescope", telescope);
        values.put("eyepiece", eyepiece);
        values.put("dsoID", DSOid);

        db.insert("Memories", null, values);
    }


    public void updateDateMemory(String memoryId, String newText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("date", newText);
        db.update("Memories", values, "_id=?", new String[]{memoryId});
    }


    public int deleteMemory(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Memories", "_id=?", new String[]{id});
    }


    public void UpdateNewOrder(String userListID, List<dsoRow> updatedList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        List<String> idsList = new ArrayList<>();

        for (int i = 0; i < updatedList.size(); i++) {
            idsList.add(updatedList.get(i).getID());
        }
        String ids = String.join(",", idsList);

        values.put("dsos", ids);
        db.update("ULists", values, "_id=?", new String[]{userListID});
    }


    public void RemoveUserListID(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("ULists", "_id=?", new String[]{id});
    }


    public Cursor getCharts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Cartas", null);
    }


    public boolean updateChart(String selectedChart, String armin, String armax, String decmin,
                            String decmax, String izquierda, String derecha, String arriba,
                            String abajo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        boolean isUpdated = true;

        values.put("ramin", armin);
        values.put("ramax", armax);
        values.put("decmin", decmin);
        values.put("decmax", decmax);
        values.put("L", izquierda);
        values.put("R", derecha);
        values.put("U", arriba);
        values.put("D", abajo);

        // si rowsAffected es cero significa que no se ha modificado ninguna fila porque no se ha
        // encontrado selectedChart
        int rowsAffected =  db.update("Cartas", values, "Name=?", new String[]{selectedChart});

        if (rowsAffected == 0) {
            isUpdated = false;
            values.put("Name", selectedChart);
            db.insert("Cartas", null, values);
        }
        return isUpdated;
    }

    public void saveWikiInfo(String id, String texto) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Obtenemos el registro anterior
        Cursor cur = db.query("Catalog", new String[]{"NOTES"}, "_id=?", new String[]{id}, null, null, null);
        String oldNotes = "";
        cur.moveToFirst();
        oldNotes = cur.getString(0);
        cur.close();

        //Concatenamos el nuevo registro con el anterior
        texto = oldNotes + "\n\n" + texto;

        ContentValues values = new ContentValues();
        values.put("NOTES", texto);
        db.update("Catalog", values, "_id=?", new String[]{id});
    }

/*
    public String getIAKey() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT value FROM data WHERE [key] = ?", new String[]{"api_key"});

        if (cursor.moveToFirst()) {
            return descifrar(cursor.getString(0));
        }
        return null;
    }


    public void saveIAKey(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("value", cifrar(key));
        db.update("data", values, "key=?", new String[]{"api_key"});
    }


    private static String cifrar(String key) {
        if (key == null) {
            return null;
        }

        int d = 17;

        String key2 = new StringBuilder(key).reverse().toString();
        StringBuilder encriptada = new StringBuilder();
        for (char caracter : key2.toCharArray()) {
            encriptada.append(desplazarCaracter(caracter, d));
        }
        return encriptada.toString();
    }


    private static char desplazarCaracter(char c, int desplazamiento) {
        final String ALFABETO_MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String ALFABETO_MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";
        final String DIGITOS = "0123456789";

        if (ALFABETO_MAYUSCULAS.indexOf(c) != -1) {
            int posOriginal = ALFABETO_MAYUSCULAS.indexOf(c);
            int nuevaPos = (posOriginal + desplazamiento) % ALFABETO_MAYUSCULAS.length();
            // Manejar desplazamiento negativo que resulte en índice negativo
            if (nuevaPos < 0) {
                nuevaPos += ALFABETO_MAYUSCULAS.length();
            }
            return ALFABETO_MAYUSCULAS.charAt(nuevaPos);
        } else if (ALFABETO_MINUSCULAS.indexOf(c) != -1) {
            int posOriginal = ALFABETO_MINUSCULAS.indexOf(c);
            int nuevaPos = (posOriginal + desplazamiento) % ALFABETO_MINUSCULAS.length();
            if (nuevaPos < 0) {
                nuevaPos += ALFABETO_MINUSCULAS.length();
            }
            return ALFABETO_MINUSCULAS.charAt(nuevaPos);
        } else if (DIGITOS.indexOf(c) != -1) {
            int posOriginal = DIGITOS.indexOf(c);
            int nuevaPos = (posOriginal + desplazamiento) % DIGITOS.length();
            if (nuevaPos < 0) {
                nuevaPos += DIGITOS.length();
            }
            return DIGITOS.charAt(nuevaPos);
        } else {
            // Si no es una letra o dígito conocido, devolver el carácter original
            return c;
        }
    }


    public static String descifrar(String claveEncriptada) {
        if (claveEncriptada == null) {
            return null;
        }
        final int DESPLAZAMIENTO = 17;

        // 1º Se aplica el desplazamiento inverso (para revertir el paso 2 de encriptación)
        StringBuilder conDesplazamientoInverso = new StringBuilder();
        for (char caracter : claveEncriptada.toCharArray()) {
            conDesplazamientoInverso.append(desplazarCaracter(caracter, -DESPLAZAMIENTO));
        }

        // 2º Se invierte la cadena completamente (para revertir el paso 1 de encriptación)
        return new StringBuilder(conDesplazamientoInverso.toString()).reverse().toString();
    }
*/

    public void insertPlace(String placeText, String bortleText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", placeText);
        values.put("bortle", bortleText);
        db.insert("Places", null, values);
    }


    public void insertTelescope(String telescopeText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", telescopeText);
        db.insert("Telescopes", null, values);
    }


    public void insertEyepiece(String eyepieceText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", eyepieceText);
        db.insert("Eyepieces", null, values);
    }


    public float[] getDSOCoords(String dsoID) {
        SQLiteDatabase db = this.getReadableDatabase();
        float[] resultado = new float[2];

        String query = "SELECT ra, dec FROM Catalog WHERE _id = ?";
        Cursor cur = db.rawQuery(query, new String[]{dsoID});

        if (cur.moveToFirst()) {
            resultado[0] = cur.getFloat(0);
            resultado[1] = cur.getFloat(1);
            return resultado;
        }
        cur.close();
        return resultado;
    }


    public Cursor getObjectsInTransitNow(double tsl) {
        // Devuelve un cursor con la lista de objetos que se encuentran en transito en el momento
        // tsl, tiempo sidereo local.
        // Se calcula la ventana de transito de anchura dos deltas.
        // Es necesario considerar el caso en el que tsl - delta sea mayor que tsl + delta en modulo
        // 24 horas.
        SQLiteDatabase db = this.getReadableDatabase();
        float delta = 0.3F;

        double maxRA = (tsl + delta) % 24;
        double minRA = (tsl - delta) % 24;

        String campos = "ltrim(CAT || ' ' || REF) as CAT, NAME,TYPE, CLASS, TOVIEW, VISTO, RATE, _id";
        // query de seleccion que funciona bien cuando ramin < ramax
        String query = "SELECT " + campos + " FROM Catalog where ra BETWEEN ? AND ? order by CON ASC";

        if (maxRA < minRA) {
            // query de seleccion que funciona cuando ramin > ramax
            // por ejemplo seleccionar objetos entre 23h y 2h
            query = "SELECT " + campos + " FROM Catalog where ra >= ? OR ra <= ? order by CON ASC";
        }

        return db.rawQuery(query, new String[]{String.valueOf(minRA), String.valueOf(maxRA)});
    }


    public void updateMemory(String id, String texto, String lugar,
                                String telescopio, String ocular) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("memo", texto);
        values.put("place", lugar);
        values.put("telescope", telescopio);
        values.put("eyepiece", ocular);

        db.update("Memories", values, "_id=?", new String[]{id});
    }

    public List<String> getMemoryPlaces() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT IFNULL(place, '') AS place FROM Memories ORDER BY place ASC";
        Cursor cur = db.rawQuery(query,null);
        while (cur.moveToNext()){
            list.add(cur.getString(0));
        }
        cur.close();
        return list;
    }


    public List<String> getMemoryDates() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT DISTINCT IFNULL(date,'') AS date FROM Memories ORDER BY date ASC",null);
        while (cur.moveToNext()){
            list.add(cur.getString(0));
        }
        cur.close();
        return list;
    }

    public Cursor getMemories(String dsoID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Memories WHERE dsoID = ?";
        return db.rawQuery(query, new String[] {dsoID});
    }

    public Cursor getAllMemories() {
        SQLiteDatabase db = this.getReadableDatabase();
        //String query = "SELECT * FROM Memories";
        String query =
                "SELECT " +
                        " ltrim(c.CAT || ' ' || c.REF || " +
                        "  CASE WHEN c.NAME IS NOT NULL AND c.NAME != '' " +
                        "       THEN ' ' || c.NAME ELSE '' END) AS dsoCodeName, " +
                        "  m.date, m.memo, m.place, m.telescope, m.eyepiece " +
                        "FROM Memories m " +
                        "LEFT JOIN Catalog c ON m.dsoID = c._id " +
                        "ORDER BY m.date DESC";

        return db.rawQuery(query, null);
    }

    public Memory getLastMemory() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Memories ORDER BY _id DESC LIMIT 1";

        Memory memory = null;
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                String place = cursor.getString(cursor.getColumnIndexOrThrow("place"));
                String ocular = cursor.getString(cursor.getColumnIndexOrThrow("eyepiece"));
                String telescope = cursor.getString(cursor.getColumnIndexOrThrow("telescope"));

                memory = new Memory("", "", "", "", place, telescope, ocular, "" );
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return memory;
    }


    public Cursor getMemoriesByLocation(String locationValue) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT m.*, " +
                "CASE " +
                "   WHEN c.NAME IS NOT NULL AND c.NAME != '' " +
                "   THEN c.CAT || c.REF || ' ' || c.NAME " +
                "   ELSE c.CAT || c.REF " +
                "END AS tag " +
                "FROM Memories m " +
                "LEFT JOIN Catalog c ON m.dsoID = c._id " +
                "WHERE m.place = ?";


        return db.rawQuery(query, new String[]{locationValue});
    }


    public Cursor getMemoriesByDate(String dateValue) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT m.*, " +
                "CASE " +
                "   WHEN c.NAME IS NOT NULL AND c.NAME != '' " +
                "   THEN c.CAT || c.REF || ' ' || c.NAME " +
                "   ELSE c.CAT || c.REF " +
                "END AS tag " +
                "FROM Memories m " +
                "LEFT JOIN Catalog c ON m.dsoID = c._id " +
                "WHERE m.date = ?";
        return db.rawQuery(query, new String[]{dateValue});
    }


    public float getDSORateByID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT RATE FROM CATALOG WHERE _id = ?";

        Float rate = 0f; //valor por defecto

        try (Cursor cursor = db.rawQuery(query, new String[]{id}))
        {
            if (cursor.moveToFirst()) {
                rate = cursor.getFloat(0);
            }
        }
        return rate;
    }
}