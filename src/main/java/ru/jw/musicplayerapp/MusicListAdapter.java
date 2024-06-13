// Адаптер для RecyclerView
package ru.jw.musicplayerapp;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder>{

    private ArrayList<AudioModel> songsList; // Список песен
    private Context context; // Контекст приложения

    // Конструктор адаптера
    public MusicListAdapter(ArrayList<AudioModel> songsList, Context context) {
        this.songsList = songsList;
        this.context = context;
    }

    // Создание новых элементов списка
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        return new MusicListAdapter.ViewHolder(view);
    }

    // Привязка данных к элементам списка
    @Override
    public void onBindViewHolder( MusicListAdapter.ViewHolder holder, int position) {
        AudioModel songData = songsList.get(position);
        holder.titleTextView.setText(songData.getTitle());

        // Изменение цвета текста в зависимости от текущего проигрываемого трека
        if(MyMediaPlayer.currentIndex==position){
            holder.titleTextView.setTextColor(Color.parseColor("#FF0000"));
        }else{
            holder.titleTextView.setTextColor(Color.parseColor("#000000"));
        }

        // Обработчик нажатия на элемент списка
        holder.itemView.setOnClickListener(v -> {
            MyMediaPlayer.getInstance().reset();
            MyMediaPlayer.currentIndex = position;
            Intent intent = new Intent(context,MusicPlayerActivity.class);
            intent.putExtra("LIST",songsList);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    // Получение количества элементов в списке
    @Override
    public int getItemCount() {
        return songsList.size();
    }

    // ViewHolder для элементов списка
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView; // TextView для названия трека
        ImageView iconImageView; // ImageView для иконки

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.music_title_text);
            iconImageView = itemView.findViewById(R.id.icon_view);
        }
    }
}
