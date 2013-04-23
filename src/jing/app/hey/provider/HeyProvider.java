package jing.app.hey.provider;

import java.io.File;
import java.io.FilenameFilter;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public class HeyProvider extends ContentProvider {
    public static final String AUTHORITY = "jing.app.hey.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    
    public static final String[] COLUMN_NAMES = {
        "_id",
        "data",
        "title",
        "time"
    };
    
    private File mDirectory;
    private Context mContext;
    @Override
    public boolean onCreate() {
        mContext = getContext();
        mDirectory = mContext.getDir("received", Context.MODE_PRIVATE);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        MatrixCursor cursor = new MatrixCursor(COLUMN_NAMES);
        if (mDirectory != null && mDirectory.exists()) {
            File[] files = mDirectory.listFiles(new FilenameFilter() {
                
                private boolean isJpg(String file){   
                    if (file.toLowerCase().endsWith(".jpg")){   
                        return true;   
                    }else{   
                        return false;   
                    }   
                } 
                
                @Override
                public boolean accept(File dir, String filename) {
                    return isJpg(filename);
                }
            });
            
            if (files != null && files.length > 0) {
                for (int i = 0 ; i < files.length ; i++) {
                    long time = 0;
                    try {
                        time = Long.valueOf(files[i].getName().split(".")[0]);
                    } catch (Exception e) {
                    }
                    
                    cursor.newRow().add((long) i)
                          .add(files[i].getAbsolutePath())
                          .add(files[i].getName())
                          .add(time);
                }
                
                return cursor;
            }
        }
        
        return null;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

}
