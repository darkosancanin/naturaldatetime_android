package com.darkosancanin.naturaldateandtime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ExamplesActivity extends BaseActivity {
    private OnClickListener onExampleClick = new OnClickListener() {
        public void onClick(View v) {
            Button button = (Button)v;
            String question = button.getText().toString();
            Intent questionIntent = new Intent(v.getContext(), QuestionActivity.class);
            questionIntent.putExtra(QuestionActivity.QUESTION_EXTRA_NAME, question);
            startActivity(questionIntent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examples);
        setUpExamples();
    }
    private void setUpExamples(){
        wireUpClickEvent(R.id.example_section_1_example_1);
        wireUpClickEvent(R.id.example_section_1_example_2);
        wireUpClickEvent(R.id.example_section_2_example_1);
        wireUpClickEvent(R.id.example_section_2_example_2);
        wireUpClickEvent(R.id.example_section_3_example_1);
        wireUpClickEvent(R.id.example_section_3_example_2);
        wireUpClickEvent(R.id.example_section_4_example_1);
        wireUpClickEvent(R.id.example_section_5_example_1);
    }

    private void wireUpClickEvent(int textViewId){
        Button button = (Button)findViewById(textViewId);
        button.setOnClickListener(onExampleClick);
    }
}