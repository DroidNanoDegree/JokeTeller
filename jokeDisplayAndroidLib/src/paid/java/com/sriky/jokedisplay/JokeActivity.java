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

package com.sriky.jokedisplay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

public class JokeActivity extends AppCompatActivity {

    public static final String JOKE_INTENT_BUNDLE_KEY = "joke_bundle_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke);

        //add support for back-key navigation.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent == null) throw new RuntimeException("Empty intent not supported!");

        if (!intent.hasExtra(JOKE_INTENT_BUNDLE_KEY)) {
            throw new RuntimeException("Intent doesn't contain joke String extra!");
        }

        String joke = intent.getStringExtra(JOKE_INTENT_BUNDLE_KEY);
        if (TextUtils.isEmpty(joke)) {
            throw new RuntimeException("Joke String extra cannot be empty!");
        }

        TextView jokeView = findViewById(R.id.tv_joke);
        jokeView.setText(joke);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                NavUtils.navigateUpFromSameTask(JokeActivity.this);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
