package personal.project.android.booksharingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder> {

    public List<BookDetails> bookDetails;
    // public List<User> userList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private Boolean button=true;
    private FirebaseAuth auth;

    public HomeRecyclerAdapter(){};

    public HomeRecyclerAdapter(List<BookDetails> bookDetails){
        this.bookDetails=bookDetails;   //Used for getting the values of the passed list
        //  this.userList=userList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem,parent,false);
        context=parent.getContext();

        firebaseFirestore=FirebaseFirestore.getInstance();

        auth=FirebaseAuth.getInstance();


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.init();
        final String uid=bookDetails.get(position).getUserId();
        firebaseFirestore.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        String uname = task.getResult().get("name").toString();
                        String url = task.getResult().get("Url").toString();
                        holder.setname(uname);
                        holder.setuserimage(url);
                    }
                } else {
                    Log.i("Tag", "Error");
                }
            }
        });

        final String BlogPostid=bookDetails.get(position).BlogPostid;
        String ti=bookDetails.get(position).getTitle();
        holder.setTitle(ti);

        String url=bookDetails.get(position).getUrl();
        holder.setPostimage(url);

        holder.setValues(bookDetails.get(position).getAuthor(),bookDetails.get(position).getPublisher(),bookDetails.get(position).getGenre());

        System.out.println(uid);

        long milisecs=bookDetails.get(position).getTimeStamp().getTime();
        String datestr= DateFormat.format("dd/MM/yyyy",new Date(milisecs)).toString();
        holder.setTime(datestr);

        firebaseFirestore.collection("Posts/").document(BlogPostid.toString()).collection("/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        int count = queryDocumentSnapshots.size();
                        holder.updatecmntcount(count);
                    } else {
                        holder.updatecmntcount(0);
                    }

                }
            }
        });

        holder.cbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(context,Comments.class);
                in.putExtra("blogPostId",BlogPostid);  //We pass the BlogPost Id too
                context.startActivity(in);
            }
        });

        holder.cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curr_id=FirebaseAuth.getInstance().getUid();

                HashMap<String, Object> map = new HashMap<>();
                map.put("url", bookDetails.get(position).getUrl());
                map.put("title",bookDetails.get(position).getTitle());
                map.put("author", bookDetails.get(position).getAuthor());
                map.put("publisher", bookDetails.get(position).getPublisher());
                map.put("genre", bookDetails.get(position).getGenre());
                map.put("timeStamp", FieldValue.serverTimestamp());
                if(curr_id!=null)
                firebaseFirestore.collection("users/").document(curr_id).collection("/Cart").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context,"Added to Cart",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return bookDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView title,author,publisher,genre,cmntct,name,date,cart;
        private ImageView cbtn,user,post;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
        }
        public void init(){

            author=view.findViewById(R.id.author);
            publisher=view.findViewById(R.id.publisher);
            genre=view.findViewById(R.id.genre);
            cbtn=view.findViewById(R.id.comment);
            cmntct=view.findViewById(R.id.commentcnt);
            cart=view.findViewById(R.id.cart);

        }

        public void setValues(String au,String pu,String ge){
           author.setText(au);
           publisher.setText(pu);
           genre.setText(ge);
        }
        public void setname(String n){
            name=view.findViewById(R.id.name);
            name.setText(n);
        }

        public void setuserimage(String urii){
            user=view.findViewById(R.id.userimage);
            Glide.with(context).load(urii).apply(RequestOptions.circleCropTransform()).into(user);

        }
        public void setTitle(String t){
            title=view.findViewById(R.id.title);
            title.setText(t);
        }

        public void setPostimage(String uri){
            post=view.findViewById(R.id.post);
            Glide.with(context).load(uri).into(post);

        }

        public void setTime(String t){
            date=view.findViewById(R.id.date);
            date.setText(t);
        }
        @SuppressLint("SetTextI18n")
        public void updatecmntcount(int c){
            cmntct.setText(c+" Comments");
        }

    }
}
