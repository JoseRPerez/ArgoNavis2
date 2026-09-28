package com.joserp.argonavis.AuxFun;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import com.joserp.argonavis.BaseRow;
import com.joserp.argonavis.Defs.ChartData;
import com.joserp.argonavis.Memory;
import com.joserp.argonavis.dsoRow;
import com.joserp.argonavis.row;
import com.joserp.argonavis.rowCon;
import com.joserp.argonavis.rowList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constructor {

    public static ArrayList<BaseRow> BaseRowToArrayList(Cursor cursor) {
        ArrayList<BaseRow> list = new ArrayList();

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            do {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String tipo = cursor.getString(2);
                String clasif = cursor.getString(3);

                row item = new row(id, name, "", "", tipo, clasif);
                list.add(item);

            } while (cursor.moveToNext());
        }

        return list;
    }


    public static List<rowCon> ConstructorCONS(Context context, Cursor cursor) {
        List<rowCon> list = new ArrayList<>();

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                int viewedIn = Integer.parseInt(cursor.getString(2));
                int dsoIn = Integer.parseInt(cursor.getString(3));
                boolean visible = Boolean.parseBoolean(cursor.getString(4));

                int score = 0;

                if (dsoIn != 0) {
                    score = 100 * viewedIn / dsoIn;
                }

                rowCon item = new rowCon(id, name, score, visible);
                list.add(item);

            } while (cursor.moveToNext());
        } else {
            if (context != null) {
                Toast.makeText(context, "No se encuentran elementos", Toast.LENGTH_LONG).show();
            }
        }

        return list;
    }


    public static List<dsoRow> ConstructorALLData(Context context, Cursor cursor) { //constructor para las listas
        ArrayList<dsoRow> dsosList = new ArrayList<>();

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            do {
                String cat_ref = cursor.getString(0);
                String name = cursor.getString(1);
                String tipo = cursor.getString(2);
                String clasif = cursor.getString(3);
                boolean toview = Boolean.valueOf(cursor.getString(4));
                boolean visto = Boolean.valueOf(cursor.getString(5));
                int rate = cursor.getInt(6);
                String id = cursor.getString(7);

                dsoRow dso = new dsoRow(cat_ref, name, tipo, clasif, toview, visto, rate, id);
                dsosList.add(dso);

            } while (cursor.moveToNext());
        } else {
            Toast.makeText(context, "No hay objetos disponibles", Toast.LENGTH_SHORT).show();
        }
        return dsosList;
    }


    public static List<Memory> ConstructorMEMO(Context context, Cursor cursor) {
        List<Memory> list = new ArrayList<>();

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                String id = cursor.getString(0);
                String dsoID = cursor.getString(1);
                String date = cursor.getString(2);
                String memo_text = cursor.getString(3);
                String place = cursor.getString(4);
                String telescope = cursor.getString(5);
                String eyepiece= cursor.getString(6);
                String dsoTag = date;

                Memory item = new Memory(id, dsoID, date, memo_text, place,
                        telescope, eyepiece, dsoTag);
                list.add(item);

            } while (cursor.moveToNext());
        } else {
            //Context context = context;
            if (context != null) {
                //Toast.makeText(context, "No se encuentran elementos", Toast.LENGTH_LONG).show();
            }
        }

        return list;
    }

    public static List<Memory> ConstructorMEMOFiltered(Context context, Cursor cursor,
                                                       boolean isDateFilter) {
        List<Memory> list = new ArrayList<>();

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                String id = cursor.getString(0);
                String dsoID = cursor.getString(1);
                String date = cursor.getString(2);
                String memo_text = cursor.getString(3);
                String place = cursor.getString(4);
                String telescope = cursor.getString(5);
                String eyepiece= cursor.getString(6);
                String dsoTag;
                if (isDateFilter) {
                    dsoTag = cursor.getString(7);

                } else {
                    dsoTag = cursor.getString(7) + " (" + date + ")";
                }

                Memory item = new Memory(id, dsoID, date, memo_text, place,
                        telescope, eyepiece, dsoTag);
                list.add(item);

            } while (cursor.moveToNext());
        } else {
            //Context context = context;
            if (context != null) {
                //Toast.makeText(context, "No se encuentran elementos", Toast.LENGTH_LONG).show();
            }
        }

        return list;
    }


    public static List<row> ConstructorRow(Context context, Cursor cursor) {
        List<row> list = new ArrayList<>();

        if (cursor == null) return list;
        try {
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                do {
                    String id = cursor.getString(0);
                    String name = cursor.getString(1);
                    String cat = cursor.getString(2);
                    String ref = cursor.getString(3);
                    String tipo = cursor.getString(4);
                    String clasif = cursor.getString(5);

                    row item = new row(id, name, cat, ref, tipo, clasif);
                    list.add(item);

                } while (cursor.moveToNext());
            } else {
                if (context != null) {
                    Toast.makeText(context, "No se encuentran elementos", Toast.LENGTH_SHORT).show();
                }
            }
        } finally {
            cursor.close();
        }

        return list;
    }


    public static List<rowList> ConstructorLists(Context context, Cursor cursor) {
        List<rowList> list = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {

            do {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String dsoIn = cursor.getString(2);
                Boolean isSeq = Boolean.parseBoolean(cursor.getString(3));
                rowList item = new rowList(id, name, dsoIn, isSeq);
                list.add(item);
            } while (cursor.moveToNext());
            if (list.isEmpty()) {
                Toast.makeText(context, "No se encuentran elementos", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "No se encuentran elementos", Toast.LENGTH_LONG).show();
        }

        return list;
    }


    public static ArrayList ConstructorWithConstellation(Context context, Cursor cursor) {
        ArrayList list = new ArrayList<>();
        String OldCon = "";

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                String con = cursor.getString(1);
                if (!OldCon.equalsIgnoreCase(con)) {
                    list.add(con);
                    OldCon = con;
                }
                String cat_ref = cursor.getString(0);
                String name = cursor.getString(2);
                String tipo = cursor.getString(3);
                String clasif = cursor.getString(4);
                boolean toview = Boolean.parseBoolean(cursor.getString(5));
                boolean visto = Boolean.parseBoolean(cursor.getString(6));
                int rate = cursor.getInt(7);
                String id = cursor.getString(8);

                dsoRow dso = new dsoRow(cat_ref, name, tipo, clasif, toview, visto, rate, id);
                list.add(dso);

            } while (cursor.moveToNext());
        } else {
            Toast.makeText(context, "La lista está vacía", Toast.LENGTH_LONG).show();
        }
        return list;
    }

    public static class ChartDataResult {
        public final List<String> chartNames;
        public final Map<String, ChartData> chartDataMap;

        public ChartDataResult(List<String> chartNames, Map<String, ChartData> chartDataMap) {
            this.chartNames = chartNames;
            this.chartDataMap = chartDataMap;
        }
    }

    public static ChartDataResult ConstructorCharts(Cursor cursor) {
        List<String> chartNames = new ArrayList<>();
        Map<String, ChartData> chartDataMap = new HashMap<>();
        List<String> maps_files = Common_functions.getNameFilesInFolder("maps");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(1); // Nombre de la carta

                ChartData data = new ChartData(
                        cursor.getString(0), // _id
                        cursor.getString(2), // izquierda
                        cursor.getString(3), // derecha
                        cursor.getString(4), // arriba
                        cursor.getString(5), // abajo
                        cursor.getDouble(6), // AR Mínima
                        cursor.getDouble(7), // AR Máxima
                        cursor.getDouble(8), // Dec Mínima
                        cursor.getDouble(9)  // Dec Máxima
                );

                chartNames.add(name);
                chartDataMap.put(name, data);
            } while (cursor.moveToNext());

            cursor.close();
        }
        for (String map_file : maps_files) {
            String map_file_name = map_file.split("\\.")[0];
            if (!chartNames.contains(map_file_name)) {
                chartNames.add(map_file_name);
                chartDataMap.put(map_file_name, new ChartData(map_file, "0", "0", "0", "0", 0, 0, 0, 0));
            }
        }

        return new ChartDataResult(chartNames, chartDataMap);
    }


}