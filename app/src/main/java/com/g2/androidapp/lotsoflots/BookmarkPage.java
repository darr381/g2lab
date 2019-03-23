package com.g2.androidapp.lotsoflots;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class BookmarkPage extends AppCompatActivity implements BookmarkDataManager {
    private static Gson gson = new Gson();
    ListView listView;
    ArrayList<BookmarkData> bookmarkData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_page);
        listView = findViewById(R.id.bookmark_list);
        layoutBookmarkList();

        Button DeleteBookmark = (Button) findViewById(R.id.DeleteBtn);
        DeleteBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add code to delete bookmark here
            }
        });

       Button ReturnHome = (Button) findViewById(R.id.ReturnHomeBtn);
        ReturnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BookmarkPage.this, MapsActivity.class));
            }
        }); 
    }

    @Override
    protected void onResume(){
        super.onResume();
        layoutBookmarkList();
    }

    private void layoutBookmarkList(){
        updateBookmarkData();
        BookmarkAdapter adapter = new BookmarkAdapter(getApplicationContext(), R.layout.bookmark_list_item, bookmarkData, this);
        listView.setAdapter(adapter);
        listView.invalidate();
    }

    private void updateBookmarkData(){
        String preferenceData = getSharedPreferences(BOOKMARK_KEY, MODE_PRIVATE).getString("bookmarkData","Not available");
        if (!preferenceData.equals("Not available") && !preferenceData.equals("")) {
            bookmarkData = gson.fromJson(preferenceData,new TypeToken<ArrayList<BookmarkData>>(){}.getType());
        }
        else {
            bookmarkData = new ArrayList<>();
        }
    }

    public void launchBookmarkAutoComplete(View view) {
        Intent intent = new Intent(this, BookmarkAutoComplete.class);
        startActivityForResult(intent,1014);
    }


    @Override
    public void deleteBookmark(String key) {
        int index = -1;
        for (int i=0; i<bookmarkData.size(); i++){
            if (bookmarkData.get(i).name.equals(key)){
                index = i;
            }
        }
        if (index >= 0){
            bookmarkData.remove(index);
        }
        SharedPreferences.Editor editor = getSharedPreferences(BOOKMARK_KEY, MODE_PRIVATE).edit();
        editor.putString("bookmarkData",gson.toJson(bookmarkData));
        editor.commit();
        layoutBookmarkList();
    }

    @Override
    public void addBookmark(BookmarkData bookmark) {
        SharedPreferences.Editor editor = getSharedPreferences(BOOKMARK_KEY, MODE_PRIVATE).edit();
        bookmarkData.add(bookmark);
        editor.putString("bookmarkData",gson.toJson(bookmarkData));
        editor.commit();
        layoutBookmarkList();
    }
}
