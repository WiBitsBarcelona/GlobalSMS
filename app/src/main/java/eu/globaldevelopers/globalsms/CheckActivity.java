package eu.globaldevelopers.globalsms;

import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CheckActivity extends AppCompatActivity {

    String codigo = "";
    String asteriscos = "";
    public static final String MyPREFERENCES2 = "MyPrefs" ;

    SharedPreferences sharedpreferences;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    String gsms = "GlobalSMS\n";
    String globaltank = "GlobalTank SLU\n";

    ProgressDialog progress;

    PinPadActivity.ContadorTimeOut contadortimeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_check);
        try {
            findBT();
            openBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            //for higher api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public void PulsadoFunction(View v){
        switch(v.getId()) {
            case R.id.btn_0:
                codigo = codigo + "0";
                break;
            case R.id.btn_1:
                codigo = codigo + "1";
                break;
            case R.id.btn_2:
                codigo = codigo + "2";
                break;
            case R.id.btn_3:
                codigo = codigo + "3";
                break;
            case R.id.btn_4:
                codigo = codigo + "4";
                break;
            case R.id.btn_5:
                codigo = codigo + "5";
                break;
            case R.id.btn_6:
                codigo = codigo + "6";
                break;
            case R.id.btn_7:
                codigo = codigo + "7";
                break;
            case R.id.btn_8:
                codigo = codigo + "8";
                break;
            case R.id.btn_9:
                codigo = codigo + "9";
                break;
        }
        asteriscos = codigo;
        TextView textView = (TextView) findViewById(R.id.codigoactual);
        textView.setText(asteriscos);
    }

    public void borrarcaracter(View v){
        int l = codigo.length();
        if(l == 0){
            asteriscos = codigo;
            TextView textView = (TextView) findViewById(R.id.codigoactual);
            textView.setText(asteriscos);
        }else{
            codigo = codigo.substring(0, codigo.length() -1);
            asteriscos = codigo;
            TextView textView = (TextView) findViewById(R.id.codigoactual);
            textView.setText(asteriscos);
        }

    }

    public void btncancelar(View v){
        Intent Intent = new Intent(this, MainActivity.class);
        startActivity(Intent);
    }

    public void btnaceptar(final View v){

        int l = codigo.length();
        if(l == 0) {
            AlertDialog.Builder alert =new AlertDialog.Builder(this);
            alert.setTitle(this.getString(R.string.alert_no_code));
            alert.setCancelable(false);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            alert.show();
        }else {
            progress = new ProgressDialog(this);
            progress.setMessage(this.getString(R.string.spinner_conectando));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.setProgress(0);
            progress.show();

            sharedpreferences = getSharedPreferences(MyPREFERENCES2, Context.MODE_PRIVATE);
            final String cabecera = sharedpreferences.getString("cabeceraKey", null) + "\n";
            final String server = sharedpreferences.getString("serverKey", null);
            final String terminal = sharedpreferences.getString("terminalKey", null);
            RequestQueue queue = Volley.newRequestQueue(this);  // this = context
            String url = server + "/check_tr.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String datareceived) {
                            // response
                            Log.d("Response", datareceived);
                            if (datareceived.equals("nohaydatos")) {
                                //No esta reservado, vuelvo
                                Toast.makeText(getBaseContext(), R.string.error_no_encontrado, Toast.LENGTH_SHORT).show();
                            } else {
                                final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                                final String hora = new SimpleDateFormat("HH:mm").format(new Date()) + "\n\n";
                                try{
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


                                    JSONArray jsonArray = new JSONArray(datareceived);

                                    String product_txt = null;
                                    // Get all jsonObject from jsonArray
                                    for (int i = 0; i < jsonArray.length(); i++)
                                    {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        String date_str = null, product_id = null, code = null, response = null, liters = null, liters_a = null, trans_code = null, status = null;


                                        //Date
                                        if (jsonObject.has("fecha_hora") && !jsonObject.isNull("fecha_hora")) {
                                            date_str = jsonObject.getString("fecha_hora");
                                            String[] f_separated = date_str.split(" ");
                                            String[] datearray = f_separated[0].split("-");
                                            String dia = datearray[2];
                                            String mes = datearray[1];
                                            String anho = datearray[0];
                                            String date = "TRANSACTION DATE\n " + dia + "/" + mes + "/" + anho + "\n\n";
                                            mmOutputStream.write(date.getBytes());
                                        }

                                        if (jsonObject.has("producto") && !jsonObject.isNull("producto")) {
                                            product_id = jsonObject.getString("producto");
                                                switch (product_id){
                                                    case "1":
                                                        product_txt = "PRODUCT: DIESEL\n\n";
                                                        break;
                                                    case "13":
                                                        product_txt = "PRODUCT: AD BLUE\n\n";
                                                        break;
                                                }
                                                //ALINEAR IZQUIERDA
                                            mmOutputStream.write(0x1B);
                                            mmOutputStream.write(0x61);
                                            mmOutputStream.write(0x00);
                                            mmOutputStream.write(product_txt.getBytes());
                                        }

                                        // code
                                        if (jsonObject.has("codigo") && !jsonObject.isNull("codigo")) {
                                            code = jsonObject.getString("codigo");
                                            String code_txt = "CODE: " + code + "\n";
                                            mmOutputStream.write(code_txt.getBytes());
                                        }

                                        // liters
                                        if (jsonObject.has("litros_reales") && !jsonObject.isNull("litros_reales")) {
                                            liters = jsonObject.getString("litros_reales");
                                            if(!liters.equals("0.00")) {
                                                String liters_txt = "LITERS: " + liters + "\n";
                                                mmOutputStream.write(liters_txt.getBytes());
                                            }
                                        }

                                        // liters authorized
                                        if (jsonObject.has("litros_autorizados") && !jsonObject.isNull("litros_autorizados")) {
                                            liters_a = jsonObject.getString("litros_autorizados");
                                            if(liters.equals("0.00")) {
                                                String liters_a_txt = "AUTHORIZED LITERS: " + liters_a + "\n";
                                                mmOutputStream.write(liters_a_txt.getBytes());
                                            }
                                        }

                                        // trans_code
                                        if (jsonObject.has("num_operacion") && !jsonObject.isNull("num_operacion")) {
                                            trans_code = jsonObject.getString("num_operacion");
                                            String tr_txt = "TRANSACTION: " + trans_code + "\n";
                                            mmOutputStream.write(tr_txt.getBytes());
                                        }

                                        // estatus
                                        if (jsonObject.has("estado") && !jsonObject.isNull("estado")) {
                                            status = jsonObject.getString("estado");
                                            String tr_txt = null;
                                            switch (status){
                                                case "ACTIVA":
                                                    tr_txt = "STATUS: RESERVED\n\n";
                                                    break;
                                                case "FINALIZADA":
                                                    tr_txt = "STATUS: FINISHED\n\n";
                                                    break;
                                            }

                                            mmOutputStream.write(tr_txt.getBytes());
                                        }
                                    }
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

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("terminal", terminal);
                    params.put("codigo", codigo);
                    return params;
                }
            };
            queue.add(postRequest);

            final Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(1000);
                        progress.dismiss();
                        CheckActivity.this.finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
        }
    }

    void findBT() throws IOException{

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

    @Override
    protected void onDestroy () {
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

}