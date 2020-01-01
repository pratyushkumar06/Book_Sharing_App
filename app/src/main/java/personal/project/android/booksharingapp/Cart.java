package personal.project.android.booksharingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import personal.project.android.booksharingapp.ui.notifications.NotificationsFragment;

public class Cart extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<BookDetails2> list;
    //  private List<User> userList;
    private String curid,blogId;
    private FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot lastVisible;
    private FirebaseAuth auth;
    private CartRecyclerAdapter cartRecyclerAdapter;
    private Boolean LoadedforfirstTime=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView=findViewById(R.id.recycler);
        firebaseFirestore=FirebaseFirestore.getInstance();

        auth=FirebaseAuth.getInstance();

        list=new ArrayList<>();
        //  userList=new ArrayList<>();
        cartRecyclerAdapter=new CartRecyclerAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(Cart.this));
        recyclerView.setAdapter(cartRecyclerAdapter);
        if(auth.getCurrentUser()!=null){

            curid=auth.getCurrentUser().getUid();

            Query query = firebaseFirestore.collection("users/").document(curid).collection("/Cart").orderBy("timeStamp",Query.Direction.DESCENDING);;
            if(auth.getCurrentUser()!=null) {
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {

                            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {//Everytime the data is added the document goes to the BlogPost class and is added to list
                                    blogId = documentChange.getDocument().getId();
                                    BookDetails2 bookDetails = documentChange.getDocument().toObject(BookDetails2.class).withId(blogId);//From here the data is sent to the constructor for gettong the details
                                    if (LoadedforfirstTime) {
                                        list.add(bookDetails);
                                    } else {
                                        list.add(0, bookDetails);  //Adds the new post to the top
                                    }
                                    cartRecyclerAdapter.notifyDataSetChanged();
                                }

                                if(documentChange.getType()==DocumentChange.Type.REMOVED){
                                    recyclerView.getRecycledViewPool().clear();
                                    //list.clear();
                                    int position=getIntent().getIntExtra("val",0);
                                    cartRecyclerAdapter.notifyItemRemoved(position);
                                    cartRecyclerAdapter.notifyDataSetChanged();
                                    finish();
                                }

                            }

                            LoadedforfirstTime = false;
                        }
                    }

                });
            }
        }
        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
