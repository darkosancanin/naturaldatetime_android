package com.darkosancanin.naturaldateandtime;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.darkosancanin.naturaldateandtime.models.Answer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class QuestionActivity extends BaseActivity {
    EditText questionEditText;
    TextView answerTextView;
    TextView noteTextView;
    Boolean hasCancelledTheRequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        questionEditText = (EditText) findViewById(R.id.question_text);
        answerTextView = (TextView) findViewById(R.id.question_answer);
        noteTextView = (TextView) findViewById(R.id.question_note);
        setupViewEventHandlers();
    }

    private void setupViewEventHandlers() {
        questionEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        answerTheQuestion();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void answerTheQuestion() {
        hideKeyboard();
        if(questionEditText.getText().length() == 0) return;
        showLoadingView();
        new AnswerQuestionTask().execute();
    }

    private void showLoadingView() {

    }

    private void hideKeyboard() {

    }

    private void showAnswer(Answer answer) {
        if(hasCancelledTheRequest) return;
        hideAll();
        answerTextView.setVisibility(LinearLayout.VISIBLE);
        if(answer.understoodQuestion()){
            answerTextView.setText(answer.getAnswerText());
            if(answer.hasNote()){
                noteTextView.setVisibility(LinearLayout.VISIBLE);
                noteTextView.setText(answer.getNote());
            }
        }
        else{
            answerTextView.setText(R.string.did_not_understand_question);
        }
    }

    private void hideAll(){
        answerTextView.setVisibility(LinearLayout.GONE);
        noteTextView.setVisibility(LinearLayout.GONE);
    }

    private void showError(int errorTextResId) {
        hideAll();
        answerTextView.setVisibility(LinearLayout.VISIBLE);
        answerTextView.setText(errorTextResId);
    }

    private class AnswerQuestionTask extends AsyncTask<Void, Void, String> {
        private int errorTextResId = R.string.generic_error_message;

        @Override
        protected String doInBackground(Void... params) {
            hasCancelledTheRequest = false;
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                errorTextResId = R.string.no_internet_error_message;
                return null;
            }

            StringBuffer jsonContent = new StringBuffer("");
            try{
                URL url = new URL("http://www.naturaldateandtime.com/api/question?q=" + URLEncoder.encode(questionEditText.getText().toString(), "UTF-8"));
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                if (connection.getResponseCode() == 200) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        jsonContent.append(line);
                    }
                    return jsonContent.toString();
                }
            } catch (IOException e) {
                Log.v("Question", e.getLocalizedMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result == null) {
                showError(errorTextResId);
            }
            else {
                Answer answer = new Answer(result);
                if(answer.isValidAnswerResult())
                    showAnswer(answer);
                else
                    showError(errorTextResId);
            }
        }
    }
}
