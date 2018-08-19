package com.example.sahil.teamie.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.sahil.teamie.R;
import com.example.sahil.teamie.util.Utility;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int score = getIntent().getIntExtra(Utility.SCORE_INTENT, 0);
        ArrayList<String> userWords = getIntent().getStringArrayListExtra(Utility.USER_WORDS_INTENT);
        ArrayList<String> words = getIntent().getStringArrayListExtra(Utility.CORRECT_WORDS_INTENT);

        Bundle bundle = new Bundle();
        bundle.putInt(Utility.SCORE_INTENT, score);
        bundle.putStringArrayList(Utility.USER_WORDS_INTENT, userWords);
        bundle.putStringArrayList(Utility.CORRECT_WORDS_INTENT, words);

        ResultActivityFragment activityFragment = new ResultActivityFragment();
        activityFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_layout, activityFragment);
        fragmentTransaction.commit();

    }

    public void replayClicked(View view){
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
