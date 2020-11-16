package eu.globaldevelopers.globalsms;

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
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class PreciosActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String MyPRECIOS = "MyPrecios" ;
    public static final String diesel = "dieselKey";
    public static final String adblue = "adblueKey";
    public static final String reddiesel = "reddieselKey";
    public static final String biodiesel = "biodieselKey";

    public static String dieselactual;
    public static String adblueactual;
    public static String reddieselactual;
    public static String biodieselactual;

    SharedPreferences sharedpreferences;
    SharedPreferences sharedpreferences2;

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
        biodieselactual = sharedpreferences.getString("biodieselKey", "0.00");

        innitView();
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

    public void BioFunction(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.precio_bio));
        builder.setMessage(this.getString(R.string.precio_bio));
        builder.setCancelable(false);
        builder.setIcon(R.drawable.pump_b);
        final EditText input = new EditText(this);
        input.append(biodieselactual);
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        int length = input.getText().length();
        input.setSelection(length);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    salvap("BioDiesel", biodieselactual, input.getText().toString());
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
                printPriceChange("DIESEL", precioAnt, precioAct);
                break;
            case "Adblue":
                editor.putString(adblue, precioAct);
                editor.apply();
                adblueactual = precioAct;
                Toast.makeText(getBaseContext(),"ADBLUE PRICE CHANGED SUCCESSFULLY",Toast.LENGTH_SHORT).show();
                printPriceChange("ADBLUE", precioAnt, precioAct);
                break;
            case "RedDiesel":
                editor.putString(reddiesel, precioAct);
                editor.apply();
                reddieselactual = precioAct;
                Toast.makeText(getBaseContext(),"RED DIESEL PRICE CHANGED SUCCESSFULLY",Toast.LENGTH_SHORT).show();
                printPriceChange("RED DIESEL", precioAnt, precioAct);
                break;
            case "BioDiesel":
                editor.putString(biodiesel, precioAct);
                editor.apply();
                biodieselactual = precioAct;
                Toast.makeText(getBaseContext(),"BIO DIESEL PRICE CHANGED SUCCESSFULLY",Toast.LENGTH_SHORT).show();
                printPriceChange("BIO DIESEL", precioAnt, precioAct);
                break;
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

    Bitmap mBitmap;

    public void printPriceChange(final String producto, final String  precioAnt, final String precioAct){
        ThreadPoolManager.getInstance().executeTask(new Runnable(){

            @Override
            public void run() {
                if( mBitmap == null ){
                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                }
                try {
                    final String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                    final String hora = new SimpleDateFormat("HH:mm").format(new Date());

                    sharedpreferences2 = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    final String cabecera = sharedpreferences2.getString("cabeceraKey", null) + "\n";
                    final String terminal = sharedpreferences2.getString("terminalKey", null);

                    String msg = producto + "\nPRICE CHANGED\n";
                    msg += " SUCCESSFULLY";
                    msg += "\n";

                    woyouService.lineWrap(2, callback);
                    woyouService.setAlignment(1, callback);
                    woyouService.printBitmap(mBitmap, callback);
                    woyouService.setFontSize(24, callback);
                    woyouService.printTextWithFont("\n"+ cabecera + "\n", "", 28, callback);
                    String pterminal = "Terminal: " + terminal + "\n\n";
                    woyouService.printTextWithFont(pterminal, "", 24, callback);
                    woyouService.printTextWithFont(fecha +  "   " + hora + "\n", "", 24, callback);
                    woyouService.lineWrap(2, callback);
                    woyouService.setAlignment(0, callback);
                    woyouService.printTextWithFont("Old Price: " + precioAnt + "\n", "", 28, callback);
                    woyouService.printTextWithFont("New Price: " + precioAct + "\n", "", 28, callback);
                    woyouService.printTextWithFont("\n\n", "", 24, callback);
                    woyouService.setAlignment(1, callback);
                    woyouService.printTextWithFont(msg, "", 36, callback);

                    woyouService.lineWrap(4, callback);
                }  catch (Exception e) {
                    e.printStackTrace();
                }

            }});
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (connService != null) {
            unbindService(connService);
        }
    }
}
