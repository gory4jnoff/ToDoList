package com.goryajnoff.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.List;

public class AddNoteActivity extends AppCompatActivity {
    private EditText editTextEnterNote;
    private RadioButton radioButtonLow;
    private RadioButton radioButtonMedium;
    private RadioButton radioButtonHigh;
    private Button buttonSave;
    private AddNoteViewModel addNoteViewModel;
    private Handler handler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        initViews();
        addNoteViewModel = new ViewModelProvider(this).get(AddNoteViewModel.class);
        addNoteViewModel.getShouldCloseScreen().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    finish();
                }
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedNote();
            }
        });

    }


    private void initViews() {
        editTextEnterNote = findViewById(R.id.editTextEnterNote);
        radioButtonLow = findViewById(R.id.radioButtonLow);
        radioButtonMedium = findViewById(R.id.radioButtonMedium);
        radioButtonHigh = findViewById(R.id.radioButtonHigh);
        buttonSave = findViewById(R.id.buttonSave);
    }


    private void savedNote() {
        int priority;
        if (radioButtonLow.isChecked()) {
            priority = 0;
        } else if (radioButtonMedium.isChecked()) {
            priority = 1;
        } else {
            priority = 2;
        }
        String text = editTextEnterNote.getText().toString().trim();
        if (text.isEmpty() || (!radioButtonLow.isChecked() && !radioButtonHigh.isChecked()
                && !radioButtonMedium.isChecked())) {
            Toast.makeText(AddNoteActivity.this, R.string.error_fields_empty, Toast.LENGTH_SHORT).show();
        } else {
            Note note = new Note(text, priority);
            addNoteViewModel.addNote(note);
        }
    }


    public static Intent newIntent(Context context) {
        return new Intent(context, AddNoteActivity.class);
    }
}