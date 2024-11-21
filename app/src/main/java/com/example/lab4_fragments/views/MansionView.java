package com.example.lab4_fragments.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.example.lab4_fragments.Door;
import com.example.lab4_fragments.R;
import com.example.lab4_fragments.entities.RoomEntity;
import com.example.lab4_fragments.RoomInfo;
import com.example.lab4_fragments.fragments.DetailRoomFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MansionView extends View {

    private Paint paintRoomOutline;
    private Paint paintDoor;
    private Paint paintText;
    private Paint paintTextVertical;
    private static final String TAG = "MansionView";

    private List<RoomEntity> rooms = new ArrayList<>();
    private List<Door> doors = new ArrayList<>();
    private Map<Integer, RoomInfo> roomDataMap = new HashMap<>();

    public MansionView(Context context) {
        super(context);
        init();
        loadCoordinates(context);
        loadRoomData(context);
    }

    public MansionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        loadCoordinates(context);
        loadRoomData(context);
    }

    public MansionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        loadCoordinates(context);
        loadRoomData(context);
    }

    private void init() {
        // Inicializar el Paint para los contornos de los cuartos
        paintRoomOutline = new Paint();
        paintRoomOutline.setColor(Color.DKGRAY);
        paintRoomOutline.setStyle(Paint.Style.STROKE);
        paintRoomOutline.setStrokeWidth(5);

        // Inicializar el Paint para las puertas
        paintDoor = new Paint();
        paintDoor.setColor(Color.YELLOW);
        paintDoor.setStrokeWidth(10);

        // Inicializar el Paint para el texto horizontal
        paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(30);
        paintText.setAntiAlias(true);

        // Inicializar el Paint para el texto vertical
        paintTextVertical = new Paint();
        paintTextVertical.setColor(Color.BLACK);
        paintTextVertical.setTextSize(30);
        paintTextVertical.setAntiAlias(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();

            Log.d(TAG, "Touch detected at: (" + touchX + ", " + touchY + ")");

            for (RoomEntity room : rooms) {
                if (isTouchInsideRoom(touchX, touchY, room)) {
                    Log.d(TAG, "Touched inside: " + room.name);
                    handleRoomTouch(room);
                    return true;
                }
            }
            Log.d(TAG, "No room touched.");
        }
        return super.onTouchEvent(event);
    }

    /**
     * Verifica si el toque está dentro de un cuarto específico.
     *
     * @param touchX Coordenada X del toque
     * @param touchY Coordenada Y del toque
     * @param room   Cuarto a verificar
     * @return Verdadero si el toque está dentro del cuarto, falso en caso contrario
     */
    private boolean isTouchInsideRoom(float touchX, float touchY, RoomEntity room) {
        float x1 = room.x1;
        float y1 = room.y1;
        float x2 = room.x2;
        float y2 = room.y2;

        boolean inside = (touchX >= x1 && touchX <= x2 && touchY >= y2 && touchY <= y1);
        Log.d(TAG, "Checking room " + room.name + ": (" + x1 + ", " + y1 + ") to (" + x2 + ", " + y2 + ") - Inside: " + inside);
        return inside;
    }

    /**
     * Maneja el evento de toque en un cuarto específico.
     *
     * @param room Cuarto que ha sido tocado
     */
    private void handleRoomTouch(RoomEntity room) {
        String name = room.name;
        String roomNumberStr = name.replaceAll("[^0-9]", "");

        if (!roomNumberStr.isEmpty()) {
            try {
                int roomNumber = Integer.parseInt(roomNumberStr);
                RoomInfo roomInfo = roomDataMap.get(roomNumber);
                if (roomInfo != null) {
                    showRoomDetails(roomInfo);
                } else {
                    Log.d(TAG, "Información no encontrada para Cuarto " + roomNumber);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error al convertir el número de cuarto", e);
            }
        } else {
            Log.d(TAG, "Número de cuarto no encontrado en el nombre: " + name);
        }
    }

    /**
     * Muestra los detalles del cuarto seleccionado en un fragmento.
     *
     * @param roomInfo Información del cuarto
     */
    private void showRoomDetails(RoomInfo roomInfo) {
        DetailRoomFragment detailRoomFragment = DetailRoomFragment.newInstance(
                roomInfo.getTitle(), roomInfo.getImageResId(), roomInfo.getDescription()
        );
        FragmentActivity activity = (FragmentActivity) getContext();
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, detailRoomFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Dibujar los contornos de los cuartos
        for (RoomEntity room : rooms) {
            float x1 = room.x1;
            float y1 = room.y1;
            float x2 = room.x2;
            float y2 = room.y2;

            canvas.drawRect(x1, y1, x2, y2, paintRoomOutline);

            // Dibujar el nombre del cuarto en vertical
            float textX = (x1 + x2) / 2;
            float textY = (y1 + y2) / 2;

            canvas.save(); // Guardar el estado actual del lienzo
            canvas.translate(textX, textY); // Mover el origen al centro del cuarto
            canvas.rotate(-90); // Rotar el lienzo 90 grados en sentido horario
            // Ajustar la posición del texto para que esté centrado
            canvas.drawText(room.name, -paintTextVertical.measureText(room.name) / 2, 0, paintTextVertical);
            canvas.restore(); // Restaurar el estado original del lienzo

            // Log de las coordenadas dibujadas
            Log.d(TAG, "Dibujando " + room.name + " desde (" + x1 + ", " + y1 + ") a (" + x2 + ", " + y2 + ")");
        }

        // Dibujar las puertas como líneas amarillas
        for (Door door : doors) {
            float doorX1 = door.x1;
            float doorY1 = door.y1;
            float doorX2 = door.x2;
            float doorY2 = door.y2;
            canvas.drawLine(doorX1, doorY1, doorX2, doorY2, paintDoor);

            // Log de las coordenadas de puertas dibujadas
            Log.d(TAG, "Dibujando Puerta desde (" + doorX1 + ", " + doorY1 + ") a (" + doorX2 + ", " + doorY2 + ")");
        }
    }

    /**
     * Carga los datos de los cuartos desde el archivo "cuartos.txt" en la carpeta assets.
     *
     * @param context Contexto de la aplicación
     */
    public void loadRoomData(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("cuartos.txt"); // Asegúrate de que el archivo esté en la carpeta assets
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int roomNumber = -1;
            String title = null;
            String description = null;
            String imageUrl = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Cuarto:")) {
                    roomNumber = Integer.parseInt(line.split(":")[1].trim());
                } else if (line.startsWith("Título:")) {
                    title = line.split(":")[1].trim();
                } else if (line.startsWith("Descripción:")) {
                    description = line.substring(line.indexOf(":") + 1).trim();
                } else if (line.startsWith("URL de la imagen:")) {
                    imageUrl = line.split(":")[1].trim();

                    // Convertir el nombre de la imagen en un recurso drawable
                    int imageResId = context.getResources().getIdentifier(imageUrl, "drawable", context.getPackageName());

                    // Guardar la información si todos los datos están completos
                    if (roomNumber != -1 && title != null && description != null && imageResId != 0) {
                        roomDataMap.put(roomNumber, new RoomInfo(title, imageResId, description));
                        roomNumber = -1;
                        title = null;
                        description = null;
                        imageUrl = null;
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar datos de cuartos", e);
        }
    }

    /**
     * Carga las coordenadas de los cuartos y puertas desde el archivo "coordenadas.txt" en la carpeta assets.
     *
     * @param context Contexto de la aplicación
     */
    private void loadCoordinates(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("coordenadas.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Ignorar líneas vacías y comentarios
                }

                String[] parts = line.split(" ");
                if (parts[0].equalsIgnoreCase("Cuarto")) {
                    if (parts.length < 6) {
                        Log.e(TAG, "Formato incorrecto para Cuarto: " + line);
                        continue;
                    }
                    String name = parts[1];
                    float x1 = Float.parseFloat(parts[2]);
                    float y1 = Float.parseFloat(parts[3]);
                    float x2 = Float.parseFloat(parts[4]);
                    float y2 = Float.parseFloat(parts[5]);
                    rooms.add(new RoomEntity(name, x1, y1, x2, y2));
                } else if (parts[0].equalsIgnoreCase("Puerta")) {
                    if (parts.length < 5) {
                        Log.e(TAG, "Formato incorrecto para Puerta: " + line);
                        continue;
                    }
                    float x1 = Float.parseFloat(parts[1]);
                    float y1 = Float.parseFloat(parts[2]);
                    float x2 = Float.parseFloat(parts[3]);
                    float y2 = Float.parseFloat(parts[4]);
                    doors.add(new Door(x1, y1, x2, y2));
                }
            }
            reader.close();
        } catch (IOException e) {
            Log.e(TAG, "Error al cargar coordenadas", e);
        }
    }
}
