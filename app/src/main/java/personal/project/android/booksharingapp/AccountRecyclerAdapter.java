package personal.project.android.booksharingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class AccountRecyclerAdapter extends RecyclerView.Adapter<AccountRecyclerAdapter.ViewHolder> {

    public List<BookDetails> bookDetails;
    // public List<User> userList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private Boolean button=true;
    private FirebaseAuth auth;

    public AccountRecyclerAdapter(){};

    public AccountRecyclerAdapter(List<BookDetails> bookDetails){
        this.bookDetails=bookDetails;   //Used for getting the values of the passed list
        //  this.userList=userList;
    }
    @NonNull
    @Override
    public AccountRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem2,parent,false);
        context=parent.getContext();

        firebaseFirestore=FirebaseFirestore.getInstance();

        auth=FirebaseAuth.getInstance();


        return new AccountRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AccountRecyclerAdapter.ViewHolder holder, final int position) {

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

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts").document(BlogPostid)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_LONG).show();
                                Log.d("TAG", "DocumentSnapshot successfully deleted!");
                                bookDetails.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error deleting document", e);
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
        private TextView title,author,publisher,genre,cmntct,name,date;
        private ImageView cbtn,user,post;
        private ImageView delete;
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
            delete=view.findViewById(R.id.del);

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