package personal.project.android.booksharingapp.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import javax.annotation.Nullable;

import personal.project.android.booksharingapp.Account;
import personal.project.android.booksharingapp.AccountRecyclerAdapter;
import personal.project.android.booksharingapp.BookDetails;
import personal.project.android.booksharingapp.HomeRecyclerAdapter;
import personal.project.android.booksharingapp.R;

public class NotificationsFragment extends Fragment {

    private Button button;
    private TextView textView;
    private ImageView imageView;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private AccountRecyclerAdapter accountRecyclerAdapter;
    private RecyclerView recyclerView;
    private List<BookDetails> list;
    String curid;
    private  String blogId;
    Boolean LoadedforfirstTime=true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_notifications, container, false);
        button=v.findViewById(R.id.button3);
        textView=v.findViewById(R.id.textView3);
        imageView=v.findViewById(R.id.imageView2);
        recyclerView=v.findViewById(R.id.rev);
        auth=FirebaseAuth.getInstance();
        list=new ArrayList<>();

        firebaseFirestore=FirebaseFirestore.getInstance();
        accountRecyclerAdapter=new AccountRecyclerAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(accountRecyclerAdapter);




        if(auth.getCurrentUser()!=null){

            curid=auth.getCurrentUser().getUid();
            firebaseFirestore.collection("users").document(curid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            String uname = task.getResult().getString("name");
                            String url = task.getResult().getString("Url");
                            textView.setText(uname);
                            Glide.with(NotificationsFragment.this).load(url).apply(RequestOptions.circleCropTransform()).into(imageView);
                        }
                    } else {
                        Log.i("Tag", "Error");
                    }
                }
            });

            Query query = firebaseFirestore.collectionGroup("Posts").whereEqualTo("userId", curid).orderBy("timeStamp",Query.Direction.DESCENDING);
            if(auth.getCurrentUser()!=null) {
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {

                            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {//Everytime the data is added the document goes to the BlogPost class and is added to list
                                    blogId = documentChange.getDocument().getId();
                                    BookDetails bookDetails = documentChange.getDocument().toObject(BookDetails.class).withId(blogId);//From here the data is sent to the constructor for gettong the details
                                    if (LoadedforfirstTime) {
                                        list.add(bookDetails);
                                    } else {
                                        list.add(0, bookDetails);  //Adds the new post to the top
                                    }
                                    accountRecyclerAdapter.notifyDataSetChanged();
                                }

                                if(documentChange.getType()==DocumentChange.Type.REMOVED){
                                    recyclerView.getRecycledViewPool().clear();
                                    list.clear();
                                    accountRecyclerAdapter.notifyDataSetChanged();
                                }

                            }

                            LoadedforfirstTime = false;
                        }
                    }

                });
            }
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(getActivity(), Account.class);
                startActivity(in);

            }
        });


        return v;
    }

}
