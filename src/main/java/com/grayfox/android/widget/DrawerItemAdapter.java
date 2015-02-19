package com.grayfox.android.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grayfox.android.R;

import java.util.List;

public class DrawerItemAdapter extends BaseAdapter {

    private final List<DrawerItem> items;

    public DrawerItemAdapter(List<DrawerItem> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public DrawerItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
        DrawerItem item = items.get(position);
        holder.itemText.setText(item.getText());
        holder.itemText.setCompoundDrawablesWithIntrinsicBounds(item.getIcon(), 0, 0, 0);
        return convertView;
    }

    private static class ViewHolder {

        private TextView itemText;

        private ViewHolder(View view) {
            itemText = (TextView) view.findViewById(R.id.item_text);
        }
    }
}
