package es.estech.ejemploconsimircontentprovider;

import es.estech.ejemploconsimircontentprovider.ClientesProvider.Clientes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog.Calls;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class    MainActivity extends Activity {

    private Button btnInsertar;
    private Button btnConsultar;
    private Button btnEliminar;
    private Button btnLimpiar;
    private Button btnContactos;
    private TextView txtResultados;


    private ListView lstNames;

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the list view
        this.lstNames = (ListView) findViewById(R.id.listNames);

        //Referencias a los controles
        txtResultados = findViewById(R.id.TxtResultados);
        btnConsultar = findViewById(R.id.BtnConsultar);
        btnInsertar = findViewById(R.id.BtnInsertar);
        btnEliminar = findViewById(R.id.BtnEliminar);
        btnLimpiar = findViewById(R.id.BtnLimpiar);
        btnContactos = findViewById(R.id.BtnContactos);

        btnConsultar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Columnas de la tabla a recuperar
                String[] projection = new String[] {
                        Clientes._ID,
                        Clientes.COL_NOMBRE,
                        Clientes.COL_TELEFONO,
                        Clientes.COL_EMAIL };

                Uri clientesUri =  ClientesProvider.CONTENT_URI;

                ContentResolver cr = getContentResolver();

                //Hacemos la consulta
                Cursor cur = cr.query(clientesUri,
                        projection, //Columnas a devolver
                        null,       //Condición de la query
                        null,       //Argumentos variables de la query
                        null);      //Orden de los resultados

                if (cur.moveToFirst())
                {
                    String nombre;
                    String telefono;
                    String email;

                    int colNombre = cur.getColumnIndex(Clientes.COL_NOMBRE);
                    int colTelefono = cur.getColumnIndex(Clientes.COL_TELEFONO);
                    int colEmail = cur.getColumnIndex(Clientes.COL_EMAIL);

                    txtResultados.setText("");

                    do
                    {

                        nombre = cur.getString(colNombre);
                        telefono = cur.getString(colTelefono);
                        email = cur.getString(colEmail);

                        txtResultados.append(nombre + " - " + telefono + " - " + email + "\n");

                    } while (cur.moveToNext());
                }
            }
        });

        btnInsertar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ContentValues values = new ContentValues();

                values.put(Clientes.COL_NOMBRE, "ClienteN");
                values.put(Clientes.COL_TELEFONO, "999111222");
                values.put(Clientes.COL_EMAIL, "nuevo@email.com");

                ContentResolver cr = getContentResolver();

                cr.insert(ClientesProvider.CONTENT_URI, values);
            }
        });

        btnEliminar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ContentResolver cr = getContentResolver();

                cr.delete(ClientesProvider.CONTENT_URI, Clientes.COL_NOMBRE + " = 'ClienteN'", null);
            }
        });

        btnLimpiar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                txtResultados.setText("");
            }
        });

        btnContactos.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // leemos y mostramos los contactos
                showContacts();

            }
        });
    }

    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            List<String> contacts = getContactNames();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contacts);
            lstNames.setAdapter(adapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Read the name of all the contacts.
     *
     * @return a list of names.
     */
    private List<String> getContactNames() {
        List<String> contacts = new ArrayList<>();
        // Get the ContentResolver
        ContentResolver cr = getContentResolver();
        // Get the Cursor of all the contacts
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        // Move the cursor to first. Also check whether the cursor is empty or not.
        if (cursor.moveToFirst()) {
            // Iterate through the cursor
            do {
                // Get the contacts name
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contacts.add(name);
            } while (cursor.moveToNext());
        }
        // Cerramos el cursor
        cursor.close();

        return contacts;
    }


}