package eu.globaldevelopers.globalsms;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.globaldevelopers.globalsms.Enums.ConfigEnum;
import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class CopiaActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = ConfigEnum.MyPREFERENCES;
    SharedPreferences sharedpreferences2;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;


    String gsms = "GlobalSMS\n";
    String globaltank = "GlobalTank SLU\n";
    String saltolinea = "\n\n";

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

        innitView();
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


        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "datos", null, BuildConfig.VERSION_CODE);

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
                //Toast.makeText(this, "NO Printer Connected",

                        //Toast.LENGTH_SHORT).show();
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

    Bitmap mBitmap;

    public void printCopy(String codigoT){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "datos", null, BuildConfig.VERSION_CODE);

        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor fila = bd.rawQuery(

                "select * from operaciones where id=" + codigoT, null);

        if (fila.moveToFirst()) {
            String tipo = fila.getString(1);
            String cabecera = fila.getString(2);
            String terminal = fila.getString(3);
            String fecha = fila.getString(4);
            String hora = fila.getString(5);
            final String resultado = fila.getString(6);
            final String codigo = fila.getString(7);
            final String operacion=fila.getString(8);;

            final String producto = fila.getString(9);
            final String litros_aceptados = fila.getString(10);
            final String litros = fila.getString(11);
            final String plate = fila.getString(16);
            final String total = fila.getString(12);
            final String codigo_error = fila.getString(13);
            final String error = fila.getString(14);

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
                        final String cabecera = sharedpreferences2.getString(ConfigEnum.ticketHeader, null) + "\n";
                        final String terminal = sharedpreferences2.getString(ConfigEnum.terminal, "99999");


                        woyouService.lineWrap(2, callback);
                        woyouService.setAlignment(1, callback);
                        woyouService.printBitmap(mBitmap, callback);
                        woyouService.setFontSize(24, callback);
                        woyouService.printTextWithFont("\n"+ cabecera + "\n", "", 28, callback);
                        String pterminal = "Terminal: " + terminal + "\n\n";
                        woyouService.printTextWithFont(pterminal, "", 24, callback);
                        woyouService.printTextWithFont(fecha +  "   " + hora + "\n", "", 24, callback);
                        woyouService.lineWrap(2, callback);
                        woyouService.printTextWithFont(resultado, "", 28, callback);
                        woyouService.setAlignment(0, callback);
                        woyouService.printTextWithFont(getString(R.string.plate) + ": " + plate + "\n", "", 30, callback);
                        woyouService.printTextWithFont("Transaction Code: " + codigo + "\n", "", 24, callback);
                        if (resultado.equals("TRANSACTION ACCEPTED\n")) {
                            woyouService.printTextWithFont( producto , "", 24, callback);
                            woyouService.printTextWithFont("Authorized Liters: " + litros_aceptados + "\n", "", 24, callback);
                        }
                        if (resultado.equals("TRANSACTION REFUSED\n")) {
                            woyouService.printTextWithFont(codigo_error + "\n", "", 28, callback);
                            woyouService.printTextWithFont(error + "\n", "", 28, callback);
                        }
                        if (resultado.equals("TRANSACTION SUCCESSFULLY\nCOMPLETED\n")) {
                            woyouService.printTextWithFont("Operation Code: " + operacion + "\n\n", "", 24, callback);
                            woyouService.setAlignment(0, callback);
                            woyouService.sendRAWData(new byte[]{0x1B, 0x21, 0x08}, callback);
                            woyouService.setFontSize(24, callback);
                            String[] text = new String[3];
                            int[] width = new int[] { 10, 6, 6 };
                            int[] align = new int[] { 1, 0, 0 };
                            text[0] = "Products";
                            text[1] = "Liters";
                            text[2] = "Total";
                            woyouService.printColumnsText(text, width, new int[] { 1, 2, 2 }, callback);

                            text[0] = producto;
                            text[1] = litros;
                            text[2] = total;
                            woyouService.printColumnsText(text, width, align, callback);

                            woyouService.sendRAWData(new byte[]{0x1B, 0x21, 0x00}, callback);
                        }

                        woyouService.printTextWithFont("\n\n", "", 24, callback);
                        woyouService.setAlignment(1, callback);
                        //woyouService.printTextWithFont(msg, "", 36, callback);

                        woyouService.lineWrap(4, callback);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }});
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (connService != null) {
            unbindService(connService);
        }
    }

    public void cargardatos(){

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "datos", null, BuildConfig.VERSION_CODE);

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

            //Toast.makeText(this, "No Transactions Data to Show",

                    //Toast.LENGTH_SHORT).show();

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
                printCopy(codigot);
            }


        });
    }
}
