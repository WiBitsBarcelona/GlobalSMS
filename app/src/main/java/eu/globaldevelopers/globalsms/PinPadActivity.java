package eu.globaldevelopers.globalsms;



import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.RemoteException;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.content.DialogInterface;
import android.content.Intent;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static java.lang.Integer.parseInt;

public class PinPadActivity extends AppCompatActivity {

    String codigo = "";
    String asteriscos = "";
    String textoproducto;
    String msg;
    String detalle;
    String litros;
    String codigoerror = "";
    String textoerror = "";
    String textoop;
    String respuesta;
    ArrayList QRArray = new ArrayList();
    int cuantosQR = 1;
    float litrostmp = 0;
    String tipo;
    String litrosT;
    Boolean EsEfiData = false;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String MySUPPLY = "MySupply";
    public static final String MyPRECIOS = "MyPrecios" ;

    SharedPreferences sharedpreferences;
    SharedPreferences sharedpreferences2;
    SharedPreferences sharedpreferences3;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    int counter = 1;
    volatile boolean stopWorker;

    String gsms = "GlobalSMS\n";
    String globaltank = "GlobalTank SLU\n";
    String saltolinea = "\n";

    ProgressDialog progress;

    ContadorTimeOut contadortimeout;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_pin_pad);
        try {
            findBT();
            openBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            //BUSCO TIPO TRANSACCIÓN Y CODIGO PRODUCTO.
            sharedpreferences2 = getSharedPreferences(MySUPPLY, Context.MODE_PRIVATE);
            final String producto = sharedpreferences2.getString("productoKey", null);
            final String transaction = sharedpreferences2.getString("tipoKey", null);

            switch (transaction){
                case "Nueva":
                    nuevaT(producto);
                    break;
                case "Cierre":
                    cierraT();
                    break;
                case "Cancela":
                    cancelaT();
                    break;
            }
        }

     }


    private static InputStream convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) );
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Source xmlSource = new DOMSource(doc);
            Result outputTarget = new StreamResult(outputStream);
            TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
            InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
            return is;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
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

    void nuevaT(final String prod){

        if (isNetworkAvailable() == false){
            mensajered();
        }else {

            contadortimeout = new ContadorTimeOut(10000, 1000);
            contadortimeout.start();

            progress = new ProgressDialog(this);
            progress.setMessage(this.getString(R.string.spinner_conectando));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.setProgress(0);
            progress.show();

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String cabecera = sharedpreferences.getString("cabeceraKey", null) + "\n";
            final String terminal = sharedpreferences.getString("terminalKey", null);
            final String secret = sharedpreferences.getString("secretKey", null);
            final String server = sharedpreferences.getString("serverKey", null);
            RequestQueue queue = Volley.newRequestQueue(this);  // this = context
            String url = server + "/reserva.php";
            Log.d("Servidor:", url);
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response", response);
                            Log.d("Codigo", codigo);
                            Log.d("Terminal", terminal);
                            Log.d("Secret", secret);
                            Log.d("Producto", prod);
                            final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                            final String hora = new SimpleDateFormat("HH:mm").format(new Date());
                            Log.d("Fecha", fecha);
                            Log.d("Hora", hora);
                            try {
                                InputStream is = convertStringToDocument(response);
                                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                                Document doc = dBuilder.parse(is);
                                Element element = doc.getDocumentElement();
                                element.normalize();
                                NodeList nList = doc.getElementsByTagName("smsdieselapi");
                                for (int i = 0; i < nList.getLength(); i++) {
                                    Node node = nList.item(i);
                                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                                        Element element2 = (Element) node;
                                        String exito = getValue("success", element2);
                                        if (exito.equals("1")) {
                                            Toast.makeText(getBaseContext(), "TRANSACTION ACCEPTED", Toast.LENGTH_SHORT).show();
                                            litros = "Authorized Liters: " + getValue("max_liters", element2) + "\n\n\n\n";
                                            String producto = getValue("product_code", element2);
                                            switch (producto) {
                                                case "DIZ":
                                                    textoproducto = "Authorized Product: DIESEL \n";

                                                    break;
                                                case "ADB":
                                                    textoproducto = "Authorized Product: AD BLUE \n";
                                                    break;
                                                case "FOD":
                                                    textoproducto = "Authorized Product: RED DIESEL \n";
                                            }
                                            try {
                                                msg = "TRANSACTION ACCEPTED";
                                                msg += "\n";
                                                for (int g = 0; g < 1; g++) {
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
                                                    //FEED 3 LINEAS
                                                    mmOutputStream.write(0x1B);
                                                    mmOutputStream.write(0x64);
                                                    mmOutputStream.write(0x03);
                                                    //DOUBLE HEIGHT ON
                                                    mmOutputStream.write(0x1B);
                                                    mmOutputStream.write(0x21);
                                                    mmOutputStream.write(0x10);
                                                    mmOutputStream.write(saltolinea.getBytes());
                                                    mmOutputStream.write(saltolinea.getBytes());
                                                    mmOutputStream.write(msg.getBytes());
                                                    //DOUBLE HEIGHT OFF
                                                    mmOutputStream.write(0x1B);
                                                    mmOutputStream.write(0x21);
                                                    mmOutputStream.write(0x00);
                                                    //ALINEAR IZQUIERDA
                                                    mmOutputStream.write(0x1B);
                                                    mmOutputStream.write(0x61);
                                                    mmOutputStream.write(0x00);
                                                    String textopin = "Transaction Code: " + codigo + "\n";
                                                    mmOutputStream.write(textopin.getBytes());
                                                    mmOutputStream.write(textoproducto.getBytes());
                                                    mmOutputStream.write(litros.getBytes());
                                                    //FEED 3 LINEAS
                                                    mmOutputStream.write(0x1B);
                                                    mmOutputStream.write(0x64);
                                                    mmOutputStream.write(0x03);
                                                    if (g == 0) {
                                                        Thread.sleep(2000);
                                                    }
                                                }
                                            } catch (NullPointerException e) {
                                                e.printStackTrace();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Toast.makeText(getBaseContext(), "TRANSACTION REFUSED", Toast.LENGTH_SHORT).show();
                                            codigoerror = getValue("error_code", element2) + "\n";
                                            textoerror = getValue("error_description", element2) + "\n\n\n\n";
                                            msg = "TRANSACTION REFUSED";
                                            msg += "\n";
                                            for (int g = 0; g < 1; g++) {
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
                                                //FEED 3 LINEAS
                                                mmOutputStream.write(0x1B);
                                                mmOutputStream.write(0x64);
                                                mmOutputStream.write(0x03);
                                                //DOUBLE HEIGHT ON
                                                mmOutputStream.write(0x1B);
                                                mmOutputStream.write(0x21);
                                                mmOutputStream.write(0x10);
                                                mmOutputStream.write(saltolinea.getBytes());
                                                mmOutputStream.write(saltolinea.getBytes());
                                                mmOutputStream.write(msg.getBytes());
                                                //DOUBLE HEIGHT OFF
                                                mmOutputStream.write(0x1B);
                                                mmOutputStream.write(0x21);
                                                mmOutputStream.write(0x00);
                                                mmOutputStream.write(saltolinea.getBytes());
                                                String textopin = "Transaction Code: " + codigo + "\n";
                                                mmOutputStream.write(textopin.getBytes());
                                                mmOutputStream.write(codigoerror.getBytes());
                                                mmOutputStream.write(textoerror.getBytes());
                                                //FEED 3 LINEAS
                                                mmOutputStream.write(0x1B);
                                                mmOutputStream.write(0x64);
                                                mmOutputStream.write(0x03);
                                                if (g == 0) {
                                                    Thread.sleep(3000);
                                                }
                                            }
                                        }
                                        salvaroperacion("Reserve", cabecera, terminal, fecha, hora, msg, codigo, textoproducto, "0", "0", litros, "0", codigoerror, textoerror);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            final Thread t = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        contadortimeout.cancel();
                                        sleep(1000);
                                        progress.dismiss();
                                        PinPadActivity.this.finish();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            t.start();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("secret", secret);
                    params.put("terminal", terminal);
                    params.put("producto", prod);
                    params.put("codigo", codigo);
                    return params;
                }
            };
            queue.add(postRequest);
        }
    }

    void cierraT(){
        if (isNetworkAvailable() == false){
            mensajered();
        }else {
            //Primero, miro si el código esta reservado.
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String server = sharedpreferences.getString("serverKey", null);
            final boolean qr = sharedpreferences.getBoolean("qrKey", false);
            final int pos = sharedpreferences.getInt("posKey", 0);
            RequestQueue queue = Volley.newRequestQueue(this);  // this = context
            String url = server + "/consultapin.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Response", response);
                            if (response.equals("noexiste")) {
                                //No esta reservado, vuelvo
                                Toast.makeText(getBaseContext(), R.string.error_no_reservado, Toast.LENGTH_SHORT).show();


                            } else {
                                //Esta reservado, miro si pido los litros o escaneo.
                                respuesta = response;
                                if (qr == true) {
                                    dialogoQR(respuesta);
                                    //scanQR();
                                } else {
                                    pedirlitros();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("codigo", codigo);
                    return params;
                }
            };
            queue.add(postRequest);
        }
    }

    void cancelaT(){
        if (isNetworkAvailable() == false){
            mensajered();
        }else {
            contadortimeout = new ContadorTimeOut(10000, 1000);
            contadortimeout.start();

            progress = new ProgressDialog(this);
            progress.setMessage(this.getString(R.string.spinner_conectando));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.setProgress(0);
            progress.show();

            try {
                openBT();
            } catch (IOException e) {
                e.printStackTrace();
            }

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String cabecera = sharedpreferences.getString("cabeceraKey", null) + "\n";
            final String terminal = sharedpreferences.getString("terminalKey", null);
            final String secret = sharedpreferences.getString("secretKey", null);
            final String server = sharedpreferences.getString("serverKey", null);
            sharedpreferences2 = getSharedPreferences(MySUPPLY, Context.MODE_PRIVATE);

            final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            final String hora = new SimpleDateFormat("HH:mm").format(new Date());

            RequestQueue queue2 = Volley.newRequestQueue(this);
            String url = server + "/cancela.php";
            StringRequest postRequest2 = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Response", response);
                            try {
                                InputStream is = convertStringToDocument(response);
                                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                                Document doc = dBuilder.parse(is);
                                Element element = doc.getDocumentElement();
                                element.normalize();
                                NodeList nList = doc.getElementsByTagName("smsdieselapi");
                                for (int i = 0; i < nList.getLength(); i++) {
                                    Node node = nList.item(i);
                                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                                        Element element2 = (Element) node;
                                        String exito = getValue("success", element2);
                                        if (exito.equals("1")) {
                                            Toast.makeText(getBaseContext(), "TRANSACTION SUCCESSFULLY CANCELLED", Toast.LENGTH_SHORT).show();
                                            try {
                                                msg = "TRANSACTION SUCCESSFULLY\n";
                                                msg += "CANCELLED";
                                                msg += "\n";
                                                for (int g2 = 0; g2 < 2; g2++) {
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
                                                    //FEED 3 LINEAS
                                                    mmOutputStream.write(0x1B);
                                                    mmOutputStream.write(0x64);
                                                    mmOutputStream.write(0x03);
                                                    //DOUBLE HEIGHT ON
                                                    mmOutputStream.write(0x1B);
                                                    mmOutputStream.write(0x21);
                                                    mmOutputStream.write(0x10);
                                                    mmOutputStream.write(saltolinea.getBytes());
                                                    mmOutputStream.write(saltolinea.getBytes());
                                                    mmOutputStream.write(msg.getBytes());
                                                    //DOUBLE HEIGHT OFF
                                                    mmOutputStream.write(0x1B);
                                                    mmOutputStream.write(0x21);
                                                    mmOutputStream.write(0x00);
                                                    mmOutputStream.write(saltolinea.getBytes());
                                                    //ALINEAR IZQUIERDA
                                                    mmOutputStream.write(0x1B);
                                                    mmOutputStream.write(0x61);
                                                    mmOutputStream.write(0x00);
                                                    String textopin = "Transaction Code: " + codigo + "\n\n\n\n";
                                                    mmOutputStream.write(textopin.getBytes());
                                                    //FEED 3 LINEAS
                                                    mmOutputStream.write(0x1B);
                                                    mmOutputStream.write(0x64);
                                                    mmOutputStream.write(0x03);
                                                    if (g2 == 0) {
                                                        Thread.sleep(4000);
                                                    }
                                                }
                                            } catch (NullPointerException e) {
                                                e.printStackTrace();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Toast.makeText(getBaseContext(), "TRANSACTION REFUSED", Toast.LENGTH_SHORT).show();
                                            codigoerror = getValue("error_code", element2) + "\n";
                                            textoerror = getValue("error_description", element2) + "\n\n\n\n";
                                            msg = "TRANSACTION REFUSED";
                                            msg += "\n";
                                            for (int g = 0; g < 2; g++) {
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
                                                mmOutputStream.write(hora.getBytes());
                                                //FEED 3 LINEAS
                                                mmOutputStream.write(0x1B);
                                                mmOutputStream.write(0x64);
                                                mmOutputStream.write(0x03);
                                                //DOUBLE HEIGHT ON
                                                mmOutputStream.write(0x1B);
                                                mmOutputStream.write(0x21);
                                                mmOutputStream.write(0x10);
                                                mmOutputStream.write(saltolinea.getBytes());
                                                mmOutputStream.write(msg.getBytes());
                                                //DOUBLE HEIGHT OFF
                                                mmOutputStream.write(0x1B);
                                                mmOutputStream.write(0x21);
                                                mmOutputStream.write(0x00);
                                                mmOutputStream.write(saltolinea.getBytes());
                                                mmOutputStream.write(codigoerror.getBytes());
                                                mmOutputStream.write(textoerror.getBytes());
                                                //FEED 3 LINEAS
                                                mmOutputStream.write(0x1B);
                                                mmOutputStream.write(0x64);
                                                mmOutputStream.write(0x03);
                                                if (g == 0) {
                                                    Thread.sleep(4000);
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            salvaroperacion("Cancellation", cabecera, terminal, fecha, hora, msg, codigo, textoproducto, "0", "0", litros, "0", codigoerror, textoerror);
                            final Thread t = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        contadortimeout.cancel();
                                        sleep(1000);
                                        progress.dismiss();
                                        PinPadActivity.this.finish();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            t.start();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("secret", secret);
                    params.put("terminal", terminal);
                    params.put("codigo", codigo);
                    params.put("fecha", fecha);
                    params.put("hora", hora);
                    return params;
                }
            };
            queue2.add(postRequest2);
        }
    }

    void pedirlitros(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.titulo_litros));
        builder.setMessage(this.getString(R.string.entrada_litros));
        builder.setCancelable(false);
        builder.setIcon(R.drawable.pump_b);
        final EditText input = new EditText(this);
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finalizaT(input.getText().toString());
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

    void finalizaT(final String litros){
        if (isNetworkAvailable() == false){
            mensajered();
        }else {
            counter = 1;
            contadortimeout = new ContadorTimeOut(10000, 1000);
            contadortimeout.start();
            progress = new ProgressDialog(this);
            progress.setMessage(this.getString(R.string.spinner_conectando));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.setProgress(0);
            progress.show();

            try {
                openBT();
            } catch (IOException e) {
                e.printStackTrace();
            }

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String cabecera = sharedpreferences.getString("cabeceraKey", null) + "\n";
            final String terminal = sharedpreferences.getString("terminalKey", null);
            final String secret = sharedpreferences.getString("secretKey", null);
            final String server = sharedpreferences.getString("serverKey", null);

            sharedpreferences2 = getSharedPreferences(MySUPPLY, Context.MODE_PRIVATE);
            final String producto = sharedpreferences2.getString("productoKey", null);

            sharedpreferences3 = getSharedPreferences(MyPRECIOS, Context.MODE_PRIVATE);

            final String matricula = " ";
            final String importe = "0";
            final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            final String hora = new SimpleDateFormat("HH:mm").format(new Date());
            final String nticket = "0";

            RequestQueue queue2 = Volley.newRequestQueue(this);
            String url = server + "/cierra.php";
            StringRequest postRequest2 = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Litros", litros);
                            Log.d("Response", response);
                            salvarrespuesta(response);
                            try {
                                InputStream is = convertStringToDocument(response);
                                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                                Document doc = dBuilder.parse(is);
                                Element element = doc.getDocumentElement();
                                element.normalize();
                                NodeList nList = doc.getElementsByTagName("smsdieselapi");
                                for (int i = 0; i < nList.getLength(); i++) {
                                    Node node = nList.item(i);
                                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                                        Element element2 = (Element) node;
                                        String exito = getValue("success", element2);
                                        if (exito.equals("1")) {
                                            Toast.makeText(getBaseContext(), "TRANSACTION SUCCESSFULLY COMPLETED", Toast.LENGTH_SHORT).show();
                                            String operation = getValue("transaction_id", element2);
                                            String litros = getValue("liters", element2);
                                            String producto = getValue("product_code", element2);
                                            String price = getValue("price", element2);
                                            String textoproducto = "";
                                            switch (producto) {
                                                case "DIZ":
                                                    textoproducto = "DIESEL      ";
                                                    price = sharedpreferences3.getString("dieselKey", "0.00");
                                                    break;
                                                case "ADB":
                                                    textoproducto = "AD BLUE     ";
                                                    price = sharedpreferences3.getString("adblueKey", "0.00");
                                                    break;
                                                case "FOD":
                                                    textoproducto = "RED DIESEL  ";
                                                    price = sharedpreferences3.getString("reddieselKey", "0.00");
                                            }
                                            double total = Float.valueOf(price) * Float.valueOf(litros);
                                            BigDecimal a = new BigDecimal(total);
                                            BigDecimal total2 = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                            try {
                                                msg = "TRANSACTION SUCCESSFULLY\n";
                                                msg += "COMPLETED";
                                                msg += "\n";
                                                for (int g2 = 0; g2 < 2; g2++) {
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
                                                    mmOutputStream.write(saltolinea.getBytes());
                                                    mmOutputStream.write(msg.getBytes());
                                                    //DOUBLE HEIGHT OFF
                                                    mmOutputStream.write(0x1B);
                                                    mmOutputStream.write(0x21);
                                                    mmOutputStream.write(0x00);
                                                    mmOutputStream.write(saltolinea.getBytes());
                                                    //ALINEAR IZQUIERDA
                                                    mmOutputStream.write(0x1B);
                                                    mmOutputStream.write(0x61);
                                                    mmOutputStream.write(0x00);
                                                    String textopin = "Transaction Code: " + codigo + "\n";
                                                    mmOutputStream.write(textopin.getBytes());
                                                    textoop = "Operation Code: " + operation + "\n\n";
                                                    mmOutputStream.write(textoop.getBytes());
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
                                                    detalle = textoproducto + litros + "      " + total2.toString() + "\n\n\n\n";
                                                    mmOutputStream.write(detalle.getBytes());
                                                    //FEED 3 LINEAS
                                                    mmOutputStream.write(0x1B);
                                                    mmOutputStream.write(0x64);
                                                    mmOutputStream.write(0x03);
                                                    if (g2 == 0) {
                                                        Thread.sleep(4000);
                                                    }
                                                }
                                            } catch (NullPointerException e) {
                                                e.printStackTrace();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Toast.makeText(getBaseContext(), "TRANSACTION REFUSED", Toast.LENGTH_SHORT).show();
                                            codigoerror = getValue("error_code", element2) + "\n";
                                            textoerror = getValue("error_description", element2) + "\n\n\n\n";
                                            msg = "TRANSACTION REFUSED";
                                            msg += "\n";
                                            for (int g = 0; g < 2; g++) {
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
                                                mmOutputStream.write(msg.getBytes());
                                                //DOUBLE HEIGHT OFF
                                                mmOutputStream.write(0x1B);
                                                mmOutputStream.write(0x21);
                                                mmOutputStream.write(0x00);
                                                mmOutputStream.write(saltolinea.getBytes());
                                                mmOutputStream.write(codigoerror.getBytes());
                                                mmOutputStream.write(textoerror.getBytes());
                                                //FEED 3 LINEAS
                                                mmOutputStream.write(0x1B);
                                                mmOutputStream.write(0x64);
                                                mmOutputStream.write(0x03);
                                                if (g == 0) {
                                                    Thread.sleep(4000);
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            salvaroperacion("Finish", cabecera, terminal, fecha, hora, msg, codigo, detalle, textoop, "0", litros, "0", codigoerror, textoerror);
                            final Thread t = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        contadortimeout.cancel();
                                        sleep(1000);
                                        progress.dismiss();
                                        PinPadActivity.this.finish();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            t.start();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("secret", secret);
                    params.put("terminal", terminal);
                    params.put("producto", respuesta);
                    params.put("codigo", codigo);
                    params.put("litros", litros);
                    params.put("matricula", matricula);
                    params.put("importe", importe);
                    params.put("fecha", fecha);
                    params.put("hora", hora);
                    params.put("nticket", nticket);
                    return params;
                }
            };
            queue2.add(postRequest2);
        }
    }

    void salvaroperacion(String tipoS, String cabeceraS, String terminalS, String fechaS, String horaS, String resultadoS, String codigoS, String txtproductoS, String operacionS, String litros_aceptadosS, String litrosS, String totalS, String codigo_errorS, String errorS){
        if(resultadoS == null){
            resultadoS = " ";
        }
        if(operacionS == null){
            operacionS = "0";
        }
        if(txtproductoS==null){
            txtproductoS = " ";
        }
        if(litrosS==null){
            litrosS = "0";
        }

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "datos", null, 2);

        SQLiteDatabase bd = admin.getWritableDatabase();


        ContentValues registro = new ContentValues();

        registro.put("tipo", tipoS);
        registro.put("cabecera", cabeceraS);
        registro.put("terminal", terminalS);
        registro.put("fecha", fechaS);
        registro.put("hora", horaS);
        registro.put("resultado", resultadoS);
        registro.put("codigo", codigoS);
        registro.put("producto", txtproductoS);
        registro.put("operacion", operacionS);
        registro.put("litros_aceptados", litros_aceptadosS);
        registro.put("litros", litrosS);
        registro.put("total", totalS);
        registro.put("codigo_error", codigo_errorS);
        registro.put("error", errorS);

        // los inserto en la base de datos
        bd.insert("operaciones", null, registro);

        bd.close();

    }

    void salvarrespuesta(String respuestaS){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "datos", null, 2);

        SQLiteDatabase bd = admin.getWritableDatabase();


        ContentValues registro = new ContentValues();

        registro.put("respuesta", respuestaS);


        // los inserto en la base de datos
        bd.insert("respuestas", null, registro);

        bd.close();

    }

    void dialogoQR(String productorespuesta){
        LayoutInflater inflater = getLayoutInflater();
        View dialogoLayout = inflater.inflate(R.layout.dialogo_cuantos_qr, null);
        TextView lbl_producto = (TextView) dialogoLayout.findViewById(R.id.labelcuantos);


        if(productorespuesta.equals("1")){
            lbl_producto.setText(R.string.dialogo_qr_diesel);
        }
        if(productorespuesta.equals("13")){
            lbl_producto.setText(R.string.dialogo_qr_adblue);
        }
        if(productorespuesta.equals("15")){
            lbl_producto.setText(R.string.dialogo_qr_diesel_rojo);
        }

        builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);

        builder.setView(dialogoLayout);

        final AlertDialog show = builder.show();

        Button alertButton1 = (Button) dialogoLayout.findViewById(R.id.QR1);
        Button alertButton2 = (Button) dialogoLayout.findViewById(R.id.QR2);
        Button alertButton3 = (Button) dialogoLayout.findViewById(R.id.QR3);
        Button alertButton4 = (Button) dialogoLayout.findViewById(R.id.QR4);

        alertButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cuantosQR = 1;
                show.dismiss();
                scanQR();
            }
        });

        alertButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cuantosQR = 2;
                show.dismiss();
                scanQR();
            }
        });

        alertButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cuantosQR = 3;
                show.dismiss();
                scanQR();
            }
        });

        alertButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cuantosQR = 4;
                show.dismiss();
                scanQR();
            }
        });
    }

    public void numerodeQRs(View v){
        switch(v.getId()) {
            case R.id.QR1:
                cuantosQR = 1;
                break;
            case R.id.QR2:
                cuantosQR = 2;
                break;
            case R.id.QR3:
                cuantosQR = 3;
                break;
            case R.id.QR4:
                cuantosQR = 4;
                break;
        }
        //scanQR();
    }

    void scanQR(){

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);

        integrator.setPrompt(this.getString(R.string.texto_qr));
        integrator.setResultDisplayDuration(0);
        integrator.autoWide();
        integrator.setOrientation(90);
        integrator.setCameraId(0);
        integrator.setCaptureLayout(R.layout.activity_scan);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            if(scanningResult.getContents() == null) {
                //El usuario ha pulaado el boton cancelar en el Scanner.
                //Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show();
            } else {
                String scanContent = scanningResult.getContents();
                String campos[] = scanContent.split("\\|");
                tipo = campos[0];
                litrosT = campos[1];
                litrosT = litrosT.replace(",",".").trim();
                litrostmp += Float.valueOf(litrosT);
                switch (cuantosQR) {
                    case 1:
                        if (CheckTipoRepostaje()) {
                            finalizaT(litrosT);
                        } else {
                            mensajeQRNovalido();
                        }
                        break;
                    case 2:
                        if (QRArray.contains(scanContent)) {
                            mensajeQRRepetido();
                        } else {
                            if (CheckTipoRepostaje()) {
                                if (counter < cuantosQR) {
                                    counter++;
                                    QRArray.add(scanContent);
                                    scanQR();
                                } else {
                                    litrosT = String.valueOf(litrostmp);
                                    finalizaT(litrosT);
                                }
                            } else {
                                mensajeQRNovalido();
                            }
                        }
                        break;
                    case 3:
                        if (QRArray.contains(scanContent)) {
                            mensajeQRRepetido();
                        } else {
                            if (CheckTipoRepostaje()) {
                                if (counter < cuantosQR) {
                                    counter++;
                                    QRArray.add(scanContent);
                                    scanQR();
                                } else {
                                    litrosT = String.valueOf(litrostmp);
                                    finalizaT(litrosT);
                                }
                            } else {
                                mensajeQRNovalido();
                            }
                        }
                        break;
                    case 4:
                        if (QRArray.contains(scanContent)) {
                            mensajeQRRepetido();
                        } else {
                            if (CheckTipoRepostaje()) {
                                if (counter < cuantosQR) {
                                    counter++;
                                    QRArray.add(scanContent);
                                    scanQR();
                                } else {
                                    litrosT = String.valueOf(litrostmp);
                                    finalizaT(litrosT);
                                }
                            } else {
                                mensajeQRNovalido();
                            }
                        }
                        break;
                }
            }
        }else{
            Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    void mensajeQRNovalido(){
        litrostmp -= Float.valueOf(litrosT);
        AlertDialog.Builder alert =new AlertDialog.Builder(this);
        alert.setTitle(R.string.titulo_error_producto);
        alert.setCancelable(false);
        alert.setMessage(R.string.error_tipo_producto);
        alert.setIcon(R.drawable.warning);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                    scanQR();
            }
        });
        alert.show();
    }

    void mensajeQRRepetido(){
        litrostmp -= Float.valueOf(litrosT);
        AlertDialog.Builder alert =new AlertDialog.Builder(this);
        alert.setTitle(R.string.titulo_error_producto);
        alert.setCancelable(false);
        alert.setMessage(R.string.mensaje_qr_repetido);
        alert.setIcon(R.drawable.warning);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                scanQR();
            }
        });
        alert.show();
    }

    void mensajered(){
        AlertDialog.Builder alert =new AlertDialog.Builder(this);
        alert.setTitle(R.string.titulo_error_nored);
        alert.setCancelable(false);
        alert.setMessage(R.string.error_nored);
        alert.setIcon(R.drawable.warning);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

    void mensajetimeout(){
        AlertDialog.Builder alert =new AlertDialog.Builder(this);
        alert.setTitle(R.string.titulo_error_timeout);
        alert.setCancelable(false);
        alert.setMessage(R.string.error_timeout);
        alert.setIcon(R.drawable.warning);

        alert.setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                contadortimeout.cancel();
                contadortimeout.start();
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                progress.dismiss();
            }
        });

        alert.setNeutralButton(R.string.btn_contacto, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {
                progress.dismiss();
                mensajecontacto();
            }
        });
        alert.show();
    }

    void mensajecontacto(){
        AlertDialog.Builder alert =new AlertDialog.Builder(this);
        alert.setTitle(R.string.titulo_contacto);
        alert.setCancelable(false);
        alert.setMessage(R.string.text_contacto);
        alert.setIcon(R.drawable.logo);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class ContadorTimeOut extends CountDownTimer {

        public ContadorTimeOut(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            mensajetimeout();
        }
    }


    public boolean CheckTipoRepostaje(){
        //Vilamalla: Diesel - Ad Blue
        //Gexa: GAS/A - AdBlue
        //Arraia: GAS/A - ADBLUE
        //Tiebas: GASOLEO A  - ADBLUE

        switch (tipo){
            case "GAS/A":
                //Es Diesel de ALVIC
                return respuesta.equals("1");
            case "AdBlue":
                //Es Ad Blue de ALVIC
                return respuesta.equals("13");
            case "GASOLEO A ":
                //Es Diesel de EFIDATA
                if(respuesta.equals("1")){
                    EsEfiData = true;
                    return true;
                    //litrosT = litrosT.replace(",",".");
                    //finalizaT(litrosT);
                }else {
                    return false;
                }
            case "ADBLUE ":
                //Es Ad Blue de EFIDATA
                if(respuesta.equals("13")){
                    EsEfiData = true;
                    //litrosT = litrosT.replace(",",".");
                    //finalizaT(litrosT);
                    return true;
                }else {
                    return false;
                }
            default:
                return false;
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


    void reservaN(final String prod){
        if (isNetworkAvailable() == false){
            mensajered();
        }else {

        }
    }
}

