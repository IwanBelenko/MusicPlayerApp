package ru.jw.musicplayerapp;

import java.io.Serializable;

// Модель данных для аудиофайла
public class AudioModel implements Serializable {
    private String path; // Путь к файлу
    private String title; // Название трека
    private String duration; // Продолжительность трека

    // Конструктор класса
    public AudioModel(String path, String title, String duration) {
        this.path = path;
        this.title = title;
        this.duration = duration;
    }

    // Геттеры и сеттеры для полей класса
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
}
