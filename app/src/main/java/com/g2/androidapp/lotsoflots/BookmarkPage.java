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

public class BookmarkPage extends AppCompatActivity {

    String location;
    String addressName;
    private static final String LOG_TAG =
            MainActivity.class.getSimpleName();
    //private SharedPreferences sharedPreferences;
    private static Gson gson = new Gson();
    ListView listView;
    //BookmarkAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_page);
        listView = findViewById(R.id.bookmark_list);
        //adapter = new BookmarkAdapter(this,R.layout.bookmark_list_item);
        //listView.setAdapter(adapter);
        listView.setAdapter(new BookmarkAdapter(getApplicationContext(), R.layout.bookmark_list_item, new ArrayList<BookmarkData>()));

        //sharedPreferences = getSharedPreferences("bookmarkData",MODE_PRIVATE);

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
        bookmarkList();
    }

    @Override
    protected void onActivityResult(int requestCode , int resultCode , Intent data){
   //     requestCode = 1014; resultCode = RESULT_OK;
//        location = data.getStringExtra("location");
//        addressName = data.getStringExtra("addressName") ;
//        Log.i("location", location);
//        Log.i("addressName" , addressName);

    }
    public void bookmarkList() {
        LinearLayout scrollContents = findViewById(R.id.scrollContents);
        ArrayList<BookmarkData> bookmarkDataList = new ArrayList<>();
        List<String> bookmarkData = new ArrayList<>();

        String preferenceData = getSharedPreferences("bookmarkData", MODE_PRIVATE).getString("bookmarkData","Not available");
        Log.d("Bookmark",preferenceData);


        if (!preferenceData.equals("Not available") && !preferenceData.equals("")) {
            bookmarkDataList = gson.fromJson(preferenceData,new TypeToken<List<BookmarkData>>(){}.getType());
        }

        for (BookmarkData data : bookmarkDataList){
            bookmarkData.add(data.getName());
        }

        //BookmarkAdapter adapter = new BookmarkAdapter(getApplicationContext(),R.layout.bookmark_list_item,bookmarkDataList);
        //listView.setAdapter(adapter);
        listView.setAdapter(new BookmarkAdapter(getApplicationContext(), R.layout.bookmark_list_item, bookmarkDataList));
        listView.invalidate();
    }


    public void launchBookmarkAutoComplete(View view) {
        Log.d(LOG_TAG, "Add Bookmark Button clicked!");
        Intent intent = new Intent(this, BookmarkAutoComplete.class);
        startActivityForResult(intent,1014);

    }

}
