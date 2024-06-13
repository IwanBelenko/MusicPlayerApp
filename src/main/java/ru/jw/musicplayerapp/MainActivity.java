// Основная активность приложения
package ru.jw.musicplayerapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView; // RecyclerView для отображения списка музыки
    private TextView noMusicTextView; // TextView для сообщения, если музыка не найдена
    private ArrayList<AudioModel> songsList = new ArrayList<>(); // Список аудиофайлов

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация компонентов интерфейса
        recyclerView = findViewById(R.id.recycler_view);
        noMusicTextView = findViewById(R.id.no_songs_text);

        // Проверка разрешений и запрос их, если необходимо
        if (!checkPermission()) {
            requestPermission();
            return;
        }

        // Запрос в базу данных медиафайлов
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                AudioModel songData = new AudioModel(cursor.getString(1), cursor.getString(0), cursor.getString(2));
                if (new File(songData.getPath()).exists())
                    songsList.add(songData);
            }
            cursor.close();
        }

        // Отображение списка музыки или сообщения, если список пуст
        if (songsList.size() == 0) {
            noMusicTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
        }
    }

    // Проверка наличия разрешений
    private boolean checkPermission() {
        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_MEDIA_AUDIO);
        } else {
            result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return result == PackageManager.PERMISSION_GRANTED;
    }

    // Запрос разрешений
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_MEDIA_AUDIO)) {
                Toast.makeText(MainActivity.this, "READ PERMISSION IS REQUIRED, PLEASE ALLOW FROM SETTINGS", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 123);
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(MainActivity.this, "READ PERMISSION IS REQUIRED, PLEASE ALLOW FROM SETTINGS", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Метод onResume, в котором можно обновить данные, если это необходимо
    }
}