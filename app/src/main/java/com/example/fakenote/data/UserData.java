package com.example.fakenote.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserData {
    // 这些信息应该从后端读取
    public String _userID;
    public String _userBio;
    public Boolean _isGuest;
    private List<NoteBook> _noteBooks;
    UserData(String userID, String bio, Boolean isGuest){
        _userID = userID;
        _userBio = bio;
        _isGuest = isGuest;
        _noteBooks = new ArrayList<>();
    }

    //根据名字查找，找不到返回null
    public NoteBook GetNoteBook(String name){
        for (NoteBook book:_noteBooks
        ) {
            if (Objects.equals(book.Name, name))
                return book;
        }
        return null;
    }
    public NoteBook GetNoteBookAt(int position){
        return _noteBooks.get(position);
    }
    public int GetNoteBookCount(){return _noteBooks.size();}

    //根据名字添加，若有重名返回false
    public NoteBook AddNoteBook(String name){
        for (NoteBook book:_noteBooks
        ) {
            if (Objects.equals(book.Name, name))
                return null;
        }
        NoteBook newBook = new NoteBook(name,this);
        _noteBooks.add(newBook);
        return newBook;
    }

    //根据名字删除，若无对应名字返回false
    public boolean DeleteNoteBook(String name){
        for (int i=0;i<_noteBooks.size();i++){
            if (Objects.equals(_noteBooks.get(i).Name, name)){
                _noteBooks.remove(i);
                return true;
            }
        }
        return false;
    }
    // Simplified method to get a NotePage from a NoteBook
    public NotePage GetPage(String bookName, String pageName) {
        NoteBook book = GetNoteBook(bookName);
        if (book != null) {
            return book.GetPage(pageName);
        }
        return null;
    }
    // For the overloaded method which takes an ArrayList, ensure it is adjusted to the new structure
    public NotePage GetPage(ArrayList<String> pageInfo) {
        if (pageInfo.size() < 2) {
            return null;  // Ensure there are at least two strings provided: book name and page name
        }
        return GetPage(pageInfo.get(0), pageInfo.get(1));
    }

}
