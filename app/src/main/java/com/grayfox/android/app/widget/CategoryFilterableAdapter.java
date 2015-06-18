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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.grayfox.android.app.R;
import com.grayfox.android.app.util.ColorTransformation;
import com.grayfox.android.client.PoisApi;
import com.grayfox.android.client.model.Category;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryFilterableAdapter extends BaseAdapter implements Filterable {

    private final Context context;
    private final List<Category> categories;

    public CategoryFilterableAdapter(Context context) {
        this.context = context;
        categories = new ArrayList<>();
    }

    public void setLike(Category[] categories) {
        this.categories.clear();
        this.categories.addAll(Arrays.asList(categories));
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Category getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
        holder.categoryNameTextView.setText(categories.get(position).getName());
        Picasso.with(context)
                .load(categories.get(position).getIconUrl())
                .transform(new ColorTransformation(context.getResources().getColor(R.color.secondary_text)))
                .placeholder(R.drawable.ic_generic_category)
                .into(holder.categoryImageView);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new CategoryFilter();
    }

    private static class ViewHolder {

        private ImageView categoryImageView;
        private TextView categoryNameTextView;

        private ViewHolder(View rootView) {
            categoryImageView = (ImageView) rootView.findViewById(R.id.category_image);
            categoryNameTextView = (TextView) rootView.findViewById(R.id.category_name);
        }
    }

    private class CategoryFilter extends Filter {

        private PoisApi poisApi;

        private CategoryFilter() {
            poisApi = new PoisApi(context);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && !constraint.toString().trim().isEmpty()) {
                Category[] result = poisApi.awaitCategoriesLikeName(constraint.toString());
                filterResults.values = result;
                filterResults.count = result.length;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                setLike((Category[]) results.values);
                notifyDataSetChanged();
            } else notifyDataSetInvalidated();
        }
    }
}