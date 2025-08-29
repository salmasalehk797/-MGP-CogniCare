package com.example.cognicare;

import android.content.Context;
import android.view.View;
//import android.widget.ImageView;

public class GameItem extends androidx.appcompat.widget.AppCompatImageView {

    private int imageResId;

    public GameItem(Context context, int imageResId) {
        super(context);
        this.imageResId = imageResId;
        setImageResource(imageResId);
        setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

            }

        });
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
        setImageResource(imageResId);
    }

    public Object getDraggableData() {
        return this;
    }
}