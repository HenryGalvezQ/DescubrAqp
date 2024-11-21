package com.example.lab4_fragments.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab4_fragments.CommentAdapter;
import com.example.lab4_fragments.R;
import com.example.lab4_fragments.database.AppDatabase;
import com.example.lab4_fragments.database.AppDatabaseSingleton;
import com.example.lab4_fragments.entities.Building;
import com.example.lab4_fragments.entities.Comment;
import com.example.lab4_fragments.entities.User;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {
    private static final String ARG_BUILDING_ID = "building_id";
    private int buildingId;
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private Button btnView360;
    private Button btnViewMansion;
    private EditText commentInput;
    private Button submitCommentButton;
    private RatingBar ratingBar;
    private Building building;
    private AppDatabase db;
    private Integer userId = null; // Initially null

    public static DetailFragment newInstance(int buildingId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_BUILDING_ID, buildingId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            buildingId = getArguments().getInt(ARG_BUILDING_ID);
        }

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        ImageView imageView = view.findViewById(R.id.image_view);
        TextView titleTextView = view.findViewById(R.id.title_text_view);
        TextView descriptionTextView = view.findViewById(R.id.description_text_view);
        btnView360 = view.findViewById(R.id.btn_view_360);
        btnViewMansion = view.findViewById(R.id.btn_view_mansion);
        commentInput = view.findViewById(R.id.comment_input);
        submitCommentButton = view.findViewById(R.id.submit_comment_button);
        ratingBar = view.findViewById(R.id.rating_bar);

        commentsRecyclerView = view.findViewById(R.id.comments_recycler_view);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        commentAdapter = new CommentAdapter(commentList, getContext()); // Pass context here
        commentsRecyclerView.setAdapter(commentAdapter);

        db = AppDatabaseSingleton.getInstance(getContext());

        // Obtener el usuario logueado
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", requireActivity().MODE_PRIVATE);
        String email = sharedPreferences.getString("loggedInUser", null);
        if (email != null) {
            // Observar el LiveData<User> para obtener el userId
            db.userDao().getUserByEmail(email).observe(getViewLifecycleOwner(), new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null) {
                        userId = user.userId;
                    } else {
                        userId = -1;
                    }
                }
            });
        } else {
            userId = -1;
        }

        // Cargar la edificación
        db.buildingDao().getBuildingById(buildingId).observe(getViewLifecycleOwner(), building -> {
            if (building != null) {
                this.building = building;
                titleTextView.setText(building.title);
                descriptionTextView.setText(building.description);
                int imageResId = getContext().getResources().getIdentifier(building.imageResId, "drawable", getContext().getPackageName());
                imageView.setImageResource(imageResId);
            }
        });

        // Cargar comentarios
        db.commentDao().getCommentsByBuildingId(buildingId).observe(getViewLifecycleOwner(), comments -> {
            commentList.clear();
            commentList.addAll(comments);
            commentAdapter.setCommentList(commentList);
            // Notify the adapter of data change
            commentAdapter.notifyDataSetChanged();
        });

        btnView360.setOnClickListener(v -> {
            Vista360Fragment vista360Fragment = new Vista360Fragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, vista360Fragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnViewMansion.setOnClickListener(v -> {
            MansionFragment mansionFragment = new MansionFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, mansionFragment)
                    .addToBackStack(null)
                    .commit();
        });

        submitCommentButton.setOnClickListener(v -> addComment());

        return view;
    }

    private void addComment() {
        String commentText = commentInput.getText().toString().trim();
        float rating = ratingBar.getRating();
        if (userId == null || userId == -1) {
            Toast.makeText(getContext(), "Usuario no identificado. Inicie sesión nuevamente.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!commentText.isEmpty()) {
            Comment newComment = new Comment();
            newComment.userId = userId;
            newComment.buildingId = buildingId;
            newComment.commentText = commentText;
            newComment.rating = rating;
            newComment.timestamp = System.currentTimeMillis();

            AppDatabase.databaseWriteExecutor.execute(() -> {
                db.commentDao().insertComment(newComment);
            });

            requireActivity().runOnUiThread(() -> {
                commentInput.setText("");
                ratingBar.setRating(0);
                Toast.makeText(getContext(), "Comentario agregado.", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(getContext(), "Por favor, ingresa un comentario.", Toast.LENGTH_SHORT).show();
        }
    }
}
