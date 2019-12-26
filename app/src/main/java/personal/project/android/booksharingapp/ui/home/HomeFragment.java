package personal.project.android.booksharingapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import personal.project.android.booksharingapp.BookDetails;
import personal.project.android.booksharingapp.HomeRecyclerAdapter;
import personal.project.android.booksharingapp.R;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<BookDetails> list;
    //  private List<User> userList;
    User user;
    private FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot lastVisible;
    private FirebaseAuth mauth;
    private HomeRecyclerAdapter homeRecyclerAdapter;
    private Boolean isdataloadedfirstTime=true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.recycler);
        firebaseFirestore=FirebaseFirestore.getInstance();

        mauth=FirebaseAuth.getInstance();

        list=new ArrayList<>();
        //  userList=new ArrayList<>();
        homeRecyclerAdapter=new HomeRecyclerAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(homeRecyclerAdapter);
        if(mauth.getCurrentUser()!=null) {
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);  //When it reaches the end of the limit more posts will be loaded
                    if (reachedBottom) {
                        loadNext();
                    }
                }
            });
            Query firstquery = firebaseFirestore.collection("Posts").orderBy("timeStamp", Query.Direction.DESCENDING).limit(3);
            firstquery.addSnapshotListener(new EventListener<QuerySnapshot>() {  //To access RealtimeData
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    //Pagination decides the number of posts that are to be loaded per page
                    if(mauth.getCurrentUser()!=null){
                        if (!queryDocumentSnapshots.isEmpty()) {
                            if (isdataloadedfirstTime) {
                                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                // list.clear();
                            }
                            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                   // String blogId = documentChange.getDocument().getId();
                                    final BookDetails bookDetails = documentChange.getDocument().toObject(BookDetails.class);//From here the data is sent to the constructor for gettong the details
                                    final String bloguid = documentChange.getDocument().getString("user_id");
                                    if (isdataloadedfirstTime) {
                                        list.add(bookDetails);
                                    } else {
                                        list.add(0, bookDetails);  //Adds the new post to the top
                                    }
                           /* firebaseFirestore.collection("users").document(bloguid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        user = task.getResult().toObject(User.class);

                                       // if (isdataloadedfirstTime) {
                                            userList.add(user);
                                       // } //else {
                                           // userList.add(0, user);//Adds the new post to the top
                                        //}
                                        blogRecyclerAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                           */
                                    homeRecyclerAdapter.notifyDataSetChanged();
                                }
                            }
                            isdataloadedfirstTime = false;
                        }
                    }
                }
            });

            // Inflate the layout for this fragment
        }
        return view;
    }
/*
    @Override public void onResume() {
        super.onResume();
        lastVisible =null;
        isdataloadedfirstTime = true;
    }
*/

    public void loadNext(){
        Query nextquery=firebaseFirestore.collection("Posts").orderBy("timeStamp",Query.Direction.DESCENDING).startAfter(lastVisible).limit(3);
        nextquery.addSnapshotListener(new EventListener<QuerySnapshot>() {  //To access RealtimeData
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(mauth.getCurrentUser()!=null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {                   //Everytime the data is added the document goes to the BlogPost class and is added to list
                                final String blogId=documentChange.getDocument().getId();
                                final String bloguid = documentChange.getDocument().getString("user_id");
                                final BookDetails bookDetails = documentChange.getDocument().toObject(BookDetails.class);
                                list.add(bookDetails);
                             /*   firebaseFirestore.collection("users").document(bloguid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            user = task.getResult().toObject(User.class);
                                                userList.add(user);
                                            blogRecyclerAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });*/
                               homeRecyclerAdapter.notifyDataSetChanged();
                            }
                            if(documentChange.getType()==DocumentChange.Type.REMOVED){
                                recyclerView.getRecycledViewPool().clear();
                                homeRecyclerAdapter.notifyDataSetChanged();

                                list.clear();
                                homeRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                        isdataloadedfirstTime=false;
                    }
                }
            }
        });

    }

}
