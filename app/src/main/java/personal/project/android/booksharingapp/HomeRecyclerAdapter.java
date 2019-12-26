package personal.project.android.booksharingapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

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
    }

    @Override
    public int getItemCount() {
        return bookDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView title,author,publisher,genre,cmntct,name,date;
        private ImageView cbtn,user;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
        }
        public void init(){
            title=view.findViewById(R.id.title);
            author=view.findViewById(R.id.author);
            publisher=view.findViewById(R.id.publisher);
            genre=view.findViewById(R.id.genre);
            cbtn=view.findViewById(R.id.comment);
            cmntct=view.findViewById(R.id.commentcnt);
            date=view.findViewById(R.id.date);

        }

        public void setname(String n){
            name=view.findViewById(R.id.name);
            name.setText(n);
        }

        public void setuserimage(String urii){
            user=view.findViewById(R.id.userimage);
            Glide.with(context).load(urii).apply(RequestOptions.circleCropTransform()).into(user);

        }
    }
}
