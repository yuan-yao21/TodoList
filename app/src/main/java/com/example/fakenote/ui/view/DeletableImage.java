package com.example.fakenote.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.fakenote.R;

public class DeletableImage extends androidx.appcompat.widget.AppCompatImageView {
    private LinearLayout _parent;
    private Drawable _picData;
    public DeletableImage(LinearLayout parent,Bitmap bitmap) {
        super(parent.getContext());
        _parent = parent;
        setImageBitmap(bitmap);
        _picData = getDrawable();
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //_parent.removeView(DeletableImage.this);
                if (hasFocus())
                    _parent.removeView(DeletableImage.this);
                else
                    requestFocus();
            }
        });
    }

    // 删除照片逻辑
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus){
            Drawable[] layers = new Drawable[2];
            layers[0] = _picData;
            layers[1] = ContextCompat.getDrawable(_parent.getContext(), R.drawable.ic_delete);
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            setImageDrawable(layerDrawable);
        }else{
            setImageDrawable(_picData);
        }

    }
}
