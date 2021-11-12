package com.example.habitshare;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class CustomHabitEventListAdapter extends RecyclerView.Adapter<CustomHabitEventListAdapter.HabitEventViewHolder> {
    private ArrayList<HabitEvent> habitEvents;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public CustomHabitEventListAdapter(Context context, ArrayList<HabitEvent> habitEvents) {
        this.habitEvents = habitEvents;
        this.context = context;
    }

    @NonNull
    @Override
    public HabitEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_habit_event, parent, false);
        HabitEventViewHolder habitEventViewHolder = new HabitEventViewHolder(view, onItemClickListener);
        return habitEventViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HabitEventViewHolder holder, int position) {
        HabitEvent habitEvent = habitEvents.get(position);
        holder.habitNameView.setText(habitEvent.getTitle());
        holder.denoteDateView.setText(habitEvent.getDenoteDate());
        Log.d("Event Adapter", "Here");
    }


    @Override
    public int getItemCount() {
        return habitEvents.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public class HabitEventViewHolder extends RecyclerView.ViewHolder {
        TextView habitNameView, denoteDateView;
        OnItemClickListener onItemClickListener;

        public HabitEventViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;
            habitNameView = itemView.findViewById(R.id.habit_event_name);
            denoteDateView = itemView.findViewById(R.id.date_habit_event);
            if(onItemClickListener != null){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(getAdapterPosition());
                    }
                });
            }
        }
    }
}
