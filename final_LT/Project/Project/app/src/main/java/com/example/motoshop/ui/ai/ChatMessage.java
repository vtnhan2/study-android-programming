package com.example.motoshop.ui.ai;

// Class lưu một tin nhắn trong màn hình chat AI.
public class ChatMessage {
    public String text;
    public boolean isUser;
    public long timestamp;
    public boolean isSuggestion; // Đánh dấu tin nhắn gợi ý để hiển thị khác một chút

    // Constructor khởi tạo object của class này.
    public ChatMessage(String text, boolean isUser) {
        this.text = text;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
    }
}
