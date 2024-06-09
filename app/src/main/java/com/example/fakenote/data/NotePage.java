package com.example.fakenote.data;

import java.util.ArrayList;
import java.util.List;
public class NotePage {
    public String Name;
    private NoteBook _parent;

    private List<NoteLine> _lines;
    public NotePage(String name, NoteBook parent){
        Name = name;
        _lines = new ArrayList<>();
        _parent = parent;
    }
    // Method to rename the page, ensuring name uniqueness within the notebook
    public boolean Rename(String name) {
        if (_parent.GetPage(name) == null) {  // Checks if the page name exists within the same notebook
            Name = name;
            return true;
        }
        return false;
    }

    // 提供任何需要的方法，如修改内容、添加附件等
}
