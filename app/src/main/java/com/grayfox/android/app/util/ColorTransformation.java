package com.grayfox.android.app.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.squareup.picasso.Transformation;

public class ColorTransformation implements Transformation {

    private final int color;

    public ColorTransformation(int color) {
        this.color = color;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int [] allpixels = new int [source.getHeight() * source.getWidth()];
        source.getPixels(allpixels, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());
        for(int i = 0; i < allpixels.length; i++) {
            if( allpixels[i] != Color.TRANSPARENT) allpixels[i] = color;
        }
        Bitmap result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        result.setPixels(allpixels, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());
        source.recycle();
        return result;
    }

    @Override
    public String key() {
        return "toColor()";
    }
}