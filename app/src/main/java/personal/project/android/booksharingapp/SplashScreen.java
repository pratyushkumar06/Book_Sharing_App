package personal.project.android.booksharingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import personal.project.android.booksharingapp.ui.login.Login;

import static java.lang.Thread.sleep;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    sleep(2500);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally{
                    Intent intent=new Intent(SplashScreen.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }

        });
        t.start();
    }
}
