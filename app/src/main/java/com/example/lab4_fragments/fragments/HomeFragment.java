package com.example.lab4_fragments.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
// ... other imports
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.lab4_fragments.database.AppDatabaseSingleton;
import com.example.lab4_fragments.entities.Building;
import com.example.lab4_fragments.BuildingRepository;
import com.example.lab4_fragments.LoginActivity;
import com.example.lab4_fragments.R;
// Google Maps imports
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
// Map marker imports
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
// Task import
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragmento que muestra el mapa con marcadores de edificaciones.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Button viewUbiButton;
    private Button logoutButton;
    private BuildingRepository buildingRepository;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // Declaración de constantes para guardar el estado del mapa
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_ZOOM_LEVEL = "zoom_level";

    // Declaración de la constante de umbral de zoom
    private static final float ZOOM_THRESHOLD = 17.0f; // Define el umbral de zoom

    // Variables para almacenar la última posición y nivel de zoom del mapa
    private LatLng lastCameraPosition = null;
    private float lastZoom = 12.0f; // Nivel de zoom predeterminado

    // Lista para almacenar todos los marcadores
    private List<Marker> allMarkers = new ArrayList<>();

    // Caché para los Bitmaps personalizados
    private Map<Integer, Bitmap> markerWithLabelCache = new HashMap<>();
    private Map<Integer, Bitmap> markerWithoutLabelCache = new HashMap<>();

    // FusedLocationProviderClient para obtener la ubicación actual
    private FusedLocationProviderClient fusedLocationClient;

    // Lista de edificios
    private List<Building> buildingList = new ArrayList<>();

    /**
     * Método para crear una nueva instancia de HomeFragment.
     */
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Constructor vacío requerido.
     */
    public HomeFragment() {
        // Constructor vacío
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /**
     * Configura la vista y los componentes del fragmento.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // Inicializar botones
        viewUbiButton = view.findViewById(R.id.viewUbi);
        logoutButton = view.findViewById(R.id.logoutButton);

        // Inicializar el repositorio de edificios
        buildingRepository = new BuildingRepository(getContext());

        // Inicializar FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Configurar el botón "Ver mi ubicación"
        viewUbiButton.setOnClickListener(v -> {
            if (mMap != null) {
                centerMapOnCurrentLocation();
            }
        });

        // Configurar el botón "Salir"
        logoutButton.setOnClickListener(v -> logout());

        // Configurar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null){
            mapFragment.getMapAsync(this);
        }

        // Solicitar permisos de ubicación si no están concedidos
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            if (mMap != null){
                mMap.setMyLocationEnabled(true);
            }
        }

        // Restaurar la posición y el zoom si hay datos guardados
        if (savedInstanceState != null) {
            lastCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            lastZoom = savedInstanceState.getFloat(KEY_ZOOM_LEVEL, 12.0f);
        }
    }

    /**
     * Centra el mapa en la ubicación actual del usuario.
     */
    private void centerMapOnCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        // Ubicación encontrada
                        if (location != null) {
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                        } else {
                            // Ubicación no disponible
                            Toast.makeText(getContext(), "No se pudo obtener la ubicación actual.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Permisos de ubicación no concedidos
            Toast.makeText(getContext(), "Permiso de ubicación denegado.", Toast.LENGTH_SHORT).show();
            // Opcional: Solicitar permisos nuevamente o guiar al usuario a los ajustes
        }
    }

    /**
     * Configura el mapa una vez que está listo.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap){
        mMap = googleMap;

        // Configurar el listener de clic en marcadores
        mMap.setOnMarkerClickListener(this);

        // Observar la lista de edificios
        buildingRepository.getBuildingList().observe(getViewLifecycleOwner(), buildings -> {
            if (buildings != null) {
                buildingList.clear();
                buildingList.addAll(buildings);
                // Agregar marcadores al mapa
                addMarkersToMap();
            }
        });

        // Restaurar la posición y el zoom si están disponibles
        if (lastCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastCameraPosition, lastZoom));
        } else {
            // Mover la cámara a Arequipa si no hay datos guardados
            LatLng arequipa = new LatLng(-16.409047, -71.537451);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(arequipa, 12));
        }

        // Habilitar la capa de ubicación si los permisos fueron concedidos
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }

        // Configurar el listener para cambios en el zoom del mapa
        mMap.setOnCameraIdleListener(() -> {
            float currentZoom = mMap.getCameraPosition().zoom;
            updateMarkerIcons(currentZoom);

            // Actualizar las variables con la posición y zoom actuales
            lastCameraPosition = mMap.getCameraPosition().target;
            lastZoom = currentZoom;
        });
    }

    /**
     * Agrega marcadores al mapa.
     */
    private void addMarkersToMap() {
        // Limpiar marcadores existentes
        mMap.clear();
        allMarkers.clear();

        for (int i = 0; i < buildingList.size(); i++) {
            Building building = buildingList.get(i);
            LatLng position = new LatLng(building.latitude, building.longitude);

            // Crear un marcador sin etiqueta inicialmente
            Bitmap customMarker = createCustomMarkerWithoutLabel(i);

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromBitmap(customMarker)) // Usar el ícono sin etiqueta
                    .snippet(String.valueOf(i)) // Usamos el índice como referencia
            );
            if (marker != null) {
                marker.setTag(i);
                allMarkers.add(marker); // Almacenar el marcador
            }
        }
    }

    /**
     * Actualiza los íconos de los marcadores según el nivel de zoom.
     * @param zoomLevel Nivel de zoom actual del mapa.
     */
    private void updateMarkerIcons(float zoomLevel) {
        for (Marker marker : allMarkers) {
            if (marker.getTag() instanceof Integer) {
                int buildingIndex = (Integer) marker.getTag();
                Building building = buildingList.get(buildingIndex);
                if (zoomLevel >= ZOOM_THRESHOLD) {
                    // Usar ícono con etiqueta
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarkerWithLabel(buildingIndex, building.title)));
                } else {
                    // Usar ícono sin etiqueta
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarkerWithoutLabel(buildingIndex)));
                }
            }
        }
    }

    /**
     * Crea un marcador personalizado sin etiqueta.
     * @param buildingId ID de la edificación.
     * @return Bitmap del marcador sin etiqueta.
     */
    private Bitmap createCustomMarkerWithoutLabel(int buildingId) {
        if (markerWithoutLabelCache.containsKey(buildingId)) {
            return markerWithoutLabelCache.get(buildingId);
        }

        // Inflar el layout personalizado sin etiqueta
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View markerView = inflater.inflate(R.layout.custom_marker_without_label, null);

        // Medir y dibujar la vista para crear el Bitmap
        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());
        markerView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerView.draw(canvas);

        // Almacenar el Bitmap en el caché
        markerWithoutLabelCache.put(buildingId, bitmap);

        return bitmap;
    }

    /**
     * Crea un marcador personalizado con etiqueta de texto.
     * @param buildingId ID de la edificación.
     * @param title Nombre de la edificación.
     * @return Bitmap del marcador con etiqueta.
     */
    private Bitmap createCustomMarkerWithLabel(int buildingId, String title) {
        if (markerWithLabelCache.containsKey(buildingId)) {
            return markerWithLabelCache.get(buildingId);
        }

        // Inflar el layout personalizado con etiqueta
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View markerView = inflater.inflate(R.layout.custom_marker_with_label, null);

        // Establecer el título de la edificación
        TextView titleTextView = markerView.findViewById(R.id.marker_title);
        titleTextView.setText(title);

        // Medir y dibujar la vista para crear el Bitmap
        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());
        markerView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerView.draw(canvas);

        // Almacenar el Bitmap en el caché
        markerWithLabelCache.put(buildingId, bitmap);

        return bitmap;
    }

    /**
     * Maneja el clic en un marcador del mapa.
     * @param marker Marcador que fue clickeado.
     * @return True si el evento fue consumido, false de lo contrario.
     */
    @Override
    public boolean onMarkerClick(@NonNull Marker marker){
        // Obtener el índice de la edificación desde el tag del marcador
        Object tag = marker.getTag();
        if (tag instanceof Integer){
            int buildingIndex = (Integer) tag;
            Building building = buildingList.get(buildingIndex);
            navigateToDetailFragment(building.buildingId); // Usar el ID real del edificio
            return true;
        }
        return false;
    }

    /**
     * Navega al DetailFragment correspondiente a la edificación seleccionada.
     * @param buildingId ID de la edificación.
     */
    private void navigateToDetailFragment(int buildingId){
        DetailFragment detailFragment = DetailFragment.newInstance(buildingId);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Maneja el evento de clic en el botón "Salir".
     */
    private void logout(){
        // Eliminar el usuario logueado de SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("loggedInUser");
        editor.apply();

        // Navegar a la actividad de Login
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    /**
     * Maneja la respuesta a la solicitud de permisos.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    if (mMap != null){
                        mMap.setMyLocationEnabled(true);
                    }
                }
            } else {
                // Permiso denegado, manejar adecuadamente
                Toast.makeText(getContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Sobrescribir onSaveInstanceState para guardar la posición y el zoom del mapa.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMap != null) {
            // Guardar la posición actual del mapa
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition().target);
            // Guardar el nivel de zoom actual
            outState.putFloat(KEY_ZOOM_LEVEL, mMap.getCameraPosition().zoom);
        }
    }
}
