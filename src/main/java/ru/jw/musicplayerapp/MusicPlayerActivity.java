package ru.jw.musicplayerapp;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

// Активность для воспроизведения музыки
public class MusicPlayerActivity extends AppCompatActivity {

    // Объявление переменных интерфейса
    TextView titleTv, currentTimeTv, totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay, nextBtn, previousBtn, musicIcon;
    ArrayList<AudioModel> songsList; // Список песен
    AudioModel currentSong; // Текущая песня
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance(); // Экземпляр MediaPlayer
    int x = 0; // Переменная для анимации иконки

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        // Инициализация компонентов интерфейса
        titleTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);

        titleTv.setSelected(true); // Для маркировки текста как выбранного

        // Получение списка песен из предыдущей активности
        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        // Установка ресурсов для текущей песни
        setResourcesWithMusic();

        // Обновление UI в реальном времени
        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    // Обновление прогресса SeekBar и времени воспроизведения
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));

                    // Изменение иконки воспроизведения и анимация иконки музыки
                    if (mediaPlayer.isPlaying()) {
                        pausePlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                        musicIcon.setRotation(x++);
                    } else {
                        pausePlay.setImageResource(R.drawable.baseline_play_circle_outline);
                        musicIcon.setRotation(0);
                    }
                }
                new Handler().postDelayed(this, 100);
            }
        });

        // Обработчик изменений SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Перемотка воспроизведения при изменении положения SeekBar
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Действия при начале перемещения ползунка (если необходимо)
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Действия после остановки перемещения ползунка (если необходимо)
            }
        });
    }

    // Установка ресурсов для текущей песни
    void setResourcesWithMusic() {
        currentSong = songsList.get(MyMediaPlayer.currentIndex);

        // Установка названия песни и общего времени воспроизведения
        titleTv.setText(currentSong.getTitle());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        // Обработчики нажатий на кнопки управления воспроизведением
        pausePlay.setOnClickListener(v -> pausePlay());
        nextBtn.setOnClickListener(v -> playNextSong());
        previousBtn.setOnClickListener(v -> playPreviousSong());

        // Воспроизведение музыки
        playMusic();
    }

    // Воспроизведение музыки
    private void playMusic() {
        mediaPlayer.reset();
        try {
            // Подготовка и запуск MediaPlayer
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Воспроизведение следующей песни
    private void playNextSong() {
        if (MyMediaPlayer.currentIndex == songsList.size() - 1)
            return;
        MyMediaPlayer.currentIndex += 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    // Воспроизведение предыдущей песни
    private void playPreviousSong() {
        if (MyMediaPlayer.currentIndex == 0)
            return;
        MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    // Пауза и воспроизведение музыки
    private void pausePlay() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }

    // Конвертация длительности в формат MM:SS
    public static String convertToMMSS(String duration) {
        long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}
