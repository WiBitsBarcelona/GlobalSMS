package eu.globaldevelopers.globalsms;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class CheckActivity extends AppCompatActivity {

    String codigo = "";
    String asteriscos = "";
    public static final String MyPREFERENCES2 = "MyPrefs" ;

    SharedPreferences sharedpreferences;

    OutputStream mmOutputStream;

    ProgressDialog progress;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_check);

        if (Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            //for higher api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }

        innitView();
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
                        public void onResponse(final String datareceived) {
                            if (datareceived.equals("nohaydatos")) {
                                //No esta reservado, vuelvo
                                //Toast.makeText(getBaseContext(), R.string.error_no_encontrado, //Toast.LENGTH_SHORT).show();
                            } else {
                                final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                                final String hora = new SimpleDateFormat("HH:mm").format(new Date()) + "\n\n";
                                try{
                                    ThreadPoolManager.getInstance().executeTask(new Runnable(){

                                        @Override
                                        public void run() {
                                            if( mBitmap == null ){
                                                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                            }
                                            try {
                                                woyouService.lineWrap(2, callback);
                                                woyouService.setAlignment(1, callback);
                                                woyouService.printBitmap(mBitmap, callback);
                                                woyouService.setFontSize(24, callback);
                                                woyouService.printTextWithFont("\n"+ cabecera + "\n", "", 28, callback);
                                                String pterminal = "Terminal: " + terminal + "\n\n";
                                                woyouService.printTextWithFont(pterminal, "", 24, callback);
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
                                                        woyouService.printTextWithFont(date, "", 24, callback);
                                                    }
                                                    woyouService.setAlignment(0, callback);
                                                    if (jsonObject.has("producto") && !jsonObject.isNull("producto")) {
                                                        product_id = jsonObject.getString("producto");
                                                        switch (product_id){
                                                            case "1":
                                                                product_txt = "PRODUCT: DIESEL\n\n";
                                                                break;
                                                            case "13":
                                                                product_txt = "PRODUCT: AD BLUE\n\n";
                                                                break;
                                                            case "15":
                                                                product_txt = "PRODUCT: RED DIESEL\n\n";
                                                        }
                                                        woyouService.printTextWithFont(product_txt, "", 26, callback);
                                                    }

                                                    // code
                                                    if (jsonObject.has("codigo") && !jsonObject.isNull("codigo")) {
                                                        code = jsonObject.getString("codigo");
                                                        String code_txt = "CODE: " + code + "\n";
                                                        woyouService.printTextWithFont(code_txt, "", 26, callback);
                                                    }

                                                    // liters
                                                    if (jsonObject.has("litros_reales") && !jsonObject.isNull("litros_reales")) {
                                                        liters = jsonObject.getString("litros_reales");
                                                        if(!liters.equals("0.00")) {
                                                            String liters_txt = "LITERS: " + liters + "\n";
                                                            woyouService.printTextWithFont(liters_txt, "", 26, callback);
                                                        }
                                                    }

                                                    // liters authorized
                                                    if (jsonObject.has("litros_autorizados") && !jsonObject.isNull("litros_autorizados")) {
                                                        liters_a = jsonObject.getString("litros_autorizados");
                                                        if(liters.equals("0.00")) {
                                                            String liters_a_txt = "AUTHORIZED LITERS: " + liters_a + "\n";
                                                            woyouService.printTextWithFont(liters_a_txt, "", 26, callback);
                                                        }
                                                    }

                                                    // trans_code
                                                    if (jsonObject.has("num_operacion") && !jsonObject.isNull("num_operacion")) {
                                                        trans_code = jsonObject.getString("num_operacion");
                                                        String tr_txt = "TRANSACTION: " + trans_code + "\n";
                                                        woyouService.printTextWithFont(tr_txt, "", 24, callback);
                                                    }
                                                    woyouService.lineWrap(2, callback);
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
                                                            case "CANCELADA":
                                                                tr_txt = "STATUS: CANCELLED\n\n";
                                                                break;
                                                        }

                                                        woyouService.printTextWithFont(tr_txt, "", 28, callback);
                                                    }
                                                }

                                                woyouService.lineWrap(4, callback);
                                            } catch (RemoteException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }});





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
                            //Toast.makeText(getBaseContext(), error.toString(), //Toast.LENGTH_SHORT).show();
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
                Log.i(TAG,"printlength:" + value + "\n");
            }

            @Override
            public void onRaiseException(int code, final String msg) throws RemoteException {
                Log.i(TAG,"onRaiseException: " + msg);
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {

                    }});

            }
        };

        Intent intent=new Intent();
        intent.setPackage("woyou.aidlservice.jiuiv5");
        intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        startService(intent);
        bindService(intent, connService, Context.BIND_AUTO_CREATE);
    }

}