package eu.globaldevelopers.globalsms;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import eu.globaldevelopers.globalsms.Class.CustomizationValue;
import eu.globaldevelopers.globalsms.Class.DataUserCustomization;
import eu.globaldevelopers.globalsms.Class.GpCallback;
import eu.globaldevelopers.globalsms.Class.PrintTicket;
import eu.globaldevelopers.globalsms.Class.Product;
import eu.globaldevelopers.globalsms.Class.SampleResponse;
import eu.globaldevelopers.globalsms.Class.UserCustomization;
import eu.globaldevelopers.globalsms.Class.globalwallet.CardQueryResponse;
import eu.globaldevelopers.globalsms.Class.globalwallet.CardResponse;
import eu.globaldevelopers.globalsms.Class.globalwallet.QrCard;
import eu.globaldevelopers.globalsms.Class.globalwallet.QrTransaction;
import eu.globaldevelopers.globalsms.Class.globalwallet.SaleTransactionResponse;
import eu.globaldevelopers.globalsms.Class.globalwallet.UnlockRequestResponse;
import eu.globaldevelopers.globalsms.Enums.ApiTypeEnum;
import eu.globaldevelopers.globalsms.Enums.ConfigEnum;
import eu.globaldevelopers.globalsms.Enums.CustomizationsEnum;
import eu.globaldevelopers.globalsms.Enums.ProcessTransactionTypeEnum;
import eu.globaldevelopers.globalsms.Enums.ProcessTypeEnum;
import eu.globaldevelopers.globalsms.Enums.ProductEnum;
import eu.globaldevelopers.globalsms.Enums.ProductGWIntEnum;
import eu.globaldevelopers.globalsms.Enums.ProductIntEnum;
import eu.globaldevelopers.globalsms.Enums.ProductPriceKeyEnum;
import eu.globaldevelopers.globalsms.Enums.ServiceTypeEnum;
import eu.globaldevelopers.globalsms.Enums.TransactionTypeEnum;
import eu.globaldevelopers.globalsms.Class.GpCallback;
import eu.globaldevelopers.globalsms.Helpers.LocaleHelper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.GONE;

public class PinPadActivity extends AppCompatActivity {

    ProcessTypeEnum processType;
    boolean gwScanLiters;
    ProductEnum productType;
    String codigo = "";
    String asteriscos = "";
    String textoproducto;
    String msg;
    String detalle;
    String litros;
    String TextEuros;
    String codigoerror = "";
    String textoerror = "";
    String operation;
    String respuesta;
    ArrayList QRArray = new ArrayList();
    int cuantosQR = 1;
    float litrostmp = 0;
    float totalLiters = 0;
    String tipo;
    String litrosT;
    String totalTxt = "0.00";
    Boolean EsEfiData = false;

    /**
     * AUTHORIZATIONS
     */
    Double AuthDiesel, DieselPrice = 0.00;
    Double AuthAdBlue, AdbluePrice = 0.00;
    Double AuthRedDiesel, RedPrice = 0.00;
    Double AuthGas, GasPrice = 0.00;
    Double AuthMoney = 0.00;
    View CampilloLitersLayout;
    View GlobalWalletLitersLayout;

    Integer KmsRequired;
    Integer HoursRequired;

    String Expendient;
    ArrayList<String> TransactionIds = new ArrayList<>();

    String RealDiesel = "";
    String RealAdBlue = "";
    String RealRedDiesel = "";
    String RealGas = "";
    String kms = "";
    String hours = "";


    public static final String MyPREFERENCES = ConfigEnum.MyPREFERENCES;
    public static final String MySUPPLY = "MySupply";
    public static final String MyPRECIOS = "MyPrecios";
    public static final String LastCampilloAuth = "Campillo";
    public static final String langKey = ConfigEnum.langKey;

    SharedPreferences sharedpreferences;
    SharedPreferences sharedpreferences2;
    SharedPreferences sharedpreferences3;
    SharedPreferences sharedCampillo;

    int counter = 1;

    public static String dieselactual;
    public static String adblueactual;
    public static String reddieselactual;
    public static String biodieselactual;

    public static final String producto = "productoKey";

    ProgressDialog progress, mProgressDialog;

    ContadorTimeOut contadortimeout;

    AlertDialog.Builder builder, builder2;

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


    //GLOBALPAY
    public static String ApiCampilloURI, ApiGPayUrl;

    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/Signatures/";
    String StoredPath = DIRECTORY;

    Button btn_get_sign, mClear, mGetSign, mCancel;

    File file;
    Dialog dialog;
    LinearLayout mContent;
    View view;
    signature mSignature;
    Bitmap bitmap;

