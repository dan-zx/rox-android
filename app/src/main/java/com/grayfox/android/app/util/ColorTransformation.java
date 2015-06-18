/*
 * Copyright 2014-2015 Daniel Pedraza-Arcega
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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