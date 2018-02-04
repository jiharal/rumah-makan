package com.example.a5days.rumahmakan.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a5days.rumahmakan.ClassItem;
import com.example.a5days.rumahmakan.R;
import com.squareup.picasso.Picasso;

/**
 * Created by 5Days on 20/11/2017.
 */

public class ListImageAdapterVertical extends ArrayAdapter<ClassItem> {
    Context context;
    int layoutResourceId;
    ClassItem data[] = null;


    ItemHolder holder;

    public ListImageAdapterVertical(Context context, int layoutResourceId,
                                    ClassItem[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ItemHolder();
            holder.imgIcon = (ImageView) row.findViewById(R.id.icon);
            holder.label = (TextView) row.findViewById(R.id.item);

            row.setTag(holder);
        } else {
            holder = (ItemHolder) row.getTag();
        }

        final ClassItem item = data[position];
        holder.imgIcon.setTag(item.icon);
        holder.label.setText(item.label);

        Picasso.with(context).load(item.icon).resize(130, 90).into(holder.imgIcon);

        return row;
    }

    static class ItemHolder {
        ImageView imgIcon;
        TextView label;
    }
}
