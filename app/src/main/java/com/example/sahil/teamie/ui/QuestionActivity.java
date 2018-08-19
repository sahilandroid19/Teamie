package com.example.sahil.teamie.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sahil.teamie.R;
import com.example.sahil.teamie.model.Question;
import com.example.sahil.teamie.util.Utility;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private Question mQuestion;

    private MaterialDialog mDialog;

    List<String> words = new ArrayList<>();
    List<String> shuffledWords = new ArrayList<>();
    List<SpannableString> spannableStrings = new ArrayList<>();
    List<String> userWords = new ArrayList<>();

    private ClickableSpan clickableSpan;

    private LinearLayout mLayout;
    private Boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        if(findViewById(R.id.detail_container) != null)
            mTwoPane = true;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((Button) findViewById(R.id.score_button)).setOnClickListener(this);

        for(int i=0; i<10; i++)
            userWords.add("");

        mLayout = findViewById(R.id.base_layout);
        showDialog();

        clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                final TextView textView = (TextView) widget;

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(QuestionActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, shuffledWords);

                final LovelyChoiceDialog choiceDialog = new LovelyChoiceDialog(QuestionActivity.this);
                choiceDialog.setTopColorRes(R.color.green)
                        .setTitle(R.string.word_chooser_title)
                        .setItems(arrayAdapter, new LovelyChoiceDialog.OnItemSelectedListener<String>() {
                            @Override
                            public void onItemSelected(int position, String item) {
                                int index = textView.getId();
                                textView.setText(replaceWord(index, item), TextView.BufferType.SPANNABLE);
                                choiceDialog.dismiss();
                            }
                        })
                .show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        requestResponse(Utility.RANDOM_URL);
    }

    private void showDialog(){
        mDialog = new MaterialDialog.Builder(this)
                .title(R.string.app_name)
                .content(R.string.progress_dialog)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .widgetColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

    private SpannableString replaceWord(int index, String word){
        userWords.set(index, word);

        StringBuilder stringBuilder = new StringBuilder(spannableStrings.get(index));

        int startIndex =  stringBuilder.toString().indexOf('_');
        int endIndex = stringBuilder.lastIndexOf("_");
        stringBuilder.replace(startIndex, endIndex+1, word);

        SpannableString spannableString = new SpannableString(stringBuilder);
        spannableString.setSpan(new ForegroundColorSpan(Color.GREEN), startIndex, startIndex + word.length(), 0);
        spannableStrings.set(index, spannableString);
        return spannableString;
    }

    /*
    Fetches json response from Wiki API
     */
    private void requestResponse(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    parseData(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);
    }

    /*
    Parses json data to custom java objects
     */
    private void parseData(String response) throws JSONException {
        mQuestion = new Question();
        JSONObject jsonObject = new JSONObject(response);
        JSONObject query = jsonObject.getJSONObject("query");
        JSONObject pages = query.getJSONObject("pages");

        Iterator<String> keys = pages.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject article = pages.getJSONObject(key);
            mQuestion.setTitle(article.getString("title"));
            mQuestion.setContent(Utility.html2text(article.getString("extract")));
        }

        int count = Utility.numOfSentences(mQuestion.getContent());

        if(count >= 10){
            formatString();
        }else {
            requestResponse(Utility.RANDOM_URL);
        }

    }

    /*
    Creates a list of 10 sentences and replaces a random word from each sentence and attaches click span to
    that word
     */
    private void formatString(){
        List<String> sentences = Utility.getSentences(mQuestion.getContent());

        for(int i=0; i<10; i++){
            List<String> wordsList = Arrays.asList(sentences.get(i).split("\\W+"));
            String word = wordsList.get(new Random().nextInt(wordsList.size()));

            int index = 0;
            Matcher m = Pattern.compile("\\b" + word + "\\b").matcher(sentences.get(i));
            while (m.find()) {
                index = m.start();
            }
            words.add(word);

            StringBuilder stringBuffer = new StringBuilder(sentences.get(i));
            int end = index+word.length();
            stringBuffer.replace(index, index + word.length(), Utility.getSpaces(word));
            sentences.set(i, stringBuffer.toString());

            SpannableString spannableString = new SpannableString(stringBuffer.toString());
            spannableString.setSpan(clickableSpan, index, end+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStrings.add(spannableString);

            TextView textView = Utility.getTextView(this, i);
            textView.setText(spannableString);
            textView.setMovementMethod(LinkMovementMethod.getInstance());

            mLayout.addView(textView);

        }
        shuffledWords = new ArrayList<>(words);
        Collections.shuffle(shuffledWords);

        mDialog.dismiss();
        ((TextView) findViewById(R.id.title)).setText(mQuestion.getTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question, menu);
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

    /*
    Submit button onClick() method
     */
    @Override
    public void onClick(View v) {
        int score = 0;

        if(userWords.size() == 0) {
            for(int i=0; i<10; i++)
                userWords.add("");
        }

        for (int i = 0; i < 10; i++) {
            if (words.get(i).equals(userWords.get(i))) {
                score++;
            }
        }

        if(!mTwoPane) {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra(Utility.SCORE_INTENT, score);
            intent.putStringArrayListExtra(Utility.USER_WORDS_INTENT, new ArrayList<>(userWords));
            intent.putStringArrayListExtra(Utility.CORRECT_WORDS_INTENT, new ArrayList<>(words));
            startActivityForResult(intent, 1);
        }else {
            Bundle bundle = new Bundle();
            bundle.putInt(Utility.SCORE_INTENT, score);
            bundle.putStringArrayList(Utility.USER_WORDS_INTENT, new ArrayList<>(userWords));
            bundle.putStringArrayList(Utility.CORRECT_WORDS_INTENT, new ArrayList<>(words));

            ResultActivityFragment activityFragment = new ResultActivityFragment();
            activityFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.detail_container, activityFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == RESULT_OK)
                freeData();
        }
    }

    private void freeData(){
        words = new ArrayList<>();
        shuffledWords = new ArrayList<>();
        userWords = new ArrayList<>();
        spannableStrings = new ArrayList<>();

        mLayout.removeAllViews();

        mDialog.cancel();
        showDialog();
        requestResponse(Utility.RANDOM_URL);
    }

    public void replayClicked(View view){
        freeData();
    }
}
