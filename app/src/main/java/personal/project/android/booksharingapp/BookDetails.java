package personal.project.android.booksharingapp;

import java.util.Date;

public class BookDetails extends BlogPostId{
   private String userId;
   private String url;
   private String author;
   private String title;
   private String publisher;
   private String genre;

   private Date timeStamp;

   public BookDetails(){}
    public BookDetails(String userId, String url, String author, String title, String publisher, String genre, Date timeStamp) {
        this.userId = userId;
        this.url = url;
        this.author = author;
        this.title = title;
        this.publisher = publisher;
        this.genre = genre;
        this.timeStamp = timeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }





}
