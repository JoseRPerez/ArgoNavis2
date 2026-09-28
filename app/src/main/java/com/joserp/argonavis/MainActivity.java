package com.joserp.argonavis;

import static com.joserp.argonavis.R.id.navigation_DsoList;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import com.joserp.argonavis.BuildConfig;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.joserp.argonavis.AuxFun.Common_functions;
import com.joserp.argonavis.AuxFun.ToolbarListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        ToolbarListener {

    private FirstFragment firstFragment = new FirstFragment();
    private SecondFragment secondFragment = new SecondFragment();
    private ThirdFragment thirdFragment = new ThirdFragment();

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private Context context = this;
    private Switch nightModeSwitch;
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String THEME_KEY = "night_mode";
//    private SharedPreferences sharedPreferences;
    private BottomNavigationView bottomNavigationView;
    private ActionBarDrawerToggle toggle;
    private ActivityResultLauncher<Intent> saveCsvLauncher;
    private ActivityResultLauncher<String> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ocultar barra de navegación de android
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.activity_main);

        // Inicializar SharedPreferences
        //sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Configurar la carpeta Argonavis en Documentos (Crear subcarpetas y gestionar base de datos)
        checkAndSetupArgonavisFolder();

        // Comprobamos y/o solicitamos permisos necesarios en tiempo de ejecución
        comprobarYSolicitarPermisos();

        // Configurar el NavigationDrawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Configurar BottomNavigation
        bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Configurar el Switch del modo noche
       /* MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_night_mode);
        if (menuItem != null) {
            nightModeSwitch = menuItem.getActionView().findViewById(R.id.night_mode_switch);
        }

        boolean isNightMode = sharedPreferences.getBoolean(THEME_KEY, false);
        if (nightModeSwitch != null) {
            nightModeSwitch.setChecked(isNightMode);
        }

        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if (nightModeSwitch != null) {
            nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (isChecked) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        editor.putBoolean(THEME_KEY, true);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        editor.putBoolean(THEME_KEY, false);
                    }
                    editor.apply();
                    recreate();
                }
            });
        }*/


        // Inicializar el launcher que manejará la respuesta del selector de archivos
        saveCsvLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            // Escribir el contenido CSV en el archivo seleccionado por el usuario
                            writeCsvToUri(uri, Common_functions.exportMemoriesToCSV(context));
                        }
                    }
                }
        );

        // Registrar launcher para importar listas CSV
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        importList(uri);
                    }
                }
        );

        // Cargar el fragmento inicial
        if (savedInstanceState == null) {
            loadfragment(firstFragment);
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }


    private void writeCsvToUri(Uri uri, String data) {
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri);
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {

            if (outputStream != null) {
                writer.write(data);
                writer.flush();
                // ¡Listo! Archivo guardado con éxito
                android.widget.Toast.makeText(this, "Archivo CSV guardado correctamente", android.widget.Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            android.widget.Toast.makeText(this, "Error al guardar el archivo: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
        }
    }

    // Launcher para manejar múltiples permisos de forma sencilla
    private final ActivityResultLauncher<String[]> requestPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                boolean concedido = false;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
                    Boolean images = permissions.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES, false);
                    Boolean userSelected = permissions.getOrDefault(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED, false);
                    concedido = (images != null && images) || (userSelected != null && userSelected);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13
                    Boolean images = permissions.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES, false);
                    concedido = images != null && images;
                } else { // Android 10, 11 y 12 (API 29-32)
                    Boolean storage = permissions.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false);
                    concedido = storage != null && storage;
                }

                if (concedido) {
                    Toast.makeText(this, "Permisos otorgados", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Se requiere permiso para mostrar imágenes", Toast.LENGTH_SHORT).show();
                }
            });

    private void comprobarYSolicitarPermisos() {
        if (tienePermisoConcedido()) {
            Log.d("ArgoNavis", "Permissions granted");
        } else {
            // Solicitar los permisos adecuados según la versión de Android
            List<String> permisosASolicitar = new ArrayList<>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34
                permisosASolicitar.add(Manifest.permission.READ_MEDIA_IMAGES);
                permisosASolicitar.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33
                permisosASolicitar.add(Manifest.permission.READ_MEDIA_IMAGES);
            } else { // API 29 a 32
                permisosASolicitar.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            requestPermissionsLauncher.launch(permisosASolicitar.toArray(new String[0]));
        }
    }

    private boolean tienePermisoConcedido() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    // =========================================================================
    // LÓGICA DE GESTIÓN DE LA CARPETA PUBLIC: Documents/Argonavis
    // =========================================================================

    /**
     * Comprueba y crea automáticamente las carpetas necesarias en Documentos/Argonavis
     * y copia la base de datos de Assets al directorio privado
     */
    private void checkAndSetupArgonavisFolder() {
        // Ruta: /Almacenamiento principal/Documents/Argonavis
        File documentsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File argonavisFolder = new File(documentsFolder, "Argonavis");

        // 1. Crear carpeta principal Argonavis si no existe
        if (!argonavisFolder.exists()) {
            argonavisFolder.mkdirs();
        }

        // 2. Crear las subcarpetas pics, maps, backup y cons
        String[] subFolders = {"pics", "maps", "backup", "cons"};
        for (String folderName : subFolders) {
            File subDir = new File(argonavisFolder, folderName);
            if (!subDir.exists()) {
                subDir.mkdirs();
            }
        }

        // Solo copiamos de Assets si es la primera vez que se ejecuta la app
        File internalDb = getDatabasePath("argonavisdb.sqlite");
        if (!internalDb.exists()) {
            copyAssetToFile("argonavisdb.sqlite", internalDb);
        }
    }

    /**
     * Copia la base de datos desde Assets hasta el almacenamiento privado.
     */
    private void copyAssetToFile(String assetName, File destFile) {
        try (InputStream in = getAssets().open(assetName);
             OutputStream out = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error al copiar base de datos desde Assets", e);
        }
    }


    // =========================================================================
    // BARRA INFERIOR DE NAVEGACIÓN Y MENU LATERAL
    // =========================================================================

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            loadfragment(firstFragment);
            return true;
        } else if (itemId == navigation_DsoList) {
            loadfragment(secondFragment);
            return true;
        } else if (itemId == R.id.navigation_UserLists) {
            loadfragment(thirdFragment);
            return true;
        } else if (itemId == R.id.buy_coffee) {
            gotoBuyMeACoffee();
            return true;
        } else if (itemId == R.id.navigation_toViewList) {
            goToToViewList();
            return true;
        } else if (itemId == R.id.navigation_meridian) {
            gotoMeridian();
            return true;
        } else if (itemId == R.id.nav_BackUp) {
            if (Common_functions.createDBBackup(context)) {
                Toast.makeText(this, "Exportada la base de datos a backup", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al crear el backup", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.nav_ImportDB) {
            importDatabase(context);
            return true;
        } else if (itemId == R.id.memory_query) {
            askForMemoryField();
            return true;
        } else if (itemId == R.id.nav_newDSO) {
            openNewObjectActivity();
            return true;
        } else if (itemId == R.id.nav_importList) {
            filePickerLauncher.launch("text/plain");
            return true;
        } else if (itemId == R.id.nav_recalculate) {
            Recalcular();
            return true;
        } else if (itemId == R.id.edit_chartssettings) {
            openChartEditor();
            return true;
        } else if (itemId == R.id.set_apikey) {
            setApiKey();
            return true;
        } else if (itemId == R.id.nav_about) {
            showInformation();
            return true;
        } else if (itemId == R.id.nav_close_app) {
            mostrarDialogoConfirmacionCierre();
            return true;
        }
        return false;
    }

    private void gotoBuyMeACoffee() {
        String url = "https://www.buymeacoffee.com/joserp"; // Sustituye por tu enlace
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }


    //inicio pruebas SAF base de datos
    private static final int REQUEST_CODE_IMPORT_DB = 1001;

    private void importDatabase(Context context) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // Todos los tipos de archivos
        startActivityForResult(intent, REQUEST_CODE_IMPORT_DB);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMPORT_DB && resultCode == RESULT_OK) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                // Obtener permisos persistentes
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                // Copiar el archivo a la carpeta privada
                copyDatabaseFromUri(fileUri);
            }
        }
    }

    private void copyDatabaseFromUri(Uri sourceUri) {
        try {
            // Obtener el nombre del archivo original
            String fileName = getFileNameFromUri(sourceUri);
            if (fileName == null || !fileName.endsWith(".sqlite")) {
                Toast.makeText(this, "Por favor selecciona un archivo .sqlite", Toast.LENGTH_SHORT).show();
                return;
            }

            // LLamamos a copia de seguridad antes de inicar el proceso de importacion
            Common_functions.createDBBackup(context);

            // Ruta destino en la carpeta de la app
            File destFile = getDatabasePath("argonavisdb.sqlite");

            // Copiar el contenido
            InputStream inputStream = getContentResolver().openInputStream(sourceUri);
            FileOutputStream outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            // Cerrar la conexión a la base de datos actual si está abierta
            if (database != null && database.isOpen()) {
                database.close();
            }

            if (dbHelper != null) {
                dbHelper.close(); // Cerrar el helper también
            }

            // Crear nueva instancia del helper
            dbHelper = new DBHelper(this);
            database = dbHelper.getWritableDatabase(); // o getReadableDatabase(

            Toast.makeText(this, "Base de datos importada correctamente", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("ImportDB", "Error al importar base de datos", e);
            Toast.makeText(this, "Error al importar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    fileName = cursor.getString(nameIndex);
                }
            }
        }
        if (fileName == null) {
            fileName = uri.getPath();
            int cut = fileName.lastIndexOf('/');
            if (cut != -1) {
                fileName = fileName.substring(cut + 1);
            }
        }
        return fileName;
    }

    //fin pruebas SAF base de datos



    private void askForMemoryField() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter_memories, null);

        Spinner spMain = dialogView.findViewById(R.id.sp_main);
        Spinner spSecondary = dialogView.findViewById(R.id.sp_secondary);
        ImageButton btnAdd = dialogView.findViewById(R.id.Btn_add);
        Button exportButton = dialogView.findViewById(R.id.exportMemories);

        ArrayAdapter<String> mainAdapter = new ArrayAdapter<>(
                this,
                //android.R.layout.simple_spinner_item,
                R.layout.textview_row,
                new String[]{"Lugar", "Fecha"}
        );
        mainAdapter.setDropDownViewResource(R.layout.my_spinner);
        spMain.setAdapter(mainAdapter);

        ArrayAdapter<String> secondaryAdapter = new ArrayAdapter<>(
                this,
//                android.R.layout.simple_spinner_item,
                R.layout.textview_row,
                new ArrayList<String>()
        );
        secondaryAdapter.setDropDownViewResource(R.layout.my_spinner);
        spSecondary.setAdapter(secondaryAdapter);

        final String[] selectedFilterValue = new String[1];

        spMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                updateSecondarySpinner(selectedOption, spSecondary);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spSecondary.getAdapter();
                adapter.clear();
                adapter.notifyDataSetChanged();
            }
        });

        spSecondary.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFilterValue[0] = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFilterValue[0] = null;
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = (AlertDialog) ((View) v.getParent().getParent()).getTag();
                if (dialog != null) {
                    dialog.dismiss();
                }
                openActivityFilteredResults(spMain.getSelectedItem().toString(), spSecondary.getSelectedItem().toString());
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = Common_functions.exportMemoriesToCSV(context);

                if (!content.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/csv");
                    intent.putExtra(Intent.EXTRA_TITLE, "memorias.csv");

                    // 4. Lanzar el selector de ubicación del sistema
                    saveCsvLauncher.launch(intent);

                } else {
                    Toast.makeText(context, "No existen registros", Toast.LENGTH_SHORT).show();
                }
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialogo);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialogView.setTag(dialog);
        dialog.show();
    }

    private void openActivityFilteredResults(String s, String s1) {
        Common_functions.vibrar("corto");
        Intent intent = new Intent(this, MemoriesActivity.class);
        intent.putExtra("FILTER_TYPE", s);
        intent.putExtra("FILTER_VALUE", s1);
        startActivity(intent);
    }

    private void updateSecondarySpinner(String mainSelection, Spinner spSecondary) {
        DBHelper db = DBHelper.getInstance(this);
        List<String> items = new ArrayList<>();

        if ("Lugar".equals(mainSelection)) {
            items = db.getMemoryPlaces();
        } else if ("Fecha".equals(mainSelection)) {
            items = db.getMemoryDates();
        }

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spSecondary.getAdapter();
        adapter.clear();
        adapter.addAll(items);
        adapter.notifyDataSetChanged();

        if (!items.isEmpty()) {
            spSecondary.setSelection(0);
        }
    }

   /* private void setApiKey() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialogo);
        builder.setTitle("Introducir Gemini Api Key");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 48, 48, 48);

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Pega aquí tu Clave");
        input.setTextColor(getResources().getColor(R.color.main));
        input.setHintTextColor(getResources().getColor(R.color.gris3));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        input.setLayoutParams(params);
        layout.addView(input);

        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String key = input.getText().toString().trim();
            if (!key.isEmpty()) {
                DBHelper db = DBHelper.getInstance(context);
                db.saveIAKey(key);
                Common_functions.makeToast(context, "Clave Guardada");
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }*/

    private void setApiKey() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialogo);
        builder.setTitle("Configurar Gemini IA");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 8, 0, 16);

        // 1. Campo para la Clave API (con máscara para no mostrar el texto en pantalla)
        final EditText inputKey = new EditText(context);
        inputKey.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputKey.setHint("Pega aquí tu Clave API");
        inputKey.setTextColor(getResources().getColor(R.color.main));
        inputKey.setHintTextColor(getResources().getColor(R.color.gris3));
        inputKey.setLayoutParams(params);

        // 2. Campo para el Nombre del Modelo
        final EditText inputModel = new EditText(context);
        inputModel.setInputType(InputType.TYPE_CLASS_TEXT);
        inputModel.setHint("Modelo (ej. gemini-2.5-flash)");
        inputModel.setTextColor(getResources().getColor(R.color.main));
        inputModel.setHintTextColor(getResources().getColor(R.color.gris3));
        inputModel.setLayoutParams(params);

        // Cargar datos almacenados anteriormente
        String savedKey = IaConfigHelper.getApiKey(context);
        String savedModel = IaConfigHelper.getModelName(context);

        if (!savedKey.isEmpty()) {
            inputKey.setText(savedKey);
        }
        inputModel.setText(savedModel);

        layout.addView(inputKey);
        layout.addView(inputModel);

        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String key = inputKey.getText().toString().trim();
            String model = inputModel.getText().toString().trim();

            if (model.isEmpty()) {
                model = "gemini-2.5-flash";
            }

            if (!key.isEmpty()) {
                // Guardar en el almacenamiento interno de la app
                IaConfigHelper.saveIaConfig(context, key, model);

                /*
                // Mantener en SQLite si tu app lo requiere
                DBHelper db = DBHelper.getInstance(context);
                db.saveIAKey(key);*/

                Common_functions.makeToast(context, "Configuración guardada");
            } else {
                Common_functions.makeToast(context, "Debes introducir una clave válida");
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }


    private void openChartEditor() {
        Intent intent = new Intent(this, SettingChartActivity.class);
        startActivity(intent);
    }

    private void mostrarDialogoConfirmacionCierre() {
        new AlertDialog.Builder(this, R.style.dialogo)
                .setTitle("Cerrar aplicación")
                .setMessage("¿Estás seguro de que quieres salir?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cerrarBaseDeDatosEnFragmentos();

                        // Guardar la versión actualizada de la BD en la carpeta pública del usuario al salir
                        //exportInternalDatabaseToUserFolder();

                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public interface OnCloseAppListener {
        void onCloseApp();
    }

    private void cerrarBaseDeDatosEnFragmentos() {
        List<Fragment> fragmentos = getSupportFragmentManager().getFragments();
        for (Fragment fragmento : fragmentos) {
            if (fragmento instanceof OnCloseAppListener) {
                ((OnCloseAppListener) fragmento).onCloseApp();
            }
        }
    }

    public void showInformation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialogo);
        builder.setTitle("Acerca de");

        String mensaje = "Autor: " + getString(R.string.autor) + "\nVersion: " + BuildConfig.VERSION_NAME;
        builder.setMessage(mensaje);

        builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());
        builder.setNegativeButton("Abrir GitHub", (dialog, which) -> {
            String url = getString(R.string.github_link);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void Recalcular() {
        Common_functions.vibrar("");
        DBHelper db = new DBHelper(this);
        db.recalcular();

        // Exportar cambios a la carpeta accesible
        //exportInternalDatabaseToUserFolder();     //creo que no es necesario realizar copia en carpeta publica

        Toast.makeText(getApplicationContext(), "Hecho", Toast.LENGTH_LONG).show();
        db.close();
    }

    private void importList(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ImportListTask task = new ImportListTask(this, inputStream);
            task.doInBackground();

            Toast.makeText(this, "Lista importada", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al abrir el archivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void openNewObjectActivity() {
        Common_functions.vibrar("corto");
        Intent intent = new Intent(this, EditDSO.class);
        intent.putExtra("DSO", "");
        intent.putExtra("Finalidad de la funcion", "NuevoObjeto");
        startActivity(intent);
    }

    private void goToToViewList() {
        Intent intent = new Intent(this, ToViewActivity.class);
        startActivity(intent);
        Common_functions.vibrar("");
    }

    private void gotoMeridian() {
        Intent intent = new Intent(this, intoConstellationActivity.class);
        intent.putExtra("Constelacion", "Meridiano");
        startActivity(intent);
        Common_functions.vibrar("");
    }

    private void loadfragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    @Override
    public void updateToolbar(String title, boolean showHamburger, int menuResId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
        if (toggle != null) {
            toggle.setDrawerIndicatorEnabled(showHamburger);
        }
        if (toolbar != null) {
            toolbar.getMenu().clear();
            if (menuResId != 0) {
                toolbar.inflateMenu(menuResId);
            }
        }
    }

    public void syncToolbarWithDrawer(Toolbar toolbar) {
        this.toolbar = toolbar;
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Se ejecuta cuando la app pasa a segundo plano (el usuario pulsa Home, cambia de app o la minimiza)
        // Garantiza que los cambios queden guardados en Documents antes de que el usuario la destruya de la lista de recientes.
        //exportInternalDatabaseToUserFolder();
    }
}