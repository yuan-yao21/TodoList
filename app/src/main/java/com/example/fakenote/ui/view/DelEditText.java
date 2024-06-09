package com.example.fakenote.ui.view;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * TODO: document your custom view class.
 */
public class DelEditText extends androidx.appcompat.widget.AppCompatEditText {
    public static DelEditText EditingLine = null;
    private LinearLayout _parent;
    private DelEditText _prevLine;

    public DelEditText(LinearLayout parent,DelEditText prevLine) {
        super(parent.getContext());
        _parent = parent;
        _prevLine = prevLine;
        setSingleLine(false);
        setHorizontallyScrolling(false);
        setBackground(null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getSelectionStart()==0 && keyCode==KeyEvent.KEYCODE_DEL
        &&_prevLine!=null) {
            _prevLine.requestFocus();
            int prevLength = _prevLine.getText().length();
            _prevLine.setText(_prevLine.getText().toString()+getText().toString());
            _prevLine.setSelection(prevLength);
            _parent.removeView(this);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        int newLinePosition = text.toString().indexOf('\n');
        if (newLinePosition!=-1){
            CreateNewLine(text.subSequence(newLinePosition+1, text.length()));
            setText(text.subSequence(0,newLinePosition));
        }
    }
    private void CreateNewLine(CharSequence content){
        DelEditText newLine = new DelEditText(_parent,this);
        _parent.addView(newLine,_parent.indexOfChild(this)+1);
        newLine.requestFocus();
        newLine.setText(content);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused)
            EditingLine = this;
    }

    public void AddPicture(Bitmap bitmap){
        DeletableImage newImage = new DeletableImage(_parent,bitmap);
        _parent.addView(newImage,_parent.indexOfChild(this)+1);
        DelEditText newLine = new DelEditText(_parent,this);
        _parent.addView(newLine,_parent.indexOfChild(newImage)+1);
        newLine.requestFocus();
    }
}