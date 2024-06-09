package com.example.fakenote.ui.search;

public class Note {
    public String category;
    public String title;
    public String textContent;
    public String updated;

    public Note(String category, String title, String textContent, String updated) {
        this.category = category;
        this.title = title;
        this.textContent = textContent;
        this.updated = updated;
    }
}
