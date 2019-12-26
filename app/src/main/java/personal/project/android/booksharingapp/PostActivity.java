package personal.project.android.booksharingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    private EditText title,author,publisher;
    private Spinner spinner;
    private FirebaseAuth auth;
    private ImageView imageView;
    private int INT_CONST=32;
    private Uri uri,url;
    private Bitmap bitmap;
    private ProgressDialog getProgressDialog,progressDialog;
    private Button post;
    private FirebaseAuth firebaseAuth;
    private String user_id;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        imageView=findViewById(R.id.imageview);
        title=findViewById(R.id.title);
        author=findViewById(R.id.author);
        publisher=findViewById(R.id.publisher);
        spinner=findViewById(R.id.spinner);
        post=findViewById(R.id.post);
        getProgressDialog=new ProgressDialog(PostActivity.this,R.style.AppCompatAlertDialogStyle);
        progressDialog=new ProgressDialog(PostActivity.this,R.style.AppCompatAlertDialogStyle);
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(auth.getCurrentUser()!=null)
            user_id= Objects.requireNonNull(auth.getCurrentUser()).getUid();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Choose Image"), INT_CONST);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getTitle = title.getText().toString();
                String getAuthor = author.getText().toString();
                String getPublisher = publisher.getText().toString();
                String Genre = spinner.getSelectedItem().toString();

                if (getTitle.isEmpty() || getAuthor.isEmpty() || getPublisher.isEmpty()) {
                    if (getTitle.isEmpty())
                        title.setError("Please Enter Title");

                    if (getAuthor.isEmpty())
                        author.setError("Please Enter Authors name");

                    if (getPublisher.isEmpty())
                        publisher.setError("Please Enter Publishers name");
                } else {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("userId", user_id);
                    map.put("url", url.toString());
                    map.put("title", getTitle);
                    map.put("author", getAuthor);
                    map.put("publisher", getPublisher);
                    map.put("genre", Genre);
                    map.put("timeStamp", FieldValue.serverTimestamp());
                    progressDialog.setTitle("Uploading");
                    progressDialog.show();
                    firebaseFirestore.collection("Posts").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            Toast.makeText(PostActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Intent in = new Intent(PostActivity.this, MainActivity.class);
                            startActivity(in);
                            finish();
                        }
                    });
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==INT_CONST && resultCode==RESULT_OK && data.getData()!=null){
             uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);
            uploadFile(bitmap);
            getProgressDialog.setTitle("Uploading Image");
            getProgressDialog.show();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadFile(Bitmap bitmap) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        System.out.println(auth.getCurrentUser().getUid());
        final StorageReference ImagesRef = storageRef.child("books/"+ auth.getCurrentUser().getUid()+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = ImagesRef.putBytes(data);

        System.out.println("Error2");

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("Error:",exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                System.out.println("Error1");

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.i("problem", Objects.requireNonNull(task.getException()).toString());
                        }

                        return ImagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            getProgressDialog.dismiss();
                            Toast.makeText(PostActivity.this,"Upload Successfull",Toast.LENGTH_SHORT).show();
                            url=downloadUri;
                            assert downloadUri != null;
                            Log.i("seeThisUri", downloadUri.toString());// This is the one you should store


                        } else {
                            getProgressDialog.dismiss();
                            Log.i("wentWrong","downloadUri failure");
                        }
                    }
                });
            }
        });

    }
}
