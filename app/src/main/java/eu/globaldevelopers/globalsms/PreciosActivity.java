package eu.globaldevelopers.globalsms;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class PreciosActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String MyPRECIOS = "MyPrecios" ;
    public static final String diesel = "dieselKey";
    public static final String adblue = "adblueKey";
    public static final String reddiesel = "reddieselKey";

    public static String dieselactual;
    public static String adblueactual;
    public static String reddieselactual;

    SharedPreferences sharedpreferences;
    SharedPreferences sharedpreferences2;

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
    String saltolinea = "\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_precios);
        if(Build.VERSION.SDK_INT < 19){
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            //for higher api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sharedpreferences = getSharedPreferences(MyPRECIOS, Context.MODE_PRIVATE);

        dieselactual = sharedpreferences.getString("dieselKey", "0.00");
        adblueactual = sharedpreferences.getString("adblueKey", "0.00");
        reddieselactual = sharedpreferences.getString("reddieselKey", "0.00");

        try {
            findBT();
            openBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void DieselFunction(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("DIESEL");
        builder.setMessage(this.getString(R.string.precio_diesel));
        builder.setCancelable(false);
        builder.setIcon(R.drawable.pump_b);
        final EditText input = new EditText(this);
        input.append(dieselactual);
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        int length = input.getText().length();
        input.setSelection(length);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    salvap("Diesel", dieselactual, input.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(this.getString(R.string.btn_pinpad_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    public void AdblueFunction(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ADBLUE");
        builder.setMessage(this.getString(R.string.precio_adblue));
        builder.setCancelable(false);
        builder.setIcon(R.drawable.pump_b);
        final EditText input = new EditText(this);
        input.append(adblueactual);
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        int length = input.getText().length();
        input.setSelection(length);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    salvap("Adblue", adblueactual, input.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(this.getString(R.string.btn_pinpad_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void RedFunction(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.precio_rojo));
        builder.setMessage(this.getString(R.string.precio_rojo));
        builder.setCancelable(false);
        builder.setIcon(R.drawable.pump_b);
        final EditText input = new EditText(this);
        input.append(reddieselactual);
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        int length = input.getText().length();
        input.setSelection(length);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    salvap("RedDiesel", reddieselactual, input.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(this.getString(R.string.btn_pinpad_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    public void CancelFunction(View view){
        PreciosActivity.this.finish();
        Intent Intent = new Intent(this, MainActivity.class);
        startActivity(Intent);
    }

    void salvap(final String producto, final String  precioAnt, final String precioAct) throws IOException {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        switch (producto){
            case "Diesel":
                editor.putString("dieselKey", precioAct);
                editor.apply();
                dieselactual = precioAct;
                Toast.makeText(getBaseContext(),"DIESEL PRICE CHANGED SUCCESSFULLY",Toast.LENGTH_SHORT).show();
                imprime("DIESEL", precioAnt, precioAct);
                break;
            case "Adblue":
                editor.putString(adblue, precioAct);
                editor.apply();
                adblueactual = precioAct;
                Toast.makeText(getBaseContext(),"ADBLUE PRICE CHANGED SUCCESSFULLY",Toast.LENGTH_SHORT).show();
                imprime("ADBLUE", precioAnt, precioAct);
                break;
            case "RedDiesel":
                editor.putString(reddiesel, precioAct);
                editor.apply();
                reddieselactual = precioAct;
                Toast.makeText(getBaseContext(),"RED DIESEL PRICE CHANGED SUCCESSFULLY",Toast.LENGTH_SHORT).show();
                imprime("RED DIESEL", precioAnt, precioAct);
                break;
        }
    }

    void imprime(final String producto, final String  precioAnt, final String precioAct) throws IOException {

        try {
            final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            final String hora = new SimpleDateFormat("HH:mm").format(new Date());

            sharedpreferences2 = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String cabecera = sharedpreferences2.getString("cabeceraKey", null) + "\n";
            final String terminal = sharedpreferences2.getString("terminalKey", null);

            String msg = producto + " PRICE CHANGED\n";
            msg += " SUCCESSFULLY";
            msg += "\n";
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
                mmOutputStream.write(saltolinea.getBytes());
                //FEED 3 LINEAS
                mmOutputStream.write(0x1B);
                mmOutputStream.write(0x64);
                mmOutputStream.write(0x03);
                //DOUBLE HEIGHT ON
                mmOutputStream.write(0x1B);
                mmOutputStream.write(0x21);
                mmOutputStream.write(0x10);
                mmOutputStream.write(msg.getBytes());
                //DOUBLE HEIGHT OFF
                mmOutputStream.write(0x1B);
                mmOutputStream.write(0x21);
                mmOutputStream.write(0x00);
                //ALINEAR IZQUIERDA
                mmOutputStream.write(0x1B);
                mmOutputStream.write(0x61);
                mmOutputStream.write(0x00);
                String textopin = "Old Price: " + precioAnt + "\n";
                mmOutputStream.write(textopin.getBytes());
                String textoop = "New Price: " + precioAct + "\n\n\n\n";
                mmOutputStream.write(textoop.getBytes());
                //FEED 3 LINEAS
                mmOutputStream.write(0x1B);
                mmOutputStream.write(0x64);
                mmOutputStream.write(0x03);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                    // MP300 is the name of the bluetooth printer device
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
}
