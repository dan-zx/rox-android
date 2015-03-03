package com.grayfox.android.widget.util;

/**
 * Created by daniel on 2/03/15.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.squareup.picasso.Transformation;

public class ColorTransformation implements Transformation {

    private final Context context;
    private final int colorId;

    public ColorTransformation(Context context, int colorId) {
        this.context = context;
        this.colorId = colorId;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int [] allpixels = new int [source.getHeight() * source.getWidth()];
        source.getPixels(allpixels, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());
        for(int i = 0; i < allpixels.length; i++) {
            if( allpixels[i] != Color.TRANSPARENT) allpixels[i] = context.getResources().getColor(colorId);
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
