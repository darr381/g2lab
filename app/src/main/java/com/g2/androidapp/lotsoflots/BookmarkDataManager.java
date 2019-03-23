package com.g2.androidapp.lotsoflots;

import com.g2.androidapp.lotsoflots.BookmarkData;


public interface BookmarkDataManager {
    public static String BOOKMARK_KEY = "bookmarkData";
    public void deleteBookmark(String key);
    public void addBookmark(BookmarkData bookmark);
}
