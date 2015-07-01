package com.darkosancanin.naturaldateandtime.models;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;

import com.darkosancanin.naturaldateandtime.R;

public class ExampleQuestionGenerator {
    private ArrayList<String> randomQuestions;
    private Random randomGenerator;

    public ExampleQuestionGenerator(Context context){
        randomGenerator = new Random();
        randomQuestions = new ArrayList<String>();
        randomQuestions.add(context.getString(R.string.example_section_1_example_1));
        randomQuestions.add(context.getString(R.string.example_section_1_example_2));
        randomQuestions.add(context.getString(R.string.example_section_2_example_1));
        randomQuestions.add(context.getString(R.string.example_section_2_example_2));
        randomQuestions.add(context.getString(R.string.example_section_3_example_1));
        randomQuestions.add(context.getString(R.string.example_section_3_example_2));
        randomQuestions.add(context.getString(R.string.example_section_4_example_1));
        randomQuestions.add(context.getString(R.string.example_section_5_example_1));
    }

    public String getRandomExampleQuestion(){
        int index = randomGenerator.nextInt(randomQuestions.size());
        return "e.g. " +  randomQuestions.get(index);
    }
}