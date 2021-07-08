package eu.globaldevelopers.globalsms;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import eu.globaldevelopers.globalsms.Class.Transaction;
import eu.globaldevelopers.globalsms.Enums.ConfigEnum;
import eu.globaldevelopers.globalsms.Helpers.LocaleHelper;
import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class MainActivity extends AppCompatActivity {

    private int currentApiVersion;

    TextView bagde;

    public static final String MyPREFERENCES = "MySupply";
    public static final String MyCONFIG = ConfigEnum.MyPREFERENCES;
    public static final String tipotrans = "tipoKey";
    public static String lang = "";
    public static final String MyPREFERENCES2 = MyCONFIG;
    public static final String langKey = ConfigEnum.langKey;
    public static final String loc = ConfigEnum.loc;

    private static final String TAG = "PrinterTestDemo";

    private IWoyouService woyouService;

    private ICallback callback = null;

    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);
        }
    };

    Bitmap mBitmap;

    ProgressDialog progress;

    SharedPreferences sharedpreferences;

    private DatePickerDialog dailyDatePickerDialog;

    public static String ApiCampilloURI, ApiGPayUrl;

    //TOTALES CIERRE TURNO
    private Transaction transactionGpWs, transactionInWs;
    private boolean pendingTransactions = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        sharedpreferences = getSharedPreferences(MyCONFIG, Context.MODE_PRIVATE);

        ApiCampilloURI = sharedpreferences.getString(ConfigEnum.apiCampilloUrl, BuildConfig.EP_URL_API_BASE_CAMPILLO);
        ApiGPayUrl = sharedpreferences.getString(ConfigEnum.apiGenericUrl, BuildConfig.EP_URL_API_BASE_GLOBALPAY);

        //SET LANG
        String lang = sharedpreferences.getString(ConfigEnum.langKey, "en");
        if(lang == null){
            Integer locN = sharedpreferences.getInt(ConfigEnum.loc, 0);
            switch (locN) {
                case 0:
                    lang = "en";
                    break;
                case 1:
                    lang = "de";
                    break;
                case 2:
                    lang = "es";
                    break;
                case 3:
                    lang = "it";
                    break;
                default:
                    lang = "es";
                    break;
            }

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(ConfigEnum.langKey, "en");
            editor.apply();
        }
        LocaleHelper.setAppLocale(lang, this);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            //for higher api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }

        CheckWorkShift();
        innitView();

        reservesCounter();

        int MY_PERMISSIONS_REQUEST_CAMERA = 0;
        // Here, this is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }

        int MY_PERMISSIONS_REQUEST_WRITE = 0;
        // Here, this is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE);
            }
        }


        int MY_PERMISSIONS_REQUEST_READ = 0;
        // Here, this is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ);
            }
        }
    }

    private SimpleDateFormat dateFormatter;

    @Override
    protected void onResume() {
        super.onResume();

        sharedpreferences = getSharedPreferences(MyCONFIG, Context.MODE_PRIVATE);
        String lang = sharedpreferences.getString(ConfigEnum.langKey, "en");

        LocaleHelper.setAppLocale(lang, this);

        if (Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            //for higher api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }

        reservesCounter();
    }

    public void ConfigFunction(View view) {
        Intent Intent = new Intent(this, LoginActivity.class);
        startActivity(Intent);
        finish();
    }

    public void NuevoFunction(View view) {
        Intent Intent = new Intent(this, ProductoActivity.class);
        startActivity(Intent);
    }

    public void NuevoV2Function(View view) {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(tipotrans, "Nueva");
        editor.apply();

        Intent Intent = new Intent(this, PinPadActivity.class);
        startActivity(Intent);
        //finish();
    }

    public void FinalizarFunction(View view) {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(tipotrans, "Cierre");
        editor.apply();

        Intent Intent = new Intent(this, PinPadActivity.class);
        startActivity(Intent);
    }

    public void CancelarFunction(View view) {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(tipotrans, "Cancela");
        editor.apply();

        Intent Intent = new Intent(this, PinPadActivity.class);
        startActivity(Intent);
        finish();
    }

    public void PreciosFunction(View view) {
        Intent Intent = new Intent(this, PreciosActivity.class);
        startActivity(Intent);
        finish();

    }

    public void CopiaFunction(View view) {
        Intent Intent = new Intent(this, activity_copia_new.class);
        startActivity(Intent);
        //finish();
    }

    public void CierreFunction(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.titulo_cierre_turno);
        alert.setCancelable(false);
        alert.setMessage(R.string.alert_cierre_turno);
        alert.setIcon(R.drawable.attention);

        //LOADER
        progress = new ProgressDialog(this);
        progress.setMessage(this.getString(R.string.closing_workshift));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(true);
        progress.setProgress(0);

        alert.setPositiveButton(R.string.btn_cierre_aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //LOADER
                progress.show();
                transactionGpWs = new Transaction();
                transactionInWs = new Transaction();
                summaryWorkShift();

            }
        });

        alert.setNegativeButton(R.string.btn_cierre_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

    public void ReportDayFunction(View view) {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar newCalendar = Calendar.getInstance();
        dailyDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                PrintDailyReport(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        dailyDatePickerDialog.show();
    }

    public void PrintDailyReport(final String dateF) {
        progress = new ProgressDialog(this);
        progress.setMessage(this.getString(R.string.spinner_conectando));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.setProgress(0);
        progress.show();

        sharedpreferences = getSharedPreferences(MyPREFERENCES2, Context.MODE_PRIVATE);
        final String cabecera = sharedpreferences.getString(ConfigEnum.ticketHeader, null) + "\n";
        final String server = sharedpreferences.getString(ConfigEnum.serverUrlSMS, BuildConfig.EP_URL_API_BASE_SMS);
        final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
        RequestQueue queue = Volley.newRequestQueue(this);  // this = context
        String url = server + "/daily_report.php";
        ////Toast.makeText(getBaseContext(), dateF, //Toast.LENGTH_SHORT).show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String DataReceived) {
                        // response
                        Log.d("Response", DataReceived);
                        if (DataReceived.equals("nohaydatos")) {
                            //No esta reservado, vuelvo
                            //Toast.makeText(getBaseContext(), R.string.error_no_data, //Toast.LENGTH_SHORT).show();


                        } else {
                            final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                            final String hora = new SimpleDateFormat("HH:mm").format(new Date()) + "\n\n";
                            try {
                                ThreadPoolManager.getInstance().executeTask(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (mBitmap == null) {
                                            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                        }
                                        try {
                                            woyouService.lineWrap(2, callback);
                                            woyouService.setAlignment(1, callback);
                                            woyouService.printBitmap(mBitmap, callback);
                                            woyouService.setFontSize(24, callback);
                                            woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                            String pterminal = "Terminal: " + terminal + "\n\n";
                                            woyouService.printTextWithFont(pterminal, "", 24, callback);
                                            woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                            woyouService.lineWrap(1, callback);
                                            woyouService.setAlignment(0, callback);

                                            JSONArray jsonArray = new JSONArray(DataReceived);

                                            int new_report = 0;
                                            float total_liters = 0;
                                            String p_id_act = null;
                                            String product_txt = null;
                                            // Get all jsonObject from jsonArray
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                                String date_str = null, product_id = null, code = null, liters = null, trans_code = null;

                                                //Date
                                                if (jsonObject.has("fecha_hora") && !jsonObject.isNull("fecha_hora")) {
                                                    date_str = jsonObject.getString("fecha_hora");
                                                    if (new_report == 0) {
                                                        String[] f_separated = date_str.split(" ");
                                                        String[] datearray = f_separated[0].split("-");
                                                        String dia = datearray[2];
                                                        String mes = datearray[1];
                                                        String anho = datearray[0];
                                                        String date = "TRANSACTIONS ON " + dia + "/" + mes + "/" + anho;
                                                        woyouService.printTextWithFont(date, "", 28, callback);
                                                        new_report = 1;
                                                    }
                                                }
                                                //ALINEAR IZQUIERDA
                                                woyouService.setAlignment(0, callback);
                                                // product_id
                                                if (jsonObject.has("producto") && !jsonObject.isNull("producto")) {
                                                    product_id = jsonObject.getString("producto");
                                                    if (!product_id.equals(p_id_act)) {
                                                        switch (product_id) {
                                                            case "1":
                                                                product_txt = "\n\nDIESEL\n\n";
                                                                break;
                                                            case "13":
                                                                if (total_liters != 0) {
                                                                    String total_diesel = "\n TOTAL DIESEL: " + String.format("%.2f", total_liters) + "\n\n";
                                                                    woyouService.printTextWithFont(total_diesel, "", 24, callback);
                                                                }
                                                                total_liters = 0;
                                                                product_txt = "\n\nAD BLUE\n\n";
                                                                break;
                                                        }

                                                        woyouService.printTextWithFont(product_txt, "", 24, callback);

                                                        String[] text = new String[3];
                                                        int[] width = new int[]{9, 8, 11};

                                                        text[0] = "Code";
                                                        text[1] = "Liters";
                                                        text[2] = "Transaction";
                                                        woyouService.printColumnsText(text, width, new int[]{0, 2, 2}, callback);
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

                                                String[] text2 = new String[3];
                                                int[] width2 = new int[]{9, 8, 11};
                                                int[] align = new int[]{0, 2, 2};
                                                text2[0] = code;
                                                text2[1] = liters;
                                                text2[2] = trans_code;
                                                woyouService.printColumnsText(text2, width2, align, callback);

                                                p_id_act = product_id;
                                            }
                                            //TOTAL ADBLUE
                                            if (total_liters != 0) {
                                                String total_adb = "\n TOTAL ADBLUE: " + String.format("%.2f", total_liters) + "\n\n";
                                                woyouService.printTextWithFont(total_adb, "", 24, callback);
                                            }
                                            woyouService.lineWrap(2, callback);
                                        } catch (RemoteException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

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

        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    progress.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    public void CheckFunction(View view) {
        Intent Intent = new Intent(this, CheckActivity.class);
        startActivity(Intent);

    }

    public void CheckWorkShift() {
        //FUNCION PARA MIRAR SI EXITE UN TURNO ABIERTO SI NO EXISTE LO ABRIMOS.
        sharedpreferences = getSharedPreferences(MyPREFERENCES2, Context.MODE_PRIVATE);
        final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
        RequestQueue queue = Volley.newRequestQueue(this);  // this = context
        String url = ApiGPayUrl + BuildConfig.EP_TERMINALS_CHECK_WORKSHIFT + "?terminal=" + terminal;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String DataReceived) {
                        try {
                            JSONObject jsonObject = new JSONObject(DataReceived);
                            boolean dataSuccess = jsonObject.getBoolean("success");

                            if (dataSuccess) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(ConfigEnum.workShiftKey, data.getString("work_shift_id"));
                                editor.putString(ConfigEnum.workShiftOpenDateKey, data.getString("date_open"));
                                editor.apply();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        //Toast.makeText(getBaseContext(), error.toString(), //Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", BuildConfig.GLOBALPAY_TOKEN);
                return headers;
            }
        };

        int socketTimeout = 30000;
        int maxRetry = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, maxRetry, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    public void summaryWorkShift() {
        sharedpreferences = getSharedPreferences(MyPREFERENCES2, Context.MODE_PRIVATE);
        final String cabecera = sharedpreferences.getString(ConfigEnum.ticketHeader, null) + "\n";
        final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
        final String server = sharedpreferences.getString(ConfigEnum.serverUrlSMS, BuildConfig.EP_URL_API_BASE_SMS);
        final String turno = sharedpreferences.getString(ConfigEnum.workShiftKey, null);

        RequestQueue queue = Volley.newRequestQueue(this);  // this = context
        String url = ApiGPayUrl + BuildConfig.EP_TERMINALS_SUMMARY_WORKSHIFT + "?terminal=" + terminal + "&turno=" + turno;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String DataReceived) {
                        try {
                            JSONObject jsonObject = new JSONObject(DataReceived);
                            boolean dataSuccess = jsonObject.getBoolean("success");
                            if (!dataSuccess) {
                                //Hay operaciones reservadas no se puede cerrar el turno.
                                //Toast.makeText(getBaseContext(), R.string.error_hay_reservas, //Toast.LENGTH_SHORT).show();
                            } else {

                                jsonObject = jsonObject.getJSONObject("data");
                                final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                                final String hora = new SimpleDateFormat("HH:mm").format(new Date()) + "\n\n";

                                try {
                                    //INSTANTIC
                                    if(jsonObject.has("instantic") && !jsonObject.isNull("instantic")) {
                                        JSONArray instanticSummary = jsonObject.getJSONArray("instantic");

                                        // Get all jsonObject from jsonArray
                                        for (int i = 0; i < instanticSummary.length(); i++) {
                                            JSONObject objSummary = instanticSummary.getJSONObject(i);
                                            // producto
                                            if (objSummary.has("producto") && !objSummary.isNull("producto")) {
                                                String producto = objSummary.getString("producto");
                                                switch (producto) {
                                                    case "1":
                                                        transactionInWs.addTotalDieselLiters(Float.parseFloat(objSummary.getString("real_liters")));
                                                        break;
                                                    case "13":
                                                        transactionInWs.addTotalAdblueLiters(Float.parseFloat(objSummary.getString("real_liters")));
                                                        break;
                                                    case "15":
                                                        transactionInWs.addTotalRedLiters(Float.parseFloat(objSummary.getString("real_liters")));
                                                        break;
                                                }
                                            }

                                            transactionInWs.addTotalTransactions(objSummary.getInt("total_transactions"));
                                        }
                                    }
                                    //CAMPILLO
                                    if(jsonObject.has("campillo") && !jsonObject.isNull("campillo")) {
                                        JSONObject campilloSummary = jsonObject.getJSONObject("campillo");
                                        transactionGpWs.addTotalDieselLiters(Float.parseFloat(campilloSummary.getString("diesel_liters")));
                                        transactionGpWs.addTotalAdblueLiters(Float.parseFloat(campilloSummary.getString("adblue_liters")));
                                        transactionGpWs.addTotalRedLiters(Float.parseFloat(campilloSummary.getString("red_liters")));
                                        transactionGpWs.addTotalTransactions(campilloSummary.getInt("total_transactions"));
                                    }

                                    //GLOBALPAY
                                    if(jsonObject.has("globalpay") && !jsonObject.isNull("globalpay")) {
                                        JSONObject globalpaySummary = jsonObject.getJSONObject("globalpay");
                                        transactionGpWs.addTotalDieselLiters(Float.parseFloat(globalpaySummary.getString("diesel_liters")));
                                        transactionGpWs.addTotalAdblueLiters(Float.parseFloat(globalpaySummary.getString("adblue_liters")));
                                        transactionGpWs.addTotalRedLiters(Float.parseFloat(globalpaySummary.getString("red_liters")));
                                        transactionGpWs.addTotalTransactions(globalpaySummary.getInt("total_transactions"));
                                    }

                                    closeWorkShift();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        //Toast.makeText(getBaseContext(), error.toString(), //Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("terminal", terminal);
                params.put("turno", turno);
                return params;
            }
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", BuildConfig.GLOBALPAY_TOKEN);
                return headers;
            }
        };

        int socketTimeout = 30000;
        int maxRetry = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, maxRetry, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    public void closeWorkShift() {
        sharedpreferences = getSharedPreferences(MyPREFERENCES2, Context.MODE_PRIVATE);
        final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
        final String secret = sharedpreferences.getString(ConfigEnum.secretWordTerminal, BuildConfig.EP_SECRET_WORD);


        RequestQueue queue = Volley.newRequestQueue(this);  // this = context

        String url = ApiGPayUrl + BuildConfig.EP_TERMINALS_CLOSE_WORKSHIFT;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String DataReceived) {
                        try {
                            JSONObject jsonObject = new JSONObject(DataReceived);
                            boolean dataSuccess = jsonObject.getBoolean("success");
                            if (!dataSuccess) {
                                ////Toast.makeText(getBaseContext(), R.string.error_hay_reservas, //Toast.LENGTH_SHORT).show();

                            } else {
                                printTotalsWorkShift();
                                CheckWorkShift();
                                progress.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        //Toast.makeText(getBaseContext(), error.toString(), //Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("terminal", terminal);
                return params;
            }
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", BuildConfig.GLOBALPAY_TOKEN);
                return headers;
            }
        };

        int socketTimeout = 30000;
        int maxRetry = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, maxRetry, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    public void printTotalsWorkShift() {


        final String cabecera = sharedpreferences.getString(ConfigEnum.ticketHeader, null) + "\n";
        final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
        final String turno = sharedpreferences.getString(ConfigEnum.workShiftKey, null);
        final String turnoDateOpen = sharedpreferences.getString(ConfigEnum.workShiftOpenDateKey, null);


        final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        final String hora = new SimpleDateFormat("HH:mm").format(new Date()) + "\n\n";

        ThreadPoolManager.getInstance().executeTask(new Runnable() {

            @Override
            public void run() {
                if (mBitmap == null) {
                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                }
                try {
                    woyouService.lineWrap(2, callback);
                    woyouService.setAlignment(1, callback);
                    woyouService.printBitmap(mBitmap, callback);
                    woyouService.setFontSize(24, callback);
                    woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                    String pterminal = "Terminal: " + terminal + "\n\n";
                    woyouService.printTextWithFont(pterminal, "", 24, callback);
                    woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                    woyouService.lineWrap(1, callback);
                    woyouService.setAlignment(0, callback);

                    woyouService.printTextWithFont("WORK SHIFT ID: " + turno + "\n\n", "", 29, callback);

                    String[] f_separated = turnoDateOpen.split(" ");
                    String[] datearray = f_separated[0].split("-");
                    String day = datearray[2];
                    String month = datearray[1];
                    String year = datearray[0];
                    woyouService.printTextWithFont("Opened at: " + day + "/" + month + "/" + year + " " + f_separated[1] + "\n\n\n", "", 28, callback);

                    if(transactionInWs.getTotalTransactions() > 0) {
                        woyouService.printTextWithFont("SMS transactions (" + transactionInWs.getTotalTransactions() + "):\n\n", "", 27, callback);

                        String strTotals = "";
                        if (transactionInWs.getTotalDieselLiters() > 0) {
                            strTotals += "Diesel Transactions finished: \nLiters: " + transactionInWs.getTotalDieselLiters() + "\n\n";
                        }
                        if (transactionInWs.getTotalAdblueLiters() > 0) {
                            strTotals += "Adblue Transactions finished: \nLiters: " + transactionInWs.getTotalAdblueLiters() + "\n\n";
                        }
                        if (transactionInWs.getTotalRedLiters() > 0) {
                            strTotals += "Gas B Transactions finished: \nLiters: " + transactionInWs.getTotalRedLiters() + "\n\n";
                        }
                        if (transactionInWs.getTotalGasKilos() > 0) {
                            strTotals += "Gas Transactions finished: \nKilos: " + transactionInWs.getTotalGasKilos() + "\n\n";
                        }

                        woyouService.printTextWithFont(strTotals, "", 24, callback);
                    }

                    if(transactionGpWs.getTotalTransactions() > 0) {
                        woyouService.printTextWithFont("GlobalPay DNI transactions (" + transactionGpWs.getTotalTransactions() + "):\n\n", "", 27, callback);

                        String strTotals = "";
                        if (transactionGpWs.getTotalDieselLiters() > 0) {
                            strTotals += "Diesel Transactions finished: \nLiters: " + transactionGpWs.getTotalDieselLiters() + "\n\n";
                        }
                        if (transactionGpWs.getTotalAdblueLiters() > 0) {
                            strTotals += "Adblue Transactions finished: \nLiters: " + transactionGpWs.getTotalAdblueLiters() + "\n\n";
                        }
                        if (transactionGpWs.getTotalRedLiters() > 0) {
                            strTotals += "Gas B Transactions finished: \nLiters: " + transactionGpWs.getTotalRedLiters() + "\n\n";
                        }
                        if (transactionGpWs.getTotalGasKilos() > 0) {
                            strTotals += "Gas Transactions finished: \nKilos: " + transactionGpWs.getTotalGasKilos() + "\n\n";
                        }

                        woyouService.printTextWithFont(strTotals, "", 24, callback);
                    }

                    woyouService.printTextWithFont("Total transactions: " + (transactionGpWs.getTotalTransactions() + transactionInWs.getTotalTransactions()) + "\n\n", "", 24, callback);

                    String dateF = "\nClosed at: " + fecha + " " + hora + "\n\n";
                    woyouService.printTextWithFont(dateF, "", 28, callback);
                    woyouService.lineWrap(2, callback);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (connService != null) {
            unbindService(connService);
        }
    }

    private void innitView() {


        new BitmapUtils(this);

        callback = new ICallback.Stub() {

            @Override
            public void onRunResult(final boolean success) throws RemoteException {
            }

            @Override
            public void onReturnString(final String value) throws RemoteException {
                Log.i(TAG, "printlength:" + value + "\n");
            }

            @Override
            public void onRaiseException(int code, final String msg) throws RemoteException {
                Log.i(TAG, "onRaiseException: " + msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            }
        };

        Intent intent = new Intent();
        intent.setPackage("woyou.aidlservice.jiuiv5");
        intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        startService(intent);
        bindService(intent, connService, Context.BIND_AUTO_CREATE);
    }

    public void reservesCounter() {
        //FUNCION PARA MIRAR SI EXITEN RESERVAVAS ACTIVAS.
        sharedpreferences = getSharedPreferences(MyPREFERENCES2, Context.MODE_PRIVATE);
        final String server = sharedpreferences.getString(ConfigEnum.serverUrlSMS, BuildConfig.EP_URL_API_BASE_SMS);
        final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
        RequestQueue queue = Volley.newRequestQueue(this);  // this = context
        String url = server + "/check_reserves.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String DataReceived) {
                        Log.d("Response", DataReceived);
                        int ReservesCounter = Integer.parseInt(DataReceived);
                        bagde = (TextView) findViewById(R.id.textcount);
                        if (ReservesCounter == 0) {
                            //NO HAY CODIGOS RESERVADOS. NO MUESTRO EL BADGE
                            bagde.setVisibility(View.INVISIBLE);
                        } else {
                            //HAY CODIGOS RESERVADOS. MUESTRO EL BADGE
                            bagde.setText(String.valueOf(ReservesCounter));
                            bagde.setVisibility(View.VISIBLE);

                            final Handler handler = new Handler();
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    didTapButton();
                                    handler.postDelayed(this, 3000);
                                }
                            };

                            handler.postDelayed(runnable, 1000);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        //Toast.makeText(getBaseContext(), error.toString(), //Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("terminal", terminal);
                return params;
            }
        };
        queue.add(postRequest);

    }

    public void didTapButton() {
        TextView badge2;
        badge2 = (TextView) findViewById(R.id.textcount);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        badge2.startAnimation(myAnim);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

    }

}

