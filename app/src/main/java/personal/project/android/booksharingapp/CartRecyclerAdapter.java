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

import static java.security.AccessController.getContext;

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.ViewHolder> {
    public List<BookDetails2> bookDetails;
    // public List<User> userList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private Boolean button=true;
    private FirebaseAuth auth;
    View v;

    public CartRecyclerAdapter(){};

    public CartRecyclerAdapter(List<BookDetails2> bookDetails){
        this.bookDetails=bookDetails;   //Used for getting the values of the passed list
        //  this.userList=userList;
    }
    @NonNull
    @Override
    public CartRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         v= LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem3,parent,false);
        context=parent.getContext();

        firebaseFirestore=FirebaseFirestore.getInstance();

        auth=FirebaseAuth.getInstance();


        return new CartRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.init();

        final String BlogPostid=bookDetails.get(position).BlogPostid;
        String ti=bookDetails.get(position).getTitle();
        holder.setTitle(ti);

        String url=bookDetails.get(position).getUrl();
        holder.setPostimage(url);

        holder.setValues(bookDetails.get(position).getAuthor(),bookDetails.get(position).getPublisher(),bookDetails.get(position).getGenre());

        final String curid=FirebaseAuth.getInstance().getUid();
        final int pos=position;
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                firebaseFirestore.collection("users/").document(curid).collection("Cart/").document(BlogPostid)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_LONG).show();
                                Log.d("TAG", "DocumentSnapshot successfully deleted!");
                               // bookDetails.remove(position);
                                Intent intent=new Intent(context,Cart.class);
                                intent.putExtra("val",pos);
                                v.getContext().startActivity(intent);
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
        private TextView title,author,publisher,genre,cmntct,remove;
        private ImageView post;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
        }
        public void init(){

            author=view.findViewById(R.id.author);
            publisher=view.findViewById(R.id.publisher);
            genre=view.findViewById(R.id.genre);
            cmntct=view.findViewById(R.id.commentcnt);
            remove=view.findViewById(R.id.remove);
        }

        public void setValues(String au,String pu,String ge){
            author.setText(au);
            publisher.setText(pu);
            genre.setText(ge);
        }

        public void setTitle(String t){
            title=view.findViewById(R.id.title);
            title.setText(t);
        }

        public void setPostimage(String uri){
            post=view.findViewById(R.id.post);
            Glide.with(context).load(uri).into(post);

        }

    }
}
