package com.joserp.argonavis.AuxFun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.joserp.argonavis.BaseRow;
import com.joserp.argonavis.DBHelper;
import com.joserp.argonavis.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Common_functions extends AppCompatActivity{
    private static String APP_PATH = "Argonavis";
    private static String PICS_PATH = "pics";
    private static String MAPS_PATH = "maps";
    private static String BACKUP_FOLDER = "backup";
    private static final String DB_NAME = "argonavisdb.sqlite";


    /**
     * Devuelve la carpeta raíz /Documents/Argonavis
     */
    public static File getArgonavisFolder() {
        File documentsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        return new File(documentsFolder, APP_PATH);
    }

    public static File getBackupFolder() {
        return new File(getArgonavisFolder(), BACKUP_FOLDER);
    }


    /**
     * Devuelve la ruta absoluta de la carpeta de imágenes (/Documents/Argonavis/pics)
     */
    public static String getPicsPath() {
        File picsFolder = new File(getArgonavisFolder(), PICS_PATH);
        return picsFolder.getAbsolutePath();
    }

    /**
     * Devuelve la ruta absoluta de la carpeta de mapas (/Documents/Argonavis/maps)
     */
    public static String getMapsPath() {
        File mapsFolder = new File(getArgonavisFolder(), MAPS_PATH);
        return mapsFolder.getAbsolutePath();
    }


    /**
     * Obtiene el listado de archivos dentro de una subcarpeta de Argonavis (ej: "pics", "maps", "backup")
     */
    public static String[] getFilesInFolder(String FolderName) {
        File carpeta = new File(getArgonavisFolder(), FolderName);
        if (carpeta.exists() && carpeta.isDirectory()) {
            return carpeta.list();
        }
        return new String[0]; // Devuelve array vacío en lugar de null si no existe la carpeta para evitar Crash (NullPointerException)
    }

    public static List<String> getNameFilesInFolder(String FolderName){
        String folderpath =  Environment.getExternalStorageDirectory() + File.separator + APP_PATH + File.separator + FolderName;
        return obtenerNombresArchivos(folderpath);
    }

    public static List<String> obtenerNombresArchivos(String rutaDirectorio) {
        List<String> nombresArchivos = new ArrayList<>();

        File directorio = new File(rutaDirectorio);

        // Verificar si el directorio existe y es accesible
        if (directorio.exists() && directorio.isDirectory()) {
            // Obtener array de nombres de archivos
            String[] archivos = directorio.list();

            if (archivos != null) {
                for (String nombreArchivo : archivos) {
                    File archivo = new File(directorio, nombreArchivo);
                    if (archivo.isFile()) {  // Solo archivos, no directorios
                        nombresArchivos.add(nombreArchivo);
                    }
                }
            }
        }

        return nombresArchivos;
    }

    public static String getMapUrl(String Name) {
        // Devuelve la String ruta absoluta del mapa Name
        return isFileExist(Name, MAPS_PATH);
    }


    public static String isFileExist(String name, String folderName) {
        if (name == null || name.trim().isEmpty()) return null;

        String cleanName = name.trim();
        File directory = new File(getArgonavisFolder(), folderName);

        if (!directory.exists()) {
            directory.mkdirs();
            return null;
        }

        // Probamos si existe directamente con las extensiones habituales
        String[] extensiones = {".jpg", ".png", ".jpeg", ".webp", ".JPG", ".PNG", ""};

        for (String ext : extensiones) {
            File fileCandidate = new File(directory, cleanName + ext);

            if (fileCandidate.exists() && fileCandidate.isFile()) {
                return fileCandidate.getAbsolutePath();
            }
        }

        return null;
    }

    public static ArrayList<String> getMapsPathIfExists(String name) {
        // Dada una lista de nombres de mapas separados por comas, devuelve un ArrayList con aquellos
        // nombres de mapas que se han encontrado en la carpeta mapas
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String[] names = name.split(",");

        // Lista para almacenar las rutas/nombres de los archivos que existen
        ArrayList<String> existingFilesPaths = new ArrayList<>();

        // Obtener la carpeta de mapas en Documentos/Argonavis/maps
        File folder = new File(getArgonavisFolder(), MAPS_PATH);

        // Verificar si la carpeta existe y es un directorio
        if (folder.exists() && folder.isDirectory()) {
            // Obtener la lista de archivos en la carpeta
            File[] files = folder.listFiles();

            if (files != null) {
                // Recorrer cada nombre en el array de nombres
                for (String mapname : names) {
                    String cleanMapName = mapname.trim(); // Limpiamos posibles espacios blancos tras la coma

                    // Buscar el archivo correspondiente en la carpeta
                    for (File file : files) {
                        if (file.isFile()) {
                            String fileName = file.getName();

                            // Extraer el nombre base sin extensión de forma segura
                            int lastDotIndex = fileName.lastIndexOf(".");
                            String baseName = (lastDotIndex > 0) ? fileName.substring(0, lastDotIndex) : fileName;

                            // Si el nombre coincide (ignorando mayúsculas/minúsculas), agregar a la lista
                            if (baseName.equalsIgnoreCase(cleanMapName)) {
                                existingFilesPaths.add(cleanMapName);
                                break; // Salir del bucle interno una vez encontrado el archivo
                            }
                        }
                    }
                }
            }
        }

        // Devolver la lista de nombres de mapas existentes
        return existingFilesPaths;
    }

    public static String getFileURL(String FolderName, String FileName) {
        File argonavisDir = getArgonavisFolder();

        if (FolderName == null || FolderName.equalsIgnoreCase("root") || FolderName.isEmpty()) {
            // Devuelve la ruta en la raíz: /Documents/Argonavis/FileName
            return new File(argonavisDir, FileName).getAbsolutePath();
        } else {
            // Devuelve la ruta en la subcarpeta: /Documents/Argonavis/FolderName/FileName
            File subFolder = new File(argonavisDir, FolderName);
            return new File(subFolder, FileName).getAbsolutePath();
        }
    }

    public static String getPicUrl(String Name) {
        return isFileExist(Name, PICS_PATH);
    }




    public static void vibrar(String duracion) {
        if (duracion.isEmpty()) {
            VibrationHelper.vibrate("estandar");
        } else {
            VibrationHelper.vibrate(duracion);
        }
    }

/*
    public static String getFechaNow(){
        Calendar cal = Calendar.getInstance();
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        int mes = cal.get(Calendar.MONTH) + 1;
        int ano = cal.get(Calendar.YEAR);
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        return ano + "_" + mes + "_" + dia + "_" + hora + "_" + min;
    }
 */

    public static String getToday() {
        Calendar cal = Calendar.getInstance();
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        int mes = cal.get(Calendar.MONTH) + 1;
        int ano = cal.get(Calendar.YEAR);

        return dia + "/" + mes + "/" + ano;
    }


    public static File getNewBackupURL(){
        //return new File(getBackupFolder(), "argonavisdb_" + Common_functions.getFechaNow() + ".sqlite");
        return new File(getBackupFolder(), "argonavisdb_" + getCurrentDateTime() + ".sqlite");
    }

    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return sdf.format(new Date());
    }


    public static boolean createDBBackup(Context context) {
        File internalDbFile = context.getDatabasePath("argonavisdb.sqlite");

        if (!internalDbFile.exists()) {
            return false;
        }

        // try-with-resources garantiza que los flujos se cierren siempre, incluso si hay error
        try (InputStream internalDB = new FileInputStream(internalDbFile);
             OutputStream publicDB = new FileOutputStream(getNewBackupURL())) {

            byte[] buffer = new byte[8192]; // Subido a 8KB para mayor rapidez
            int read;
            while ((read = internalDB.read(buffer)) != -1) {
                publicDB.write(buffer, 0, read);
            }

            return true;
        } catch (Exception e) {
            Log.e("Backup", "Error al crear la copia de seguridad", e);
            return false;
        }
    }



    public static String exportMemoriesToCSV(Context c) {
        //Abre la base de datos y extrae el contenido de memories en un string con formato csv
        StringBuilder csvBuilder = new StringBuilder();
        Cursor cursor = null;
        DBHelper db = DBHelper.getInstance(c);

        try {
            // Consultar todos los datos de la tabla
            cursor = db.getAllMemories();

            if (cursor != null && cursor.getCount() > 0) {
                String[] columnNames = cursor.getColumnNames();

                // 1. Escribir los encabezados de las columnas
                for (int i = 0; i < columnNames.length; i++) {
                    csvBuilder.append("\"").append(columnNames[i]).append("\"");
                    if (i < columnNames.length - 1) {
                        csvBuilder.append(",");
                    }
                }
                csvBuilder.append("\n");

                // 2. Escribir las filas de datos
                while (cursor.moveToNext()) {
                    for (int i = 0; i < columnNames.length; i++) {
                        String val = cursor.getString(i);
                        if (val == null) {
                            val = "";
                        } else {
                            // Escapar comillas dobles internas reemplazándolas por dos comillas
                            val = val.replace("\"", "\"\"");
                        }

                        csvBuilder.append("\"").append(val).append("\"");
                        if (i < columnNames.length - 1) {
                            csvBuilder.append(",");
                        }
                    }
                    csvBuilder.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return csvBuilder.toString();
    }




    public enum EstiloAstro {
        GALAXIA_GRUPO("Galaxia", "Grupo de Galaxias", R.drawable.galaxy, R.color.magenta2),
        GALAXIA_DEFAULT("Galaxia", null, R.drawable.galaxy, R.color.Naranja2),

        ESTRELLA_CARBONO("Estrella", "Carbono", R.drawable.carbono, R.color.granate2),
        ESTRELLA_DOBLE("Estrella", "Doble", R.drawable.doublestar, R.color.Naranja2),
        ESTRELLA_DOBLE_COLORIDA("Estrella", null, R.drawable.doublestar, R.color.magenta2),

        EXOTICO("Exótico", null, R.drawable.exotic, R.color.main2),

        CUMULO_GLOBULAR("Cúmulo", "Globular", R.drawable.globular, R.color.Naranja2),
        CUMULO_ABIERTO("Cúmulo", null, R.drawable.opencluster, R.color.Naranja2),

        NEBULOSA_PLANETARIA("Nebulosa", "Planetaria", R.drawable.planetaryneb, R.color.main2),
        NEBULOSA_PLANETARIA_PUNTUAL("Nebulosa", "Planetaria Puntual", R.drawable.planetaryneb, R.color.granate2),

        NEBULOSA_DEFAULT("Nebulosa", null, R.drawable.nebula, R.color.main2);

        private final String tipo;
        private final String clasif;
        private final int iconResId;
        private final Integer colorResId; // Integer permite nulos (como en Exótico)

        EstiloAstro(String tipo, String clasif, int iconResId, Integer colorResId) {
            this.tipo = tipo;
            this.clasif = clasif;
            this.iconResId = iconResId;
            this.colorResId = colorResId;
        }

        public static EstiloAstro obtenerConfig(String tipo, String clasif) {
            if (tipo == null) return null;

            EstiloAstro coincidenciaParcial = null;
            for (EstiloAstro estilo : values()) {
                if (estilo.tipo.equalsIgnoreCase(tipo)) {
                    // Si coincide el tipo y la clasificación es idéntica
                    if (estilo.clasif != null && estilo.clasif.equalsIgnoreCase(clasif)) {
                        return estilo;
                    }
                    // Guardamos el estilo por defecto del tipo por si no hay una clasificación específica
                    if (estilo.clasif == null) {
                        coincidenciaParcial = estilo;
                    }
                }
            }
            return coincidenciaParcial;
        }

        public int getIconResId() { return iconResId; }
        public Integer getColorResId() { return colorResId; }
    }

    public static void setIconForDsoRow(Context context, ImageView iconImageView, BaseRow row) {
        if (iconImageView == null || row == null) return;

        // 1. Estado por defecto y reset de filtros para reciclaje de vistas
        iconImageView.setImageResource(android.R.drawable.stat_notify_error);
        iconImageView.clearColorFilter();

        // 2. Buscar la configuración correspondiente
        EstiloAstro estilo = EstiloAstro.obtenerConfig(row.getTipo(), row.getClasif());

        // 3. Aplicar icono y color solo si se encontró una coincidencia
        if (estilo != null) {
            iconImageView.setImageResource(estilo.getIconResId());

            if (estilo.getColorResId() != null) {
                changeIconColor(context, iconImageView, estilo.getColorResId());
            }
        }
    }

    private static void changeIconColor(Context context, ImageView iconImageView, int colorId) {
        if (iconImageView != null && context != null) {
            int color = ContextCompat.getColor(context, colorId);
            iconImageView.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    public static void createFolder(Context context, String folderName) {
        File folder = new File(Environment.getExternalStorageDirectory() + folderName);
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                makeToast(context,"Carpeta creada");
            } else {
                makeToast(context,"Error al crear la carpeta");
            }
        }
    }

    public static void makeToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void makeSimpleCloseDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Aceptar", (dialog, id) -> {
                    // Acción al pulsar Aceptar
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static boolean isDataBase() {
        // Ruta al almacenamiento externo
        File directory = new File(Environment.getExternalStorageDirectory(), APP_PATH);

        File database = new File(directory, DB_NAME); // Ruta completa a la base de datos

        return database.exists();
    }

    public static String getStarCode(String dsoCat, String dsoRef) {
        String[] info = dsoRef.split(" ");  //Se busca un patron pi And o 59 Eri

        if (info.length == 2){
            String starCode = info[0];
            String shortCon = info[1];

            switch (shortCon.toLowerCase()) {
                case "and": shortCon = "Andromedae"; break;
                case "ant": shortCon = "Antliae"; break;
                case "apu": shortCon = "Apodis"; break;
                case "aqr": shortCon = "Aquarii"; break;
                case "aql": shortCon = "Aquilae"; break;
                case "ara": shortCon = "Arae"; break;
                case "ari": shortCon = "Arietis"; break;
                case "aur": shortCon = "Aurigae"; break;
                case "boo": shortCon = "Bootis"; break;
                case "cae": shortCon = "Caeli"; break;
                case "cam": shortCon = "Camelopardi"; break;
                case "cnc": shortCon = "Cancri"; break;
                case "cvn": shortCon = "Canum Venaticorum"; break;
                case "cma": shortCon = "Canis Majoris"; break;
                case "cmi": shortCon = "Canis Minoris"; break;
                case "cap": shortCon = "Capricorni"; break;
                case "car": shortCon = "Carinae"; break;
                case "cas": shortCon = "Cassiopeiae"; break;
                case "cen": shortCon = "Centauri"; break;
                case "cep": shortCon = "Cephei"; break;
                case "cet": shortCon = "Ceti"; break;
                case "cha": shortCon = "Chamaeleontis"; break;
                case "cir": shortCon = "Circini"; break;
                case "col": shortCon = "Columbae"; break;
                case "com": shortCon = "Comae Berenices"; break;
                case "cra": shortCon = "Coronae Australis"; break;
                case "crb": shortCon = "Coronae Borealis"; break;
                case "crv": shortCon = "Corvi"; break;
                case "crt": shortCon = "Crateris"; break;
                case "cru": shortCon = "Crucis"; break;
                case "cyg": shortCon = "Cygni"; break;
                case "del": shortCon = "Delphini"; break;
                case "dor": shortCon = "Doradus"; break;
                case "dra": shortCon = "Draconis"; break;
                case "equ": shortCon = "Equulei"; break;
                case "eri": shortCon = "Eridani"; break;
                case "for": shortCon = "Fornacis"; break;
                case "gem": shortCon = "Geminorum"; break;
                case "gru": shortCon = "Gruis"; break;
                case "her": shortCon = "Herculis"; break;
                case "hor": shortCon = "Horologii"; break;
                case "hya": shortCon = "Hydrae"; break;
                case "hyi": shortCon = "Hydri"; break;
                case "ind": shortCon = "Indi"; break;
                case "lac": shortCon = "Lacertae"; break;
                case "leo": shortCon = "Leonis"; break;
                case "lmi": shortCon = "Leonis Minoris"; break;
                case "lep": shortCon = "Leporis"; break;
                case "lib": shortCon = "Librae"; break;
                case "lup": shortCon = "Lupi"; break;
                case "lyn": shortCon = "Lyncis"; break;
                case "lyr": shortCon = "Lyrae"; break;
                case "men": shortCon = "Mensae"; break;
                case "mic": shortCon = "Microscopii"; break;
                case "mon": shortCon = "Monocerotis"; break;
                case "mus": shortCon = "Muscae"; break;
                case "nor": shortCon = "Normae"; break;
                case "oct": shortCon = "Octantis"; break;
                case "oph": shortCon = "Ophiuchi"; break;
                case "ori": shortCon = "Orionis"; break;
                case "pav": shortCon = "Pavonis"; break;
                case "peg": shortCon = "Pegasi"; break;
                case "per": shortCon = "Persei"; break;
                case "phe": shortCon = "Phoenicis"; break;
                case "pic": shortCon = "Pictoris"; break;
                case "psc": shortCon = "Piscium"; break;
                case "psa": shortCon = "Piscis Austrini"; break;
                case "pup": shortCon = "Puppis"; break;
                case "pyx": shortCon = "Pyxidis"; break;
                case "ret": shortCon = "Reticuli"; break;
                case "sge": shortCon = "Sagittae"; break;
                case "sgr": shortCon = "Sagittarii"; break;
                case "sco": shortCon = "Scorpii"; break;
                case "scl": shortCon = "Sculptoris"; break;
                case "sct": shortCon = "Scuti"; break;
                case "ser": shortCon = "Serpentis"; break;
                case "sex": shortCon = "Sextantis"; break;
                case "tau": shortCon = "Tauri"; break;
                case "tel": shortCon = "Telescopii"; break;
                case "tri": shortCon = "Trianguli"; break;
                case "tra": shortCon = "Trianguli Australis"; break;
                case "tuc": shortCon = "Tucanae"; break;
                case "uma": shortCon = "Ursae Majoris"; break;
                case "umi": shortCon = "Ursae Minoris"; break;
                case "vel": shortCon = "Velorum"; break;
                case "vir": shortCon = "Virginis"; break;
                case "vol": shortCon = "Volantis"; break;
                case "vul": shortCon = "Vulpeculae"; break;
                default: shortCon = info[1];
            }
            return starCode + " " + shortCon;
        } else {
            return dsoCat;
        }
    }

}
