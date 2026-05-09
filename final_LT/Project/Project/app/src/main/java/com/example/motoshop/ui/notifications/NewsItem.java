package com.example.motoshop.ui.notifications;

public class NewsItem {
    public final String source;
    public final String date;
    public final String title;
    public final String content;
    public final String tag;

    public NewsItem(String source, String date, String title, String content, String tag) {
        this.source = source;
        this.date = date;
        this.title = title;
        this.content = content;
        this.tag = tag;
    }
}
