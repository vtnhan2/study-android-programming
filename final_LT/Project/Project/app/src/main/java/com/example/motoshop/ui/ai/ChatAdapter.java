package com.example.motoshop.ui.ai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.motoshop.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Adapter dùng để đưa dữ liệu lên RecyclerView.
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private final List<ChatMessage> messages = new ArrayList<>();

    // Xử lý nội dung AI hoặc tin nhắn trả về cho người dùng.
    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    // Tạo view cho từng item trong RecyclerView.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    // Đưa dữ liệu vào từng item đang hiển thị trên RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = sdf.format(new Date(message.timestamp));

        if (message.isUser) {
            holder.layoutUser.setVisibility(View.VISIBLE);
            holder.layoutAi.setVisibility(View.GONE);
            holder.tvMessageUser.setText(message.text);
            holder.tvTimeUser.setText(time);
        } else {
            holder.layoutAi.setVisibility(View.VISIBLE);
            holder.layoutUser.setVisibility(View.GONE);
            holder.tvMessageAi.setText(message.text);
            holder.tvTimeAi.setText(time);
        }
    }

    // Trả về số lượng item đang có trong danh sách.
    @Override
    public int getItemCount() {
        return messages.size();
    }

    // ViewHolder giữ các view của một item để RecyclerView dùng lại.
    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutAi, layoutUser;
        TextView tvMessageAi, tvTimeAi, tvMessageUser, tvTimeUser;

        ViewHolder(View itemView) {
            super(itemView);
            layoutAi = itemView.findViewById(R.id.layoutAi);
            layoutUser = itemView.findViewById(R.id.layoutUser);
            tvMessageAi = itemView.findViewById(R.id.tvMessageAi);
            tvTimeAi = itemView.findViewById(R.id.tvTimeAi);
            tvMessageUser = itemView.findViewById(R.id.tvMessageUser);
            tvTimeUser = itemView.findViewById(R.id.tvTimeUser);
        }
    }
}
