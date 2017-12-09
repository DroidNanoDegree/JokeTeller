package com.sriky.joketeller.task;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.sriky.joketeller.event.Message;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import timber.log.Timber;

/**
 * AsyncTask to get jokes from the backend.
 */

public class FetchJokeAsyncTask extends AsyncTask<String, Void, String> {
    private MyApi myApiService = null;

    @Override
    protected void onPreExecute() {
        EventBus.getDefault().post(new Message.FetchJokeAsyncTaskOnPreExecute());
    }

    @Override
    protected String doInBackground(String... params) {
        if (myApiService == null) {  // Only do this once
            String backendUrl = params[0];
            Timber.i("URL_SERVER: %s", backendUrl);
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    .setRootUrl(backendUrl)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();
        }

        try {
            return myApiService.getJoke().execute().getData();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        EventBus.getDefault().post(new Message.FetchJokeAsyncTaskOnPostExecute(s));
    }
}
