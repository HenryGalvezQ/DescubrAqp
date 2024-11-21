package com.example.lab4_fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab4_fragments.database.AppDatabase;
import com.example.lab4_fragments.database.AppDatabaseSingleton;
import com.example.lab4_fragments.entities.Comment;
import com.example.lab4_fragments.entities.User;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private Context context;
    private AppDatabase db;

    public CommentAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
        this.db = AppDatabaseSingleton.getInstance(context);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.comment_item, parent, false);  // Adjusted to your layout file name
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        // Obtener el usuario asociado al comentario
        LiveData<User> userLiveData = db.userDao().getUserById(comment.userId);
        userLiveData.observeForever(new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    holder.usernameTextView.setText(user.firstName + " " + user.lastName);
                } else {
                    holder.usernameTextView.setText("Usuario desconocido");
                }
                // Remover el observer para evitar fugas de memoria
                userLiveData.removeObserver(this);
            }
        });

        holder.textTextView.setText(comment.commentText);

        // Set the rating text
        holder.ratingTextView.setText(String.valueOf(comment.rating));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
        notifyDataSetChanged();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, textTextView, ratingTextView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.comment_username);
            textTextView = itemView.findViewById(R.id.comment_text);
            ratingTextView = itemView.findViewById(R.id.comment_rating_text);
        }
    }
}
