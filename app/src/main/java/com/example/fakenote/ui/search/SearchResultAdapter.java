package com.example.fakenote.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakenote.R;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private List<Note> data;

    public SearchResultAdapter(List<Note> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = data.get(position);
        holder.categoryView.setText(note.category);
        holder.titleView.setText(note.title);
        holder.contentView.setText(note.textContent);
        holder.updateTimeView.setText(note.updated);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryView;
        TextView titleView;
        TextView contentView;
        TextView updateTimeView;

        ViewHolder(View itemView) {
            super(itemView);
            categoryView = itemView.findViewById(R.id.category_text_view);
            titleView = itemView.findViewById(R.id.title_text_view);
            contentView = itemView.findViewById(R.id.content_text_view);
            updateTimeView = itemView.findViewById(R.id.update_time_text_view);
        }
    }

    public void updateData(List<Note> newData) {
        this.data.clear();
        this.data.addAll(newData);
    }
}
