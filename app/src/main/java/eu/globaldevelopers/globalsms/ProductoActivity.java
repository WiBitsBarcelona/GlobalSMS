package eu.globaldevelopers.globalsms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ProductoActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MySupply" ;
    public static final String producto = "productoKey";
    public static final String tipotrans = "tipoKey";

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_producto);
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



    public void DieselFunction(View View){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(producto, "1");
        editor.putString(tipotrans, "Nueva");
        editor.apply();
        Toast.makeText(getBaseContext(),this.getString(R.string.toast_diesel),Toast.LENGTH_SHORT).show();
        ProductoActivity.this.finish();
        Intent Intent = new Intent(this, PinPadActivity.class);
        startActivity(Intent);
    }

    public void AdblueFunction(View View){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(producto, "13");
        editor.putString(tipotrans, "Nueva");
        editor.apply();
        Toast.makeText(getBaseContext(),this.getString(R.string.toast_adblue),Toast.LENGTH_SHORT).show();
        ProductoActivity.this.finish();
        Intent Intent = new Intent(this, PinPadActivity.class);
        startActivity(Intent);
    }

    public void RedFunction(View View){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(producto, "15");
        editor.putString(tipotrans, "Nueva");
        editor.apply();
        Toast.makeText(getBaseContext(),this.getString(R.string.toast_rojo),Toast.LENGTH_SHORT).show();
        ProductoActivity.this.finish();
        Intent Intent = new Intent(this, PinPadActivity.class);
        startActivity(Intent);
    }

    public void CancelFunction(View view){
        ProductoActivity.this.finish();
        Intent Intent = new Intent(this, MainActivity.class);
        startActivity(Intent);
    }
}
