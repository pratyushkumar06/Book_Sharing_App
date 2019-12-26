package personal.project.android.booksharingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import personal.project.android.booksharingapp.ui.login.Login;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        FloatingActionButton fab=findViewById(R.id.floatingActionButton2);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,PostActivity.class);
                startActivity(intent);
            }
        });
        mAuth=FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null && user.getDisplayName()==null && user.getPhotoUrl()==null){  //As creating a profile is an essential process it will cause to create profile
            Intent in=new Intent(MainActivity.this,Account.class);
            startActivity(in);
            Toast.makeText(MainActivity.this,"Create Profile to proceed",Toast.LENGTH_LONG).show();
        }
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.logout){
            mAuth.signOut();
            Intent in=new Intent(MainActivity.this, Login.class);
            startActivity(in);
            finish();
        }
        if(id==R.id.settings){
            Intent in=new Intent(MainActivity.this, Account.class);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }
}
