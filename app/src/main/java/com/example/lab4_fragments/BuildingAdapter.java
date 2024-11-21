package com.example.lab4_fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab4_fragments.dao.CategoryDao;
import com.example.lab4_fragments.database.AppDatabaseSingleton;
import com.example.lab4_fragments.entities.Building;
import com.example.lab4_fragments.entities.Category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.BuildingViewHolder> {

    private List<Building> buildingList;
    private OnBuildingClickListener onBuildingClickListener;
    private Map<Integer, String> categoryCache = new HashMap<>();

    public interface OnBuildingClickListener {
        void onBuildingClick(int position);
    }

    public BuildingAdapter(List<Building> buildingList, OnBuildingClickListener onBuildingClickListener) {
        this.buildingList = buildingList;
        this.onBuildingClickListener = onBuildingClickListener;
    }

    public void setBuildingList(List<Building> buildingList) {
        this.buildingList = buildingList;
    }

    @NonNull
    @Override
    public BuildingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_building, parent, false);
        return new BuildingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingViewHolder holder, int position) {
        Building building = buildingList.get(position);
        holder.title.setText(building.title);
        holder.description.setText(building.description);

        // Obtener nombre de categoría
        String categoryName = getCategoryName(holder.itemView.getContext(), building.categoryId);
        holder.category.setText(categoryName);

        int imageResId = holder.itemView.getContext().getResources().getIdentifier(building.imageResId, "drawable", holder.itemView.getContext().getPackageName());
        holder.image.setImageResource(imageResId);

        // Manejar el clic en el elemento
        holder.itemView.setOnClickListener(v -> {
            if (onBuildingClickListener != null) {
                onBuildingClickListener.onBuildingClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return buildingList.size();
    }

    private String getCategoryName(Context context, int categoryId) {
        if (categoryCache.containsKey(categoryId)) {
            return categoryCache.get(categoryId);
        } else {
            CategoryDao categoryDao = AppDatabaseSingleton.getInstance(context).categoryDao();
            Category category = categoryDao.getCategoryByIdSync(categoryId);
            if (category != null) {
                categoryCache.put(categoryId, category.categoryName);
                return category.categoryName;
            } else {
                return "Sin categoría";
            }
        }
    }

    public static class BuildingViewHolder extends RecyclerView.ViewHolder {
        TextView title, category, description;
        ImageView image;

        public BuildingViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.building_title);
            category = itemView.findViewById(R.id.building_category);
            description = itemView.findViewById(R.id.building_description);
            image = itemView.findViewById(R.id.building_image);
        }
    }
}
