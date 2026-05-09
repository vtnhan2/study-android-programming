package com.example.motoshop.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private final List<NewsItem> items;

    public NewsAdapter(List<NewsItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsItem item = items.get(position);
        holder.tvSource.setText(item.source);
        holder.tvDate.setText(item.date);
        holder.tvTitle.setText(item.title);
        holder.tvSnippet.setText(item.content);

        holder.tvReadMore.setOnClickListener(v -> {
            if (holder.tvSnippet.getMaxLines() == 3) {
                holder.tvSnippet.setMaxLines(Integer.MAX_VALUE);
                holder.tvReadMore.setText("Thu gọn ▴");
            } else {
                holder.tvSnippet.setMaxLines(3);
                holder.tvReadMore.setText("Xem thêm ▾");
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSource, tvDate, tvTitle, tvSnippet, tvReadMore;

        ViewHolder(View itemView) {
            super(itemView);
            tvSource = itemView.findViewById(R.id.tvNewsSource);
            tvDate = itemView.findViewById(R.id.tvNewsDate);
            tvTitle = itemView.findViewById(R.id.tvNewsTitle);
            tvSnippet = itemView.findViewById(R.id.tvNewsSnippet);
            tvReadMore = itemView.findViewById(R.id.tvReadMore);
        }
    }
}
