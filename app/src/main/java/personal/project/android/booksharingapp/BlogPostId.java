package personal.project.android.booksharingapp;

import androidx.annotation.NonNull;

public class BlogPostId {
    public String BlogPostid;


    //We pass the id to this class and it simply retursns it so that we can use it elsewhere
    public <T extends BlogPostId>T withId(@NonNull final String id){
        this.BlogPostid=id;
        return (T) this;
    }
}
