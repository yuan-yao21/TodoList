package com.example.fakenote.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NoteBook {
    public Number _id;
    public String Name;
    private UserData _parent;
    private List<NotePage> _pages;  // 直接使用NotePage列表

    public NoteBook(String name, UserData parent) {
        Name = name;
        _pages = new ArrayList<>();
        _parent = parent;
    }
    public boolean removePage(NotePage page) {
        return _pages.remove(page);
    }
    public NotePage GetPage(String name) {
        for (NotePage page : _pages) {
            if (Objects.equals(page.Name, name))
                return page;
        }
        return null;
    }

    public NotePage GetPageAt(int position) {
        return _pages.get(position);
    }

    public int GetPageCount() {
        return _pages.size();
    }

    // 添加页面方法
    public NotePage AddPage(String name) {
        // 检查是否已存在同名页面
        for (NotePage page : _pages) {
            if (Objects.equals(page.Name, name)) {
                return null; // 返回null表示添加失败
            }
        }
        // 创建新页面
        NotePage newPage = new NotePage(name, this);
        _pages.add(newPage);
        return newPage; // 返回新创建的页面
    }

    public boolean Rename(String name) {
        if (_parent.GetNoteBook(name) == null) {
            Name = name;
            return true;
        }
        return false;
    }
}