    Context pinpadActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_pin_pad);

        //OBTENEMOS LA URL DE LA API DE LA CONFIGURACION DEL TERMINAL
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        ApiCampilloURI = sharedpreferences.getString(ConfigEnum.apiCampilloUrl, BuildConfig.EP_URL_API_BASE_CAMPILLO);
        ApiGPayUrl = sharedpreferences.getString(ConfigEnum.apiGenericUrl, BuildConfig.EP_URL_API_BASE_GLOBALPAY);
        pinpadActivity = this;

        //SET LANG
        String lang = sharedpreferences.getString(ConfigEnum.langKey, "en");
        LocaleHelper.setAppLocale(lang, this);

        if (Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(GONE);
        } else {
            //for higher api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }

        innitView();

        file = new File(DIRECTORY);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //SET LANG
        String lang = sharedpreferences.getString(ConfigEnum.langKey, "en");
        LocaleHelper.setAppLocale(lang, this);
    }

    public void PulsadoFunction(View v) {
        switch (v.getId()) {
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

    public void borrarcaracter(View v) {
        int l = codigo.length();
        if (l == 0) {
            asteriscos = codigo;
            TextView textView = (TextView) findViewById(R.id.codigoactual);
            textView.setText(asteriscos);
        } else {
            codigo = codigo.substring(0, codigo.length() - 1);
            asteriscos = codigo;
            TextView textView = (TextView) findViewById(R.id.codigoactual);
            textView.setText(asteriscos);
        }

    }

    public void btncancelar(View v) {
        Intent Intent = new Intent(this, MainActivity.class);
        startActivity(Intent);
        finish();
    }

    public void btnaceptar(final View v) {

        int l = codigo.length();

        sharedpreferences2 = getSharedPreferences(MySUPPLY, Context.MODE_PRIVATE);
        final String productselected = sharedpreferences2.getString("productoKey", null);
        final String transaction = sharedpreferences2.getString("tipoKey", null);

        switch (l) {
            case 0:
                //NO DRIVERS CODE OR IDENTY.ERROR
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(this.getString(R.string.alert_no_code));
                alert.setCancelable(false);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();
                break;
            case 7:
                //INSTANTIC TRANSACTION
                processType = ProcessTypeEnum.INSTANTIC;
                switch (transaction) {
                    case "Nueva":
                        ProductDialog();
                        break;
                    case "Cierre":
                        cierraT();
                        break;
                    case "Cancela":
                        cancelaT();
                        break;
                }

                break;
            default:
                //CAMPILLO TRANSACTION O GLOBALPAY, PRIMERO COMPRUEBA SI SE TRATA DE UNA TRANSACCION DE CAMPILLO Y SI NO LA ENCUENTRA LA BUSCA EN GLOBALPAY
                processType = ProcessTypeEnum.GLOBALPAY;
                switch (transaction) {
                    case "Nueva":
                        CampilloPreReserve();
                        //CampilloReserve();
                        break;
                    case "Cierre":
                        CheckCampilloTrx();
                        break;
                    case "Cancela":
                        //cancelaT();
                        //Toast.makeText(getBaseContext(), "OPCION NO HABILITADA PARA CAMPILLO", //Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }


    private static InputStream convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
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
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        }catch(Exception ex){
            return "";
        }
    }

    void cierraT() {
        if (isNetworkAvailable() == false) {
            mensajered();
        } else {
            //Primero, miro si el código esta reservado.
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String server = sharedpreferences.getString(ConfigEnum.serverUrlSMS, BuildConfig.EP_URL_API_BASE_SMS);
            final boolean qr = sharedpreferences.getBoolean(ConfigEnum.qr, false);
            final int pos = sharedpreferences.getInt(ConfigEnum.pos, 0);
            RequestQueue queue = Volley.newRequestQueue(this);  // this = context
            String url = server + "/consultapin.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("noexiste")) {
                                //No esta reservado, vuelvo
                                //Toast.makeText(getBaseContext(), R.string.error_no_reservado, //Toast.LENGTH_SHORT).show();
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
                            mensajetimeout();
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

            int socketTimeout = 30000;
            int maxRetry = 0;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, maxRetry, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy);
            queue.add(postRequest);
        }
    }

    void cancelaT() {
        if (isNetworkAvailable() == false) {
            mensajered();
        } else {

            progress = new ProgressDialog(this);
            progress.setMessage(this.getString(R.string.spinner_conectando));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.setProgress(0);
            progress.show();
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String cabecera = sharedpreferences.getString(ConfigEnum.ticketHeader, null) + "\n";
            final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
            final String secret = sharedpreferences.getString(ConfigEnum.secretWordTerminal, BuildConfig.EP_SECRET_WORD);
            final String server = sharedpreferences.getString(ConfigEnum.serverUrlSMS, BuildConfig.EP_URL_API_BASE_SMS);
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

                                            int ReservesCounter = sharedpreferences.getInt("reservesCount", 0);
                                            int Reserves = ReservesCounter - 1;
                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putInt("reservesCount", Reserves);
                                            editor.apply();

                                            //Toast.makeText(getBaseContext(), "TRANSACTION SUCCESSFULLY CANCELLED", //Toast.LENGTH_SHORT).show();
                                            try {
                                                msg = "TRANSACTION SUCCESSFULLY\n";
                                                msg += "CANCELLED";
                                                msg += "\n";
                                                for (int g2 = 0; g2 < 2; g2++) {
                                                    ThreadPoolManager.getInstance().executeTask(new Runnable() {

                                                        @Override
                                                        public void run() {
                                                            if (mBitmap == null) {
                                                                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                                            }
                                                            try {
                                                                msg = "TRANSACTION SUCCESSFULLY\n";
                                                                msg += "CANCELLED";
                                                                msg += "\n";

                                                                woyouService.lineWrap(2, callback);
                                                                woyouService.setAlignment(1, callback);
                                                                woyouService.printBitmap(mBitmap, callback);
                                                                woyouService.setFontSize(24, callback);
                                                                woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                                                String pterminal = "Terminal: " + terminal + "\n\n";
                                                                woyouService.printTextWithFont(pterminal, "", 24, callback);
                                                                woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                                                woyouService.lineWrap(2, callback);
                                                                woyouService.setAlignment(0, callback);
                                                                woyouService.printTextWithFont("Transaction Code: " + codigo + "\n", "", 30, callback);
                                                                woyouService.printTextWithFont("\n", "", 24, callback);
                                                                woyouService.setAlignment(1, callback);
                                                                woyouService.printTextWithFont(msg, "", 32, callback);
                                                                woyouService.lineWrap(4, callback);
                                                            } catch (RemoteException e) {
                                                                // TODO Auto-generated catch block
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    });
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
                                            //Toast.makeText(getBaseContext(), "TRANSACTION REFUSED", //Toast.LENGTH_SHORT).show();
                                            codigoerror = getValue("error_code", element2);
                                            textoerror = getValue("error_description", element2);
                                            for (int g = 0; g < 2; g++) {
                                                ThreadPoolManager.getInstance().executeTask(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        if (mBitmap == null) {
                                                            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                                        }
                                                        try {
                                                            msg = "TRANSACTION REFUSED";
                                                            msg += "\n";

                                                            woyouService.lineWrap(2, callback);
                                                            woyouService.setAlignment(1, callback);
                                                            woyouService.printBitmap(mBitmap, callback);
                                                            woyouService.setFontSize(24, callback);
                                                            woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                                            String pterminal = "Terminal: " + terminal + "\n\n";
                                                            woyouService.printTextWithFont(pterminal, "", 24, callback);
                                                            woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                                            woyouService.lineWrap(2, callback);
                                                            woyouService.setAlignment(0, callback);
                                                            woyouService.printTextWithFont("Transaction Code: " + codigo + "\n", "", 30, callback);
                                                            woyouService.printTextWithFont("Error Code: " + codigoerror + "\n", "", 28, callback);
                                                            woyouService.printTextWithFont("Error: " + textoerror + "\n", "", 28, callback);
                                                            woyouService.printTextWithFont("\n", "", 24, callback);
                                                            woyouService.setAlignment(1, callback);
                                                            woyouService.printTextWithFont(msg, "", 36, callback);
                                                            woyouService.lineWrap(4, callback);
                                                        } catch (RemoteException e) {
                                                            // TODO Auto-generated catch block
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                });
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

                            salvaroperacion(TransactionTypeEnum.CANCEL, cabecera, terminal, fecha, hora, msg, codigo, textoproducto, "0", sharedpreferences.getString(codigo + ConfigEnum.plate, ""), "0", litros, "0", codigoerror, textoerror);
                            //REMOVE PLATE AND CUSTOMER_NAME FROM LOCALSTORAGE
                            SharedPreferences.Editor localStorage = sharedpreferences.edit();
                            localStorage.remove(codigo + ConfigEnum.plate);
                            localStorage.remove(codigo + ConfigEnum.customerName);
                            localStorage.apply();
                            final Thread t = new Thread() {
                                @Override
                                public void run() {
                                    try {
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
                            mensajetimeout();
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

            int socketTimeout = 30000;
            int maxRetry = 0;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, maxRetry, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest2.setRetryPolicy(policy);
            queue2.add(postRequest2);
        }
    }

    void pedirlitros() {
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

    void finalizaT(final String litros) {
        if (!isNetworkAvailable()) {
            mensajered();
        } else {
            counter = 1;
            progress = new ProgressDialog(this);
            progress.setMessage(this.getString(R.string.spinner_conectando));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.setProgress(0);
            progress.show();

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String cabecera = sharedpreferences.getString(ConfigEnum.ticketHeader, null) + "\n";
            final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
            final String secret = sharedpreferences.getString(ConfigEnum.secretWordTerminal, BuildConfig.EP_SECRET_WORD);
            final String server = sharedpreferences.getString(ConfigEnum.serverUrlSMS, BuildConfig.EP_URL_API_BASE_SMS);

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

                                            int ReservesCounter = sharedpreferences.getInt("reservesCount", 0);
                                            int Reserves = ReservesCounter - 1;
                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putInt("reservesCount", Reserves);
                                            editor.apply();

                                            //Toast.makeText(getBaseContext(), "TRANSACTION SUCCESSFULLY COMPLETED", //Toast.LENGTH_SHORT).show();
                                            operation = getValue("transaction_id", element2);
                                            final String litros = getValue("liters", element2);
                                            final String plate = sharedpreferences.getString(codigo + ConfigEnum.plate, "");
                                            final String customerName = sharedpreferences.getString(codigo + ConfigEnum.customerName, "");
                                            String producto = getValue("product_code", element2);
                                            String price = getValue("price", element2);
                                            switch (producto) {
                                                case "DIZ":
                                                    textoproducto = "DIESEL";
                                                    price = sharedpreferences3.getString("dieselKey", "0.00");
                                                    break;
                                                case "ADB":
                                                    textoproducto = "AD BLUE";
                                                    price = sharedpreferences3.getString("adblueKey", "0.00");
                                                    break;
                                                case "FOD":
                                                    textoproducto = "RED DIESEL";
                                                    price = sharedpreferences3.getString("reddieselKey", "0.00");
                                            }
                                            detalle = textoproducto;
                                            double total = Float.valueOf(price) * Float.valueOf(litros);
                                            BigDecimal a = new BigDecimal(total);
                                            final BigDecimal total2 = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                            totalTxt = total2.toString();
                                            try {
                                                for (int g2 = 0; g2 < 2; g2++) {
                                                    ThreadPoolManager.getInstance().executeTask(new Runnable() {

                                                        @Override
                                                        public void run() {
                                                            if (mBitmap == null) {
                                                                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                                            }
                                                            try {
                                                                msg = "TRANSACTION SUCCESSFULLY\n";
                                                                msg += "COMPLETED";
                                                                msg += "\n";
                                                                woyouService.lineWrap(2, callback);
                                                                woyouService.setAlignment(1, callback);
                                                                woyouService.printBitmap(mBitmap, callback);
                                                                woyouService.setFontSize(24, callback);
                                                                woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                                                String pterminal = "Terminal: " + terminal + "\n\n";
                                                                woyouService.printTextWithFont(pterminal, "", 24, callback);
                                                                woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                                                woyouService.lineWrap(2, callback);
                                                                woyouService.setAlignment(0, callback);
                                                                woyouService.printTextWithFont(getString(R.string.plate) + ": " + plate + "\n", "", 30, callback);
                                                                woyouService.printTextWithFont(getString(R.string.customer_name) + ": " + customerName + "\n", "", 30, callback);
                                                                woyouService.printTextWithFont("Transaction Code: " + codigo + "\n", "", 30, callback);
                                                                woyouService.printTextWithFont("Operation Code: " + operation + "\n", "", 30, callback);
                                                                woyouService.printTextWithFont("\n", "", 28, callback);
                                                                woyouService.setFontSize(28, callback);
                                                                String[] text = new String[3];
                                                                int[] width = new int[]{10, 8, 8};
                                                                int[] align = new int[]{0, 2, 2}; //

                                                                text[0] = "Product";
                                                                text[1] = "Liters";
                                                                text[2] = "Total";
                                                                woyouService.printColumnsText(text, width, new int[]{0, 2, 2}, callback);

                                                                text[0] = textoproducto;
                                                                text[1] = litros;
                                                                text[2] = total2.toString();
                                                                woyouService.printColumnsText(text, width, align, callback);
                                                                woyouService.lineWrap(2, callback);
                                                                woyouService.setAlignment(1, callback);
                                                                woyouService.printTextWithFont(msg, "", 32, callback);
                                                                woyouService.lineWrap(4, callback);
                                                            } catch (RemoteException e) {
                                                                // TODO Auto-generated catch block
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    });

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
                                            //Toast.makeText(getBaseContext(), "TRANSACTION REFUSED", //Toast.LENGTH_SHORT).show();
                                            codigoerror = getValue("error_code", element2);
                                            textoerror = getValue("error_description", element2);
                                            for (int g = 0; g < 2; g++) {
                                                ThreadPoolManager.getInstance().executeTask(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        if (mBitmap == null) {
                                                            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                                        }
                                                        try {
                                                            msg = "TRANSACTION REFUSED";
                                                            msg += "\n";

                                                            woyouService.lineWrap(2, callback);
                                                            woyouService.setAlignment(1, callback);
                                                            woyouService.printBitmap(mBitmap, callback);
                                                            woyouService.setFontSize(24, callback);
                                                            woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                                            String pterminal = "Terminal: " + terminal + "\n\n";
                                                            woyouService.printTextWithFont(pterminal, "", 24, callback);
                                                            woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                                            woyouService.lineWrap(2, callback);
                                                            woyouService.setAlignment(0, callback);
                                                            woyouService.printTextWithFont("Transaction Code: " + codigo + "\n", "", 30, callback);
                                                            woyouService.printTextWithFont("Error Code: " + codigoerror + "\n", "", 28, callback);
                                                            woyouService.printTextWithFont("Error: " + textoerror + "\n", "", 28, callback);
                                                            woyouService.printTextWithFont("\n", "", 24, callback);
                                                            woyouService.setAlignment(1, callback);
                                                            woyouService.printTextWithFont(msg, "", 36, callback);
                                                            woyouService.lineWrap(4, callback);
                                                        } catch (RemoteException e) {
                                                            // TODO Auto-generated catch block
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                });
                                                if (g == 0) {
                                                    Thread.sleep(4000);
                                                }
                                            }
                                        }
                                        salvaroperacion(TransactionTypeEnum.FINISH, cabecera, terminal, fecha, hora, msg, codigo, detalle, operation, sharedpreferences.getString(codigo + ConfigEnum.plate, ""), "0", litros, totalTxt, codigoerror, textoerror);
                                        //REMOVE PLATE FROM LOCALSTORAGE
                                        SharedPreferences.Editor localStorage = sharedpreferences.edit();
                                        localStorage.remove(codigo + ConfigEnum.plate);
                                        localStorage.remove(codigo + ConfigEnum.customerName);
                                        localStorage.apply();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            final Thread t = new Thread() {
                                @Override
                                public void run() {
                                    try {
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
                            mensajetimeout();
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

            int socketTimeout = 30000;
            int maxRetry = 0;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, maxRetry, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest2.setRetryPolicy(policy);
            queue2.add(postRequest2);
        }
    }

    void salvaroperacion(String tipoS, String cabeceraS, String terminalS, String fechaS, String horaS, String resultadoS, String codigoS, String txtproductoS, String operacionS, String plate, String litros_aceptadosS, String litrosS, String totalS, String codigo_errorS, String errorS) {
        if (resultadoS == null) {
            resultadoS = " ";
        }
        if (operacionS == null) {
            operacionS = "0";
        }
        if (txtproductoS == null) {
            txtproductoS = " ";
        }
        if (litrosS == null) {
            litrosS = "0";
        }

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "datos", null, BuildConfig.VERSION_CODE);

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
        registro.put("plate", plate);
        registro.put("litros_aceptados", litros_aceptadosS);
        registro.put("litros", litrosS);
        registro.put("total", totalS);
        registro.put("codigo_error", codigo_errorS);
        registro.put("error", errorS);

        // los inserto en la base de datos
        bd.insert("operaciones", null, registro);

        bd.close();

    }

    void saveTrxToMemory(int service, String tipoS, String cabecera, String terminal, String fecha, String hora, String resultado, String codigo, String txtproducto, String operacion, String litros_aceptados, Double diesel_liters, Double adblue_liters, Double red_liters, Double gas_kilos, String litros, String plate, String trailer_plate, Boolean showPrices) {
        if (resultado == null) {
            resultado = " ";
        }
        if (operacion == null) {
            operacion = "0";
        }
        if (txtproducto == null) {
            txtproducto = " ";
        }
        if (litros == null) {
            litros = "0";
        }

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "datos", null, BuildConfig.VERSION_CODE);

        SQLiteDatabase bd = admin.getWritableDatabase();


        ContentValues registro = new ContentValues();

        registro.put("service_type", service);
        registro.put("tipo", tipoS);
        registro.put("cabecera", cabecera);
        registro.put("terminal", terminal);
        registro.put("fecha", fecha);
        registro.put("hora", hora);
        registro.put("resultado", resultado);
        registro.put("codigo", codigo);
        registro.put("producto", txtproducto);
        registro.put("operacion", operacion);
        registro.put("litros_aceptados", litros_aceptados);
        registro.put("litros", litros);
        registro.put("diesel_liters", diesel_liters);
        registro.put("adblue_liters", adblue_liters);
        registro.put("red_liters", red_liters);
        registro.put("gas_kilos", gas_kilos);
        registro.put("plate", plate);
        registro.put("trailer_plate", trailer_plate);
        registro.put("show_prices", showPrices ? 1 : 0);

        // los inserto en la base de datos
        bd.insert("operaciones", null, registro);

        bd.close();

    }

    void salvarrespuesta(String respuestaS) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "datos", null, BuildConfig.VERSION_CODE);

        SQLiteDatabase bd = admin.getWritableDatabase();


        ContentValues registro = new ContentValues();

        registro.put("respuesta", respuestaS);


        // los inserto en la base de datos
        bd.insert("respuestas", null, registro);

        bd.close();

    }

    void dialogoQR(String productorespuesta) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogoLayout = inflater.inflate(R.layout.dialogo_cuantos_qr, null);
        TextView lbl_producto = (TextView) dialogoLayout.findViewById(R.id.labelcuantos);


        if (productorespuesta.equals("1")) {
            lbl_producto.setText(R.string.dialogo_qr_diesel);
        }
        if (productorespuesta.equals("13")) {
            lbl_producto.setText(R.string.dialogo_qr_adblue);
        }
        if (productorespuesta.equals("15")) {
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

    void dialogQR() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogoLayout = inflater.inflate(R.layout.dialogo_cuantos_qr, null);
        TextView lbl_producto = (TextView) dialogoLayout.findViewById(R.id.labelcuantos);

        switch (productType) {
            case DIESEL:
                lbl_producto.setText(R.string.dialogo_qr_diesel);
                break;
            case ADBLUE:
                lbl_producto.setText(R.string.dialogo_qr_adblue);
                break;
            case RED:
                lbl_producto.setText(R.string.dialogo_qr_diesel_rojo);
                break;
            case GAS:
                lbl_producto.setText(R.string.dialogo_qr_gas);
                break;
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
                counter = 1;
                show.dismiss();
                scanQR();
            }
        });

        alertButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cuantosQR = 2;
                counter = 1;
                show.dismiss();
                scanQR();
            }
        });

        alertButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cuantosQR = 3;
                counter = 1;
                show.dismiss();
                scanQR();
            }
        });

        alertButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cuantosQR = 4;
                counter = 1;
                show.dismiss();
                scanQR();
            }
        });
    }

    public void numerodeQRs(View v) {
        switch (v.getId()) {
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

    void scanQR() {
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

    //SCAN QR FROM PINPAN DIRECTLY, PROCESSTYPE = GLOBALWALLET
    public void scanQR(View v) {
        processType = ProcessTypeEnum.GLOBALWALLET;//SET PROCESS GLOBALWALLET
        gwScanLiters = false;

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

        //SET LANG
        String lang = sharedpreferences.getString(ConfigEnum.langKey, "en");
        LocaleHelper.setAppLocale(lang, this);

        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            if (scanningResult.getContents() == null) {
                //El usuario ha pulaado el boton cancelar en el Scanner.
                ////Toast.makeText(this, "Cancelado", //Toast.LENGTH_LONG).show();
            } else {
                switch (processType) {
                    case INSTANTIC:
                        String scanContent = scanningResult.getContents();
                        String campos[] = scanContent.split("\\|");
                        tipo = campos[0];
                        litrosT = campos[1];
                        litrosT = litrosT.replace(",", ".").trim();
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
                        break;
                    case GLOBALPAY:
                        String scanQrContent = scanningResult.getContents();
                        String data[] = scanQrContent.split("\\|");
                        tipo = data[0];
                        String liters = data[1];
                        liters = liters.replace(",", ".").trim();
                        totalLiters += Float.valueOf(liters);
                        if (counter < cuantosQR) {
                            counter++;
                            scanQR();
                        } else {
                            //View campilloLitersLayout = this.findViewById(R.id.litersDialog);
                            //Obtenemos la vista de litros y le asginamos el valor al textView de la layout dependiendo del producto en el que estemos
                            switch (productType) {
                                case DIESEL:
                                    final TextView dieselLiters = (TextView) CampilloLitersLayout.findViewById(R.id.dieselLiters);
                                    dieselLiters.setText(String.valueOf(totalLiters));
                                    break;
                                case ADBLUE:
                                    final TextView adblueLiters = (TextView) CampilloLitersLayout.findViewById(R.id.adblueLiters);
                                    adblueLiters.setText(String.valueOf(totalLiters));
                                    break;
                                case RED:
                                    final TextView reddieselLiters = (TextView) CampilloLitersLayout.findViewById(R.id.reddieselLiters);
                                    reddieselLiters.setText(String.valueOf(totalLiters));
                                    break;
                                case GAS:
                                    final TextView gasLiters = (TextView) CampilloLitersLayout.findViewById(R.id.gasLiters);
                                    gasLiters.setText(String.valueOf(totalLiters));
                                    break;
                            }
                        }
                        break;
                    case GLOBALWALLET:
                        if (!gwScanLiters) {
                            sharedpreferences2 = getSharedPreferences(MySUPPLY, Context.MODE_PRIVATE);
                            final String methodType = sharedpreferences2.getString("tipoKey", null);

                            switch (methodType) {
                                case "Nueva":
                                    scanQrContent = scanningResult.getContents();
                                    getQrCardQuery(scanQrContent);
                                    break;
                                case "Cierre":
                                    scanQrContent = scanningResult.getContents();
                                    getQrCardInfo(scanQrContent);
                                    break;
                                case "Cancela":
                                    //Toast toast = Toast.makeText(getApplicationContext(), "Invalid operation", //Toast.LENGTH_SHORT);
                                    //Toast.show();
                                    break;
                            }
                        } else {
                            scanQrContent = scanningResult.getContents();
                            String dataLiters[] = scanQrContent.split("\\|");
                            if (dataLiters.length > 1) {
                                tipo = dataLiters[0];
                                liters = dataLiters[1];
                                liters = liters.replace(",", ".").trim();
                                totalLiters += Float.valueOf(liters);
                                if (counter < cuantosQR) {
                                    counter++;
                                    scanQR();
                                } else {
                                    //View campilloLitersLayout = this.findViewById(R.id.litersDialog);
                                    //Obtenemos la vista de litros y le asginamos el valor al textView de la layout dependiendo del producto en el que estemos
                                    switch (productType) {
                                        case DIESEL:
                                            final EditText dieselLiters = (EditText) GlobalWalletLitersLayout.findViewById(R.id.gas_a_quantity);
                                            dieselLiters.setText(String.valueOf(totalLiters));
                                            break;
                                        case ADBLUE:
                                            final TextView adblueLiters = (TextView) GlobalWalletLitersLayout.findViewById(R.id.adblue_quantity);
                                            adblueLiters.setText(String.valueOf(totalLiters));
                                            break;
                                        case RED:
                                            final TextView reddieselLiters = (TextView) GlobalWalletLitersLayout.findViewById(R.id.gas_b_quantity);
                                            reddieselLiters.setText(String.valueOf(totalLiters));
                                            break;
                                        case GAS:
                                            final TextView gasLiters = (TextView) GlobalWalletLitersLayout.findViewById(R.id.gas_quantity);
                                            gasLiters.setText(String.valueOf(totalLiters));
                                            break;
                                    }
                                }
                            } else {
                                AlertDialog dialog = showAlertError(getString(R.string.error_tipo_producto));
                                dialog.show();
                            }
                        }
                        break;
                }
            }
        } else {
            // Toast toast = //Toast.makeText(getApplicationContext(), "No scan data received!", //Toast.LENGTH_SHORT);
            //Toast.show();
        }
    }

    void mensajeQRNovalido() {
        litrostmp -= Float.valueOf(litrosT);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
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

    void mensajeQRRepetido() {
        litrostmp -= Float.valueOf(litrosT);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
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

    void mensajered() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
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

    void mensajetimeout() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
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

        alert.setNeutralButton(R.string.btn_contacto, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                progress.dismiss();
                mensajecontacto();
            }
        });
        alert.show();
    }

    void mensajecontacto() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
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


    public boolean CheckTipoRepostaje() {
        //Vilamalla: Diesel - Ad Blue
        //Gexa: GAS/A - AdBlue
        //Arraia: GAS/A - ADBLUE
        //Tiebas: GASOLEO A  - ADBLUE

        switch (tipo) {
            case "Diesel":
                //Es Diesel ALVIC Vilamalla
            case "GAS/A":
                //Es Diesel de ALVIC Gexa, Alfajarin y Arraia
                return respuesta.equals("1");
            case "GASOLEO A":
                //Es Diesel de EFIDATA Tiebas
                if (respuesta.equals("1")) {
                    EsEfiData = true;
                    return true;
                    //litrosT = litrosT.replace(",",".");
                    //finalizaT(litrosT);
                } else {
                    return false;
                }
            case "Ad Blue":
                //Es Ad Blue de ALVIC Vilamalla
                return respuesta.equals("13");
            case "AdBlue":
                //Es Ad Blue de ALVIC Gexa y Alfajarin
                return respuesta.equals("13");
            case "ADBLUE":
                //Es Ad Blue de ALVIC Arraia
                return respuesta.equals("13");
            case "AD BLUE":
                //Es Ad Blue de EFIDATA
                if (respuesta.equals("13")) {
                    EsEfiData = true;
                    //litrosT = litrosT.replace(",",".");
                    //finalizaT(litrosT);
                    return true;
                } else {
                    return false;
                }
            default:
                return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (connService != null) {
            unbindService(connService);
        }
    }


    void newReserve(final String prod) {
        if (isNetworkAvailable() == false) {
            mensajered();
        } else {
            progress = new ProgressDialog(this);
            progress.setMessage(this.getString(R.string.spinner_conectando));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.setProgress(0);
            progress.show();
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String cabecera = sharedpreferences.getString(ConfigEnum.ticketHeader, null) + "\n";
            final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
            final String secret = sharedpreferences.getString(ConfigEnum.secretWordTerminal, BuildConfig.EP_SECRET_WORD);
            final String server = sharedpreferences.getString(ConfigEnum.serverUrlSMS, BuildConfig.EP_URL_API_BASE_SMS);
            final String turno = sharedpreferences.getString(ConfigEnum.workShiftKey, null);
            RequestQueue queue = Volley.newRequestQueue(this);  // this = context
            String url = server + "/reserva_v2.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                            final String hora = new SimpleDateFormat("HH:mm").format(new Date());
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
                                            //Toast.makeText(getBaseContext(), "TRANSACTION ACCEPTED", //Toast.LENGTH_SHORT).show();

                                            int ReservesCounter = sharedpreferences.getInt("reservesCount", 0);
                                            int Reserves = ReservesCounter + 1;
                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putInt("reservesCount", Reserves);
                                            editor.apply();
                                            Log.d("Reserves", String.valueOf(Reserves));
                                            litros = "Authorized Liters: " + getValue("max_liters", element2);
                                            final String plate = getValue("plate_number", element2);
                                            final String customerName = getValue("customer_name", element2);
                                            //SAVE PLATE TO LOCAL STORAGE
                                            if(plate != null){
                                                SharedPreferences.Editor localStorage = sharedpreferences.edit();
                                                localStorage.putString(codigo + ConfigEnum.plate, plate);
                                                localStorage.putString(codigo + ConfigEnum.customerName, customerName);
                                                localStorage.apply();
                                            }
                                            String producto = getValue("product_code", element2);
                                            switch (producto) {
                                                case "DIZ":
                                                    textoproducto = "Authorized Product: DIESEL";
                                                    break;
                                                case "ADB":
                                                    textoproducto = "Authorized Product: AD BLUE";
                                                    break;
                                                case "FOD":
                                                    textoproducto = "Authorized Product: RED DIESEL";
                                                    break;
                                            }

                                            //Comprobamos si esta activada la opcion de mostrar los euros en la configuracion del temrinal
                                            final boolean showEuros = sharedpreferences.getBoolean(ConfigEnum.showEuro, false);
                                            TextEuros = "";
                                            switch (prod) {
                                                case ProductIntEnum.DIESEL:
                                                    if (showEuros) {
                                                        dieselactual = sharedpreferences2.getString("dieselKey", "0.00");
                                                        TextEuros = "Authorized Euros: " + (Float.valueOf(getValue("max_liters", element2)) * Float.valueOf(dieselactual));
                                                    }
                                                    break;
                                                case ProductIntEnum.ADBLUE:
                                                    if (showEuros) {
                                                        adblueactual = sharedpreferences2.getString("adblueKey", "0.00");
                                                        TextEuros = "Authorized Euros: " + (Float.valueOf(getValue("max_liters", element2)) * Float.valueOf(adblueactual));
                                                    }
                                                    break;
                                                case ProductIntEnum.RED:
                                                    if (showEuros) {
                                                        reddieselactual = sharedpreferences2.getString("reddieselKey", "0.00");
                                                        TextEuros = "Authorized Euros: " + (Float.valueOf(getValue("max_liters", element2)) * Float.valueOf(reddieselactual));
                                                    }
                                                    break;
                                                case ProductIntEnum.BIODIESEL:
                                                    if (showEuros) {
                                                        biodieselactual = sharedpreferences2.getString("biodieselKey", "0.00");
                                                        TextEuros = "Authorized Euros: " + (Float.valueOf(getValue("max_liters", element2)) * Float.valueOf(biodieselactual));
                                                    }
                                                    break;
                                            }

                                            try {

                                                for (int g = 0; g < 1; g++) {

                                                    ThreadPoolManager.getInstance().executeTask(new Runnable() {

                                                        @Override
                                                        public void run() {
                                                            if (mBitmap == null) {
                                                                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                                            }
                                                            try {
                                                                msg = "TRANSACTION ACCEPTED";
                                                                msg += "\n";

                                                                woyouService.lineWrap(2, callback);
                                                                woyouService.setAlignment(1, callback);
                                                                woyouService.printBitmap(mBitmap, callback);
                                                                woyouService.setFontSize(24, callback);
                                                                woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                                                String pterminal = "Terminal: " + terminal + "\n\n";
                                                                woyouService.printTextWithFont(pterminal, "", 24, callback);
                                                                woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                                                woyouService.lineWrap(2, callback);
                                                                woyouService.setAlignment(0, callback);
                                                                woyouService.printTextWithFont(getString(R.string.plate) + ": " + plate + "\n", "", 30, callback);
                                                                woyouService.printTextWithFont(getString(R.string.customer_name) + ": " + customerName + "\n", "", 30, callback);
                                                                woyouService.printTextWithFont("Transaction Code: " + codigo + "\n", "", 30, callback);
                                                                woyouService.printTextWithFont(textoproducto + "\n", "", 28, callback);
                                                                woyouService.printTextWithFont(litros + "\n", "", 28, callback);
                                                                woyouService.printTextWithFont(TextEuros + "\n", "", 28, callback);
                                                                woyouService.printTextWithFont("\n\n\n\n", "", 24, callback);
                                                                woyouService.setAlignment(1, callback);
                                                                woyouService.printTextWithFont(msg, "", 36, callback);
                                                                woyouService.lineWrap(4, callback);
                                                            } catch (RemoteException e) {
                                                                // TODO Auto-generated catch block
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    });
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
                                            //Toast.makeText(getBaseContext(), "TRANSACTION REFUSED", //Toast.LENGTH_SHORT).show();
                                            codigoerror = getValue("error_code", element2);
                                            textoerror = getValue("error_description", element2);
                                            msg = "TRANSACTION REFUSED";
                                            msg += "\n";
                                            for (int g = 0; g < 1; g++) {

                                                ThreadPoolManager.getInstance().executeTask(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        if (mBitmap == null) {
                                                            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                                        }
                                                        try {
                                                            msg = "TRANSACTION REFUSED";
                                                            msg += "\n";

                                                            woyouService.lineWrap(2, callback);
                                                            woyouService.setAlignment(1, callback);
                                                            woyouService.printBitmap(mBitmap, callback);
                                                            woyouService.setFontSize(24, callback);
                                                            woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                                            String pterminal = "Terminal: " + terminal + "\n\n";
                                                            woyouService.printTextWithFont(pterminal, "", 24, callback);
                                                            woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                                            woyouService.lineWrap(2, callback);
                                                            woyouService.setAlignment(0, callback);
                                                            woyouService.printTextWithFont("Transaction Code: " + codigo + "\n", "", 30, callback);
                                                            woyouService.printTextWithFont("Error Code: " + codigoerror + "\n", "", 28, callback);
                                                            woyouService.printTextWithFont("Error: " + textoerror + "\n", "", 28, callback);
                                                            woyouService.printTextWithFont("\n", "", 24, callback);
                                                            woyouService.setAlignment(1, callback);
                                                            woyouService.printTextWithFont(msg, "", 36, callback);
                                                            woyouService.lineWrap(4, callback);
                                                        } catch (RemoteException e) {
                                                            // TODO Auto-generated catch block
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                });

                                                if (g == 0) {
                                                    Thread.sleep(3000);
                                                }
                                            }
                                        }
                                        salvaroperacion(TransactionTypeEnum.RESERVE, cabecera, terminal, fecha, hora, msg, codigo, textoproducto, "0", sharedpreferences.getString(codigo + ConfigEnum.plate, ""), "0", litros, "0", codigoerror, textoerror);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            final Thread t = new Thread() {
                                @Override
                                public void run() {
                                    try {
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
                            mensajetimeout();
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
                    params.put("turno", turno);
                    return params;
                }
            };

            int socketTimeout = 30000;
            int maxRetry = 0;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, maxRetry, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy);
            queue.add(postRequest);
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


    void ProductDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogoLayout = inflater.inflate(R.layout.products_dialog, null);
        TextView lbl_producto = (TextView) dialogoLayout.findViewById(R.id.labelcuantos);


        builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);

        builder.setView(dialogoLayout);

        final AlertDialog show = builder.show();

        Button alertButton1 = (Button) dialogoLayout.findViewById(R.id.btnDiesel);
        Button alertButton2 = (Button) dialogoLayout.findViewById(R.id.btnAdblue);
        Button alertButton3 = (Button) dialogoLayout.findViewById(R.id.btnReddiesel);
        Button alertButton4 = (Button) dialogoLayout.findViewById(R.id.btnBiodiesel);


        sharedpreferences2 = getSharedPreferences(MyPRECIOS, Context.MODE_PRIVATE);

        dieselactual = sharedpreferences2.getString("dieselKey", "0.00");
        adblueactual = sharedpreferences2.getString("adblueKey", "0.00");
        reddieselactual = sharedpreferences2.getString("reddieselKey", "0.00");
        biodieselactual = sharedpreferences2.getString("biodieselKey", "0.00");

        if (dieselactual.equals("0.00")) {
            //Diesel no tiene precio no lo muestro

            alertButton1.setVisibility(Button.INVISIBLE);
        }
        if (adblueactual.equals("0.00")) {
            //Adblue no tiene precio no lo muestro

            alertButton2.setVisibility(Button.INVISIBLE);
        }
        if (reddieselactual.equals("0.00")) {
            //Diesel Rojo no tiene precio no lo muestro

            alertButton3.setVisibility(Button.INVISIBLE);
        }
        if (biodieselactual.equals("0.00")) {
            //Bio Diesel no tiene precio no lo muestro

            alertButton4.setVisibility(Button.INVISIBLE);
        }

        alertButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(producto, "1");
                editor.apply();
                show.dismiss();
                newReserve(ProductIntEnum.DIESEL);

            }
        });

        alertButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(producto, "13");
                editor.apply();
                show.dismiss();
                newReserve(ProductIntEnum.ADBLUE);

            }
        });

        alertButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(producto, "15");
                editor.apply();
                show.dismiss();
                newReserve(ProductIntEnum.RED);

            }
        });

        alertButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(producto, "14");
                editor.apply();
                show.dismiss();
                newReserve(ProductIntEnum.BIODIESEL);

            }
        });
    }

    void CampilloPreReserve() {
        if (!isNetworkAvailable()) {
            mensajered();
        } else {
            progress = new ProgressDialog(this);
            progress.setMessage(this.getString(R.string.spinner_conectando));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.setProgress(0);
            progress.show();
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String cabecera = sharedpreferences.getString(ConfigEnum.ticketHeader, null) + "\n";
            final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
            final String secret = sharedpreferences.getString(ConfigEnum.secretWordTerminal, BuildConfig.EP_SECRET_WORD);
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = ApiCampilloURI + BuildConfig.EP_GLOBALPAY_PRERESERVE;
            Log.e(TAG, "Uri: " + url);
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                            final String hora = new SimpleDateFormat("HH:mm").format(new Date());
                            try {
                                Log.e(TAG, "Response: " + response);
                                JSONObject jsonObj = new JSONObject(response);
                                String Success = jsonObj.getString("success");

                                if (Success.equals("false")) {
                                    Thread.sleep(1000);
                                    progress.dismiss();
                                    //Si la transaccion de campillo no se encuentra la buscamos en la API generica
                                    globalPayPreReserve();
                                } else {
                                    //Toast.makeText(getBaseContext(), "TRANSACTION ACCEPTED", //Toast.LENGTH_SHORT).show();

                                    int ReservesCounter = sharedpreferences.getInt("reservesCount", 0);
                                    int Reserves = ReservesCounter + 1;
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putInt("reservesCount", Reserves);
                                    editor.apply();

                                    final String Expedient = jsonObj.getString("expedient");

                                    final JSONArray AuthProducts = jsonObj.getJSONArray("auth_products");

                                    for (int i = 0; i < AuthProducts.length(); i++) {
                                        JSONObject c = AuthProducts.getJSONObject(i);
                                        AuthDiesel = c.getDouble("diesel");
                                        AuthAdBlue = c.getDouble("adblue");
                                        AuthRedDiesel = c.getDouble("red");
                                        AuthGas = c.getDouble("gas");
                                        AuthMoney = c.getDouble("money");
                                    }
                                    KmsRequired = jsonObj.getInt("kms_required");
                                    HoursRequired = jsonObj.getInt("hours_required");

                                    sharedCampillo = getSharedPreferences(LastCampilloAuth, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = sharedCampillo.edit();
                                    editor2.putString("code", codigo);
                                    editor2.putString("Diesel", AuthDiesel.toString());
                                    editor2.putString("AdBlue", AuthAdBlue.toString());
                                    editor2.putString("RedDiesel", AuthRedDiesel.toString());
                                    editor2.putString("Money", AuthMoney.toString());
                                    editor2.apply();

                                    try {
                                        for (int g = 0; g < 1; g++) {

                                            ThreadPoolManager.getInstance().executeTask(new Runnable() {

                                                @Override
                                                public void run() {
                                                    if (mBitmap == null) {
                                                        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                                    }
                                                    try {
                                                        msg = "NO VALIDA PARA\nREPOSTAJE";
                                                        msg += "\n";

                                                        woyouService.lineWrap(2, callback);
                                                        woyouService.setAlignment(1, callback);
                                                        woyouService.printBitmap(mBitmap, callback);
                                                        woyouService.setFontSize(24, callback);
                                                        woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                                        String pterminal = "Terminal: " + terminal + "\n\n";
                                                        woyouService.printTextWithFont(pterminal, "", 24, callback);
                                                        woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                                        woyouService.lineWrap(2, callback);
                                                        woyouService.setAlignment(0, callback);
                                                        woyouService.printTextWithFont("TRX Code: " + codigo + "\n", "", 30, callback);
                                                        woyouService.printTextWithFont("Expedient: " + Expedient + "\n\n", "", 30, callback);
                                                        woyouService.printTextWithFont("PRODUCTOS AUTORIZADOS:" + "\n", "", 30, callback);
                                                        if (AuthDiesel != 0) {
                                                            woyouService.printTextWithFont("DIESEL: " + AuthDiesel + " Liters\n", "", 28, callback);
                                                            if (KmsRequired != 0) {
                                                                msg += "\n**ATENCION**\n\nEL CHOFER TENDRA QUE INFORMAR LOS KILOMETROS AL FINALIZAR LA OPERACION";
                                                            }
                                                        }
                                                        if (AuthAdBlue != 0) {
                                                            woyouService.printTextWithFont("AD BLUE: " + AuthAdBlue + " Liters\n", "", 28, callback);
                                                        }
                                                        if (AuthRedDiesel != 0) {
                                                            woyouService.printTextWithFont("RED DIESEL: " + AuthRedDiesel + " Liters\n", "", 28, callback);
                                                            if (HoursRequired != 0) {
                                                                msg += "\n**ATENCION**\n\nEL CHOFER TENDRA QUE INFORMAR LAS HORAS DEL FRIGO AL FINALIZAR LA OPERACION\n";
                                                            }
                                                        }
                                                        if (AuthGas != 0) {
                                                            woyouService.printTextWithFont("GAS: " + AuthGas + " Kilos\n", "", 28, callback);
                                                        }
                                                        if (AuthMoney != 0) {
                                                            woyouService.printTextWithFont("MONEY: " + AuthMoney + " Euros\n", "", 28, callback);
                                                        }

                                                        woyouService.printTextWithFont("\n", "", 24, callback);
                                                        woyouService.setAlignment(1, callback);
                                                        woyouService.printTextWithFont(msg, "", 36, callback);
                                                        woyouService.lineWrap(4, callback);
                                                    } catch (RemoteException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }

                                                }
                                            });
                                            if (g == 0) {
                                                Thread.sleep(2000);
                                            }
                                        }
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    //Si existe mas de un tipo de transacción en la reserva lanzamos el dialog de seleccion
                                    LayoutInflater inflater = getLayoutInflater();
                                    View PreReserveLayout = inflater.inflate(R.layout.prereserve_dialog, null);
                                    builder2 = new AlertDialog.Builder(PinPadActivity.this);

                                    builder2.setCancelable(false);

                                    builder2.setView(PreReserveLayout);

                                    final AlertDialog show = builder2.show();

                                    Button btnRefuel = (Button) PreReserveLayout.findViewById(R.id.btnRefuel);
                                    Button btnMoney = (Button) PreReserveLayout.findViewById(R.id.btnMoney);
                                    Button btnAll = (Button) PreReserveLayout.findViewById(R.id.btnAll);
                                    Button btnCancel = (Button) PreReserveLayout.findViewById(R.id.btnCancel);

                                    boolean authRefuel = AuthDiesel > 0 || AuthAdBlue > 0 || AuthRedDiesel > 0 || AuthGas > 0;
                                    boolean authMoney = AuthMoney > 0;

                                    if (!authRefuel) {
                                        btnRefuel.setVisibility(GONE);
                                    }

                                    if (!authMoney) {
                                        btnMoney.setVisibility(GONE);
                                    }

                                    if (!authMoney || !authRefuel) {
                                        btnAll.setVisibility(GONE);
                                    }


                                    btnRefuel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            show.dismiss();
                                            CampilloReserve(String.valueOf(TransactionTypeEnum.REFUEL));
                                        }
                                    });

                                    btnMoney.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            show.dismiss();
                                            CampilloReserve(String.valueOf(TransactionTypeEnum.AMOUNT));
                                        }
                                    });

                                    btnAll.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            show.dismiss();
                                            CampilloReserve(null);
                                        }
                                    });

                                    btnCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            show.dismiss();
                                            PinPadActivity.this.finish();
                                        }
                                    });
                                }

                            } catch (final JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


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
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mensajetimeout();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("terminal", terminal);
                    params.put("code", codigo);
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

    }

    //Llamada a la API generica de globalpay
    void globalPayPreReserve() {
        if (!isNetworkAvailable()) {
            mensajered();
        } else {
            progress = new ProgressDialog(this);
            progress.setMessage(this.getString(R.string.spinner_conectando));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.setProgress(0);
            progress.show();
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String cabecera = sharedpreferences.getString(ConfigEnum.ticketHeader, null) + "\n";
            final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
            final String secret = sharedpreferences.getString(ConfigEnum.secretWordTerminal, BuildConfig.EP_SECRET_WORD);
            final String turno = sharedpreferences.getString(ConfigEnum.workShiftKey, null);
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = ApiGPayUrl + BuildConfig.EP_GLOBALPAY_PRERESERVE;
            Log.e(TAG, "Uri: " + url);
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                            final String hora = new SimpleDateFormat("HH:mm").format(new Date());
                            try {
                                Log.e(TAG, "Response: " + response);
                                JSONObject jsonObj = new JSONObject(response);
                                String Success = jsonObj.getString("success");

                                if (Success.equals("false")) {
                                    //Toast.makeText(getBaseContext(), "TRANSACTION REFUSED", //Toast.LENGTH_SHORT).show();
                                    codigoerror = jsonObj.getString("error_code");
                                    textoerror = jsonObj.getString("error_description");
                                    msg = "TRANSACTION REFUSED";
                                    msg += "\n";
                                    for (int g = 0; g < 1; g++) {

                                        ThreadPoolManager.getInstance().executeTask(new Runnable() {

                                            @Override
                                            public void run() {
                                                if (mBitmap == null) {
                                                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                                }
                                                try {
                                                    msg = "TRANSACTION REFUSED";
                                                    msg += "\n";

                                                    woyouService.lineWrap(2, callback);
                                                    woyouService.setAlignment(1, callback);
                                                    woyouService.printBitmap(mBitmap, callback);
                                                    woyouService.setFontSize(24, callback);
                                                    woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                                    String pterminal = "Terminal: " + terminal + "\n\n";
                                                    woyouService.printTextWithFont(pterminal, "", 24, callback);
                                                    woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                                    woyouService.lineWrap(2, callback);
                                                    woyouService.setAlignment(0, callback);
                                                    woyouService.printTextWithFont("TRX Code: " + codigo + "\n", "", 30, callback);
                                                    woyouService.printTextWithFont("Error Code: " + codigoerror + "\n", "", 28, callback);
                                                    woyouService.printTextWithFont("Error: " + textoerror + "\n", "", 28, callback);
                                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                                    woyouService.setAlignment(1, callback);
                                                    woyouService.printTextWithFont(msg, "", 36, callback);
                                                    woyouService.lineWrap(4, callback);
                                                } catch (Exception e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                }

                                            }
                                        });

                                        if (g == 0) {
                                            Thread.sleep(2000);
                                        }
                                    }


                                } else {
                                    //Toast.makeText(PinPadActivity.this, "TRANSACTION ACCEPTED", //Toast.LENGTH_SHORT).show();

                                    int ReservesCounter = sharedpreferences.getInt("reservesCount", 0);
                                    int Reserves = ReservesCounter + 1;
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putInt("reservesCount", Reserves);
                                    editor.apply();

                                    final JSONArray transactionIds = jsonObj.getJSONArray("transaction_ids");
                                    final Integer transactionId = transactionIds.getInt(0);//GET FIRST TRASNSACTION ID
                                    final JSONArray AuthProducts = jsonObj.getJSONArray("auth_products");

                                    for (int i = 0; i < AuthProducts.length(); i++) {
                                        JSONObject c = AuthProducts.getJSONObject(i);
                                        AuthDiesel = c.getDouble("diesel");
                                        AuthAdBlue = c.getDouble("adblue");
                                        AuthRedDiesel = c.getDouble("red");
                                        AuthGas = c.getDouble("gas");
                                        AuthMoney = c.getDouble("money");
                                    }

                                    KmsRequired = jsonObj.getInt("kms_required");
                                    HoursRequired = jsonObj.getInt("hours_required");
                                    final String verifyCode = jsonObj.getString("verify_code");

                                    sharedCampillo = getSharedPreferences(LastCampilloAuth, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = sharedCampillo.edit();
                                    editor2.putString("code", codigo);
                                    editor2.putString("Diesel", AuthDiesel.toString());
                                    editor2.putString("AdBlue", AuthAdBlue.toString());
                                    editor2.putString("RedDiesel", AuthRedDiesel.toString());
                                    editor2.putString("Money", AuthMoney.toString());
                                    editor2.apply();

                                    try {
                                        for (int g = 0; g < 1; g++) {

                                            ThreadPoolManager.getInstance().executeTask(new Runnable() {

                                                @Override
                                                public void run() {
                                                    if (mBitmap == null) {
                                                        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                                    }
                                                    try {
                                                        msg = "NO VALIDA PARA\nREPOSTAJE";
                                                        msg += "\n";

                                                        woyouService.lineWrap(2, callback);
                                                        woyouService.setAlignment(1, callback);
                                                        woyouService.printBitmap(mBitmap, callback);
                                                        woyouService.setFontSize(24, callback);
                                                        woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                                        String pterminal = "Terminal: " + terminal + "\n\n";
                                                        woyouService.printTextWithFont(pterminal, "", 24, callback);
                                                        woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                                        woyouService.lineWrap(2, callback);
                                                        woyouService.setAlignment(0, callback);
                                                        woyouService.printTextWithFont("TRX Code: " + codigo + "\n", "", 30, callback);
                                                        //woyouService.printTextWithFont("Expedient: " + Expedient + "\n\n", "", 30, callback);
                                                        woyouService.printTextWithFont("PRODUCTOS AUTORIZADOS:" + "\n", "", 30, callback);
                                                        if (AuthDiesel != 0) {
                                                            woyouService.printTextWithFont("DIESEL: " + AuthDiesel + " Liters\n", "", 28, callback);
                                                            if (KmsRequired != 0) {
                                                                msg += "\n**ATENCION**\n\nEL CHOFER TENDRA QUE INFORMAR LOS KILOMETROS AL FINALIZAR LA OPERACION";
                                                            }
                                                        }
                                                        if (AuthAdBlue != 0) {
                                                            woyouService.printTextWithFont("AD BLUE: " + AuthAdBlue + " Liters\n", "", 28, callback);
                                                        }
                                                        if (AuthRedDiesel != 0) {
                                                            woyouService.printTextWithFont("RED DIESEL: " + AuthRedDiesel + " Liters\n", "", 28, callback);
                                                            if (HoursRequired != 0) {
                                                                msg += "\n**ATENCION**\n\nEL CHOFER TENDRA QUE INFORMAR LAS HORAS DEL FRIGO AL FINALIZAR LA OPERACION\n";
                                                            }
                                                        }
                                                        if (AuthGas != 0) {
                                                            woyouService.printTextWithFont("GAS: " + AuthGas + " Kilos\n", "", 28, callback);
                                                        }
                                                        if (AuthMoney != 0) {
                                                            woyouService.printTextWithFont("MONEY: " + AuthMoney + " Euros\n", "", 28, callback);
                                                        }

                                                        woyouService.printTextWithFont("\n", "", 24, callback);
                                                        woyouService.setAlignment(1, callback);
                                                        woyouService.printTextWithFont(msg, "", 36, callback);
                                                        woyouService.lineWrap(4, callback);
                                                    } catch (Exception e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }

                                                }
                                            });
                                            if (g == 0) {
                                                Thread.sleep(2000);
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    //Si existe mas de un tipo de transacción en la reserva lanzamos el dialog de seleccion
                                    LayoutInflater inflater = getLayoutInflater();
                                    View PreReserveLayout = inflater.inflate(R.layout.prereserve_dialog, null);
                                    builder2 = new AlertDialog.Builder(PinPadActivity.this);

                                    builder2.setCancelable(false);

                                    builder2.setView(PreReserveLayout);

                                    final AlertDialog show = builder2.show();

                                    Button btnRefuel = (Button) PreReserveLayout.findViewById(R.id.btnRefuel);
                                    Button btnMoney = (Button) PreReserveLayout.findViewById(R.id.btnMoney);
                                    Button btnAll = (Button) PreReserveLayout.findViewById(R.id.btnAll);
                                    Button btnCancel = (Button) PreReserveLayout.findViewById(R.id.btnCancel);

                                    boolean authRefuel = AuthDiesel > 0 || AuthAdBlue > 0 || AuthRedDiesel > 0 || AuthGas > 0;
                                    boolean authMoney = AuthMoney > 0;

                                    if (!authRefuel) {
                                        btnRefuel.setVisibility(GONE);
                                    }

                                    if (!authMoney) {
                                        btnMoney.setVisibility(GONE);
                                    }

                                    if (!authMoney || !authRefuel) {
                                        btnAll.setVisibility(GONE);
                                    }


                                    btnRefuel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            show.dismiss();
                                            if (!verifyCode.isEmpty()) {
                                                showProgressDialog(pinpadActivity, pinpadActivity.getString(R.string.spinner_conectando));
                                                sendTrxVerifyCode(transactionId, new GpCallback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        mProgressDialog.dismiss();
                                                        showInputVerifyCode(String.valueOf(TransactionTypeEnum.REFUEL), transactionId, verifyCode);
                                                    }

                                                    @Override
                                                    public void onError() {
                                                        mProgressDialog.dismiss();
                                                        //Toast.makeText(getBaseContext(), getResources().getString(R.string.verify_send_code_error), //Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                globalPayReserve(String.valueOf(TransactionTypeEnum.REFUEL));
                                            }
                                        }
                                    });

                                    btnMoney.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            show.dismiss();
                                            globalPayReserve(String.valueOf(TransactionTypeEnum.AMOUNT));
                                        }
                                    });

                                    btnAll.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            show.dismiss();
                                            if (!verifyCode.isEmpty()) {
                                                showProgressDialog(pinpadActivity, pinpadActivity.getString(R.string.spinner_conectando));
                                                sendTrxVerifyCode(transactionId, new GpCallback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        mProgressDialog.dismiss();
                                                        showInputVerifyCode(null, transactionId, verifyCode);
                                                    }

                                                    @Override
                                                    public void onError() {
                                                        mProgressDialog.dismiss();
                                                        //Toast.makeText(getBaseContext(), getResources().getString(R.string.verify_send_code_error), //Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                globalPayReserve(null);
                                            }
                                        }
                                    });

                                    btnCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            show.dismiss();
                                            PinPadActivity.this.finish();
                                        }
                                    });
                                }

                            } catch (final JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


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
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mensajetimeout();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    Log.e(TAG, "Setting body PreReserveCampillo");
                    Log.e(TAG, "Setting body PreReserveCampillo " + terminal + " " + codigo);
                    params.put("terminal", terminal);
                    params.put("turno", turno);
                    params.put("code", codigo);
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

    }

    void showInputVerifyCode(final String trxType, final Integer transaction_id, final String verifyCode) {
        LayoutInflater inflater = getLayoutInflater();
        View verifyTrxCodeLayout = inflater.inflate(R.layout.verify_trx_code, null);
        builder = new AlertDialog.Builder(PinPadActivity.this);
        builder.setCancelable(false);
        builder.setView(verifyTrxCodeLayout);

        final AlertDialog show = builder.show();

        final EditText verifyCodeEdit = (EditText) verifyTrxCodeLayout.findViewById(R.id.verifyCode);
        Button btnCancel = (Button) verifyTrxCodeLayout.findViewById(R.id.btnCancel);
        Button btnAccept = (Button) verifyTrxCodeLayout.findViewById(R.id.btnAccept);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyTrxCode(transaction_id, verifyCodeEdit.getText().toString(), new GpCallback() {
                    @Override
                    public void onSuccess() {
                        show.dismiss();
                        globalPayReserve(trxType);
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        });
    }

    void sendTrxVerifyCode(final Integer transaction_id, final GpCallback callback) {
        final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
        FileApi service = RetroClient.getApiService(ApiGPayUrl, BuildConfig.GLOBALPAY_TOKEN);
        Call<SampleResponse> validateTransaction = service.sendTransactionVerifyCode(transaction_id, terminal);

        validateTransaction.enqueue(new Callback<SampleResponse>() {
            @Override
            public void onResponse(Call<SampleResponse> call, retrofit2.Response<SampleResponse> response) {
                SampleResponse data = response.body();
                if (data.success) {
                    callback.onSuccess();
                } else {
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.verify_code_error), //Toast.LENGTH_SHORT).show();
                    callback.onError();
                }
            }

            @Override
            public void onFailure(Call<SampleResponse> call, Throwable t) {
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.verify_code_error), //Toast.LENGTH_SHORT).show();
                callback.onError();
            }
        });
    }

    boolean verifyTrxCode(final Integer transaction_id, final String verifyCode, final GpCallback callback) {
        FileApi service = RetroClient.getApiService(ApiGPayUrl, BuildConfig.GLOBALPAY_TOKEN);
        Call<SampleResponse> validateTransaction = service.verifyTransactionCode(transaction_id, verifyCode);

        validateTransaction.enqueue(new Callback<SampleResponse>() {
            @Override
            public void onResponse(Call<SampleResponse> call, retrofit2.Response<SampleResponse> response) {
                SampleResponse data = response.body();
                if (data.success) {
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.verify_code_success), //Toast.LENGTH_SHORT).show();
                    callback.onSuccess();
                } else {
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.verify_code_error), //Toast.LENGTH_SHORT).show();
                    callback.onError();
                }
            }

            @Override
            public void onFailure(Call<SampleResponse> call, Throwable t) {
                callback.onError();
            }
        });

        return true;
    }

    void CReserve(String trx_type) {

    }

    void CampilloReserve(final String trx_type) {
        //void CampilloReserve(){
        if (!isNetworkAvailable()) {
            mensajered();
        } else {
            //contadortimeout = new ContadorTimeOut(15000, 10000);
            //contadortimeout.start();
            progress = new ProgressDialog(this);
            progress.setMessage(this.getString(R.string.spinner_conectando));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.setProgress(0);
            progress.show();
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String cabecera = sharedpreferences.getString(ConfigEnum.ticketHeader, null) + "\n";
            final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
            final String secret = sharedpreferences.getString(ConfigEnum.secretWordTerminal, BuildConfig.EP_SECRET_WORD);
            final String server = sharedpreferences.getString(ConfigEnum.serverUrlSMS, BuildConfig.EP_URL_API_BASE_SMS);
            final String turno = sharedpreferences.getString(ConfigEnum.workShiftKey, null);
            RequestQueue queue = Volley.newRequestQueue(this);
            final String Checksum = md5(terminal + secret + codigo);
            String url = ApiCampilloURI + BuildConfig.EP_GLOBALPAY_RESERVE + Checksum;
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                            final String hora = new SimpleDateFormat("HH:mm").format(new Date());
                            try {
                                Log.e(TAG, "Response: " + response);
                                JSONObject jsonObj = new JSONObject(response);
                                String Success = jsonObj.getString("success");

                                if (Success.equals("false")) {
                                    //Toast.makeText(getBaseContext(), "TRANSACTION REFUSED", //Toast.LENGTH_SHORT).show();
                                    codigoerror = jsonObj.getString("error_code");
                                    textoerror = jsonObj.getString("error_description");
                                    msg = "TRANSACTION REFUSED";
                                    msg += "\n";
                                    for (int g = 0; g < 1; g++) {

                                        ThreadPoolManager.getInstance().executeTask(new Runnable() {

                                            @Override
                                            public void run() {
                                                if (mBitmap == null) {
                                                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                                }
                                                try {
                                                    msg = "TRANSACTION REFUSED";
                                                    msg += "\n";

                                                    woyouService.lineWrap(2, callback);
                                                    woyouService.setAlignment(1, callback);
                                                    woyouService.printBitmap(mBitmap, callback);
                                                    woyouService.setFontSize(24, callback);
                                                    woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                                    String pterminal = "Terminal: " + terminal + "\n\n";
                                                    woyouService.printTextWithFont(pterminal, "", 24, callback);
                                                    woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                                    woyouService.lineWrap(2, callback);
                                                    woyouService.setAlignment(0, callback);
                                                    woyouService.printTextWithFont("TRX Code: " + codigo + "\n", "", 30, callback);
                                                    woyouService.printTextWithFont("Error Code: " + codigoerror + "\n", "", 28, callback);
                                                    woyouService.printTextWithFont("Error: " + textoerror + "\n", "", 28, callback);
                                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                                    woyouService.setAlignment(1, callback);
                                                    woyouService.printTextWithFont(msg, "", 36, callback);
                                                    woyouService.lineWrap(4, callback);
                                                } catch (RemoteException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                }

                                            }
                                        });

                                        if (g == 0) {
                                            Thread.sleep(3000);
                                        }
                                    }


                                } else {
                                    //Toast.makeText(getBaseContext(), "TRANSACTION ACCEPTED", //Toast.LENGTH_SHORT).show();

                                    int ReservesCounter = sharedpreferences.getInt("reservesCount", 0);
                                    int Reserves = ReservesCounter + 1;
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putInt("reservesCount", Reserves);
                                    editor.apply();

                                    final String Expedient = jsonObj.getString("expedient");

                                    final JSONArray AuthProducts = jsonObj.getJSONArray("auth_products");

                                    for (int i = 0; i < AuthProducts.length(); i++) {
                                        JSONObject c = AuthProducts.getJSONObject(i);
                                        AuthDiesel = c.getDouble("diesel");
                                        AuthAdBlue = c.getDouble("adblue");
                                        AuthRedDiesel = c.getDouble("red");
                                        AuthMoney = c.getDouble("money");
                                    }
                                    KmsRequired = jsonObj.getInt("kms_required");
                                    HoursRequired = jsonObj.getInt("hours_required");

                                    sharedCampillo = getSharedPreferences(LastCampilloAuth, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = sharedCampillo.edit();
                                    editor2.putString("code", codigo);
                                    editor2.putString("Diesel", AuthDiesel.toString());
                                    editor2.putString("AdBlue", AuthAdBlue.toString());
                                    editor2.putString("RedDiesel", AuthRedDiesel.toString());
                                    editor2.putString("Money", AuthMoney.toString());
                                    editor2.apply();

                                    try {

                                        for (int g = 0; g < 1; g++) {

                                            ThreadPoolManager.getInstance().executeTask(new Runnable() {

                                                @Override
                                                public void run() {
                                                    if (mBitmap == null) {
                                                        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                                    }
                                                    try {
                                                        msg = "TRANSACTION ACCEPTED";
                                                        msg += "\n";

                                                        woyouService.lineWrap(2, callback);
                                                        woyouService.setAlignment(1, callback);
                                                        woyouService.printBitmap(mBitmap, callback);
                                                        woyouService.setFontSize(24, callback);
                                                        woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                                        String pterminal = "Terminal: " + terminal + "\n\n";
                                                        woyouService.printTextWithFont(pterminal, "", 24, callback);
                                                        woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                                        woyouService.lineWrap(2, callback);
                                                        woyouService.setAlignment(0, callback);
                                                        woyouService.printTextWithFont("TRX Code: " + codigo + "\n", "", 30, callback);
                                                        woyouService.printTextWithFont("Expedient: " + Expedient + "\n\n", "", 30, callback);
                                                        woyouService.printTextWithFont("PRODUCTOS AUTORIZADOS:" + "\n", "", 30, callback);
                                                        if (AuthDiesel != 0) {
                                                            woyouService.printTextWithFont("DIESEL: " + AuthDiesel + " Liters\n", "", 28, callback);
                                                            if (KmsRequired != 0) {
                                                                msg += "\n**ATENCION**\n\nEL CHOFER TENDRA QUE INFORMAR LOS KILOMETROS AL FINALIZAR LA OPERACION";
                                                            }
                                                        }
                                                        if (AuthAdBlue != 0) {
                                                            woyouService.printTextWithFont("AD BLUE: " + AuthAdBlue + " Liters\n", "", 28, callback);
                                                        }
                                                        if (AuthRedDiesel != 0) {
                                                            woyouService.printTextWithFont("RED DIESEL: " + AuthRedDiesel + " Liters\n", "", 28, callback);
                                                            if (HoursRequired != 0) {
                                                                msg += "\n**ATENCION**\n\nEL CHOFER TENDRA QUE INFORMAR LAS HORAS DEL FRIGO AL FINALIZAR LA OPERACION\n";
                                                            }
                                                        }
                                                        if (AuthGas != 0) {
                                                            woyouService.printTextWithFont("GAS: " + AuthGas + " Kilos\n", "", 28, callback);
                                                        }
                                                        if (AuthMoney != 0) {
                                                            woyouService.printTextWithFont("MONEY: " + AuthMoney + " Euros\n", "", 28, callback);
                                                        }

                                                        woyouService.printTextWithFont("\n", "", 24, callback);
                                                        woyouService.setAlignment(1, callback);
                                                        woyouService.printTextWithFont(msg, "", 36, callback);
                                                        woyouService.lineWrap(4, callback);
                                                    } catch (RemoteException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }

                                                }
                                            });
                                            if (g == 0) {
                                                Thread.sleep(2000);
                                            }
                                        }
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    //Si la transacción que estamos intentando finalizar es un tipo AMOUNT lanzamos directamente el proceso de firma y finalziado de la misma
                                    if (trx_type == String.valueOf(TransactionTypeEnum.AMOUNT)) {
                                        CheckCampilloTrx();
                                    }
                                }

                            } catch (final JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                            final Thread t = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        //contadortimeout.cancel();
                                        sleep(1000);
                                        progress.dismiss();
                                        if (trx_type != String.valueOf(TransactionTypeEnum.AMOUNT)) {
                                            PinPadActivity.this.finish();
                                        }
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
                            mensajetimeout();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("terminal", terminal);
                    params.put("turno", turno);
                    params.put("code", codigo);
                    if (trx_type != null) {
                        params.put("transaction_type", trx_type);
                    }
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

    }

    void globalPayReserve(final String trx_type) {
        if (!isNetworkAvailable()) {
            mensajered();
        } else {
            progress = new ProgressDialog(this);
            progress.setMessage(this.getString(R.string.spinner_conectando));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.setProgress(0);
            progress.show();
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String cabecera = sharedpreferences.getString(ConfigEnum.ticketHeader, null) + "\n";
            final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
            final String secret = sharedpreferences.getString(ConfigEnum.secretWordTerminal, BuildConfig.EP_SECRET_WORD);
            final String turno = sharedpreferences.getString(ConfigEnum.workShiftKey, null);
            RequestQueue queue = Volley.newRequestQueue(this);
            final String Checksum = md5(terminal + secret + codigo);
            String url = ApiGPayUrl + BuildConfig.EP_GLOBALPAY_RESERVE + Checksum;
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                            final String hora = new SimpleDateFormat("HH:mm").format(new Date());
                            try {
                                Log.e(TAG, "Response: " + response);
                                JSONObject jsonObj = new JSONObject(response);
                                String Success = jsonObj.getString("success");

                                if (Success.equals("false")) {
                                    //Toast.makeText(getBaseContext(), "TRANSACTION REFUSED", //Toast.LENGTH_SHORT).show();
                                    codigoerror = jsonObj.getString("error_code");
                                    textoerror = jsonObj.getString("error_description");
                                    msg = "TRANSACTION REFUSED";
                                    msg += "\n";
                                    for (int g = 0; g < 1; g++) {

                                        ThreadPoolManager.getInstance().executeTask(new Runnable() {

                                            @Override
                                            public void run() {
                                                if (mBitmap == null) {
                                                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                                }
                                                try {
                                                    msg = "TRANSACTION REFUSED";
                                                    msg += "\n";

                                                    woyouService.lineWrap(2, callback);
                                                    woyouService.setAlignment(1, callback);
                                                    woyouService.printBitmap(mBitmap, callback);
                                                    woyouService.setFontSize(24, callback);
                                                    woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                                    String pterminal = "Terminal: " + terminal + "\n\n";
                                                    woyouService.printTextWithFont(pterminal, "", 24, callback);
                                                    woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                                    woyouService.lineWrap(2, callback);
                                                    woyouService.setAlignment(0, callback);
                                                    woyouService.printTextWithFont("TRX Code: " + codigo + "\n", "", 30, callback);
                                                    woyouService.printTextWithFont("Error Code: " + codigoerror + "\n", "", 28, callback);
                                                    woyouService.printTextWithFont("Error: " + textoerror + "\n", "", 28, callback);
                                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                                    woyouService.setAlignment(1, callback);
                                                    woyouService.printTextWithFont(msg, "", 36, callback);
                                                    woyouService.lineWrap(4, callback);
                                                } catch (RemoteException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                }

                                            }
                                        });

                                        if (g == 0) {
                                            Thread.sleep(3000);
                                        }
                                    }


                                } else {
                                    //Toast.makeText(getBaseContext(), "TRANSACTION ACCEPTED", //Toast.LENGTH_SHORT).show();

                                    int ReservesCounter = sharedpreferences.getInt("reservesCount", 0);
                                    int Reserves = ReservesCounter + 1;
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putInt("reservesCount", Reserves);
                                    editor.apply();

                                    //final String Expedient = jsonObj.getString("expedient");

                                    final JSONArray AuthProducts = jsonObj.getJSONArray("auth_products");

                                    for (int i = 0; i < AuthProducts.length(); i++) {
                                        JSONObject c = AuthProducts.getJSONObject(i);
                                        AuthDiesel = c.getDouble("diesel");
                                        AuthAdBlue = c.getDouble("adblue");
                                        AuthRedDiesel = c.getDouble("red");
                                        AuthMoney = c.getDouble("money");
                                    }
                                    KmsRequired = jsonObj.getInt("kms_required");
                                    HoursRequired = jsonObj.getInt("hours_required");

                                    sharedCampillo = getSharedPreferences(LastCampilloAuth, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = sharedCampillo.edit();
                                    editor2.putString("code", codigo);
                                    editor2.putString("Diesel", AuthDiesel.toString());
                                    editor2.putString("AdBlue", AuthAdBlue.toString());
                                    editor2.putString("RedDiesel", AuthRedDiesel.toString());
                                    editor2.putString("Money", AuthMoney.toString());
                                    editor2.apply();

                                    try {

                                        for (int g = 0; g < 1; g++) {

                                            ThreadPoolManager.getInstance().executeTask(new Runnable() {

                                                @Override
                                                public void run() {
                                                    if (mBitmap == null) {
                                                        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                                    }
                                                    try {
                                                        msg = "TRANSACTION ACCEPTED";
                                                        msg += "\n";

                                                        woyouService.lineWrap(2, callback);
                                                        woyouService.setAlignment(1, callback);
                                                        woyouService.printBitmap(mBitmap, callback);
                                                        woyouService.setFontSize(24, callback);
                                                        woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                                        String pterminal = "Terminal: " + terminal + "\n\n";
                                                        woyouService.printTextWithFont(pterminal, "", 24, callback);
                                                        woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                                        woyouService.lineWrap(2, callback);
                                                        woyouService.setAlignment(0, callback);
                                                        woyouService.printTextWithFont("TRX Code: " + codigo + "\n", "", 30, callback);
                                                        //woyouService.printTextWithFont("Expedient: " + Expedient + "\n\n", "", 30, callback);
                                                        woyouService.printTextWithFont("PRODUCTOS AUTORIZADOS:" + "\n", "", 30, callback);
                                                        if (AuthDiesel != 0) {
                                                            woyouService.printTextWithFont("DIESEL: " + AuthDiesel + " Liters\n", "", 28, callback);
                                                            if (KmsRequired != 0) {
                                                                msg += "\n**ATENCION**\n\nEL CHOFER TENDRA QUE INFORMAR LOS KILOMETROS AL FINALIZAR LA OPERACION";
                                                            }
                                                        }
                                                        if (AuthAdBlue != 0) {
                                                            woyouService.printTextWithFont("AD BLUE: " + AuthAdBlue + " Liters\n", "", 28, callback);
                                                        }
                                                        if (AuthRedDiesel != 0) {
                                                            woyouService.printTextWithFont("RED DIESEL: " + AuthRedDiesel + " Liters\n", "", 28, callback);
                                                            if (HoursRequired != 0) {
                                                                msg += "\n**ATENCION**\n\nEL CHOFER TENDRA QUE INFORMAR LAS HORAS DEL FRIGO AL FINALIZAR LA OPERACION\n";
                                                            }
                                                        }
                                                        if (AuthGas != 0) {
                                                            woyouService.printTextWithFont("GAS: " + AuthGas + " Kilos\n", "", 28, callback);
                                                        }
                                                        if (AuthMoney != 0) {
                                                            woyouService.printTextWithFont("MONEY: " + AuthMoney + " Euros\n", "", 28, callback);
                                                        }

                                                        woyouService.printTextWithFont("\n", "", 24, callback);
                                                        woyouService.setAlignment(1, callback);
                                                        woyouService.printTextWithFont(msg, "", 36, callback);
                                                        woyouService.lineWrap(4, callback);
                                                    } catch (Exception e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }

                                                }
                                            });
                                            if (g == 0) {
                                                Thread.sleep(2000);
                                            }
                                        }
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    //Si la transacción que estamos intentando finalizar es un tipo AMOUNT lanzamos directamente el proceso de firma y finalziado de la misma
                                    if (trx_type == String.valueOf(TransactionTypeEnum.AMOUNT)) {
                                        checkGlobalPayTrx();
                                    }
                                }

                            } catch (final JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                            final Thread t = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        //contadortimeout.cancel();
                                        sleep(1000);
                                        progress.dismiss();
                                        if (trx_type != String.valueOf(TransactionTypeEnum.AMOUNT)) {
                                            PinPadActivity.this.finish();
                                        }
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
                            mensajetimeout();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("terminal", terminal);
                    params.put("turno", turno);
                    params.put("code", codigo);
                    if (trx_type != null) {
                        params.put("transaction_type", trx_type);
                    }
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

    }

    void CheckCampilloTrx() {
        if (!isNetworkAvailable()) {
            mensajered();
        } else {
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
            RequestQueue queue = Volley.newRequestQueue(this);

            String url = ApiCampilloURI + BuildConfig.EP_GLOBALPAY_CHECK;
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e(TAG, "Response: " + response);
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                String Success = jsonObj.getString("success");

                                if (Success.equals("false")) {
                                    checkGlobalPayTrx();
                                    ////Toast.makeText(getBaseContext(), R.string.error_no_reservado, //Toast.LENGTH_SHORT).show();
                                } else {
                                    final JSONArray AuthProducts = jsonObj.getJSONArray("auth_products");

                                    for (int i = 0; i < AuthProducts.length(); i++) {
                                        JSONObject c = AuthProducts.getJSONObject(i);
                                        AuthDiesel = c.getDouble("diesel");
                                        AuthAdBlue = c.getDouble("adblue");
                                        AuthRedDiesel = c.getDouble("red");
                                        AuthGas = c.getDouble("gas");
                                        AuthMoney = c.getDouble("money");
                                    }
                                    Expendient = jsonObj.getString("expedient");

                                    //Guardamos todas las ids de las transacciones pendientes para procesar las distintas firmas
                                    TransactionIds.clear();
                                    final JSONArray transactionIds = jsonObj.getJSONArray("transaction_ids");
                                    for (int i = 0; i < transactionIds.length(); i++) {
                                        TransactionIds.add(String.valueOf(transactionIds.getInt(i)));
                                    }

                                    KmsRequired = jsonObj.getInt("kms_required");
                                    HoursRequired = jsonObj.getInt("hours_required");

                                    sharedCampillo = getSharedPreferences(LastCampilloAuth, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = sharedCampillo.edit();
                                    editor2.putString("code", codigo);
                                    editor2.putString("Diesel", AuthDiesel.toString());
                                    editor2.putString("AdBlue", AuthAdBlue.toString());
                                    editor2.putString("RedDiesel", AuthRedDiesel.toString());
                                    editor2.putString("Gas", AuthGas.toString());
                                    editor2.putString("Money", AuthMoney.toString());
                                    editor2.apply();
                                    if (AuthDiesel == 0 && AuthAdBlue == 0 && AuthRedDiesel == 0 && AuthGas == 00) {
                                        CampilloSignature(0.00, 0.00, 0.00, 0.00, 0.00, 0.00);
                                    } else {
                                        showQts(ApiTypeEnum.CAMPILLO);
                                    }
                                }

                            } catch (final JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            mensajetimeout();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("terminal", terminal);
                    params.put("code", codigo);
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
    }

    void checkGlobalPayTrx() {
        if (!isNetworkAvailable()) {
            mensajered();
        } else {
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
            RequestQueue queue = Volley.newRequestQueue(this);

            String url = ApiGPayUrl + BuildConfig.EP_GLOBALPAY_CHECK;
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e(TAG, "Response: " + response);
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                String Success = jsonObj.getString("success");

                                if (Success.equals("false")) {
                                    //Toast.makeText(getBaseContext(), R.string.error_no_reservado, //Toast.LENGTH_SHORT).show();
                                } else {
                                    final JSONArray AuthProducts = jsonObj.getJSONArray("auth_products");

                                    for (int i = 0; i < AuthProducts.length(); i++) {
                                        JSONObject c = AuthProducts.getJSONObject(i);
                                        AuthDiesel = c.getDouble("diesel");
                                        AuthAdBlue = c.getDouble("adblue");
                                        AuthRedDiesel = c.getDouble("red");
                                        AuthGas = c.getDouble("gas");
                                        AuthMoney = c.getDouble("money");
                                    }
                                    //Expendient = jsonObj.getString("expedient");

                                    //Guardamos todas las ids de las transacciones pendientes para procesar las distintas firmas
                                    TransactionIds.clear();
                                    final JSONArray transactionIds = jsonObj.getJSONArray("transaction_ids");
                                    for (int i = 0; i < transactionIds.length(); i++) {
                                        TransactionIds.add(String.valueOf(transactionIds.getInt(i)));
                                    }

                                    KmsRequired = jsonObj.getInt("kms_required");
                                    HoursRequired = jsonObj.getInt("hours_required");

                                    sharedCampillo = getSharedPreferences(LastCampilloAuth, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = sharedCampillo.edit();
                                    editor2.putString("code", codigo);
                                    editor2.putString("Diesel", AuthDiesel.toString());
                                    editor2.putString("AdBlue", AuthAdBlue.toString());
                                    editor2.putString("RedDiesel", AuthRedDiesel.toString());
                                    editor2.putString("Gas", AuthGas.toString());
                                    editor2.putString("Money", AuthMoney.toString());
                                    editor2.apply();
                                    if (AuthDiesel == 0 && AuthAdBlue == 0 && AuthRedDiesel == 0 && AuthGas == 00) {
                                        globalPaySignature(0.00, 0.00, 0.00, 0.00, 0.00, 0.00);
                                    } else {
                                        showQts(ApiTypeEnum.GLOBALPAY);
                                    }
                                }

                            } catch (final JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            mensajetimeout();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("terminal", terminal);
                    params.put("code", codigo);
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
    }


    void showQts(ApiTypeEnum type) {
        LayoutInflater inflater = getLayoutInflater();
        CampilloLitersLayout = inflater.inflate(R.layout.liters_dialog, null);
        TextView lbl_producto = (TextView) CampilloLitersLayout.findViewById(R.id.labelcuantos);

        final ApiTypeEnum apiType = type;

        final CardView DieselCard = (CardView) CampilloLitersLayout.findViewById(R.id.DieselCard);
        final CardView AdblueCard = (CardView) CampilloLitersLayout.findViewById(R.id.AdblueCard);
        final CardView RedDieselCard = (CardView) CampilloLitersLayout.findViewById(R.id.RedDieselCard);
        final CardView GasCard = (CardView) CampilloLitersLayout.findViewById(R.id.GasCard);
        final CardView KilometersCard = (CardView) CampilloLitersLayout.findViewById(R.id.KilometersCard);
        final CardView HoursCard = (CardView) CampilloLitersLayout.findViewById(R.id.HoursCard);

        final TextView dieselLiters = (TextView) CampilloLitersLayout.findViewById(R.id.dieselLiters);
        final TextView adblueLiters = (TextView) CampilloLitersLayout.findViewById(R.id.adblueLiters);
        final TextView reddieselLiters = (TextView) CampilloLitersLayout.findViewById(R.id.reddieselLiters);
        final TextView gasLiters = (TextView) CampilloLitersLayout.findViewById(R.id.gasLiters);
        final TextView kilometers = (TextView) CampilloLitersLayout.findViewById(R.id.kilometers);
        final TextView hours = (TextView) CampilloLitersLayout.findViewById(R.id.hours);

        dieselLiters.setText("0");
        adblueLiters.setText("0");
        reddieselLiters.setText("0");
        gasLiters.setText("0");

        Button btnCancel = (Button) CampilloLitersLayout.findViewById(R.id.btnCancel);
        Button btnAccept = (Button) CampilloLitersLayout.findViewById(R.id.btnAccept);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final boolean qr = sharedpreferences.getBoolean(ConfigEnum.qr, false);

        if (AuthDiesel == 0) {
            DieselCard.setVisibility(GONE);
        }
        if (AuthAdBlue == 0) {
            AdblueCard.setVisibility(GONE);
        }
        if (AuthRedDiesel == 0) {
            RedDieselCard.setVisibility(GONE);
        }
        if (AuthGas == 0) {
            GasCard.setVisibility(GONE);
        }

        if (KmsRequired == 0) {
            KilometersCard.setVisibility(GONE);
        }
        if (HoursRequired == 0) {
            HoursCard.setVisibility(GONE);
        }


        builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);

        builder.setView(CampilloLitersLayout);

        final AlertDialog show = builder.show();

        //Si el terminal tiene los QR activados generamo todos los eventos de los 4 productos para que salte el lector
        if (qr) {
            dieselLiters.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        productType = ProductEnum.DIESEL;
                        totalLiters = 0;
                        dialogQR();
                    }
                }
            });
            adblueLiters.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        productType = ProductEnum.ADBLUE;
                        totalLiters = 0;
                        dialogQR();
                    }
                }
            });
            reddieselLiters.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        productType = ProductEnum.RED;
                        totalLiters = 0;
                        dialogQR();
                    }
                }
            });
            gasLiters.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        productType = ProductEnum.GAS;
                        totalLiters = 0;
                        dialogQR();
                    }
                }
            });
        }


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DieselCard.getVisibility() != GONE) {
                    int l = dieselLiters.length();
                    if (l == 0) {
                        //Toast.makeText(getBaseContext(), "Litros de DIESEL A Obligatorios!!!", //Toast.LENGTH_SHORT).show();
                        dieselLiters.requestFocus();
                        return;
                    }
                }
                if (AdblueCard.getVisibility() != GONE) {
                    int l = adblueLiters.length();
                    if (l == 0) {
                        //Toast.makeText(getBaseContext(), "Litros de AD BLUE Obligatorios!!!", //Toast.LENGTH_SHORT).show();
                        adblueLiters.requestFocus();
                        return;
                    }
                }
                if (RedDieselCard.getVisibility() != GONE) {
                    int l = reddieselLiters.length();
                    if (l == 0) {
                        //Toast.makeText(getBaseContext(), "Litros de DIESEL ROJO Obligatorios!!!", //Toast.LENGTH_SHORT).show();
                        reddieselLiters.requestFocus();
                        return;
                    }
                }
                if (GasCard.getVisibility() != GONE) {
                    int l = gasLiters.length();
                    if (l == 0) {
                        //Toast.makeText(getBaseContext(), "Kilos de GAS Obligatorios!!!", //Toast.LENGTH_SHORT).show();
                        gasLiters.requestFocus();
                        return;
                    }
                }
                if (KilometersCard.getVisibility() != GONE) {
                    int l = kilometers.length();
                    if (l == 0) {
                        //Toast.makeText(getBaseContext(), "Se han de informar los KILÓMETROS!!!", //Toast.LENGTH_SHORT).show();
                        kilometers.requestFocus();
                        return;
                    }
                }
                if (HoursCard.getVisibility() != GONE) {
                    int l = hours.length();
                    if (l == 0) {
                        //Toast.makeText(getBaseContext(), "Se han de informar las HORAS del frigo!!!", //Toast.LENGTH_SHORT).show();
                        hours.requestFocus();
                        return;
                    }
                }


                show.dismiss();


                if (dieselLiters.getText().toString().matches("")) {
                    dieselLiters.setText("0.00");
                }
                if (adblueLiters.getText().toString().matches("")) {
                    adblueLiters.setText("0.00");
                }
                if (reddieselLiters.getText().toString().matches("")) {
                    reddieselLiters.setText("0.00");
                }
                if (gasLiters.getText().toString().matches("")) {
                    gasLiters.setText("0.00");
                }
                if (kilometers.getText().toString().matches("")) {
                    kilometers.setText("0.00");
                }
                if (hours.getText().toString().matches("")) {
                    hours.setText("0.00");
                }


                Log.d("Diesel", dieselLiters.getText().toString());
                Log.d("Adblue", adblueLiters.getText().toString());
                Log.d("RedDiesel", reddieselLiters.getText().toString());
                Log.d("Gas", gasLiters.getText().toString());
                Log.d("KMS", kilometers.getText().toString());
                Log.d("Hours", hours.getText().toString());

                double rDiesel = Double.parseDouble(dieselLiters.getText().toString());
                double rAdBlue = Double.parseDouble(adblueLiters.getText().toString());
                double rRedDiesel = Double.parseDouble(reddieselLiters.getText().toString());
                double rGas = Double.parseDouble(gasLiters.getText().toString());
                double rKilometers = Double.parseDouble(kilometers.getText().toString());
                double rHours = Double.parseDouble(hours.getText().toString());

                switch (apiType) {
                    case CAMPILLO:
                        CampilloSignature(rDiesel, rAdBlue, rRedDiesel, rGas, rKilometers, rHours);
                        break;
                    case GLOBALPAY:
                        globalPaySignature(rDiesel, rAdBlue, rRedDiesel, rGas, rKilometers, rHours);
                        break;
                }
            }
        });
    }

    void CampilloSignature(final Double rDiesel, final Double rAdBlue, final Double rRedDiesel, final Double rGas, final Double rKms, final Double rHours) {
        dialog = new Dialog(PinPadActivity.this);
        // Removing the features of Normal Dialogs
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.signature_dialog);
        dialog.setCancelable(true);
        mContent = (LinearLayout) dialog.findViewById(R.id.linearLayout);
        mSignature = new signature(getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) dialog.findViewById(R.id.clear);
        mGetSign = (Button) dialog.findViewById(R.id.getsign);
        mGetSign.setEnabled(false);
        mCancel = (Button) dialog.findViewById(R.id.cancel);
        view = mContent;

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });

        mGetSign.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                for (int i = 0; i < TransactionIds.size(); i++) {
                    Log.v("log_tag", "Panel Saved");
                    StoredPath = DIRECTORY + TransactionIds.get(i) + ".png";
                    view.setDrawingCacheEnabled(true);
                    mSignature.save(view, StoredPath);
                    dialog.dismiss();
                    ////Toast.makeText(getApplicationContext(), "Successfully Saved", //Toast.LENGTH_SHORT).show();
                    // Calling the same class
                    //recreate();
                    FileApi service = RetroClient.getApiService(ApiCampilloURI, BuildConfig.GLOBALPAY_TOKEN);
                    File file = new File(StoredPath);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("signature", file.getName(), requestFile);

                    Call<Respond> resultCall = service.uploadImage(body);

                    resultCall.enqueue(new Callback<Respond>() {
                        @Override
                        public void onResponse(Call<Respond> call, retrofit2.Response<Respond> response) {

                        }

                        @Override
                        public void onFailure(Call<Respond> call, Throwable t) {
                            Log.e("Error upload signature", "Error");
                        }
                    });
                }

                CampilloFinish(rDiesel, rAdBlue, rRedDiesel, rGas, rKms, rHours);
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Canceled");
                dialog.dismiss();
                // Calling the same class
                //recreate();
            }
        });
        dialog.show();

    }

    void globalPaySignature(final Double rDiesel, final Double rAdBlue, final Double rRedDiesel, final Double rGas, final Double rKms, final Double rHours) {
        dialog = new Dialog(PinPadActivity.this);
        // Removing the features of Normal Dialogs
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.signature_dialog);
        dialog.setCancelable(true);
        mContent = (LinearLayout) dialog.findViewById(R.id.linearLayout);
        mSignature = new signature(getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) dialog.findViewById(R.id.clear);
        mGetSign = (Button) dialog.findViewById(R.id.getsign);
        mGetSign.setEnabled(false);
        mCancel = (Button) dialog.findViewById(R.id.cancel);
        view = mContent;

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });

        mGetSign.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                for (int i = 0; i < TransactionIds.size(); i++) {
                    Log.v("log_tag", "Panel Saved");
                    StoredPath = DIRECTORY + TransactionIds.get(i) + ".png";
                    view.setDrawingCacheEnabled(true);
                    mSignature.save(view, StoredPath);
                    dialog.dismiss();
                    ////Toast.makeText(getApplicationContext(), "Successfully Saved", //Toast.LENGTH_SHORT).show();
                    // Calling the same class
                    //recreate();
                    FileApi service = RetroClient.getApiService(ApiGPayUrl, BuildConfig.GLOBALPAY_TOKEN);
                    File file = new File(StoredPath);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("signature", file.getName(), requestFile);

                    Call<Respond> resultCall = service.uploadImage(body);

                    resultCall.enqueue(new Callback<Respond>() {
                        @Override
                        public void onResponse(Call<Respond> call, retrofit2.Response<Respond> response) {

                        }

                        @Override
                        public void onFailure(Call<Respond> call, Throwable t) {
                            Log.e("Error upload signature", "Error");
                        }
                    });
                }

                globalPayTrxFinish(rDiesel, rAdBlue, rRedDiesel, rGas, rKms, rHours);
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Canceled");
                dialog.dismiss();
                // Calling the same class
                //recreate();
            }
        });
        dialog.show();

    }

    void CampilloFinish(final Double rDiesel, final Double rAdBlue, final Double rRedDiesel, final Double rGas, final Double rKms, final Double rHours) {
        if (!isNetworkAvailable()) {
            mensajered();
        } else {
            //contadortimeout = new ContadorTimeOut(15000, 10000);
            //contadortimeout.start();
            progress = new ProgressDialog(this);
            progress.setMessage(this.getString(R.string.spinner_conectando));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.setProgress(0);
            progress.show();
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String cabecera = sharedpreferences.getString(ConfigEnum.ticketHeader, null) + "\n";
            final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
            final String secret = sharedpreferences.getString(ConfigEnum.secretWordTerminal, BuildConfig.EP_SECRET_WORD);
            final String server = sharedpreferences.getString(ConfigEnum.serverUrlSMS, BuildConfig.EP_URL_API_BASE_SMS);
            final String turno = sharedpreferences.getString(ConfigEnum.workShiftKey, null);

            sharedpreferences3 = getSharedPreferences(MyPRECIOS, Context.MODE_PRIVATE);
            final String Dieselprice = sharedpreferences3.getString("dieselKey", "0.00");
            final String Adblueprice = sharedpreferences3.getString("adblueKey", "0.00");
            final String RedDieselprice = sharedpreferences3.getString("reddieselKey", "0.00");
            final String Gasprice = sharedpreferences3.getString("gasKey", "0.00");

            RequestQueue queue = Volley.newRequestQueue(this);
            final String Checksum = md5(terminal + secret + codigo);
            String url = ApiCampilloURI + BuildConfig.EP_GLOBALPAY_FINISH + Checksum;
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                            final String hora = new SimpleDateFormat("HH:mm").format(new Date());
                            try {
                                Log.e(TAG, "Response: " + response);
                                JSONObject jsonObj = new JSONObject(response);
                                String Success = jsonObj.getString("success");

                                if (Success.equals("false")) {
                                    //Toast.makeText(getBaseContext(), "TRANSACTION REFUSED", //Toast.LENGTH_SHORT).show();
                                    codigoerror = jsonObj.getString("error_code");
                                    textoerror = jsonObj.getString("error_description");
                                    msg = "TRANSACTION REFUSED";
                                    msg += "\n";
                                    for (int g = 0; g < 1; g++) {

                                        ThreadPoolManager.getInstance().executeTask(new Runnable() {

                                            @Override
                                            public void run() {
                                                if (mBitmap == null) {
                                                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                                }


                                                try {
                                                    msg = "TRANSACTION REFUSED";
                                                    msg += "\n";

                                                    woyouService.lineWrap(2, callback);
                                                    woyouService.setAlignment(1, callback);
                                                    woyouService.printBitmap(mBitmap, callback);
                                                    woyouService.setFontSize(24, callback);
                                                    woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                                    String pterminal = "Terminal: " + terminal + "\n\n";
                                                    woyouService.printTextWithFont(pterminal, "", 24, callback);
                                                    woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                                    woyouService.lineWrap(2, callback);
                                                    woyouService.setAlignment(0, callback);
                                                    woyouService.printTextWithFont("TRX Code: " + codigo + "\n", "", 30, callback);
                                                    woyouService.printTextWithFont("Error Code: " + codigoerror + "\n", "", 28, callback);
                                                    woyouService.printTextWithFont("Error: " + textoerror + "\n", "", 28, callback);
                                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                                    woyouService.setAlignment(1, callback);
                                                    woyouService.printTextWithFont(msg, "", 36, callback);
                                                    woyouService.lineWrap(4, callback);
                                                } catch (RemoteException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                }

                                            }
                                        });

                                        if (g == 0) {
                                            Thread.sleep(5000);
                                        }
                                    }


                                } else {
                                    //Toast.makeText(getBaseContext(), "TRANSACTION SUCCESSFULLY COMPLETED", //Toast.LENGTH_SHORT).show();

                                    try {

                                        for (int g = 0; g < 2; g++) {

                                            ThreadPoolManager.getInstance().executeTask(new Runnable() {

                                                @Override
                                                public void run() {
                                                    if (mBitmap == null) {
                                                        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                                    }
                                                    Bitmap bitmap = BitmapFactory.decodeFile(StoredPath);
                                                    try {
                                                        msg = "TRANSACTION SUCCESSFULLY\n";
                                                        msg += "COMPLETED";
                                                        msg += "\n";
                                                        woyouService.lineWrap(2, callback);
                                                        woyouService.setAlignment(1, callback);
                                                        woyouService.printBitmap(mBitmap, callback);
                                                        woyouService.setFontSize(24, callback);
                                                        woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                                        String pterminal = "Terminal: " + terminal + "\n\n";
                                                        woyouService.printTextWithFont(pterminal, "", 24, callback);
                                                        woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                                        woyouService.lineWrap(2, callback);
                                                        woyouService.setAlignment(0, callback);
                                                        woyouService.printTextWithFont("TRX Code: " + codigo + "\n", "", 30, callback);
                                                        //woyouService.printTextWithFont( "Operation Code: " + operation + "\n", "", 30, callback);
                                                        woyouService.printTextWithFont("\n", "", 28, callback);
                                                        woyouService.setFontSize(28, callback);
                                                        String[] text = new String[3];
                                                        int[] width = new int[]{10, 8, 8};
                                                        int[] align = new int[]{0, 2, 2}; //

                                                        text[0] = "Product";
                                                        text[1] = "Liters";
                                                        text[2] = "Total";
                                                        woyouService.printColumnsText(text, width, new int[]{0, 2, 2}, callback);

                                                        if (rDiesel > 0) {
                                                            double total = Float.valueOf(Dieselprice) * rDiesel;
                                                            BigDecimal a = new BigDecimal(total);
                                                            final BigDecimal total2 = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                                            totalTxt = total2.toString();
                                                            text[0] = "DIESEL A";
                                                            text[1] = rDiesel.toString();
                                                            text[2] = total2.toString();
                                                            woyouService.printColumnsText(text, width, align, callback);
                                                        }

                                                        if (rAdBlue > 0) {
                                                            double total = Float.valueOf(Adblueprice) * rAdBlue;
                                                            BigDecimal a = new BigDecimal(total);
                                                            final BigDecimal total2 = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                                            totalTxt = total2.toString();
                                                            text[0] = "AD BLUE";
                                                            text[1] = rAdBlue.toString();
                                                            text[2] = total2.toString();
                                                            woyouService.printColumnsText(text, width, align, callback);
                                                        }

                                                        if (rRedDiesel > 0) {
                                                            double total = Float.valueOf(RedDieselprice) * rRedDiesel;
                                                            BigDecimal a = new BigDecimal(total);
                                                            final BigDecimal total2 = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                                            totalTxt = total2.toString();
                                                            text[0] = "D. ROJO";
                                                            text[1] = rRedDiesel.toString();
                                                            text[2] = total2.toString();
                                                            woyouService.printColumnsText(text, width, align, callback);
                                                        }

                                                        if (rGas > 0) {
                                                            double total = Float.valueOf(Gasprice) * rGas;
                                                            BigDecimal a = new BigDecimal(total);
                                                            final BigDecimal total2 = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                                            totalTxt = total2.toString();
                                                            text[0] = "GAS";
                                                            text[1] = rGas.toString();
                                                            text[2] = total2.toString();
                                                            woyouService.printColumnsText(text, width, align, callback);
                                                        }

                                                        if (AuthMoney > 0) {
                                                            text[0] = "ENTREGA";
                                                            text[1] = " ";
                                                            text[2] = AuthMoney.toString();
                                                            woyouService.printColumnsText(text, width, align, callback);
                                                        }

                                                        woyouService.lineWrap(2, callback);
                                                        //woyouService.printBitmap(bitmap, callback);
                                                        woyouService.setAlignment(1, callback);
                                                        woyouService.printTextWithFont(msg, "", 32, callback);
                                                        woyouService.lineWrap(4, callback);
                                                    } catch (RemoteException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }

                                                }
                                            });
                                            if (g == 0) {
                                                Thread.sleep(5000);
                                            }
                                        }
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    saveTrxToMemory(ServiceTypeEnum.GLOBALPAY, TransactionTypeEnum.FINISH, cabecera, terminal, fecha, hora, null, codigo, null, codigo, null, rDiesel, rAdBlue, rRedDiesel, rGas, null, "", "", false);
                                }

                            } catch (final JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                            final Thread t = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        //contadortimeout.cancel();
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
                            mensajetimeout();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("terminal", terminal);
                    params.put("code", codigo);
                    params.put("diesel_liters", rDiesel.toString());
                    params.put("adblue_liters", rAdBlue.toString());
                    params.put("red_liters", rRedDiesel.toString());
                    params.put("gas_kilos", rGas.toString());
                    params.put("amount_euros", AuthMoney.toString());
                    params.put("diesel_pump_price", Dieselprice);
                    params.put("adblue_pump_price", Adblueprice);
                    params.put("red_pump_price", RedDieselprice);
                    params.put("gas_pump_price", Gasprice);
                    params.put("ticket_number", "0");
                    params.put("odometer", rKms.toString());
                    params.put("frigo_hours", rHours.toString());
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
    }

    void globalPayTrxFinish(final Double rDiesel, final Double rAdBlue, final Double rRedDiesel, final Double rGas, final Double rKms, final Double rHours) {
        if (!isNetworkAvailable()) {
            mensajered();
        } else {
            progress = new ProgressDialog(this);
            progress.setMessage(this.getString(R.string.spinner_conectando));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.setProgress(0);
            progress.show();
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String cabecera = sharedpreferences.getString(ConfigEnum.ticketHeader, null) + "\n";
            final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
            final String secret = sharedpreferences.getString(ConfigEnum.secretWordTerminal, BuildConfig.EP_SECRET_WORD);

            sharedpreferences3 = getSharedPreferences(MyPRECIOS, Context.MODE_PRIVATE);
            DieselPrice = Double.parseDouble(sharedpreferences3.getString("dieselKey", "0.00"));
            AdbluePrice = Double.parseDouble(sharedpreferences3.getString("adblueKey", "0.00"));
            RedPrice = Double.parseDouble(sharedpreferences3.getString("reddieselKey", "0.00"));
            GasPrice = Double.parseDouble(sharedpreferences3.getString("gasKey", "0.00"));

            RequestQueue queue = Volley.newRequestQueue(this);
            final String Checksum = md5(terminal + secret + codigo);
            String url = ApiGPayUrl + BuildConfig.EP_GLOBALPAY_FINISH + Checksum;
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                            final String hora = new SimpleDateFormat("HH:mm").format(new Date());
                            try {
                                Log.e(TAG, "Response: " + response);
                                JSONObject jsonObj = new JSONObject(response);
                                String Success = jsonObj.getString("success");

                                if (Success.equals("false")) {
                                    //Toast.makeText(getBaseContext(), "TRANSACTION REFUSED", //Toast.LENGTH_SHORT).show();
                                    codigoerror = jsonObj.getString("error_code");
                                    textoerror = jsonObj.getString("error_description");
                                    msg = "TRANSACTION REFUSED";
                                    msg += "\n";
                                    for (int g = 0; g < 1; g++) {

                                        ThreadPoolManager.getInstance().executeTask(new Runnable() {

                                            @Override
                                            public void run() {
                                                if (mBitmap == null) {
                                                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                                }


                                                try {
                                                    msg = "TRANSACTION REFUSED";
                                                    msg += "\n";

                                                    woyouService.lineWrap(2, callback);
                                                    woyouService.setAlignment(1, callback);
                                                    woyouService.printBitmap(mBitmap, callback);
                                                    woyouService.setFontSize(24, callback);
                                                    woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                                    String pterminal = "Terminal: " + terminal + "\n\n";
                                                    woyouService.printTextWithFont(pterminal, "", 24, callback);
                                                    woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                                    woyouService.lineWrap(2, callback);
                                                    woyouService.setAlignment(0, callback);
                                                    woyouService.printTextWithFont("TRX Code: " + codigo + "\n", "", 30, callback);
                                                    woyouService.printTextWithFont("Error Code: " + codigoerror + "\n", "", 28, callback);
                                                    woyouService.printTextWithFont("Error: " + textoerror + "\n", "", 28, callback);
                                                    woyouService.printTextWithFont("\n", "", 24, callback);
                                                    woyouService.setAlignment(1, callback);
                                                    woyouService.printTextWithFont(msg, "", 36, callback);
                                                    woyouService.lineWrap(4, callback);
                                                } catch (RemoteException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                        if (g == 0) {
                                            Thread.sleep(5000);
                                        }
                                    }
                                } else {

                                    //GET PLATE AND TRAILER_PLATE OF TRANSACTION
                                    final JSONObject transaction = jsonObj.getJSONObject("transaction");
                                    final String plate = transaction.getString("Plate");
                                    final String trailerPlate = transaction.getString("Trailer_plate");

                                    //Toast.makeText(getBaseContext(), "TRANSACTION SUCCESSFULLY COMPLETED", //Toast.LENGTH_SHORT).show();

                                    //USER CUSTOMIZATIONS
                                    FileApi service = RetroClient.getApiService(ApiGPayUrl, BuildConfig.GLOBALPAY_TOKEN);

                                    String customer_code = jsonObj.getString("customer_code");
                                    Call<DataUserCustomization> resultCall = service.getUserCustomizations(customer_code, terminal);

                                    try {
                                        if (mBitmap == null) {
                                            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                                        }

                                        //USER CUSTOMIZATIONS
                                        resultCall.enqueue(new Callback<DataUserCustomization>() {
                                            @Override
                                            public void onResponse(Call<DataUserCustomization> call, retrofit2.Response<DataUserCustomization> response) {
                                                boolean showPrices = false;
                                                if (response.body() != null) {
                                                    DataUserCustomization data = response.body();
                                                    if (data.success) {
                                                        for (UserCustomization user_customization : data.data.user_customizations) {
                                                            //SI EL CLIENTE TIENE UNA CUSTOMIZACION
                                                            if (user_customization.customization.code.equals(CustomizationsEnum.TICKET_CUSTOMER_PRICE.toString())) {
                                                                showPrices = true;
                                                                double rapple = 0.00;
                                                                for (CustomizationValue customization_value : user_customization.customization_values) {
                                                                    double value = Double.parseDouble(customization_value.value);
                                                                    switch (customization_value.name) {
                                                                        case ProductPriceKeyEnum.DIESEL:
                                                                            DieselPrice = value > 0 ? value : DieselPrice;
                                                                            break;
                                                                        case ProductPriceKeyEnum.ADBLUE:
                                                                            AdbluePrice = value > 0 ? value : AdbluePrice;
                                                                            break;
                                                                        case ProductPriceKeyEnum.RED:
                                                                            RedPrice = value > 0 ? value : RedPrice;
                                                                            break;
                                                                        case "rapple":
                                                                            rapple = value;
                                                                            break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                //PRINT TICKET
                                                PrintTicket printTicket = new PrintTicket(woyouService, callback, cabecera, terminal, fecha, hora, mBitmap, getResources(), getPackageName());

                                                //PRINTING TICKET
                                                printTicket.printFinishTicket(rDiesel, rAdBlue, rRedDiesel, rGas, AuthMoney, codigo, DieselPrice, AdbluePrice, RedPrice, GasPrice, showPrices, plate, trailerPlate);
                                                saveTrxToMemory(ServiceTypeEnum.GLOBALPAY, TransactionTypeEnum.FINISH, cabecera, terminal, fecha, hora, null, codigo, null, codigo, null, rDiesel, rAdBlue, rRedDiesel, rGas, null, plate, trailerPlate, showPrices);

                                            }

                                            @Override
                                            public void onFailure(Call<DataUserCustomization> call, Throwable t) {

                                            }
                                        });
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            } catch (final JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                            final Thread t = new Thread() {
                                @Override
                                public void run() {
                                    try {
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
                            mensajetimeout();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("terminal", terminal);
                    params.put("code", codigo);
                    params.put("diesel_liters", rDiesel.toString());
                    params.put("adblue_liters", rAdBlue.toString());
                    params.put("red_liters", rRedDiesel.toString());
                    params.put("gas_kilos", rGas.toString());
                    params.put("amount_euros", AuthMoney.toString());
                    params.put("diesel_pump_price", DieselPrice.toString());
                    params.put("adblue_pump_price", AdbluePrice.toString());
                    params.put("red_pump_price", RedPrice.toString());
                    params.put("gas_pump_price", GasPrice.toString());
                    params.put("ticket_number", "0");
                    params.put("odometer", rKms.toString());
                    params.put("frigo_hours", rHours.toString());
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
    }

    private String md5(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v, String StoredPath) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file

                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();


            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }

        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    /*
    UTILITIES
     */
    private void showProgressDialog(Context context, String message) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(message);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        mProgressDialog.dismiss();
    }


    /*
    GLOBALWALLET
     */
    private void getQrCardQuery(String card_number) {
        final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
        String lang = sharedpreferences.getString(ConfigEnum.langKey, "en");
        final boolean qr = sharedpreferences.getBoolean(ConfigEnum.qr, false);

        FileApi service = RetroClient.getApiService(BuildConfig.URL_BASE + BuildConfig.URL_GLOBALWALLET, BuildConfig.GLOBALWALLET_TOKEN);
        Call<CardQueryResponse> cardQuery = service.getQrCardQuery(card_number, terminal, lang);

        showProgressDialog(this, getString(R.string.loading));
        cardQuery.enqueue(new Callback<CardQueryResponse>() {
            @Override
            public void onResponse(Call<CardQueryResponse> call, retrofit2.Response<CardQueryResponse> response) {
                CardQueryResponse data = response.body();
                dismissProgressDialog();
                Log.w("card query", String.valueOf(data.success));
                if (data.success) {
                    final QrTransaction transaction = data.data;//Declare transaction as final for use in validate onClickListener

                    LayoutInflater inflater = getLayoutInflater();

                    final AlertDialog.Builder builder = new AlertDialog.Builder(PinPadActivity.this);

                    View qrCardValidate = inflater.inflate(R.layout.globalwallet_qrcard_validate, null);
                    builder.setView(qrCardValidate);//SHOW ALERT DIALOG
                    builder.setCancelable(false);
                    final AlertDialog dialog = builder.show();

                    Button btnCancel = (Button) qrCardValidate.findViewById(R.id.buttonCancel);
                    Button btnValidate = (Button) qrCardValidate.findViewById(R.id.buttonValidate);
                    TextView maxQuantityTxt = (TextView) qrCardValidate.findViewById(R.id.maxQuantity);

                    DecimalFormat df = new DecimalFormat("#.##");
                    String maxQuantity = df.format(transaction.card.max_quantity);

                    maxQuantityTxt.setText(maxQuantity);//SET MAX QUANTITY OF PRODUCTS

                    //CARDS VISIBILITY
                    for (Product product : transaction.card.type.products) {
                        int productId = getResources().getIdentifier(product.lang_code, "id", getPackageName());
                        CardView productCard = (CardView) qrCardValidate.findViewById(productId);
                        productCard.setVisibility(CardView.VISIBLE);
                    }

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    btnValidate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showProgressDialog(PinPadActivity.this, getString(R.string.loading));
                            sendQrCardUnlockRequest(transaction.card.card_number, new GpCallback() {
                                @Override
                                public void onSuccess() {
                                    dismissProgressDialog();
                                    validateQrTransaction(transaction, dialog);
                                }

                                @Override
                                public void onError() {
                                    dismissProgressDialog();
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                } else {
                    AlertDialog dialog = showErrorCardQuery(data.message);
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Call<CardQueryResponse> call, Throwable t) {
                t.getMessage();
            }
        });
    }

    private void validateQrTransaction(final QrTransaction transaction, final AlertDialog dialog) {
        FileApi service = RetroClient.getApiService(BuildConfig.URL_BASE + BuildConfig.URL_GLOBALWALLET, BuildConfig.GLOBALWALLET_TOKEN);
        Call<SampleResponse> validateTransaction = service.validateTransaction(transaction.id);

        validateTransaction.enqueue(new Callback<SampleResponse>() {
            @Override
            public void onResponse(Call<SampleResponse> call, retrofit2.Response<SampleResponse> response) {
                SampleResponse data = response.body();
                if (data.success) {
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.transaction_validated), //Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    //PRINT TICKET
                    printTransactionValidated(transaction);
                }
            }

            @Override
            public void onFailure(Call<SampleResponse> call, Throwable t) {

            }
        });
    }

    private void printTransactionValidated(QrTransaction transaction) {
        final String cabecera = "GLOBALWALLET\n" + sharedpreferences.getString(ConfigEnum.ticketHeader, null) + "\n";
        final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
        final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        final String hora = new SimpleDateFormat("HH:mm").format(new Date());

        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
        }

        PrintTicket printTicket = new PrintTicket(woyouService, callback, cabecera, terminal, fecha, hora, mBitmap, getResources(), getPackageName());

        printTicket.printTransactionValidated(transaction);
    }

    private void printTransactionCompleted(QrTransaction transaction) {
        final String cabecera = sharedpreferences.getString(ConfigEnum.ticketHeader, null) + "\n";
        final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
        final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        final String hora = new SimpleDateFormat("HH:mm").format(new Date());

        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
        }

        PrintTicket printTicket = new PrintTicket(woyouService, callback, cabecera, terminal, fecha, hora, mBitmap, getResources(), getPackageName());

        printTicket.printTransactionValidated(transaction);
    }

    private void getQrCardInfo(final String card_number) {
        final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
        String lang = sharedpreferences.getString(ConfigEnum.langKey, "en");
        final boolean qr = sharedpreferences.getBoolean(ConfigEnum.qr, false);

        FileApi service = RetroClient.getApiService(BuildConfig.URL_BASE + BuildConfig.URL_GLOBALWALLET, BuildConfig.GLOBALWALLET_TOKEN);
        Call<CardQueryResponse> cardQuery = service.getQrCardQuery(card_number, terminal, lang);

        showProgressDialog(this, getString(R.string.loading));
        cardQuery.enqueue(new Callback<CardQueryResponse>() {
            @Override
            public void onResponse(Call<CardQueryResponse> call, retrofit2.Response<CardQueryResponse> response) {
                dismissProgressDialog();
                CardQueryResponse data = response.body();
                if (data.success) {
                    final QrCard card = data.data.card;
                    LayoutInflater inflater = getLayoutInflater();

                    final AlertDialog.Builder builder = new AlertDialog.Builder(PinPadActivity.this);

                    GlobalWalletLitersLayout = inflater.inflate(R.layout.liters_dialog_hidden_products, null);
                    builder.setView(GlobalWalletLitersLayout);//SHOW ALERT DIALOG
                    builder.setCancelable(false);
                    final AlertDialog dialog = builder.show();

                    Button btnCancel = (Button) GlobalWalletLitersLayout.findViewById(R.id.btnCancel);
                    Button btnAccept = (Button) GlobalWalletLitersLayout.findViewById(R.id.btnAccept);

                    //CARDS PRODUCT VISIBILITY
                    for (Product product : card.type.products) {
                        int productId = getResources().getIdentifier(product.lang_code, "id", getPackageName());
                        CardView productCard = (CardView) GlobalWalletLitersLayout.findViewById(productId);
                        productCard.setVisibility(CardView.VISIBLE);
                    }

                    final EditText diesel = (EditText) GlobalWalletLitersLayout.findViewById(R.id.gas_a_quantity);
                    final EditText adblue = (EditText) GlobalWalletLitersLayout.findViewById(R.id.adblue_quantity);
                    final EditText red = (EditText) GlobalWalletLitersLayout.findViewById(R.id.gas_b_quantity);
                    final EditText gas = (EditText) GlobalWalletLitersLayout.findViewById(R.id.gas_quantity);

                    if (qr) {
                        gwScanLiters = true;
                        diesel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean hasFocus) {
                                if (hasFocus) {
                                    productType = ProductEnum.DIESEL;
                                    totalLiters = 0;
                                    dialogQR();
                                }
                            }
                        });
                        adblue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean hasFocus) {
                                if (hasFocus) {
                                    productType = ProductEnum.ADBLUE;
                                    totalLiters = 0;
                                    dialogQR();
                                }
                            }
                        });
                        red.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean hasFocus) {
                                if (hasFocus) {
                                    productType = ProductEnum.RED;
                                    totalLiters = 0;
                                    dialogQR();
                                }
                            }
                        });
                        gas.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean hasFocus) {
                                if (hasFocus) {
                                    productType = ProductEnum.GAS;
                                    totalLiters = 0;
                                    dialogQR();
                                }
                            }
                        });
                    }

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final double dieselQuantity = Double.parseDouble(diesel.getText().toString());
                            final double adblueQuantity = Double.parseDouble(adblue.getText().toString());
                            final double redQuantity = Double.parseDouble(red.getText().toString());
                            final double gasQuantity = Double.parseDouble(gas.getText().toString());

                            double totalQuantity = dieselQuantity + adblueQuantity + redQuantity + gasQuantity;
                            if (totalQuantity > card.max_quantity) {
                                showAlertMaxQuantity(new GpCallback() {
                                    @Override
                                    public void onSuccess() {
                                        dialog.dismiss();
                                        finishQrTransaction(card_number, dieselQuantity, adblueQuantity, redQuantity, gasQuantity);
                                    }

                                    @Override
                                    public void onError() {
                                        dialog.dismiss();
                                    }
                                });
                            } else {
                                dialog.dismiss();
                                finishQrTransaction(card_number, dieselQuantity, adblueQuantity, redQuantity, gasQuantity);
                            }
                        }
                    });
                } else {
                    AlertDialog dialog = showErrorCardQuery(data.message);
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Call<CardQueryResponse> call, Throwable t) {
                dismissProgressDialog();
                t.getMessage();
            }
        });

    }

    private void sendQrCardUnlockRequest(final String card_number, final GpCallback callback) {
        final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");

        FileApi service = RetroClient.getApiService(BuildConfig.URL_BASE + BuildConfig.URL_GLOBALWALLET, BuildConfig.GLOBALWALLET_TOKEN);
        Call<SampleResponse> unlockRequest = service.sendQrCardUnlockRequest(card_number, terminal);

        unlockRequest.enqueue(new Callback<SampleResponse>() {
            @Override
            public void onResponse(Call<SampleResponse> call, retrofit2.Response<SampleResponse> response) {
                SampleResponse data = response.body();
                if (data.success) {
                    final int maxTries = 5;
                    final int[] tries = {0};
                    final long SECOND_IN_MILLI = 2000;//10 seconds
                    final Handler timerHandler = new Handler();
                    final Runnable timerRunnable = new Runnable() {
                        private Runnable context = this;

                        @Override
                        public void run() {
                            tries[0]++;//SUM TRY
                            checkQrCardUnlockedRequest(card_number, new GpCallback() {
                                @Override
                                public void onSuccess() {
                                    callback.onSuccess();
                                }

                                @Override
                                public void onError() {
                                    if (tries[0] <= maxTries) {
                                        timerHandler.postDelayed(context, SECOND_IN_MILLI);
                                    } else {
                                        callback.onError();
                                        AlertDialog dialog = showErrorUnlockPin();
                                        dialog.show();
                                    }
                                }
                            });

                        }
                    };
                    timerHandler.postDelayed(timerRunnable, SECOND_IN_MILLI);
                }
            }

            @Override
            public void onFailure(Call<SampleResponse> call, Throwable t) {
                t.getMessage();
            }
        });
    }

    private void checkQrCardUnlockedRequest(final String card_number, final GpCallback callback) {
        FileApi service = RetroClient.getApiService(BuildConfig.URL_BASE + BuildConfig.URL_GLOBALWALLET, BuildConfig.GLOBALWALLET_TOKEN);
        Call<UnlockRequestResponse> unlockRequest = service.getQrCardUnlockRequest(card_number);

        unlockRequest.enqueue(new Callback<UnlockRequestResponse>() {
            @Override
            public void onResponse(Call<UnlockRequestResponse> call, retrofit2.Response<UnlockRequestResponse> response) {
                UnlockRequestResponse data = response.body();
                if (data.success && data.data.unlock_pin == 1) {
                    //PROCESO DE COMPRA TERMINADO - EXECUTE CALLBACK FOR FINISH TRANSACTION

                    callback.onSuccess();
                } else {
                    try {
                        callback.onError();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UnlockRequestResponse> call, Throwable t) {
                t.getMessage();
            }
        });
    }

    private void finishQrTransaction(final String card_number, final double dieselQuantity, final double adblueQuantity, final double redQuantity, final double gasQuantity) {
        //SEND UNLOCK REQUEST
        showProgressDialog(PinPadActivity.this, getResources().getString(R.string.progress_dialog_transaction));
        sendQrCardUnlockRequest(card_number, new GpCallback() {
            @Override
            public void onSuccess() {
                Log.println(Log.DEBUG, "GLOBALWALLET", "PROCESO DE COMPRA TERMINADO");
                saveQrTransactionSale(card_number, dieselQuantity, adblueQuantity, redQuantity, gasQuantity);
            }

            @Override
            public void onError() {
                dismissProgressDialog();
            }
        });
    }

    private void saveQrTransactionSale(String card_number, double dieselQuantity, double adbueQuantity, double redQuantity, double gasQuantity) {
        sharedpreferences3 = getSharedPreferences(MyPRECIOS, Context.MODE_PRIVATE);
        final String terminal = sharedpreferences.getString(ConfigEnum.terminal, "99999");
        final String cabecera = sharedpreferences.getString(ConfigEnum.ticketHeader, null) + "\n";
        FileApi service = RetroClient.getApiService(BuildConfig.URL_BASE + BuildConfig.URL_GLOBALWALLET, BuildConfig.GLOBALWALLET_TOKEN);
        ArrayList<Double> pumpPrices = new ArrayList<>();
        ArrayList<Double> quantities = new ArrayList<>();
        ArrayList<Integer> products = new ArrayList<>();

        if (dieselQuantity > 0) {
            pumpPrices.add(Double.parseDouble(sharedpreferences3.getString("dieselKey", "0.00")));
            quantities.add(dieselQuantity);
            products.add(ProductGWIntEnum.DIESEL);
        }

        if (adbueQuantity > 0) {
            pumpPrices.add(Double.parseDouble(sharedpreferences3.getString("adblueKey", "0.00")));
            quantities.add(adbueQuantity);
            products.add(ProductGWIntEnum.ADBLUE);
        }

        if (redQuantity > 0) {
            pumpPrices.add(Double.parseDouble(sharedpreferences3.getString("reddieselKey", "0.00")));
            quantities.add(redQuantity);
            products.add(ProductGWIntEnum.RED);
        }

        if (gasQuantity > 0) {
            pumpPrices.add(Double.parseDouble(sharedpreferences3.getString("gasKey", "0.00")));
            quantities.add(gasQuantity);
            products.add(ProductGWIntEnum.GAS);
        }

        Call<SaleTransactionResponse> saleTransactionRequest = service.saveTransactionSale(card_number, terminal, pumpPrices, quantities, products);

        saleTransactionRequest.enqueue(new Callback<SaleTransactionResponse>() {
            @Override
            public void onResponse(Call<SaleTransactionResponse> call, retrofit2.Response<SaleTransactionResponse> response) {
                SaleTransactionResponse data = response.body();
                if (data.success) {
                    dismissProgressDialog();
                    Log.println(Log.DEBUG, "GLOBALWALLET", "VENTA FINALIZADA");
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.transaction_completed), //Toast.LENGTH_SHORT).show();

                    PrintTicket printTicket = new PrintTicket(woyouService, callback, sharedpreferences, getResources(), getPackageName());

                    printTicket.printTransactionCompleted(data.data);

                    String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                    String time = new SimpleDateFormat("HH:mm").format(new Date());


                    Double dieselLiters = 0.0;
                    Double adblueLiters = 0.0;
                    Double redLiters = 0.0;
                    Double gasKilos = 0.0;
                    String trxIds = "";
                    for (QrTransaction trx : data.data) {
                        trxIds += trx.id;
                        switch (trx.product_id) {
                            case ProductGWIntEnum.DIESEL:
                                dieselLiters += (double) trx.quantity;
                                break;
                            case ProductGWIntEnum.ADBLUE:
                                adblueLiters += (double) trx.quantity;
                                break;
                            case ProductGWIntEnum.RED:
                                redLiters += (double) trx.quantity;
                                break;
                            case ProductGWIntEnum.GAS:
                                gasKilos += (double) trx.quantity;
                                break;
                        }
                    }
                    saveTrxToMemory(ServiceTypeEnum.GLOBALWALLET, TransactionTypeEnum.FINISH, cabecera, terminal, date, time, null, trxIds, null, codigo, null, dieselLiters, adblueLiters, redLiters, gasKilos, null, "", "", false);
                }
            }

            @Override
            public void onFailure(Call<SaleTransactionResponse> call, Throwable t) {

            }
        });

    }

    public AlertDialog showErrorCardQuery(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.card_not_allowed))
                .setMessage(errorMessage)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        return builder.create();
    }

    public AlertDialog showErrorUnlockPin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.card_unlocking_title))
                .setMessage(getString(R.string.card_unlocking_description))
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        return builder.create();
    }

    public void showAlertMaxQuantity(final GpCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.sure_alert))
                .setMessage(getString(R.string.max_quantity_exceed_alert))
                .setPositiveButton(getString(R.string.btn_accept),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                callback.onError();
                            }
                        })
                .setCancelable(true)
                .setNegativeButton(getString(R.string.btn_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                callback.onError();
                            }
                        });


        //builder.create();
        builder.show();
    }

    public AlertDialog showAlertError(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("ERROR")
                .setMessage(errorMessage)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        return builder.create();
    }
}

