package com.example.sahil.teamie.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.example.sahil.teamie.R;
import com.example.sahil.teamie.util.Utility;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ResultActivityFragment extends Fragment {

    public ResultActivityFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_result, container, false);

        assert getArguments() != null;
        String score = getString(R.string.score, getArguments().getInt("score"));
        ArrayList<String> userWords = getArguments().getStringArrayList("user");
        ArrayList<String> words = getArguments().getStringArrayList("correct");

        ((TextView) view.findViewById(R.id.score_text)).setText(score);

        GridLayout gridLayout = view.findViewById(R.id.result_layout);

        for(int i=0; i<11; i++){
            for(int j=0; j<gridLayout.getColumnCount(); j++) {
                if (i == 0) {
                    if (j == 0)
                        gridLayout.addView(Utility.getGridView(getActivity(), getString(R.string.grid_number), i));
                    if (j == gridLayout.getColumnCount() - 2)
                        gridLayout.addView(Utility.getGridView(getActivity(), getString(R.string.user_answer), i));
                    if (j == gridLayout.getColumnCount() - 1)
                        gridLayout.addView(Utility.getGridView(getActivity(), getString(R.string.correct_answer), i));
                }
                else {
                    if(j == 0)
                        gridLayout.addView(Utility.getGridView(getActivity(), i+"", i));
                    if(j == gridLayout.getColumnCount() - 2) {
                        assert userWords != null;
                        gridLayout.addView(Utility.getGridView(getActivity(), userWords.get(i-1), i));
                    }
                    if(j == gridLayout.getColumnCount() - 1) {
                        assert words != null;
                        gridLayout.addView(Utility.getGridView(getActivity(), words.get(i-1), i));
                    }
                }
            }
        }

        return view;
    }
}
