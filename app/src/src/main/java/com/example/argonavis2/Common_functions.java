package com.joserp.argonavis;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class Common_functions {

    public static String[] getFilesInFolder(String FolderName){
        String APP_PATH = "Argonavis";
        String folderpath =  Environment.getExternalStorageDirectory() + File.separator + APP_PATH + File.separator + FolderName;
        File carpeta = new File(folderpath);
        return carpeta.list();
    }

    public static String getFileURL(String FolderName, String FileName) {
        String APP_PATH = "Argonavis";
        if (FolderName.equalsIgnoreCase("root")) {
            return Environment.getExternalStorageDirectory().getPath() + File.separator + "Argonavis" + File.separator + FileName;
        } else {
            return Environment.getExternalStorageDirectory() + File.separator + APP_PATH + File.separator + FolderName + File.separator + FileName;
        }
    }

    public static String getFolderURL(String FolderName){
        return Environment.getExternalStorageDirectory().getPath() + File.separator + "Argonavis" + File.separator + FolderName;
    }

    public static String getFechaNow(){
        Calendar cal = Calendar.getInstance();
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        int mes = cal.get(Calendar.MONTH) + 1;
        int ano = cal.get(Calendar.YEAR);
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        return ano + "_" + mes + "_" + dia + "_" + hora + "_" + min;
    }

    public static String getZipFileURL(){
        String namefile =  "Argonavis_" + Common_functions.getFechaNow() + ".zip";
        return Common_functions.getFileURL("backup", namefile);
    }

    public static ArrayList<String> Comprobar_mapa(String Mapas) { //Comprueba si el mapa existe en el directorio
        ArrayList<String> mapas = new ArrayList<String>();
        String PICS_PATH = "maps";

        for (String map: Mapas.split(",")){
            //String filepath = Environment.getExternalStorageDirectory() + File.separator + APP_PATH + File.separator + PICS_PATH + File.separator + map.trim() + ".jpg";
            File imgFile = new File(getFileURL(PICS_PATH,map.trim() + ".jpg"));
            if (imgFile.exists()){      //si el archivo existe se comprueba si ya esta en la lista mapas y en caso de que no este se agrega a la lista
                if (!mapas.contains(map.trim()))
                    mapas.add(map.trim());
            }
            //El siguiente fragmento de codigo ordena la lista con los mapas disponibles
            //mapas.sort(String::compareToIgnoreCase);      //api insuficiente
            Collections.sort(mapas, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });
        }
        return mapas;
    }

}
