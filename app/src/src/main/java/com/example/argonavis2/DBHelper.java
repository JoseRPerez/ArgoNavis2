package com.joserp.argonavis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Jose on 09/04/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
//public class DBHelper extends SQLiteAssetHelper{
    private static String DB_PATH = "Argonavis";
    private static final String DB_NAME = "argonavisdb.sqlite";
    private static final int DB_VER = 1;
    public static final String DB_TABLE = "Catalog";
    //public static final String DB_COLUMN = "TaskName";

    /* //Forma antigua sin singleton
    public DBHelper(final Context context) {
    super(context, Environment.getExternalStorageDirectory().getPath()
            + File.separator + DB_PATH
            + File.separator + DB_NAME, null, DB_VER);
    }
     */

    //Forma nueva con singleton, solo una instancia por activity
    private DBHelper(Context context) {
        super(context, Environment.getExternalStorageDirectory().getPath()
                + File.separator + DB_PATH
                + File.separator + DB_NAME, null, DB_VER);
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
        //para crear la BBDD, nuestra BBDD se crea externamente
        //String query = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL)", DB_TABLE, DB_COLUMN);
        //db.execSQL(query);
    }

    /*public class MyDatabase extends SQLiteAssetHelper {

        private static final String DATABASE_NAME = "argonavisdb.sqlite";
        private static final int DATABASE_VERSION = 1;

        public MyDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
    }*/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Cuando cambie la estructura de la BBDD con nuevas tablas o campos, no es nuestro caso
        //String query = String.format("DELETE TABLE IF EXISTS %s", DB_TABLE);
        //db.execSQL(query);
        //onCreate(db);
    }

    public String getAppData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT DATA_VISIBLE FROM appData", null);
        cur.moveToFirst();
        String state = cur.getString(0);
        cur.close();
        return state;
    }

    public String getTiposName(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT NAMES FROM appData", null);
        cur.moveToFirst();
        String tipoNombre = cur.getString(0);
        cur.close();
        return tipoNombre;
    }


    public void updateDSOField(String dsoID, String Field, String Info){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Field, Info);

        db.update("Catalog", values, "_id=?", new String[] {dsoID});
        db.close();
    }

    public void setAppData(String Column, String info){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Column, info);

        db.update("appData", values, null,null);
        db.close();
    }


    public void NewItemToDB(String ID, String cat, String ref, String other, String name, String con, String tipo, String clas, String carta, String mag, String size, String notes, String memory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("_id", ID);
        values.put("CAT", cat);
        values.put("REF", ref);
        values.put("OTHER", other);
        values.put("NAME", name);
        values.put("CON", con);
        values.put("TYPE", tipo);
        values.put("CLASS", clas);
        values.put("CARTA", carta);
        values.put("MAG", mag);
        values.put("SIZE", size);
        values.put("NOTES", notes);
        values.put("MEMORY", memory);

        db.insert("Catalog", null, values);

        Integer data[] = this.getTotalDSOinCon(con);

        ContentValues values2 = new ContentValues();
        values2.put("dsoIn", data[1] + 1);
        db.update("Cons", values2,"_id=?", new String[]{con});

        db.close();


    }

    public void InsertNewCatalogName(String NameCat, ArrayList ListaCatalogos) {
        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        if (!ListaCatalogos.contains(NameCat)){
            ContentValues values = new ContentValues();
            values.put("name", NameCat);
            db.insert("NamesCat", null, values);
        }
        db.close();
    }

    public ArrayList<String> getCatalogListArrayList_2() {
        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query("NamesCat", new String[]{"name"}, null, null, null, null, "name ASC");
        while (cur.moveToNext()) {
            int index = cur.getColumnIndex("name");
            lista.add(cur.getString(index));
        }
        cur.close();
        db.close();
        return lista;
    }


    public void UpdateInfoItem_old(String id, String Name, String Info, String Rate, String Carta,String Checked, String Viewed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("RATE", Rate);
        values.put("NAME", Name);
        values.put("NOTES", Info);
        values.put("TOVIEW", Checked);
        values.put("VISTO", Viewed);
        values.put("CARTA", Carta);

        db.update("Catalog", values, "_id=?", new String[]{id});
        db.close();
    }

    public void UpdateInfoItem(String id, String cat, String ref, String other, String name,String con, String tipo, String clas, String carta, String mag, String size, String notes, String memory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("CAT", cat);
        values.put("REF", ref);
        values.put("OTHER", other);
        values.put("NAME", name);
        values.put("CON", con);
        values.put("TYPE", tipo);
        values.put("CLASS", clas);
        values.put("CARTA", carta);
        values.put("MAG", mag);
        values.put("SIZE", size);
        values.put("NOTES", notes);
        values.put("MEMORY", memory);


        db.update("Catalog", values, "_id=?", new String[]{id});
        db.close();
    }

    public void RenameList(String List_ID, String NewName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("NAME", NewName);
        db.update("Lists", values, "_id=?", new String[]{List_ID});
        db.close();

    }


    public void UpdateRateItem(String id, String Rate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("RATE", Rate);
        db.update("Catalog", values, "_id=?", new String[]{id});
        db.close();
    }

    public void UpdateConstellationCharts(String ConsName, String Charts){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("CHARTS", Charts);
        db.update("Cons", values, "NAME = ?", new String[]{ConsName});
        db.close();
    }


    /*public void AddItem2UserList(String id, String UserList){
        String listado = this.getIDsInList(UserList,1);   //o es nulo o numeros speradados por ; y acabado en numero porque en la funcion anterior se elimina el ultimo caracter ;

        List<String> list = new ArrayList<String>();

        if (listado != null){
            list = new ArrayList<String>(Arrays.asList(listado.split(";")));
        }

        list.add(id);

        ContentValues values = new ContentValues();
        values.put("If_Ref", android.text.TextUtils.join(";", list));

        SQLiteDatabase db = this.getWritableDatabase();
        db.update("Lists", values, "NAME=?", new String[]{UserList});
        db.close();

    }*/

    public String GetListkeyFromListName(String Name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur;
        cur = db.query("Lists", new String[]{"_id"}, "Name=?", new String[]{Name}, null, null, null);
        cur.moveToFirst();
        return cur.getString(0);
    }

    public String getConstelacionIDFromName(String Name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur;
        cur = db.rawQuery("select \"_id\" from Cons where NAME = ?", new String[]{Name});
        cur.moveToFirst();
        return cur.getString(0);
    }

    public void AddItem2UserList(String id, String UserList){
        //UserList es el nombre de la lista, primero debemos obtener el id de esa lista
        String UserList_key = GetListkeyFromListName(UserList);

        //Una vez tenemos la id de la lista de usuario hay que comprobar si el objeto ya esta en lista
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur;

        cur = db.query("UserLists", new String[]{"ListKey"}, "ListKey=? AND Id_Object=?", new String[] {UserList_key,id}, null, null, null);
        cur.moveToFirst();

        int existe = cur.getCount();

        cur.close();
        db.close();

        if (existe == 0) {
            ContentValues values = new ContentValues();
            values.put("ListKey", UserList_key);
            values.put("Id_Object", id);

            SQLiteDatabase db2 = this.getWritableDatabase();
            db2.insert("UserLists", null, values);
            ModifyTotalinUserlist(UserList_key, 1); //sumamos uno en el totar de objetos en la columna dsoIn de la tabla Lists
            db2.close();
        }
    }

    public void ModifyTotalinUserlist(String UserList_id, Integer delta){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT dsoIn FROM Lists where _id=?", new String[] {UserList_id});
        cur.moveToFirst();
        Integer count = cur.getInt(0);

        Integer newcount = count + delta;

        ContentValues values = new ContentValues();
        values.put("dsoIn", newcount);
        db.update("Lists", values, "_id=?", new String[] {UserList_id});
        db.close();
    }


    public void RemoveItemFromUserList(String id_objeto, String ID_UserList){
        SQLiteDatabase db = this.getWritableDatabase();
        //ContentValues values = new ContentValues();
        //values.put("Name", Name);

        db.delete("UserLists", " ListKey=? AND Id_Object=?" , new String[] {ID_UserList,id_objeto});
        //db.close();
        ModifyTotalinUserlist(ID_UserList, -1); //sumamos uno en el totar de objetos en la columna dsoIn de la tabla Lists
    }




    public Cursor getObjectsFromUserList(String ID_List){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT ltrim(Catalog.CAT || \" \" || Catalog.REF) as CAT, Cons.Name, Catalog.NAME, DSO.type, Clasificacion.Clasif_name, Catalog.TOVIEW, Catalog.VISTO, Catalog._id FROM Catalog INNER JOIN DSO ON Catalog.Type = DSO._id INNER JOIN Clasificacion ON Catalog.CLASS = Clasificacion._id INNER JOIN Cons ON Catalog.CON = Cons._id WHERE Catalog._id in (SELECT Id_Object FROM UserLists WHERE ListKey = ?) ORDER BY CON ASC" , new String[]{ID_List});
    }

    public Cursor getToObserveList_cursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT ltrim(Catalog.CAT || \" \" || Catalog.REF) as CAT, Cons.Name, Catalog.NAME, DSO.type, Clasificacion.Clasif_name, Catalog.TOVIEW, Catalog.VISTO, Catalog._id FROM Catalog INNER JOIN DSO ON Catalog.Type = DSO._id INNER JOIN Clasificacion ON Catalog.CLASS = Clasificacion._id INNER JOIN Cons ON Catalog.CON = Cons._id WHERE Catalog.TOVIEW = ? ORDER BY CON ASC" , new String[]{"true"});
    }

    public String getMapsCalledForConstellation(String IDConstelacion){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("select group_concat(DISTINCT CARTA) FROM Catalog where CON=?", new String[]{IDConstelacion});
        cur.moveToFirst();
        String Mapas = cur.getString(0);
        cur.close();
        return Mapas;
    }

    public String getChartsInconstellation(String ConsName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("select CHARTS from Cons Where NAME = ?", new String[] {ConsName});
        cur.moveToFirst();
        String Mapas = cur.getString(0);
        cur.close();
        return Mapas;
    }

    public String recalcular(){
        SQLiteDatabase db = this.getWritableDatabase();
        //Actualizamos el numero de objetos en cada constelacion
        db.execSQL( "UPDATE Cons SET dsoIn = (SELECT Count(*) Valor FROM Catalog WHERE Catalog.CON = Cons._id)" );
        //Actualizamos el numero de objetos vistos en cada constelacion
        db.execSQL( "UPDATE Cons SET viewed = (SELECT Count(*) Valor FROM Catalog WHERE Catalog.CON = Cons._id AND Catalog.VISTO == 'true')" );
        //Actualizamos el numero de objetos en cada lista
        db.execSQL( "UPDATE Lists SET dsoIn = (SELECT Count(*) Valor FROM UserLists WHERE Lists._id == UserLists.ListKey)");
        db.close();
        return "Hecho";
    }


    public void AddNewUserList(String Name, String Seq){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("NAME", Name);
        values.put("SEQ", Seq);
        //values.put("If_Ref", "");     desde el cambio de gestion de userlist ya no se guardan aqui los id de los objetos

        db.insert("Lists", null, values);
    }


    public void RemoveUserList(String Name){
        SQLiteDatabase db = this.getWritableDatabase();
        //ContentValues values = new ContentValues();
        //values.put("Name", Name);

        db.delete("Lists", "Name=?", new String[] {Name});
    }


    public void RemoveUserListID(String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        //ContentValues values = new ContentValues();
        //values.put("_id", ID);

        db.delete("Lists", "_id=?", new String[] {ID});
        db.delete("UserLists", "ListKey=?", new String[] {ID});
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

        if (Viewed.equals("true")){
            delta = 1;
        } else {
            delta = -1;
        }

        ContentValues values2 = new ContentValues();
        Integer r[] = getVistosInConstelacion(id_dso);
        values2.put("viewed", r[1] + delta);
        db.update("Cons", values2, "_id=?", new String[]{r[0].toString()});
        //db.close();
    }


    public Integer[] getVistosInConstelacion( String id_dso ){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("select _id, viewed FROM Cons WHERE _id = (SELECT Catalog.CON from Catalog WHERE _id = ?)",new String[]{id_dso});
        cur.moveToFirst();
        Integer v[] = {cur.getInt(0),cur.getInt(1)};  // devuelve {id_cons, numero de vistos}

        cur.close();
        return v;
    }



    public Integer[] getTotalDSOinCon( String id_cons ){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query("Cons", new String[] {"dsoIn"} , "_id=?", new String[]{id_cons}, null, null, null);
        cur.moveToFirst();

        Integer v[] = {Integer.parseInt(id_cons), cur.getInt(0)};
        cur.close();

        return v;   //devuelve {id_cons, numero total de DSO en la constelacion}
    }


    public void deleteItem(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Restamos en uno el numero de objetos que hay en la constelacion
        ContentValues values = new ContentValues();
        Integer v[] = this.getTotalDSOinCon(ID); // devuelve {id_cons, numero de vistos}
        values.put("dsoIn", v[1] - 1);
        db.update("Cons", values, "_id=?", new String[] {v[0].toString()});

        //Comprobamos si el objetos tiene true en VISTO
        Cursor cur = db.query("Catalog", new String[]{"VISTO"}, "_id=?", new String[]{ID}, null, null, null);
        cur.moveToFirst();
        String status = cur.getString(0);

        if (status.equals("true")){
            //Restamos en uno el numero de objetos vistos si el objeto tiene true en VISTO
            ContentValues values2 = new ContentValues();
            Integer w[] = this.getVistosInConstelacion(ID); // devuelve {id_cons, numero de vistos}
            values2.put("dsoIn", w[1] - 1);
            db.update("Cons", values, "_id=?", new String[] {v[0].toString()});
            Integer[] info = this.getTotalDSOinCon(ID);
        }

        //eliminamos el elemento de la base de Catalog
        db.delete("Catalog", "_id = ?", new String[]{ID});
        db.close();
    }

    public ArrayList<String> getClasifcationListArrayList(String Tipo) {
        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        if (Tipo.equalsIgnoreCase("Estrella")){
           lista = get_STARS_TypeListArraylist();
        } else {
            //lista = get_DSO_TypeListArraylist();
            Cursor cur = db.query("Catalog", new String[]{"CLASS"}, "CLASS != '' and TYPE =?", new String[]{Tipo}, "CLASS", null, "CLASS ASC");
            while (cur.moveToNext()) {
                int index = cur.getColumnIndex("CLASS");
                lista.add(cur.getString(index));
            }
            cur.close();
            db.close();
        }

        return lista;
    }

    public ArrayList<String> getClasifcationListArrayList_2(String Tipo) {
        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        //Obtenemos el _id de Tipo
        Cursor cur = db.query("DSO", new String[]{"_id"}, "type = ?", new String[]{Tipo}, null, null, null);
        cur.moveToFirst();
        String _id = cur.getString(0);

        Cursor cur2 = db.query("Clasificacion", new String[]{"Clasif_name"}, "Tipo_key = ?", new String[]{_id}, null, null, null);


        while (cur.moveToNext()) {
            int index = cur.getColumnIndex("Clasif_name");
            lista.add(cur.getString(index));
        }
        cur.close();
        db.close();

        return lista;
    }

    public ArrayList<String> getClassesFromType(String Tipo_ID){
        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Clasificacion", new String[]{"Clasif_name"}, "Tipo_key = ?", new String[]{Tipo_ID}, null, null, null);
        while (cursor.moveToNext()){
            lista.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return lista;
    }


    public ArrayList<String> getCatalogListArrayList() {
        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query("Catalog", new String[]{"CAT"}, "CAT !=''", null, "CAT", null, "CAT ASC");
        while (cur.moveToNext()) {
            int index = cur.getColumnIndex("CAT");
            lista.add(cur.getString(index));
        }
        cur.close();
        db.close();
        lista.add(0,"*Todos*");
        return lista;
    }

    public ArrayList<String> getSavedLists(){
        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query("Lists", new String[]{"NAME"}, null, null, null, null, null);
        while (cur.moveToNext()) {
            int index = cur.getColumnIndex("NAME");
            lista.add(cur.getString(index));
        }
        cur.close();
        db.close();
        return lista;
    }


    public Cursor getSavedLists_cursor(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("Lists", new String[]{"NAME", "dsoIn", "SEQ" , "_id"}, null, null, null, null, null);
    }

    public boolean IsNameListInUserList(String Name){
        ArrayList<String> UserList = getSavedLists();

        if (UserList.contains(Name)){
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String> getConstellationsArraylist() {
        ArrayList<String> listaNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query("Cons", new String[]{"NAME"}, null, null, null, null, null);
        while (cur.moveToNext()) {
            //int index1 = cur.getColumnIndex("NAME");
            listaNames.add(cur.getString(0));
        }
        cur.close();
        db.close();
        //listaNames.add(0,"*Todos*");
        return listaNames;
    }

    public ArrayList<String> getClasificatoinArrayList() {
        ArrayList<String> listaNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query("Clasificacion", new String[]{"Clasif_name"}, null, null, null, null, null);
        while (cur.moveToNext()) {
            //int index1 = cur.getColumnIndex("NAME");
            listaNames.add(cur.getString(0));
        }
        cur.close();
        db.close();
        //listaNames.add(0,"*Todos*");
        return listaNames;
    }


    public Cursor getConstellationsCursor(Boolean TodasConstelaciones, String TipoNombre) {
        SQLiteDatabase db = this.getReadableDatabase();

        String columna = "NAME";

        if (TipoNombre.equals("true")){
            columna = "NAME";
        } else if (TipoNombre.equals("false")){
            columna = "ALTNAME";
        }

        if (TodasConstelaciones){
            String query = "SELECT " + columna + " as NAME, _id, visible, viewed, dsoIn FROM Cons";
            return db.rawQuery(query, null);
            //return db.query("Cons", new String[]{columna,"_id","visible", "viewed", "dsoIn"}, null, null, null, null, null);
        } else {
            String query = "SELECT " + columna + " as NAME, _id, visible, viewed, dsoIn FROM Cons WHERE visible=1";
            return db.rawQuery(query, null);
            //return db.query("Cons", new String[]{columna,"_id","visible", "viewed", "dsoIn"}, "visible=1", null, null, null, null);
        }
    }

    public String getConstellationNameFromID(String ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Cons", new String[]{"NAME"}, "_id=?", new String[]{ID}, null, null, null);
        cursor.moveToFirst();
        String Name = cursor.getString(0);
        cursor.close();
        db.close();
        return Name;
    }

    public void changeVisibleCons(String ID, String visible) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        visible = (visible.equals("0")) ? "1": "0";

        values.put("visible", visible );
        db.update("Cons", values, "_id=?", new String[]{ID});
    }

    public void updateField(String Field, String ID, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Field, value);
        db.update("Catalog",values, "_id=?", new String[]{ID});
    }


    public String getConstellationShortNameFromID(String ID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Cons", new String[]{"ABR"}, "_id=?", new String[]{ID}, null, null, null);
        cursor.moveToFirst();
        String shortName = cursor.getString(0);
        cursor.close();
        //db.close();
        return shortName;
    }

    public String getConstellationIDFromName(String Name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Cons", new String[]{"_id"}, "NAME=?", new String[]{Name}, null, null, null);
        cursor.moveToFirst();
        String id = cursor.getString(0);
        cursor.close();
        db.close();
        return id;
    }


    public Cursor getConstellationsCursor_FilteredByString(String text){
        SQLiteDatabase db = this.getReadableDatabase();
        text = "%" + text + "%";

        return db.query("Cons", new String[]{"NAME","_id"}, "NAME LIKE ?", new String[]{text}, null, null, null);

    }

    public String getTypeFromClass(String Clasif) {
        SQLiteDatabase db = this.getReadableDatabase();
        //String Type = new String();
        Cursor cur = db.query("Catalog", new String[]{"TYPE"}, "CLASS=?",  new String[]{Clasif}, "TYPE", null, null);
        cur.moveToNext();
            //int index = cur.getColumnIndex("NAME");
        String Type = cur.getString(0);
        //cur.close();
        //db.close();
        return Type;
    }

    public String getType(String ID){
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.query("DSO", new String[]{"type"}, "_id=?", new String[]{ID}, null, null, null);
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        cursor.close();
        //db.close();
        return tipo;
    }

    public String getTypeIDFromTypeName(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query("DSO", new String[]{"_id"}, "type=?", new String[]{name}, null, null, null);
        if (cur.getCount() == 0){
            return "0";
        } else {
            cur.moveToNext();
            String Typeid = cur.getString(0);
            cur.close();
            db.close();
            return Typeid;
        }
    }

    public String getClassIDFromClassName(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query("Clasificacion", new String[]{"_id"}, "Clasif_name=?",  new String[]{name}, null, null, null);

        if (cur.getCount() == 0){
            return "0";
        } else{
            cur.moveToNext();
            String Typeid = cur.getString(0);
            cur.close();
            db.close();
            return Typeid;
        }
    }

    public ArrayList<String> getTypeListArraylist_old() {
        ArrayList<String> lista1 = new ArrayList<>();
        //ArrayList<String> lista2 = new ArrayList<>();
        //ArrayList<String> lista = new ArrayList<>();

        //lista1 = get_DSO_TypeListArraylist();
        //lista1.remove(0); //quitamos el elemento *TODOS*
        //lista2 = get_STARS_TypeListArraylist();
        //lista2.remove(0);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query("Catalog", new String[]{"CLASS"}, null, null, "CLASS", null, "CLASS ASC");
        while (cur.moveToNext()) {
            //int index = cur.getColumnIndex("TYPE");
            lista1.add(cur.getString(0));
        }
        cur.close();
        db.close();
        //lista.remove("CS");
        //lista.remove("DS");
        //lista.add(0, "*Todos*");

        //lista.remove(0);
        //lista.remove("");//borrar campo vacio
        return lista1;
        //lista.addAll(lista1);
        //lista.addAll(lista2);

        //return lista;
    }


    public ArrayList<String> getTypeListArraylist() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query("Clasificacion", new String[]{"Clasif_name"}, null, null, null, null, null);
        while (cur.moveToNext()) {
            //int index = cur.getColumnIndex("TYPE");
            list.add(cur.getString(0));
        }
        cur.close();
        db.close();

        return list;
    }


    public ArrayList<String> get_DSO_TypeListArraylist_old() {
        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query("DSO", new String[]{"TYPE"}, null, null, null, null, null);
        while (cur.moveToNext()) {
            //int index = cur.getColumnIndex("TYPE");
            lista.add(cur.getString(0));
        }

        cur.close();
        db.close();
        lista.add(0, "*Todos*");
        return lista;
    }


    public ArrayList<String> get_DSO_TypeListArraylist() {
        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query("DSO", new String[]{"TYPE"}, null, null, null, null, null);
        while (cur.moveToNext()) {
            lista.add(cur.getString(0));
        }
        cur.close();
        db.close();
        return lista;
    }

    public ArrayList<String> get_Clasification_list() {
        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query("Clasificacion", new String[]{"Clasif_name"}, null, null, null, null, null);
        while (cur.moveToNext()) {
            lista.add(cur.getString(0));
        }

        cur.close();
        db.close();
        return lista;
    }


    public ArrayList<String> get_STARS_TypeListArraylist() {
        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query("STARS", new String[]{"TYPE"}, null, null, null, null, null);
        while (cur.moveToNext()) {
            //int index = cur.getColumnIndex("TYPE");
            lista.add(cur.getString(0));
        }
        cur.close();
        db.close();
        //lista.add(0, "*Todos*");
        return lista;
    }

    public ArrayList<String> getTypeArrayList(){
        ArrayList<String> lista = get_DSO_TypeListArraylist();
        lista.add(1,"Estrella");
        //ArrayList<String> lista2 = get_STARS_TypeListArraylist();
        //lista2.remove(0);

        //ArrayList<String> union = new ArrayList<>();
        //union.addAll(lista1);
        //union.addAll(lista2);
        //union.add(0, "*Todos*");
        //lista.add(0,"*Todos*");
        return lista;
    }


    public Cursor getComplexSearch(String ConstID, String TypeID, String ClassID, String Rating) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] campos = new String[]{"CAT", "REF", "NAME", "TYPE", "CLASS", "TOVIEW", "VISTO", "_id"};
        Cursor cursor;
        if (TypeID.equalsIgnoreCase("0") && ClassID.equalsIgnoreCase("0")){
            //cursor =  db.rawQuery("SELECT ltrim(CAT || \" \" || REF ) as CAT, REF, NAME, TYPE, CLASS, TOVIEW, VISTO, _id FROM Catalog WHERE CON = ? AND RATE>=? ORDER BY TYPE ASC", new String[]{ConstID,Rating});
            return db.rawQuery("SELECT ltrim(Catalog.CAT || \" \" || Catalog.REF) as CAT, Catalog.NAME, DSO.type, Clasificacion.Clasif_name, Catalog.TOVIEW, Catalog.VISTO, Catalog._id FROM Catalog INNER JOIN DSO ON Catalog.Type = DSO._id INNER JOIN Clasificacion ON Catalog.CLASS = Clasificacion._id WHERE Catalog.CON=? AND Catalog.RATE>=? order by Catalog.Type ASC", new String[]{ConstID,Rating});
        } else if (TypeID.equalsIgnoreCase("0") && ClassID != "0"){
            //cursor =  db.rawQuery("SELECT ltrim(CAT || \" \" || REF ) as CAT, REF, NAME, TYPE, CLASS, TOVIEW, VISTO, _id FROM Catalog WHERE CON = ? AND CLASS=? AND RATE>=? ORDER BY TYPE ASC", new String[]{ConstID, ClassID, Rating});
            return db.rawQuery("SELECT ltrim(Catalog.CAT || \" \" || Catalog.REF) as CAT, Catalog.NAME, DSO.type, Clasificacion.Clasif_name, Catalog.TOVIEW, Catalog.VISTO, Catalog._id FROM Catalog INNER JOIN DSO ON Catalog.Type = DSO._id INNER JOIN Clasificacion ON Catalog.CLASS = Clasificacion._id WHERE  Catalog.CON=? AND Catalog.Class=? AND Catalog.RATE>=? order by Catalog.Type ASC", new String[]{ConstID, ClassID, Rating});
        } else if (TypeID != "0" && ClassID.equalsIgnoreCase("0")){
            //cursor =  db.rawQuery("SELECT ltrim(CAT || \" \" || REF ) as CAT, REF, NAME, TYPE, CLASS, TOVIEW, VISTO, _id FROM Catalog WHERE CON = ? AND TYPE=? AND RATE>=? ORDER BY TYPE ASC", new String[]{ConstID, TypeID, Rating});
            return db.rawQuery("SELECT ltrim(Catalog.CAT || \" \" || Catalog.REF) as CAT, Catalog.NAME, DSO.type, Clasificacion.Clasif_name, Catalog.TOVIEW, Catalog.VISTO, Catalog._id FROM Catalog INNER JOIN DSO ON Catalog.Type = DSO._id INNER JOIN Clasificacion ON Catalog.CLASS = Clasificacion._id WHERE Catalog.CON=? AND Catalog.Type=? AND Catalog.RATE>=? order by Catalog.Type ASC", new String[]{ConstID,TypeID, Rating});
        } else {
            //cursor =  db.rawQuery("SELECT ltrim(CAT || \" \" || REF ) as CAT, REF, NAME, TYPE, CLASS, TOVIEW, VISTO, _id FROM Catalog WHERE CON = ? AND TYPE=? AND CLASS=? AND RATE>=? ORDER BY TYPE ASC", new String[]{ConstID, TypeID, ClassID, Rating});
            return db.rawQuery("SELECT ltrim(Catalog.CAT || \" \" || Catalog.REF) as CAT, Catalog.NAME, DSO.type, Clasificacion.Clasif_name, Catalog.TOVIEW, Catalog.VISTO, Catalog._id FROM Catalog INNER JOIN DSO ON Catalog.Type = DSO._id INNER JOIN Clasificacion ON Catalog.CLASS = Clasificacion._id WHERE Catalog.CON=? AND Catalog.Type=? AND Catalog.Class=? AND Catalog.RATE>=? order by Catalog.Type ASC", new String[]{ConstID, TypeID, ClassID, Rating});
        }
        //return cursor;
    }

    public Cursor getComplexSearchInToViewList(String Type, String Class, String Rating, String ToVIEW) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] campos = new String[]{"CAT", "REF", "NAME", "TYPE", "CLASS", "TOVIEW", "VISTO", "_id"};

        if (Type.equals("0") && Class.equals("0")){
            //return db.query("Catalog", campos, "RATE>=? AND TOVIEW=?", new String[]{Rating, ToVIEW}, null, null, "TYPE ASC");
            return db.rawQuery("SELECT ltrim(Catalog.CAT || \" \" || Catalog.REF) as CAT, Cons.Name, Catalog.NAME, DSO.type, Clasificacion.Clasif_name, Catalog.TOVIEW, Catalog.VISTO, Catalog._id FROM Catalog INNER JOIN DSO ON Catalog.Type = DSO._id INNER JOIN Clasificacion ON Catalog.CLASS = Clasificacion._id INNER JOIN Cons ON Catalog.CON = Cons._id WHERE Catalog.RATE>=? AND Catalog.TOVIEW=? order by Catalog.Type ASC", new String[]{Rating, ToVIEW});
        } else if (Type.equals("0")){
            //return db.query("Catalog", campos, "CLASS=? AND RATE>=? AND TOVIEW=?", new String[]{Class, Rating, ToVIEW}, null, null, "TYPE ASC");
            return db.rawQuery("SELECT ltrim(Catalog.CAT || \" \" || Catalog.REF) as CAT, Cons.Name, Catalog.NAME, DSO.type, Clasificacion.Clasif_name, Catalog.TOVIEW, Catalog.VISTO, Catalog._id FROM Catalog INNER JOIN DSO ON Catalog.Type = DSO._id INNER JOIN Clasificacion ON Catalog.CLASS = Clasificacion._id INNER JOIN Cons ON Catalog.CON = Cons._id WHERE Catalog.CLASS=? AND Catalog.RATE>=? AND Catalog.TOVIEW=? order by Catalog.Type ASC", new String[]{Class, Rating, ToVIEW});
        } else if (Class.equals("0")) {
            //return db.query("Catalog", campos, "TYPE=? AND RATE>=? AND TOVIEW=?", new String[]{Type, Rating, ToVIEW}, null, null, "TYPE ASC");
            return db.rawQuery("SELECT ltrim(Catalog.CAT || \" \" || Catalog.REF) as CAT, Cons.Name, Catalog.NAME, DSO.type, Clasificacion.Clasif_name, Catalog.TOVIEW, Catalog.VISTO, Catalog._id FROM Catalog INNER JOIN DSO ON Catalog.Type = DSO._id INNER JOIN Clasificacion ON Catalog.CLASS = Clasificacion._id INNER JOIN Cons ON Catalog.CON = Cons._id WHERE Catalog.TYPE=? AND Catalog.RATE>=? AND Catalog.TOVIEW=? order by Catalog.Type ASC", new String[]{Type, Rating, ToVIEW});

        } else {
            //return db.query("Catalog", campos, "TYPE =? AND CLASS=? AND RATE>=? AND TOVIEW=?", new String[]{Type, Class, Rating, ToVIEW}, null, null, "TYPE ASC");
            return db.rawQuery("SELECT ltrim(Catalog.CAT || \" \" || Catalog.REF) as CAT, Cons.Name, Catalog.NAME, DSO.type, Clasificacion.Clasif_name, Catalog.TOVIEW, Catalog.VISTO, Catalog._id FROM Catalog INNER JOIN DSO ON Catalog.Type = DSO._id INNER JOIN Clasificacion ON Catalog.CLASS = Clasificacion._id INNER JOIN Cons ON Catalog.CON = Cons._id WHERE  Catalog.TYPE=? AND Catalog.CLASS=? AND Catalog.RATE>=? AND Catalog.TOVIEW=? order by Catalog.Type ASC", new String[]{Type, Class, Rating, ToVIEW});
        }
    }

    public Cursor getComplexSearchList_cursor(String Const, String Type, String Class, String Catalogo) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] campos = new String[]{"CAT", "REF", "NAME", "TYPE", "CLASS", "TOVIEW", "VISTO", "_id"};

        String[] source = new String[]{Const, Type, Class, Catalogo};
        String[] columns = new String[]{"CON", "TYPE", "CLASS", "CAT"};
        String sql_orden = "";

        int size = source.length;

        ArrayList<String> datos = new ArrayList<String>();

        int j = 0;

        for (int i = 0; i < size; i++) {
            if (source[i].toString().equalsIgnoreCase("*Todos*")) {
                sql_orden = sql_orden + " " + columns[i] + " " + "is not null and";

            } else {
                datos.add(source[i]);
                j++;
                sql_orden = sql_orden + " " + columns[i] + "=? and";
            }
        }

        String[] datos_array = new String[datos.size()];
        String sql_orden_final = sql_orden.substring(0, sql_orden.length() - 4);

        return db.query("Catalog", campos, sql_orden_final, datos.toArray(datos_array), null, null, null);

    }

    public Cursor getComplexSearch_cursor(String Const, String Type, String Class, String Rating) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] campos = new String[]{"CAT", "REF", "NAME", "TYPE", "CLASS", "TOVIEW", "VISTO", "_id"};

        String[] source = new String[]{Const, Type, Class, Rating};
        String[] columns = new String[]{"CON", "TYPE", "CLASS", "RATE"};
        String sql_orden = "";

        int size = source.length;

        ArrayList<String> datos = new ArrayList<String>();

        int j = 0;
        for (int i = 0; i < size - 1; i++) {
            if (source[i].toString().equalsIgnoreCase("")) {
                sql_orden = sql_orden + " " + columns[i] + " " + "is not null and";

            } else {
                datos.add(source[i]);
                j++;
                sql_orden = sql_orden + " " + columns[i] + "=? and";
            }
        }

        sql_orden = sql_orden + " " + columns[size] + ">= ?";

        String[] datos_array = new String[datos.size()];
        String sql_orden_final = sql_orden.substring(0, sql_orden.length() - 4);

        return db.query("Catalog", campos, sql_orden_final, datos.toArray(datos_array), null, null, null);
    }


    public Cursor getObjectList_cursor(String Const, String Type) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] campos = new String[]{"CAT", "REF", "NAME", "TYPE", "CLASS", "TOVIEW", "VISTO", "_id"};

        if (Type.equalsIgnoreCase("*Todos*") && Const.equalsIgnoreCase("*Todos*")) {
            return db.query("Catalog", campos, null, null, null, null, null);
        } else if (Const.equalsIgnoreCase("*Todos*")) {
            return db.query("Catalog", campos, "TYPE=?", new String[]{Type}, null, null, null);
        } else if (Type.equalsIgnoreCase("*Todos*")) {
            return db.query("Catalog", campos, "CON=?", new String[]{Const}, null, null, null);
        } else {
            return db.query("Catalog", campos, "CON=? and TYPE=?", new String[]{Const, Type}, null, null, null);
        }
    }

    public Cursor getObjectsInConstellation_cursor(String ID_Cons){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT ltrim(Catalog.CAT || \" \" || Catalog.REF) as CAT, Catalog.NAME, DSO.type, Clasificacion.Clasif_name, Catalog.TOVIEW, Catalog.VISTO, Catalog._id FROM Catalog INNER JOIN DSO ON Catalog.Type = DSO._id INNER JOIN Clasificacion ON Catalog.CLASS = Clasificacion._id INNER JOIN Cons ON Catalog.CON = Cons._id WHERE CON = ?", new String[]{ID_Cons});
    }



    public String getTypeFromClasification(String class_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Clasificacion", new String[]{"Tipo_key"}, "_id=?", new String[]{class_id}, null, null, null);
        cursor.moveToFirst();
        String type =  cursor.getString(0);
        cursor.close();
        db.close();
        return type;
    }


    public String getClasificacion(String ID){
        SQLiteDatabase db = this.getReadableDatabase();
        String tipo;

        Cursor cursor = db.query("Clasificacion", new String[]{"Clasif_name"}, "_id=?", new String[]{ID}, null, null, null);
        cursor.moveToFirst();
        String clasif = cursor.getString(0);
        cursor.close();
        //db.close();
        return clasif;
    }


    public Cursor BuscarObjByString_cursor(String info) {
        SQLiteDatabase db = this.getReadableDatabase();

        info = "%" + info + "%";

        String[] datos = new String[]{info, info, info};
        Cursor cursor =  db.rawQuery("SELECT ltrim(CAT || \" \" || REF || replace(\" (\" || NAME || \")\", \"()\", \"\")) as NAME, _id FROM Catalog WHERE NAME LIKE ? OR REF LIKE ? OR OTHER LIKE ?", datos);
        //Cursor cursor =  db.rawQuery("SELECT CAT || \" \" || REF || \" \" || NAME as NAME, _id FROM Catalog WHERE NAME LIKE ? OR REF LIKE ? OR OTHER LIKE ?", datos);

        return cursor;
    }

    public Cursor BuscarFilteredObjByString_cursor(String info, String Type_clause) {
        SQLiteDatabase db = this.getReadableDatabase();

        info = "%" + info + "%";
        String key_clause = "SELECT ltrim(CAT || \" \" || REF || replace(\" (\" || NAME || \")\", \"()\", \"\")) as NAME, _id FROM Catalog WHERE (" + Type_clause + ") AND (NAME LIKE ? OR REF LIKE ? OR OTHER LIKE ?)";

        String[] datos = new String[]{info, info, info};
        //Cursor cursor =  db.rawQuery("SELECT ltrim(CAT || \" \" || REF || replace(\" (\" || NAME || \")\", \"()\", \"\")) as NAME, _id FROM Catalog WHERE (key_clause) AND (NAME LIKE ? OR REF LIKE ? OR OTHER LIKE ?)", datos);
        Cursor cursor =  db.rawQuery(key_clause, datos);

        return cursor;
    }



    public ArrayList<String> getObjectInfo(String localizador) {
        ArrayList<String> ObjInfo = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] ID = new String[]{localizador};

        String[] campos = new String[]{"*"};

        Cursor cur = db.query("Catalog", campos, "_id=?", ID, null, null, null);

        while (cur.moveToNext()) {
            int i = 0;

            while (i < cur.getColumnCount()) {
                ObjInfo.add(cur.getString(i));
                i++;
            }
        }
        cur.close();
        db.close();
        return ObjInfo;
    }

    public Integer Find_Next_MaxID_in_Catalog(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(_id) FROM Catalog", null);
        Integer next_id;
        cursor.moveToFirst();
        next_id = Integer.valueOf(cursor.getString(0)) + 1;
        cursor.close();
        db.close();
        return next_id;
    }

    public Cursor getEmptyCursor(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("Cons", new String[]{"NAME, _id"}, "_id=500", null, null, null, null);
    }

}