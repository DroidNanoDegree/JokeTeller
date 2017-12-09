package com.sriky.joketeller;

import android.content.Intent;
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

import com.sriky.jokedisplay.JokeActivity;
import com.sriky.joketeller.event.Message;
import com.sriky.joketeller.task.FetchJokeAsyncTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final String POWERED_BY = ": Powered by";
    private static final String END_POINT_URL = BuildConfig.URL_SERVER + ":8080/_ah/api/";
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
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPreExecute(Message.FetchJokeAsyncTaskOnPreExecute event) {
        //disable the fetch joke button.
        mFetchJokeBtn.setEnabled(false);
        //show the progress bar.
        mProgressBar.setVisibility(View.VISIBLE);
        //hide the error text view.
        mErrorView.setVisibility(View.INVISIBLE);

        //set idling resource state to not idle.
        getIdlingResource().setIdleState(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostExecute(Message.FetchJokeAsyncTaskOnPostExecute event) {
        String result = event.getResult();

        //disable the fetch joke button.
        mFetchJokeBtn.setEnabled(true);
        //show the progress bar.
        mProgressBar.setVisibility(View.INVISIBLE);
        //set idling resource state to idle.
        getIdlingResource().setIdleState(true);

        if (TextUtils.isEmpty(result)) {
            Timber.e("No joke returned from server!!!");
            return;
        }

        String[] jokeData = result.split(POWERED_BY);

        if (jokeData.length > 1) {
            Intent intent = new Intent(MainActivity.this, JokeActivity.class);
            intent.putExtra(JokeActivity.JOKE_INTENT_BUNDLE_KEY, jokeData[0]);
            startActivity(intent);
        } else {
            Timber.e("Error getting jokes: %s", result);
            mErrorView.setVisibility(View.VISIBLE);
        }
    }

    public void tellJoke(View view) {
        new FetchJokeAsyncTask().execute(END_POINT_URL);
    }
}
