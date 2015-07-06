package com.darkosancanin.naturaldateandtime;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.darkosancanin.naturaldateandtime.models.Answer;
import com.darkosancanin.naturaldateandtime.models.ExampleQuestionGenerator;
import com.darkosancanin.naturaldateandtime.views.ClearableEditTextLayout;
import com.darkosancanin.naturaldateandtime.views.OnClearHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class QuestionActivity extends BaseActivity {
    public final static String QUESTION_EXTRA_NAME = "QUESTION_EXTRA";
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    ExampleQuestionGenerator exampleQuestionGenerator;
    ClearableEditTextLayout clearableEditTextLayout;
    TextView answerTextView;
    LinearLayout noteView;
    TextView noteTextView;
    LinearLayout loadingView;
    ImageView loadingImageView;
    AnimationDrawable loadingAnimation;
    ImageButton speechButton;
    boolean hasCancelledTheRequest = false;
    boolean textHasChangedSinceAnswer = false;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            if (intent.getExtras().containsKey(QUESTION_EXTRA_NAME)) {
                String question = intent.getExtras().getString(QUESTION_EXTRA_NAME);
                clearableEditTextLayout.getEditText().setTextColor(getResources().getColor(android.R.color.black));
                clearableEditTextLayout.getEditText().setText(question);
                answerTheQuestion();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        exampleQuestionGenerator = new ExampleQuestionGenerator(this);
        clearableEditTextLayout = (ClearableEditTextLayout) findViewById(R.id.question_edittext);
        answerTextView = (TextView) findViewById(R.id.question_answer);
        noteTextView = (TextView) findViewById(R.id.question_note);
        noteView = (LinearLayout)findViewById(R.id.note_view);
        loadingView = (LinearLayout)findViewById(R.id.loading_view);
        loadingImageView = (ImageView)findViewById(R.id.loading_imageview);
        loadingImageView.setBackgroundResource(R.drawable.loading_animation);
        loadingAnimation = (AnimationDrawable) loadingImageView.getBackground();
        setupViews();
    }

    private void setupViews() {
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(end > 0) {
                    char lastCharacter = source.charAt(end - 1);
                    if(lastCharacter == '\n')
                    {
                        answerTheQuestion();
                        return "";
                    }
                }
                return null;
            }
        };
        clearableEditTextLayout.getEditText().setFilters(new InputFilter[]{filter});
        clearableEditTextLayout.getEditText().addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textHasChangedSinceAnswer == false) {
                    hideAll();
                    clearableEditTextLayout.getEditText().setHint("");
                    clearableEditTextLayout.getEditText().setTextColor(getResources().getColor(android.R.color.black));
                }
                textHasChangedSinceAnswer = true;
            }

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
        clearableEditTextLayout.getEditText().setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    answerTheQuestion();
                    return true;
                }
                return false;
            }
        });
        clearableEditTextLayout.setOnClearHandler(new OnClearHandler() {
            public void onClear() {
                hideAll();
                showRandomExampleQuestion();
                hasCancelledTheRequest = true;
                if (speechButton.isEnabled()) {
                    speechButton.setVisibility(ImageButton.VISIBLE);
                }
            }
        });
        TextView footer = (TextView) findViewById(R.id.footer);
        footer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.naturaldateandtime.com"));
                startActivity(browserIntent);
            }
        });
        setUpSpeechButton();
        showRandomExampleQuestion();
    }

    private void setUpSpeechButton(){
        speechButton = (ImageButton)findViewById(R.id.speech_button);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognitionActivity();
            }
        });
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            speechButton.setEnabled(false);
            speechButton.setVisibility(ImageButton.GONE);
        }
    }

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string.speak_now));
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String spokenText = matches.get(0);
                clearableEditTextLayout.getEditText().setText(spokenText);
                answerTheQuestion();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showRandomExampleQuestion(){
        clearableEditTextLayout.getEditText().setHint(exampleQuestionGenerator.getRandomExampleQuestion());
        clearableEditTextLayout.getEditText().setTextColor(getResources().getColor(R.color.clearable_edit_text_hint_color));
    }

    private void answerTheQuestion() {
        hideKeyboard();
        if(clearableEditTextLayout.getEditText().getText().length() == 0) return;
        showLoadingView();
        new AnswerQuestionTask().execute();
    }

    private void showLoadingView() {
        hideAll();
        loadingView.setVisibility(LinearLayout.VISIBLE);
        loadingAnimation.start();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) answerTextView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(answerTextView.getWindowToken(), 0);
    }

    private void showAnswer(Answer answer) {
        if(hasCancelledTheRequest) return;
        hideAll();
        answerTextView.setVisibility(LinearLayout.VISIBLE);
        if(answer.understoodQuestion()){
            answerTextView.setText(answer.getAnswerText());
            if(answer.hasNote()){
                noteView.setVisibility(LinearLayout.VISIBLE);
                noteTextView.setText(answer.getNote());
            }
        } else {
            answerTextView.setText(R.string.did_not_understand_question);
        }
        textHasChangedSinceAnswer = false;
    }

    private void hideAll(){
        speechButton.setVisibility(ImageButton.GONE);
        answerTextView.setVisibility(LinearLayout.GONE);
        noteView.setVisibility(LinearLayout.GONE);
        loadingView.setVisibility(LinearLayout.GONE);
        loadingAnimation.stop();
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

            StringBuffer jsonContentBuffer = new StringBuffer("");
            try{
                URL url = new URL("http://www.naturaldateandtime.com/api/question?client=android&client_version=2_0&q=" + URLEncoder.encode(clearableEditTextLayout.getEditText().getText().toString(), "UTF-8"));
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                if (connection.getResponseCode() == 200) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        jsonContentBuffer.append(line);
                    }
                    return jsonContentBuffer.toString();
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
