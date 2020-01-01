package personal.project.android.booksharingapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

import personal.project.android.booksharingapp.MainActivity;
import personal.project.android.booksharingapp.R;
import personal.project.android.booksharingapp.ui.login.Login;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    EditText email,pass;
    String username,password;
    TextView textView;
    private FirebaseFirestore firebaseFirestore;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        textView=findViewById(R.id.textView2);
        email=findViewById(R.id.editText);
        pass=findViewById(R.id.editText2);
        button=findViewById(R.id.button);
        firebaseFirestore=FirebaseFirestore.getInstance();
        progressBar=findViewById(R.id.progressBar2);
        mAuth=FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(SignUp.this, Login.class);
                startActivity(intent);
                finish();

            }
        });
    }

    @Override
    protected void onStart() {  //To check if any current user is signed in
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();

    }

    private void registerUser() {
        username = email.getText().toString();
        password = pass.getText().toString();
        if(username.isEmpty() ||password.isEmpty()|| password.length()<6)
        { if (username.isEmpty()) {
            email.setError("Username cannot be left blank");
            email.requestFocus();
            return;
        }
            if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
                email.setError("Enter a Valid Email");
                email.requestFocus();
                return;
            }
            if(password.isEmpty() || password.length()<6){
                pass.setError("Minimum Length of password should be 6 charecters");
                pass.requestFocus();
                return;
            }

        }
        else {

            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        finish();
                        Toast.makeText(SignUp.this,"Registered Successfully",Toast.LENGTH_LONG).show();
                        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                        if(user!=null && user.getDisplayName()==null && user.getPhotoUrl()==null){  //As creating a profile is an essential process it will cause to create profile
                            HashMap<String ,String> map =new HashMap<>();
                            map.put("Url","https://firebasestorage.googleapis.com/v0/b/fir-storage-1739c.appspot.com/o/profile_pic.png?alt=media&token=e5da74aa-3d7b-4fbc-b094-d51cd9919e23");
                            map.put("name","username");
                            String user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
                            firebaseFirestore.collection("users").document(user_id)
                                    .set(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SignUp.this,"Success",Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(SignUp.this,"Error",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                    else {
                        if(task.getException() instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(SignUp.this,"Email id Already Registered",Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(SignUp.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}
