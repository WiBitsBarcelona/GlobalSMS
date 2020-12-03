package eu.globaldevelopers.globalsms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ProductoActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MySupply" ;
    public static final String producto = "productoKey";
    public static final String tipotrans = "tipoKey";

    public static final String MyPRECIOS = "MyPrecios" ;

    public static String dieselactual;
    public static String adblueactual;
    public static String reddieselactual;
    public static String biodieselactual;

    SharedPreferences sharedpreferences;
    SharedPreferences sharedpreferences2;

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

        sharedpreferences2 = getSharedPreferences(MyPRECIOS, Context.MODE_PRIVATE);

        dieselactual = sharedpreferences2.getString("dieselKey", "0.00");
        adblueactual = sharedpreferences2.getString("adblueKey", "0.00");
        reddieselactual = sharedpreferences2.getString("reddieselKey", "0.00");
        biodieselactual = sharedpreferences2.getString("biodieselKey", "0.00");

        if(dieselactual.equals("0.00")){
            //Diesel no tiene precio no lo muestro
            Button btn=(Button)findViewById(R.id.btnDiesel);
            btn.setVisibility(Button.INVISIBLE);
        }
        if(adblueactual.equals("0.00")){
            //Adblue no tiene precio no lo muestro
            Button btn=(Button)findViewById(R.id.btnAdblue);
            btn.setVisibility(Button.INVISIBLE);
        }
        if(reddieselactual.equals("0.00")){
            //Diesel Rojo no tiene precio no lo muestro
            Button btn=(Button)findViewById(R.id.btnReddiesel);
            btn.setVisibility(Button.INVISIBLE);
        }
        if(biodieselactual.equals("0.00")){
            //Bio Diesel no tiene precio no lo muestro
            Button btn=(Button)findViewById(R.id.btnBiodiesel);
            btn.setVisibility(Button.INVISIBLE);
        }
    }



    public void DieselFunction(View View){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(producto, "1");
        editor.putString(tipotrans, "Nueva");
        editor.apply();
        //Toast.makeText(getBaseContext(),this.getString(R.string.toast_diesel),//Toast.LENGTH_SHORT).show();
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
        //Toast.makeText(getBaseContext(),this.getString(R.string.toast_adblue),//Toast.LENGTH_SHORT).show();
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
        //Toast.makeText(getBaseContext(),this.getString(R.string.toast_rojo),//Toast.LENGTH_SHORT).show();
        ProductoActivity.this.finish();
        Intent Intent = new Intent(this, PinPadActivity.class);
        startActivity(Intent);
    }

    public void BioFunction(View View){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(producto, "14");
        editor.putString(tipotrans, "Nueva");
        editor.apply();
        //Toast.makeText(getBaseContext(),this.getString(R.string.toast_bio),//Toast.LENGTH_SHORT).show();
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
