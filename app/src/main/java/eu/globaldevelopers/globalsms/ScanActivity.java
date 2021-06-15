package eu.globaldevelopers.globalsms;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import eu.globaldevelopers.globalsms.Enums.ConfigEnum;
import eu.globaldevelopers.globalsms.Helpers.LocaleHelper;


public class ScanActivity extends AppCompatActivity {

    public static final String langKey = ConfigEnum.langKey;
    public static final String MyPREFERENCES = ConfigEnum.MyPREFERENCES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //SET LANG
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String lang = sharedpreferences.getString(ConfigEnum.langKey, "en");
        LocaleHelper.setAppLocale(lang, this);

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
}
