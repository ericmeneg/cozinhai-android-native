package com.example.cozinhai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserCommentsAdapter extends RecyclerView.Adapter<UserCommentsAdapter.CommentViewHolder> {

    private final List<UserComment> comments;

    public UserCommentsAdapter(List<UserComment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        UserComment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvRecipeTitle;
        private final RatingBar commentRatingBar;
        private final TextView tvCommentText;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipeTitle = itemView.findViewById(R.id.tvRecipeTitle);
            commentRatingBar = itemView.findViewById(R.id.commentRatingBar);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
        }

        public void bind(UserComment comment) {
            tvRecipeTitle.setText(comment.getTitle() != null ? comment.getTitle() : "Receita #" + comment.getRecipeId());
            commentRatingBar.setRating(comment.getGrade());
            tvCommentText.setText(comment.getComment());
        }
    }
}
