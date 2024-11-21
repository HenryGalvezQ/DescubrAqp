package com.example.lab4_fragments.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.lab4_fragments.BuildingAdapter;
import com.example.lab4_fragments.R;
import com.example.lab4_fragments.database.AppDatabaseSingleton;
import com.example.lab4_fragments.entities.Building;
import com.example.lab4_fragments.BuildingRepository;

import java.util.ArrayList;
import java.util.List;

public class EdificacionesFragment extends Fragment {
    private RecyclerView recyclerView;
    private BuildingAdapter buildingAdapter;
    private List<Building> buildingList = new ArrayList<>();
    private List<Building> filteredBuildingList = new ArrayList<>();

    private EditText searchBar;
    private RadioGroup filterOptions;
    private TextView emptyView;
    private BuildingRepository buildingRepository;

    public static EdificacionesFragment newInstance() {
        return new EdificacionesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edificaciones, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchBar = view.findViewById(R.id.search_bar);
        filterOptions = view.findViewById(R.id.filter_options);
        emptyView = view.findViewById(R.id.empty_view);

        buildingAdapter = new BuildingAdapter(filteredBuildingList, position -> {
            // Manejar el clic en el elemento
            Building selectedBuilding = filteredBuildingList.get(position);
            int buildingId = selectedBuilding.buildingId;
            DetailFragment detailFragment = DetailFragment.newInstance(buildingId);
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(buildingAdapter);

        buildingRepository = new BuildingRepository(getContext());
        buildingRepository.getBuildingList().observe(getViewLifecycleOwner(), buildings -> {
            buildingList.clear();
            buildingList.addAll(buildings);
            filter(searchBar.getText().toString());
        });

        // Configurar el listener para el cambio de texto en la barra de búsqueda
        searchBar.addTextChangedListener(new TextWatcher() {
            // Implementación de los métodos...
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita implementar
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No se necesita implementar
            }
        });

        // Configurar el listener para el cambio de opción en el RadioGroup
        filterOptions.setOnCheckedChangeListener((group, checkedId) -> {
            // Reiniciar el filtrado cada vez que se cambia la opción
            filter(searchBar.getText().toString());
        });

        return view;
    }

    private void filter(String text) {
        String lowerText = text.toLowerCase();
        filteredBuildingList.clear();

        int checkedId = filterOptions.getCheckedRadioButtonId();

        for (Building building : buildingList) {
            boolean matches = false;

            if (checkedId == R.id.rb_title) {
                matches = building.title.toLowerCase().contains(lowerText);
            } else if (checkedId == R.id.rb_category) {
                // Necesitas obtener el nombre de la categoría
                String categoryName = getCategoryName(building.categoryId);
                matches = categoryName != null && categoryName.toLowerCase().contains(lowerText);
            } else if (checkedId == R.id.rb_description) {
                matches = building.description.toLowerCase().contains(lowerText);
            }

            if (matches) {
                filteredBuildingList.add(building);
            }
        }

        buildingAdapter.notifyDataSetChanged();
        updateEmptyView();
    }

    private String getCategoryName(int categoryId) {
        // Implementar método para obtener el nombre de la categoría dado el ID
        // Puedes obtenerlo desde la base de datos o mantener un mapa de categorías
        return "Categoría"; // Placeholder
    }

    private void updateEmptyView() {
        if (filteredBuildingList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}
