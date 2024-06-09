package com.example.fakenote.ui.fakenote;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FakenoteViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public FakenoteViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is Fakenote fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}