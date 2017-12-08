package com.sriky.joketeller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.sriky.jokedisplay.JokeActivity;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import java.io.IOException;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final String POWERED_BY = ": Powered by";

    private ProgressBar mProgressBar;
    private TextView mErrorView;
    private Button mFetchJokeBtn;

    // the idling resource used for UI testing.
    @Nullable
    private JokeTellerIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());

        mProgressBar = findViewById(R.id.progressBar);
        mErrorView = findViewById(R.id.tv_error_msg);
        mFetchJokeBtn = findViewById(R.id.btn_fetchJoke);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Create or returns an instance of idling resource to test {@link MainActivity}
     *
     * @return {@link JokeTellerIdlingResource} instance.
     */
    @VisibleForTesting
    @NonNull
    public JokeTellerIdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new JokeTellerIdlingResource();
        }
        return mIdlingResource;
    }

    public void tellJoke(View view) {
        new FetchJokeTask().execute();
    }

    class FetchJokeTask extends AsyncTask<Void, Void, String> {

        private MyApi myApiService = null;

        @Override
        protected void onPreExecute() {
            //disable the fetch joke button.
            mFetchJokeBtn.setEnabled(false);
            //show the progress bar.
            mProgressBar.setVisibility(View.VISIBLE);
            //hide the error text view.
            mErrorView.setVisibility(View.INVISIBLE);

            //set idling resource state to not idle.
            getIdlingResource().setIdleState(false);
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (myApiService == null) {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        //TODO: make it work on android device.
                        .setRootUrl("http://192.168.1.7:8080/_ah/api/")
                        .setApplicationName(getString(R.string.app_name))
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
            //disable the fetch joke button.
            mFetchJokeBtn.setEnabled(true);
            //show the progress bar.
            mProgressBar.setVisibility(View.INVISIBLE);
            //set idling resource state to idle.
            getIdlingResource().setIdleState(true);

            if (TextUtils.isEmpty(s)) {
                Timber.e("No joke returned from server!!!");
                return;
            }

            String[] jokeData = s.split(POWERED_BY);

            if (jokeData.length > 1) {
                Intent intent = new Intent(MainActivity.this, JokeActivity.class);
                intent.putExtra(JokeActivity.JOKE_INTENT_BUNDLE_KEY, jokeData[0]);
                startActivity(intent);
            } else {
                Timber.e("Error getting jokes: %s", s);
                mErrorView.setVisibility(View.VISIBLE);
            }
        }
    }
}
