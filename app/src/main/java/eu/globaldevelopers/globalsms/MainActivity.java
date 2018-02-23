package eu.globaldevelopers.globalsms;


import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.content.*;
import android.content.res.Configuration;
import android.widget.DatePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MainActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MySupply" ;
    public static final String MyCONFIG = "MyPrefs" ;
    public static final String tipotrans = "tipoKey";
    public static String lang="";
    public static final String MyPREFERENCES2 = "MyPrefs" ;

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

    SharedPreferences sharedpreferences;

    private DatePickerDialog dailyDatePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            findBT();
            openBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sharedpreferences = getSharedPreferences(MyCONFIG, Context.MODE_PRIVATE);
        int locactual = sharedpreferences.getInt("locKey", 0);

        switch(locactual){
            case 0:
                lang = "en";
                break;
            case 1:
                lang = "de";
                break;
            case 2:
                lang = "es";
                break;
            default:
                lang = "es";
                break;
        }
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
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
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onResume() {
        super.onResume();

        sharedpreferences = getSharedPreferences(MyCONFIG, Context.MODE_PRIVATE);
        int locactual = sharedpreferences.getInt("locKey", 0);

        switch(locactual){
            case 0:
                lang = "en";
                break;
            case 1:
                lang = "de";
                break;
            case 2:
                lang = "es";
                break;
            default:
                lang = "es";
                break;
        }
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

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

    public void ConfigFunction(View view) {
        Intent Intent = new Intent(this, LoginActivity.class);
        startActivity(Intent);
    }

    public void NuevoFunction(View view) {
        Intent Intent = new Intent(this, ProductoActivity.class);
        startActivity(Intent);
    }

    public void FinalizarFunction(View view){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(tipotrans, "Cierre");
        editor.apply();

        Intent Intent = new Intent(this, PinPadActivity.class);
        startActivity(Intent);
    }

    public void CancelarFunction(View view){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(tipotrans, "Cancela");
        editor.apply();

        Intent Intent = new Intent(this, PinPadActivity.class);
        startActivity(Intent);
    }

    public void PreciosFunction(View view){
        Intent Intent = new Intent(this, PreciosActivity.class);
        startActivity(Intent);

    }

    public void CopiaFunction(View view){
        Intent Intent = new Intent(this, CopiaActivity.class);
        startActivity(Intent);

    }

    public void CierreFunction(View view){
        AlertDialog.Builder alert =new AlertDialog.Builder(this);
        alert.setTitle(R.string.titulo_cierre_turno);
        alert.setCancelable(false);
        alert.setMessage(R.string.alert_cierre_turno);
        alert.setIcon(R.drawable.attention);

        alert.setPositiveButton(R.string.btn_cierre_aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.setNegativeButton(R.string.btn_cierre_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }

    public void ReportDayFunction(View view){
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar newCalendar = Calendar.getInstance();
        dailyDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                //Toast.makeText(getBaseContext(),dateFormatter.format(newDate.getTime()),Toast.LENGTH_SHORT).show();
                PrintDailyReport(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        dailyDatePickerDialog.show();
    }

    public void PrintDailyReport(final String dateF){
        sharedpreferences = getSharedPreferences(MyPREFERENCES2, Context.MODE_PRIVATE);
        final String cabecera = sharedpreferences.getString("cabeceraKey", null) + "\n";
        final String server = sharedpreferences.getString("serverKey", null);
        final String terminal = sharedpreferences.getString("terminalKey", null);
        RequestQueue queue = Volley.newRequestQueue(this);  // this = context
        String url = server + "/daily_report.php";
        //Toast.makeText(getBaseContext(), dateF, Toast.LENGTH_SHORT).show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String datareceived) {
                        // response
                        Log.d("Response", datareceived);
                        if (datareceived.equals("nohaydatos")) {
                            //No esta reservado, vuelvo
                            Toast.makeText(getBaseContext(), R.string.error_no_reservado, Toast.LENGTH_SHORT).show();


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

                                int new_report = 0;
                                float total_liters = 0;
                                String p_id_act = null;
                                String product_txt = null;
                                // Get all jsonObject from jsonArray
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    String date_str = null, product_id = null, code = null, liters = null, trans_code = null;

                                    //Date
                                    if (jsonObject.has("fecha_hora") && !jsonObject.isNull("fecha_hora")) {
                                        date_str = jsonObject.getString("fecha_hora");
                                        if(new_report == 0){
                                            String[] f_separated = date_str.split(" ");
                                            String[] datearray = f_separated[0].split("-");
                                            String dia = datearray[2];
                                            String mes = datearray[1];
                                            String anho = datearray[0];
                                            String date = "TRANSACTIONS ON " + dia + "/" + mes + "/" + anho;
                                            mmOutputStream.write(date.getBytes());
                                            new_report = 1;
                                        }
                                    }

                                    // product_id
                                    if (jsonObject.has("producto") && !jsonObject.isNull("producto")) {
                                        product_id = jsonObject.getString("producto");
                                        if(!product_id.equals(p_id_act)){
                                            switch (product_id){
                                                case "1":
                                                    product_txt = "\n\nDIESEL\n\nCode   Liters  Transaction\n\n";
                                                    break;
                                                case "13":
                                                    product_txt = "\n TOTAL DIESEL: "+ total_liters + "\n\nAD BLUE\n\nCode   Liters  Transaction\n\n";
                                                    total_liters = 0;
                                                    break;
                                            }
                                            //ALINEAR IZQUIERDA
                                            mmOutputStream.write(0x1B);
                                            mmOutputStream.write(0x61);
                                            mmOutputStream.write(0x00);
                                            mmOutputStream.write(product_txt.getBytes());
                                        }


                                    }

                                    // code
                                    if (jsonObject.has("codigo") && !jsonObject.isNull("codigo")) {
                                        code = jsonObject.getString("codigo");
                                    }

                                    // liters
                                    if (jsonObject.has("litros_reales") && !jsonObject.isNull("litros_reales")) {
                                        liters = jsonObject.getString("litros_reales");
                                        total_liters = total_liters + Float.parseFloat(liters);
                                    }

                                    // trans_code
                                    if (jsonObject.has("num_operacion") && !jsonObject.isNull("num_operacion")) {
                                        trans_code = jsonObject.getString("num_operacion");
                                    }

                                    String detalle = code + "   " + liters + "  " + trans_code + "\n";
                                    mmOutputStream.write(detalle.getBytes());

                                    //Log.d("SUCCESS", "JSON Object: " + "\nProduct: " + product_id + "\nCode: " + code+ "\nLiters: " + liters + "\nTransaction: " + trans_code);
                                    p_id_act = product_id;
                                }
                                //TOTAL ADBLUE
                                String total_adb = "\n TOTAL ADBLUE: " + total_liters + "\n\n";
                                mmOutputStream.write(total_adb.getBytes());
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
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("terminal", terminal);
                params.put("fecha", dateF);
                return params;
            }
        };
        queue.add(postRequest);
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

    void findBT() throws IOException {

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

}

