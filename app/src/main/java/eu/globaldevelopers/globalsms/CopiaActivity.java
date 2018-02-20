package eu.globaldevelopers.globalsms;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ListMenuItemView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class CopiaActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    String gsms = "GlobalSMS\n";
    String globaltank = "GlobalTank SLU\n";
    String saltolinea = "\n\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copia);
        if(Build.VERSION.SDK_INT < 19){
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            //for higher api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }

        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#68a9ea")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        cargardatos();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT < 19){
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            //for higher api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    void imprime(String codigoT) throws IOException, InterruptedException {
        findBT();
        try {
            openBT();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "datos", null, 2);

        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor fila = bd.rawQuery(

                "select * from operaciones where id=" + codigoT, null);

        if (fila.moveToFirst()) {
            String tipo = fila.getString(1);
            String cabecera = fila.getString(2);
            String terminal = fila.getString(3);
            String fecha = fila.getString(4);
            String hora = fila.getString(5);
            String resultado = fila.getString(6);
            String codigo = fila.getString(7);
            String operacion="";
            operacion = fila.getString(8);
            String producto ="";
            producto = fila.getString(9);
            String litros_aceptados = fila.getString(10);
            String litros = "";
            litros = fila.getString(11);
            String total = "";
            total = fila.getString(12);
            String codigo_error = fila.getString(13);
            String error = fila.getString(14);

            if(!mmSocket.isConnected()){
                Toast.makeText(this, "NO Printer Connected",

                        Toast.LENGTH_SHORT).show();
            }else {

                //RESET
                mmOutputStream.write(0x1B);
                mmOutputStream.write(0x40);
                //CENTER
                mmOutputStream.write(0x1B);
                mmOutputStream.write(0x61);
                mmOutputStream.write(0x01);
                //GRANDE
                mmOutputStream.write(0x1D);
                mmOutputStream.write(0x21);
                mmOutputStream.write(0x22);
                //ENFASIS ON
                mmOutputStream.write(0x1B);
                mmOutputStream.write(0x45);
                mmOutputStream.write(0x01);
                mmOutputStream.write(gsms.getBytes());
                //NORMAL
                mmOutputStream.write(0x1D);
                mmOutputStream.write(0x21);
                mmOutputStream.write(0x00);
                mmOutputStream.write(globaltank.getBytes());
                //ENFASIS OFF
                mmOutputStream.write(0x1B);
                mmOutputStream.write(0x45);
                mmOutputStream.write(0x02);
                mmOutputStream.write(cabecera.getBytes());
                String pterminal = "\n Terminal: " + terminal + "\n\n";
                mmOutputStream.write(pterminal.getBytes());
                mmOutputStream.write(fecha.getBytes());
                String tabu = "     ";
                mmOutputStream.write(tabu.getBytes());
                mmOutputStream.write(hora.getBytes());
                mmOutputStream.write(saltolinea.getBytes());
                //FEED 3 LINEAS
                mmOutputStream.write(0x1B);
                mmOutputStream.write(0x64);
                mmOutputStream.write(0x03);
                //DOUBLE HEIGHT ON
                mmOutputStream.write(0x1B);
                mmOutputStream.write(0x21);
                mmOutputStream.write(0x10);
                mmOutputStream.write(resultado.getBytes());
                //DOUBLE HEIGHT OFF
                mmOutputStream.write(0x1B);
                mmOutputStream.write(0x21);
                mmOutputStream.write(0x00);

                //ES RESERVA ACEPTADA?
                if (resultado.equals("TRANSACTION ACCEPTED\n")) {
                    //ALINEAR IZQUIERDA
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x61);
                    mmOutputStream.write(0x00);
                    String textopin = "Transaction Code: " + codigo + "\n";
                    mmOutputStream.write(textopin.getBytes());
                    mmOutputStream.write(producto.getBytes());
                    mmOutputStream.write(litros.getBytes());
                    //FEED 3 LINEAS
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x64);
                    mmOutputStream.write(0x03);
                }

                //ES RESERVA CANCELADA?
                if (resultado.equals("TRANSACTION REFUSED\n")) {
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x21);
                    mmOutputStream.write(0x00);
                    mmOutputStream.write(codigo_error.getBytes());
                    mmOutputStream.write(error.getBytes());
                    //FEED 3 LINEAS
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x64);
                    mmOutputStream.write(0x03);
                }


                //ES FINALIZACION
                if (resultado.equals("TRANSACTION SUCCESSFULLY\nCOMPLETED\n")) {
                    //ALINEAR IZQUIERDA
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x61);
                    mmOutputStream.write(0x00);
                    String textopin = "Transaction Code: " + codigo + "\n";
                    mmOutputStream.write(textopin.getBytes());
                    mmOutputStream.write(operacion.getBytes());
                    String titulos = "Product     Liters      Total\n";
                    //UNDERLINE ON
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x2D);
                    mmOutputStream.write(0x01);
                    mmOutputStream.write(titulos.getBytes());
                    //UNDERLINE OFF
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x2D);
                    mmOutputStream.write(0x00);
                    mmOutputStream.write(producto.getBytes());
                    //FEED 3 LINEAS
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x64);
                    mmOutputStream.write(0x03);
                }

                //ES CANCELACION
                if (resultado.equals("TRANSACTION SUCCESSFULLY\nCANCELLED\n")) {
                    String textopin = "Transaction Code: " + codigo;
                    mmOutputStream.write(textopin.getBytes());
                    String saltos = "\n\n\n\n";
                    mmOutputStream.write(saltos.getBytes());
                    //FEED 3 LINEAS
                    mmOutputStream.write(0x1B);
                    mmOutputStream.write(0x64);
                    mmOutputStream.write(0x03);
                }
            }
        }
        bd.close();



    }

    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                Toast.makeText(getBaseContext(),"No bluetooth adapter available",Toast.LENGTH_SHORT).show();
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    if (device.getName().equals("Inner printer")) {
                        mmDevice = device;
                        //Toast.makeText(getBaseContext(),"Bluetooth Device Found",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (device.getName().equals("SPP-R200II")) {
                        mmDevice = device;
                        //Toast.makeText(getBaseContext(),"Bluetooth Device Found",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (device.getName().equals("HM-E200")) {
                        mmDevice = device;
                        //Toast.makeText(getBaseContext(),"Bluetooth Device Found",Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            //Toast.makeText(getBaseContext(),"Bluetooth Opened",Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                //myLabel.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onStop () {
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    public void cargardatos(){

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "datos", null, 2);

        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor fila = bd.rawQuery(

                "select * from operaciones order by id desc", null);

        LinearLayout linearLayout = new LinearLayout(this);

        ListView DynamicListView = new ListView(this);

        final String[] DynamicListElements = new String[fila.getCount()];


        int i = 0;
        if (fila.moveToFirst()) {
            do {
                if (fila.getString(1).equals("Reserve")){
                    String limiteimp;
                    if(fila.getString(11).equals("0")) {
                        limiteimp = "";
                    }else{
                        limiteimp = fila.getString(11).substring(0, fila.getString(11).length() - 3);
                    }
                    DynamicListElements[i] = fila.getString(0) + "    Type: " + fila.getString(1) + "\nCode: " + fila.getString(7) + "\n" + fila.getString(9) + limiteimp + fila.getString(4) + "    " + fila.getString(5) + "\n" + fila.getString(6);
                }
                if (fila.getString(1).equals("Finish")){
                    DynamicListElements[i] = fila.getString(0)+ "    Type: " + fila.getString(1) + "\nCode: " + fila.getString(7) + "\n" + fila.getString(4) + "    " + fila.getString(5) + "\n" + fila.getString(6);
                }
                if (fila.getString(1).equals("Cancel")){
                    DynamicListElements[i] = fila.getString(0)+ "    Type: " + fila.getString(1) + "\nCode: " + fila.getString(7) + "\n" + fila.getString(4) + "    " + fila.getString(5) + "\n" + fila.getString(6);
                }
                i++;
            } while (fila.moveToNext());



        } else

            Toast.makeText(this, "No Transactions Data to Show",

                    Toast.LENGTH_SHORT).show();

        bd.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (CopiaActivity.this, android.R.layout.simple_list_item_1, DynamicListElements);

        DynamicListView.setAdapter(adapter);

        linearLayout.addView(DynamicListView);

        this.setContentView(linearLayout, new LinearLayout.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));

        DynamicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String cadena = DynamicListElements[position];
                String codigot = cadena.substring(0,5);
                codigot = codigot.trim();
                try {
                    imprime(codigot);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


        });
    }
}
