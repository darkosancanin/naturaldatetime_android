package com.darkosancanin.naturaldateandtime.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Answer {
    private String answerText = null;
    private String note = null;
    private Boolean understoodQuestion = null;
    private Boolean isValidAnswerResult = true;;

    public Answer(String jsonString){
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            answerText = jsonObject.getString("AnswerText");
            setNote(jsonObject.getString("Note"));
            understoodQuestion = jsonObject.getBoolean("UnderstoodQuestion");
        } catch (JSONException e) {
            isValidAnswerResult = false;
            Log.v("Answer", e.getLocalizedMessage());
        }
    }

    public String getAnswerText() {
        return answerText;
    }

    public String getNote() {
        return note;
    }

    private void setNote(String theNote){
        if(theNote != null && theNote != "null")
            note = theNote;
    }

    public Boolean hasNote() {
        return note != null && note.length() > 0;
    }

    public Boolean understoodQuestion() {
        return understoodQuestion;
    }

    public Boolean isValidAnswerResult() {
        return isValidAnswerResult;
    }
}