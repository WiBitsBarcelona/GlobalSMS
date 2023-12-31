package eu.globaldevelopers.globalsms;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import eu.globaldevelopers.globalsms.Class.PrintTicket;
import eu.globaldevelopers.globalsms.Enums.ConfigEnum;
import eu.globaldevelopers.globalsms.Enums.ServiceTypeEnum;
import eu.globaldevelopers.globalsms.Enums.TransactionTypeEnum;
import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class activity_copia_new extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static final String MyPREFERENCES = ConfigEnum.MyPREFERENCES;

    public static final String MyPRECIOS = "MyPrecios";
    SharedPreferences sharedpreferences2, sharedpreferences3;

    private GridView gridView;
    ArrayList<operacion> operacionList;
    AdaptadorDeOperaciones adapter;

    AlertDialog.Builder builder;

    String code = null;

    private static final String TAG = "PrinterTestDemo";

    private IWoyouService woyouService;

    private ICallback callback = null;

    Double dieselPrice, adbluePrice, redPrice, gasPrice;

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
        setContentView(R.layout.activity_copia_new);


        gridView = (GridView) findViewById(R.id.grid);

        AdminSQLiteOpenHelper databaseHelper = new AdminSQLiteOpenHelper(this, "datos", null, BuildConfig.VERSION_CODE);
        operacionList = new ArrayList<operacion>();

        operacionList = databaseHelper.getAllOperaciones();
        adapter = new AdaptadorDeOperaciones(this, operacionList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);


        if (Build.VERSION.SDK_INT < 19) {
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

        innitView();

        //PRICES
        sharedpreferences3 = getSharedPreferences(MyPRECIOS, Context.MODE_PRIVATE);
        dieselPrice = Double.parseDouble(sharedpreferences3.getString("dieselKey", "0.00"));
        adbluePrice = Double.parseDouble(sharedpreferences3.getString("adblueKey", "0.00"));
        redPrice = Double.parseDouble(sharedpreferences3.getString("reddieselKey", "0.00"));
        gasPrice = Double.parseDouble(sharedpreferences3.getString("gasKey", "0.00"));

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        code = ((TextView) view.findViewById(R.id.codigo)).getText().toString();

        ////Toast.makeText(this, code, //Toast.LENGTH_SHORT).show();

        LayoutInflater inflater = getLayoutInflater();
        View dialogoLayout = inflater.inflate(R.layout.dialogo_impresion, null);
        TextView lbl_codigo = (TextView) dialogoLayout.findViewById(R.id.codigoimp);
        lbl_codigo.setText(code);

        builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);

        builder.setView(dialogoLayout);

        final AlertDialog show = builder.show();

        Button alertButton1 = (Button) dialogoLayout.findViewById(R.id.btnReserve);
        Button alertButton2 = (Button) dialogoLayout.findViewById(R.id.btnFinish);
        Button alertButton3 = (Button) dialogoLayout.findViewById(R.id.btnCancel);

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "datos", null, BuildConfig.VERSION_CODE);

        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor fila = bd.rawQuery(

                "select * from operaciones where codigo='" + code + "' and tipo='" + TransactionTypeEnum.RESERVE + "'", null);

        if (fila.moveToFirst()) {
            alertButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyOfReserve();
                    show.dismiss();

                }
            });
        } else {
            alertButton1.setVisibility(view.INVISIBLE);
        }
        bd.close();

        AdminSQLiteOpenHelper admin2 = new AdminSQLiteOpenHelper(this,

                "datos", null, BuildConfig.VERSION_CODE);

        SQLiteDatabase bd2 = admin2.getWritableDatabase();

        Cursor fila2 = bd2.rawQuery(

                "select * from operaciones where codigo='" + code + "' and tipo='" + TransactionTypeEnum.FINISH + "'", null);

        if (fila2.moveToFirst()) {
            alertButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyOfFinish();
                    show.dismiss();

                }
            });
        } else {
            alertButton2.setVisibility(view.INVISIBLE);
        }
        bd2.close();

        AdminSQLiteOpenHelper admin3 = new AdminSQLiteOpenHelper(this,

                "datos", null, BuildConfig.VERSION_CODE);

        SQLiteDatabase bd3 = admin3.getWritableDatabase();

        Cursor fila3 = bd3.rawQuery(

                "select * from operaciones where codigo='" + code + "' and tipo='" + TransactionTypeEnum.CANCEL + "'", null);

        if (fila3.moveToFirst()) {
            alertButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyOfCancel();
                    show.dismiss();

                }
            });
        } else {
            alertButton3.setVisibility(view.INVISIBLE);
        }
        bd2.close();
    }

    Bitmap mBitmap;

    public void copyOfReserve() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "datos", null, BuildConfig.VERSION_CODE);

        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor fila = bd.rawQuery(

                "select * from operaciones where codigo='" + code + "' and tipo='" + TransactionTypeEnum.RESERVE + "'", null);

        if (fila.moveToFirst()) {
            final String fecha = fila.getString(fila.getColumnIndex("fecha"));
            final String hora = fila.getString(fila.getColumnIndex("hora"));
            final String resultado = fila.getString(fila.getColumnIndex("resultado"));
            final String codigo = fila.getString(fila.getColumnIndex("codigo"));

            final String producto = fila.getString(fila.getColumnIndex("operacion"));
            final String litros = fila.getString(fila.getColumnIndex("litros"));
            final String codigo_error = fila.getString(fila.getColumnIndex("codigo_error"));
            final String error = fila.getString(fila.getColumnIndex("error"));
            Double dieselLiters = fila.getDouble(fila.getColumnIndex("diesel_liters"));
            Double adblueLiters = fila.getDouble(fila.getColumnIndex("adblue_liters"));
            Double redLiters = fila.getDouble(fila.getColumnIndex("red_liters"));
            Double gasKilos = fila.getDouble(fila.getColumnIndex("gas_kilos"));
            String plate = fila.getString(fila.getColumnIndex("plate"));
            String trailerPlate = fila.getString(fila.getColumnIndex("trailer_plate"));
            int showPrices = fila.getInt(fila.getColumnIndex("show_prices"));
            int service = fila.getInt(fila.getColumnIndex("service_type"));
            try {
                if (service == ServiceTypeEnum.GLOBALPAY) {
                    if (mBitmap == null) {
                        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                    }
                    sharedpreferences2 = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    final String cabecera = sharedpreferences2.getString(ConfigEnum.ticketHeader, null) + "\n";
                    final String terminal = sharedpreferences2.getString(ConfigEnum.terminal, "99999");

                    //PRINT TICKET
                    PrintTicket printTicket = new PrintTicket(woyouService, callback, cabecera, terminal, fecha, hora, mBitmap, getResources(), getPackageName());

                    //PRINTING TICKET
                    printTicket.printFinishTicketOnce(dieselLiters, adblueLiters, redLiters, gasKilos, 0.0, codigo, dieselPrice, adbluePrice, redPrice, gasPrice, showPrices == 1, plate, trailerPlate);

                } else {
                    ThreadPoolManager.getInstance().executeTask(new Runnable() {

                        @Override
                        public void run() {
                            if (mBitmap == null) {
                                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                            }
                            try {
                                sharedpreferences2 = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                final String cabecera = sharedpreferences2.getString(ConfigEnum.ticketHeader, null) + "\n";
                                final String terminal = sharedpreferences2.getString(ConfigEnum.terminal, "99999");


                                woyouService.lineWrap(2, callback);
                                woyouService.setAlignment(1, callback);
                                woyouService.printTextWithFont("*** COPY ***\n", "", 36, callback);
                                woyouService.printBitmap(mBitmap, callback);
                                woyouService.setFontSize(24, callback);
                                woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                String pterminal = "Terminal: " + terminal + "\n\n";
                                woyouService.printTextWithFont(pterminal, "", 24, callback);
                                woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                woyouService.lineWrap(2, callback);
                                woyouService.setAlignment(0, callback);
                                woyouService.printTextWithFont("Transaction Code: " + codigo + "\n", "", 30, callback);
                                if (resultado.equals("TRANSACTION ACCEPTED\n")) {
                                    woyouService.printTextWithFont(producto + "\n", "", 28, callback);
                                    woyouService.printTextWithFont(litros, "", 28, callback);
                                }
                                if (resultado.equals("TRANSACTION REFUSED\n")) {
                                    woyouService.printTextWithFont(codigo_error + "\n", "", 28, callback);
                                    woyouService.printTextWithFont(error + "\n", "", 28, callback);
                                }
                                woyouService.printTextWithFont("\n\n", "", 24, callback);
                                woyouService.setAlignment(1, callback);
                                woyouService.printTextWithFont(resultado, "", 36, callback);

                                woyouService.lineWrap(4, callback);
                            } catch (RemoteException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (Exception ex) {

                            }
                        }
                    });
                }
            } catch (Exception ex) {

            }
        } else {
            //Toast.makeText(this, "No Reserve for this Code", //Toast.LENGTH_SHORT).show();
        }
        bd.close();

    }

    public void copyOfFinish() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "datos", null, BuildConfig.VERSION_CODE);

        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor fila = bd.rawQuery(

                "select * from operaciones where codigo='" + code + "' and tipo='" + TransactionTypeEnum.FINISH + "'", null);

        if (fila.moveToFirst()) {
            final String fecha = fila.getString(fila.getColumnIndex("fecha"));
            final String hora = fila.getString(fila.getColumnIndex("hora"));
            final String resultado = fila.getString(fila.getColumnIndex("resultado"));
            final String codigo  = fila.getString(fila.getColumnIndex("codigo"));
            final String operacion = fila.getString(fila.getColumnIndex("operacion"));
            final String producto = fila.getString(fila.getColumnIndex("operacion"));
            final String litros = fila.getString(fila.getColumnIndex("litros"));
            final String total = fila.getString(fila.getColumnIndex("total"));
            final String codigo_error = fila.getString(fila.getColumnIndex("codigo_error"));
            final String error = fila.getString(fila.getColumnIndex("error"));
            Double dieselLiters = fila.getDouble(fila.getColumnIndex("diesel_liters"));
            Double adblueLiters = fila.getDouble(fila.getColumnIndex("adblue_liters"));
            Double redLiters = fila.getDouble(fila.getColumnIndex("red_liters"));
            Double gasKilos = fila.getDouble(fila.getColumnIndex("gas_kilos"));
            final String plate = fila.getString(fila.getColumnIndex("plate"));
            String trailerPlate = fila.getString(fila.getColumnIndex("trailer_plate"));
            int showPrices = fila.getInt(fila.getColumnIndex("show_prices"));
            int service = fila.getInt(fila.getColumnIndex("service_type"));
            try {
                if (service == ServiceTypeEnum.GLOBALPAY || service == ServiceTypeEnum.GLOBALWALLET) {
                    if (mBitmap == null) {
                        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                    }
                    sharedpreferences2 = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    final String cabecera = sharedpreferences2.getString(ConfigEnum.ticketHeader, null) + "\n";
                    final String terminal = sharedpreferences2.getString(ConfigEnum.terminal, "99999");

                    //PRINT TICKET
                    PrintTicket printTicket = new PrintTicket(woyouService, callback, cabecera, terminal, fecha, hora, mBitmap, getResources(), getPackageName());

                    //PRINTING TICKET
                    printTicket.printFinishTicketOnce(dieselLiters, adblueLiters, redLiters, gasKilos, 0.0, codigo, dieselPrice, adbluePrice, redPrice, gasPrice, showPrices == 1, plate, trailerPlate);

                } else {
                    ThreadPoolManager.getInstance().executeTask(new Runnable() {

                        @Override
                        public void run() {
                            if (mBitmap == null) {
                                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                            }
                            try {
                                sharedpreferences2 = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                final String cabecera = sharedpreferences2.getString(ConfigEnum.ticketHeader, null) + "\n";
                                final String terminal = sharedpreferences2.getString(ConfigEnum.terminal, "99999");


                                woyouService.lineWrap(2, callback);
                                woyouService.setAlignment(1, callback);
                                woyouService.printTextWithFont("*** COPY ***\n", "", 36, callback);
                                woyouService.printBitmap(mBitmap, callback);
                                woyouService.setFontSize(24, callback);
                                woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                String pterminal = "Terminal: " + terminal + "\n\n";
                                woyouService.printTextWithFont(pterminal, "", 24, callback);
                                woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                woyouService.lineWrap(2, callback);
                                woyouService.setAlignment(0, callback);
                                woyouService.printTextWithFont(getString(R.string.plate) + ": " + plate + "\n", "", 30, callback);
                                woyouService.printTextWithFont("Transaction Code: " + codigo + "\n", "", 30, callback);
                                if (resultado.equals("TRANSACTION REFUSED\n")) {
                                    woyouService.printTextWithFont(codigo_error + "\n", "", 28, callback);
                                    woyouService.printTextWithFont(error + "\n", "", 28, callback);
                                }
                                if (resultado.equals("TRANSACTION SUCCESSFULLY\nCOMPLETED\n")) {
                                    woyouService.printTextWithFont("Operation Code: " + operacion + "\n\n", "", 30, callback);
                                    woyouService.setAlignment(0, callback);
                                    woyouService.sendRAWData(new byte[]{0x1B, 0x21, 0x08}, callback);
                                    woyouService.setFontSize(28, callback);
                                    String[] text = new String[3];
                                    int[] width = new int[]{10, 8, 8};
                                    int[] align = new int[]{0, 2, 2};
                                    text[0] = "Product";
                                    text[1] = "Liters";
                                    text[2] = "Total";
                                    woyouService.printColumnsText(text, width, new int[]{0, 2, 2}, callback);

                                    text[0] = producto;
                                    text[1] = litros;
                                    text[2] = total;
                                    woyouService.printColumnsText(text, width, align, callback);

                                    woyouService.sendRAWData(new byte[]{0x1B, 0x21, 0x00}, callback);
                                }

                                woyouService.printTextWithFont("\n\n", "", 24, callback);
                                woyouService.setAlignment(1, callback);
                                woyouService.printTextWithFont(resultado, "", 32, callback);

                                woyouService.lineWrap(4, callback);
                            } catch (RemoteException e) {
                            } catch (Exception ex) {

                            }
                        }
                    });
                }
            } catch (Exception ex) {
                Log.d(TAG, ex.getMessage());
            }
        } else {
            //Toast.makeText(this, "No Finish Transaction for this Code", //Toast.LENGTH_SHORT).show();
        }
        bd.close();
    }

    public void copyOfCancel() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "datos", null, BuildConfig.VERSION_CODE);

        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor fila = bd.rawQuery(

                "select * from operaciones where codigo='" + code + "' and tipo='" + TransactionTypeEnum.CANCEL + "'", null);

        if (fila.moveToFirst()) {
            final String fecha = fila.getString(fila.getColumnIndex("fecha"));
            final String hora = fila.getString(fila.getColumnIndex("hora"));
            final String resultado = fila.getString(fila.getColumnIndex("resultado"));
            final String codigo = fila.getString(fila.getColumnIndex("codigo"));
            final String codigo_error = fila.getString(fila.getColumnIndex("codigo_error"));
            final String error = fila.getString(fila.getColumnIndex("error"));
            Double dieselLiters = fila.getDouble(fila.getColumnIndex("diesel_liters"));
            Double adblueLiters = fila.getDouble(fila.getColumnIndex("adblue_liters"));
            Double redLiters = fila.getDouble(fila.getColumnIndex("red_liters"));
            Double gasKilos = fila.getDouble(fila.getColumnIndex("gas_kilos"));
            String plate = fila.getString(fila.getColumnIndex("plate"));
            String trailerPlate = fila.getString(fila.getColumnIndex("trailer_plate"));
            int showPrices = fila.getInt(fila.getColumnIndex("show_prices"));
            int service = fila.getInt(fila.getColumnIndex("service_type"));
            try {
                if (service == ServiceTypeEnum.GLOBALPAY) {
                    if (mBitmap == null) {
                        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                    }
                    sharedpreferences2 = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    final String cabecera = sharedpreferences2.getString(ConfigEnum.ticketHeader, null) + "\n";
                    final String terminal = sharedpreferences2.getString(ConfigEnum.terminal, "99999");

                    //PRINT TICKET
                    PrintTicket printTicket = new PrintTicket(woyouService, callback, cabecera, terminal, fecha, hora, mBitmap, getResources(), getPackageName());

                    //PRINTING TICKET
                    printTicket.printFinishTicketOnce(dieselLiters, adblueLiters, redLiters, gasKilos, 0.0, codigo, dieselPrice, adbluePrice, redPrice, gasPrice, showPrices == 1, plate, trailerPlate);

                } else {
                    ThreadPoolManager.getInstance().executeTask(new Runnable() {

                        @Override
                        public void run() {
                            if (mBitmap == null) {
                                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globalsms);
                            }
                            try {
                                sharedpreferences2 = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                final String cabecera = sharedpreferences2.getString(ConfigEnum.ticketHeader, null) + "\n";
                                final String terminal = sharedpreferences2.getString(ConfigEnum.terminal, "99999");


                                woyouService.lineWrap(2, callback);
                                woyouService.setAlignment(1, callback);
                                woyouService.printTextWithFont("*** COPY ***\n", "", 36, callback);
                                woyouService.printBitmap(mBitmap, callback);
                                woyouService.setFontSize(24, callback);
                                woyouService.printTextWithFont("\n" + cabecera + "\n", "", 28, callback);
                                String pterminal = "Terminal: " + terminal + "\n\n";
                                woyouService.printTextWithFont(pterminal, "", 24, callback);
                                woyouService.printTextWithFont(fecha + "   " + hora + "\n", "", 24, callback);
                                woyouService.lineWrap(2, callback);
                                woyouService.setAlignment(0, callback);
                                woyouService.printTextWithFont("Transaction Code: " + codigo + "\n", "", 30, callback);
                                if (resultado.equals("TRANSACTION REFUSED\n")) {
                                    woyouService.printTextWithFont(codigo_error + "\n", "", 28, callback);
                                    woyouService.printTextWithFont(error + "\n", "", 28, callback);
                                }
                                woyouService.printTextWithFont("\n\n", "", 24, callback);
                                woyouService.setAlignment(1, callback);
                                woyouService.printTextWithFont(resultado, "", 32, callback);

                                woyouService.lineWrap(4, callback);
                            } catch (RemoteException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (Exception ex) {

                            }
                        }
                    });
                }
            } catch (Exception ex) {

            }
        } else {
            //Toast.makeText(this, "No Cancel Transaction for this Code", //Toast.LENGTH_SHORT).show();
        }
        bd.close();
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (connService != null) {
            unbindService(connService);
        }
    }
}
