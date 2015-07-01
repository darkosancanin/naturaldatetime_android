package com.darkosancanin.naturaldateandtime.views;

import com.darkosancanin.naturaldateandtime.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class ClearableEditText extends RelativeLayout
{
    private LayoutInflater inflater = null;
    private EditText edit_text;
    private Button btn_clear;
    private OnClearHandler onClearHandler = null;

    public ClearableEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.clearable_edit_text, this, true);
        edit_text = (EditText) findViewById(R.id.clearable_edit);
        btn_clear = (Button) findViewById(R.id.clearable_button_clear);
        wireUpClearTextButton();
    }

    public ClearableEditText(Context context)
    {
        super(context);
    }

    public void setOnClearHandler(OnClearHandler handler){
        onClearHandler = handler;
    }

    private void wireUpClearTextButton()
    {
        btn_clear.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                edit_text.setText("");
                if(onClearHandler != null)
                    onClearHandler.onClear();
            }
        });
    }

    public EditText getEditText()
    {
        return edit_text;
    }
}
