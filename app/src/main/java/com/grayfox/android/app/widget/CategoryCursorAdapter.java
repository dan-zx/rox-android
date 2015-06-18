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
package com.grayfox.android.app.widget;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grayfox.android.app.R;
import com.grayfox.android.app.util.ColorTransformation;
import com.grayfox.android.client.model.Category;

import com.squareup.picasso.Picasso;

public class CategoryCursorAdapter extends CursorAdapter {

    public CategoryCursorAdapter(Context context) {
        super(context, new MatrixCursor(new String[]{CursorColums._ID,CursorColums.CATEGORY_ICON_URL, CursorColums.CATEGORY_NAME, CursorColums.CATEGORY_FOURSQUARE_ID}), 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView categoryImageView = (ImageView) view.findViewById(R.id.category_image);
        TextView categoryNameTextView = (TextView) view.findViewById(R.id.category_name);
        Picasso.with(context)
                .load(cursor.getString(cursor.getColumnIndex(CursorColums.CATEGORY_ICON_URL)))
                .transform(new ColorTransformation(context.getResources().getColor(R.color.secondary_text)))
                .placeholder(R.drawable.ic_generic_category)
                .into(categoryImageView);
        categoryNameTextView.setText(cursor.getString(cursor.getColumnIndex(CursorColums.CATEGORY_NAME)));
    }

    public void set(Category... categories) {
        MatrixCursor cursor = createCursor();
        for (int i = 0; i < categories.length; i++) {
            cursor.addRow(new String[]{String.valueOf(i+1), categories[i].getName(), categories[i].getIconUrl(), categories[i].getFoursquareId()});
        }
        changeCursor(cursor);
    }

    public Category get(int position) {
        Cursor cursor = (Cursor) getItem(position);
        Category category = new Category();
        category.setName(cursor.getString(cursor.getColumnIndex(CursorColums.CATEGORY_NAME)));
        category.setIconUrl(cursor.getString(cursor.getColumnIndex(CursorColums.CATEGORY_ICON_URL)));
        category.setFoursquareId(cursor.getString(cursor.getColumnIndex(CursorColums.CATEGORY_FOURSQUARE_ID)));
        return category;
    }

    private MatrixCursor createCursor() {
        return new MatrixCursor(new String[]{CursorColums._ID, CursorColums.CATEGORY_NAME, CursorColums.CATEGORY_ICON_URL, CursorColums.CATEGORY_FOURSQUARE_ID});
    }

    public static final class CursorColums implements BaseColumns {
        public static final String CATEGORY_NAME = "categoryName";
        public static final String CATEGORY_ICON_URL = "categoryIconUrl";
        public static final String CATEGORY_FOURSQUARE_ID = "categoryFoursquareId";
    }
}