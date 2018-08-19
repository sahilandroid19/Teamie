package com.example.sahil.teamie.util;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sahil.teamie.R;

import org.jsoup.Jsoup;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Utility {

    public static final String RANDOM_URL = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&generator=random&exsentences=10&grnnamespace=0";

    public static final String SCORE_INTENT = "score";
    public static final String USER_WORDS_INTENT = "user";
    public static final String CORRECT_WORDS_INTENT = "correct";

    /*
    Parses text from html tags using JSoup library
     */
    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    /*
    Returns list of sentences from response string
     */
    public static List<String> getSentences(String source) {
        List<String> sentences = new ArrayList<>();

        Locale currentLocale = new Locale("en", "US");
        BreakIterator iterator = BreakIterator.getSentenceInstance(currentLocale);
        iterator.setText(source);
        int start = iterator.first();
        for (int end = iterator.next();
             end != BreakIterator.DONE;
             start = end, end = iterator.next()) {
             sentences.add(source.substring(start, end));
        }
        return sentences;
    }

    /*
    Returns number of senteces in  a string
     */
    public static int numOfSentences(String text){
        Locale currentLocale = new Locale("en", "US");
        BreakIterator iterator
                = BreakIterator.getSentenceInstance(currentLocale);

        StringBuilder markers = new StringBuilder();
        markers.setLength(text.length() + 1);
        for (int k = 0; k < markers.length(); k++) {
            markers.setCharAt(k, ' ');
        }
        int count = 0;
        iterator.setText(text);
        int boundary = iterator.first();
        while (boundary != BreakIterator.DONE) {
            markers.setCharAt(boundary, '^');
            ++count;
            boundary = iterator.next();
        }
        return count-1;
    }

    /*
    Returns blanks accordint to word length
     */
    public static String getSpaces(String word){
        StringBuilder space = new StringBuilder();
        for(int i=0; i<word.length(); i++){
            if(i == 0 )
                space.append(' ');

            space.append('_');

            if(i == word.length()-1)
                space.append(' ');
        }
        return space.toString();
    }

    /*
    Returns custom textview to display text
     */
    public static TextView getTextView(Context context, int index){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0 , 0);

        TextView textView = new TextView(context);
        textView.setId(index);
        textView.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        textView.setTextColor(context.getResources().getColor(R.color.textVew));
        textView.setLayoutParams(params);
        return textView;
    }

    /*
    Returns textview for Grid layout
     */
    public static TextView getGridView(Context context, String text, int i){
        TextView textView = new TextView(context);
        textView.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        textView.setText(text);

        if(i == 0)
            textView.setTextColor(context.getResources().getColor(R.color.black));
        else
            textView.setTextColor(context.getResources().getColor(R.color.textVew));
        return textView;
    }

}
