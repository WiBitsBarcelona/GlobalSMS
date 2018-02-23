package eu.globaldevelopers.globalsms;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


public class ConfigActivity extends AppCompatActivity {
    EditText ed1,ed2,ed3, ed4;
    Spinner PosList, locList;
    ToggleButton QrSiNo;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String cab = "cabeceraKey";
    public static final String ter = "terminalKey";
    public static final String sec = "secretKey";
    public static final String ser = "serverKey";
    public static final String qr = "qrKey";
    public static final String pos = "posKey";
    public static final String loc = "locKey";

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#68a9ea")));
        getSupportActionBar().setTitle("GlobalSMS - System Configuration");

        ed1=(EditText)findViewById(R.id.cabecera);
        ed2=(EditText)findViewById(R.id.instanticterminal);
        ed3=(EditText)findViewById(R.id.instanticsecret);
        ed4=(EditText)findViewById(R.id.serveraddress);
        QrSiNo=(ToggleButton)findViewById(R.id.QrSiNo);
        PosList=(Spinner)findViewById(R.id.PosList);
        locList=(Spinner)findViewById(R.id.locList);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String cabactual = sharedpreferences.getString("cabeceraKey", null);
        String teractual = sharedpreferences.getString("terminalKey", null);
        String secactual = sharedpreferences.getString("secretKey", null);
        String serveractual = sharedpreferences.getString("serverKey", null);
        boolean qractual = sharedpreferences.getBoolean("qrKey", false);
        int posactual = sharedpreferences.getInt("posKey", 0);
        int locactual = sharedpreferences.getInt("locKey", 0);

        ed1.setText(cabactual);
        ed2.setText(teractual);
        ed3.setText(secactual);
        ed4.setText(serveractual);

        if(qractual == true)
        {
            QrSiNo.setChecked(true);
        }
        else
        {
            QrSiNo.setChecked(false);
        }
        PosList.setSelection(posactual);
        locList.setSelection(locactual);
    }

    public void BorrardatosFunction(View view){
        AlertDialog.Builder alert =new AlertDialog.Builder(this);
        alert.setTitle("CAUTION!!");
        alert.setCancelable(false);
        alert.setMessage("This process will delete all transactions data stored in your device, are you sure?");
        alert.setIcon(R.drawable.warning);

        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                borra();
            }
        });

        alert.setNegativeButton("NO", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    void borra(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,

                "datos", null, 2 );

        SQLiteDatabase bd = admin.getWritableDatabase();

        bd.delete("operaciones", null, null);
        bd.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'operaciones'");
        bd.close();
    }

    public void ConfigSystemFunction(View view){
        startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
    }

    public void GrabarFunction(View view) {

        String cabN  = ed1.getText().toString();
        String terN  = ed2.getText().toString();
        String secN  = ed3.getText().toString();
        String serN  = ed4.getText().toString();
        int posN = PosList.getSelectedItemPosition();
        int locN = locList.getSelectedItemPosition();

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(cab, cabN);
        editor.putString(ter, terN);
        editor.putString(sec, secN);
        editor.putString(ser, serN);
        if(QrSiNo.isChecked()){
            editor.putBoolean(qr,true);
        }else{
            editor.putBoolean(qr,false);
        }
        editor.putInt(pos, posN);
        editor.putInt(loc, locN);
        editor.apply();
        Toast.makeText(getBaseContext(),"Config Saved",Toast.LENGTH_SHORT).show();
        Intent myIntent = new Intent(ConfigActivity.this,MainActivity.class);
        ConfigActivity.this.startActivity(myIntent);
    }

    public void CancelarFunction(View view){
        Intent myIntent = new Intent(ConfigActivity.this,MainActivity.class);
        ConfigActivity.this.startActivity(myIntent);
    }

    public void SalirFunction(View view){
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    public void CopiardatosFunction(View view) throws IOException {
        File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "datos");
        File currentDB = getApplicationContext().getDatabasePath("datos");
        if (currentDB.exists()) {
            Toast.makeText(getBaseContext(),"Database Copied to Downloads",Toast.LENGTH_SHORT).show();
            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
        }else{
            Toast.makeText(getBaseContext(),"No Database to Copy",Toast.LENGTH_SHORT).show();
        }
    }
}
