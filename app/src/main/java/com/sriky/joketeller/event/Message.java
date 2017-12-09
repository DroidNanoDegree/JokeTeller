package com.sriky.joketeller.event;

import android.os.AsyncTask;

/**
 * Used to send events using {@link org.greenrobot.eventbus.EventBus}
 */

public class Message {

    /**
     * Triggered upon {@link AsyncTask#onPreExecute()}
     */
    public static class FetchJokeAsyncTaskOnPreExecute {
    }

    /**
     * Triggered upon {@link AsyncTask#onPostExecute(Object)}
     */
    public static class FetchJokeAsyncTaskOnPostExecute {
        private String mResult;

        public FetchJokeAsyncTaskOnPostExecute(String result) {
            mResult = result;
        }

        public String getResult() {
            return mResult;
        }
    }
}
