package eu.globaldevelopers.globalsms;

import java.util.*;

import android.app.*;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;

import android.os.*;

public class Splash extends AppCompatActivity {
    //------------------DECLARANDO VARIALBES---------------------------//
    private Timer timer;
    private ProgressBar progressBar;
    private TextView footerText;
    private int i = 0;
    //---------------------FIN-----------------------------//

    public class BootReciever extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Intent myIntent = new Intent(context, MainActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myIntent);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_splash);

        String versionName = BuildConfig.VERSION_NAME;
        int y = Calendar.getInstance().get(Calendar.YEAR);

        ////Toast.makeText(getBaseContext(), versionName + " " + y, //Toast.LENGTH_SHORT).show();

        footerText = (TextView) findViewById(R.id.footerText);
        footerText.setText("© GlobalTank " + y + " - GlobalPay ® - v: " + versionName);

//---------------------------------------SPLASH CON BARRA DE PROGRESO ------------------------------------------------//
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        final long intervalo = 45;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (i < 100){
                    progressBar.setProgress(i);
                    i++;
                }else{
                    timer.cancel();
                    Intent intent = new Intent(Splash.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },0,intervalo);
//--------------------------------------- FIN ------------------------------------------------//
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

}
