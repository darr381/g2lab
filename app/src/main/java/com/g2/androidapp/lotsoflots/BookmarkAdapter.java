package com.g2.androidapp.lotsoflots;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class BookmarkAdapter extends ArrayAdapter<BookmarkData> {
    BookmarkDataManager manager;

    public BookmarkAdapter(Context context, int resource, ArrayList<BookmarkData> data, BookmarkDataManager bookmarkManager){
        super(context,resource,data);
        manager = bookmarkManager;
    }


    @Override
    public View getView (final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LinearLayout view;
        if (convertView == null) {
            view = ((LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.bookmark_list_item, parent, false));
        } else {
            view = ((LinearLayout) convertView);
        }
        TextView textView = view.findViewById(R.id.bookmark_address);
        textView.setText(getItem(position).name);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("com.g2.androidapp.lotsoflots.BMT", getItem(position).latlng);
                getContext().startActivity(intent);
            }
        });
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                manager.deleteBookmark(getItem(position).name);
                return false;
            }
        });
        return view;
    }
}
