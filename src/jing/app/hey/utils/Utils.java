package jing.app.hey.utils;

import android.os.AsyncTask;

public class Utils {
	public static AsyncTask<Void, Void, Void> runAsync(final Runnable r) {
        return new AsyncTask<Void, Void, Void>() {
            @Override protected Void doInBackground(Void... params) {
                r.run();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
