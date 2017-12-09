/*
 * Copyright (C) 2017 Srikanth Basappa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.sriky.joketeller;

import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import com.sriky.joketeller.event.Message;
import com.sriky.joketeller.task.FetchJokeAsyncTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import timber.log.Timber;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Class to test intents used in Bakelicious.
 */

@RunWith(AndroidJUnit4.class)
public class JokeTellerIntentTests {
    private static final String END_POINT_URL = BuildConfig.URL_SERVER + ":8080/_ah/api/";
    private static final String POWERED_BY = ": Powered by[JokeTeller JavaLib]";
    CountDownLatch mSignal = null;
    private FetchJokeAsyncTask mFetchJokeTask;
    private String mResult = null;

    @Before
    public void startUp() {
        mFetchJokeTask = new FetchJokeAsyncTask();
        mSignal = new CountDownLatch(1);
        EventBus.getDefault().register(this);
    }

    @After
    public void shutDown() {
        mSignal.countDown();
        EventBus.getDefault().unregister(this);
    }

    @Test
    public void test_JokeRetrievalFromAsyncTask() {
        try {
            mFetchJokeTask.execute(END_POINT_URL);
            mSignal.await();
            assertFalse(TextUtils.isEmpty(mResult));
            assertTrue(mResult.endsWith(POWERED_BY));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostExecute(Message.FetchJokeAsyncTaskOnPostExecute event) {
        mResult = event.getResult();
        Timber.d("***Result: %s", mResult);
        mSignal.countDown();
    }
}
