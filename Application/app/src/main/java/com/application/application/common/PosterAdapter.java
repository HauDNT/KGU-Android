package com.application.application.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.application.R;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder> {

    private final int[] posterImages;
    private final LayoutInflater inflater;

    public PosterAdapter(Context context, int[] posterImages) {
        this.posterImages = posterImages;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_poster, parent, false);
        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosterViewHolder holder, int position) {
        // Cài đặt kích thước cho poster
        ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();
        params.width = (int) (holder.itemView.getContext().getResources().getDisplayMetrics().widthPixels * 1); // 80% chiều rộng màn hình
        holder.imageView.setLayoutParams(params);

        holder.imageView.setImageResource(posterImages[position]);
    }

    @Override
    public int getItemCount() {
        return posterImages.length;
    }

    static class PosterViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        PosterViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.posterImage);
        }
    }
}