package com.grayfox.android.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.grayfox.android.app.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class PicassoMarker implements Target {

    private final Marker marker;
    private final int backgroundRes;
    private View layout;

    public PicassoMarker(Marker marker, int backgroundRes, Context context) {
        this.marker = marker;
        this.backgroundRes = backgroundRes;
        layout = LayoutInflater.from(context).inflate(R.layout.category_marker, null);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconToLayoutBitmap(bitmap)));
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconToLayoutBitmap(errorDrawable)));
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconToLayoutBitmap(placeHolderDrawable)));
    }

    private Bitmap iconToLayoutBitmap(Drawable iconDrawable) {
        ImageView categoryImageView = (ImageView) layout.findViewById(R.id.category_image);
        categoryImageView.setBackgroundResource(backgroundRes);
        categoryImageView.setImageDrawable(iconDrawable);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(layout.getMeasuredWidth(), layout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        layout.draw(c);
        return bitmap;
    }

    private Bitmap iconToLayoutBitmap(Bitmap iconBitmap) {
        ImageView categoryImageView = (ImageView) layout.findViewById(R.id.category_image);
        categoryImageView.setBackgroundResource(backgroundRes);
        categoryImageView.setImageBitmap(iconBitmap);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(layout.getMeasuredWidth(), layout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        layout.draw(c);
        return bitmap;
    }
}