package com.example.todomanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todomanager.controllers.FileController;
import com.example.todomanager.controllers.XmlController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;

import static android.view.View.VISIBLE;

/*
TODO
Auto select time and date pickers if isUpdate
 */
public class SaveUpdateActivity extends AppCompatActivity {
    private EditText time;
    private EditText date;
    private FloatingActionButton saveUpdateButton;
    private FloatingActionButton deleteButton;
    private EditText title;
    private EditText description;
    private TextView mainTitle;
    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;
    private FileController fileController;

    private String isUpdateTitle;
    private String isUpdateDesc;
    private String isUpdateTime;
    private String isUpdateDate;


    private boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_update);

        Bundle b = getIntent().getExtras();

        if (b != null) {
            isUpdate = true;
        }

        title = findViewById(R.id.taskTitle);
        mainTitle = findViewById(R.id.mainTitle);
        description = findViewById(R.id.taskDescription);
        time = findViewById(R.id.time);
        date = findViewById(R.id.date);
        fileController = new FileController();

        saveUpdateButton = findViewById(R.id.saveUpdateButton);
        deleteButton = findViewById(R.id.deleteButton);

        if (isUpdate) {
            deleteButton.setVisibility(VISIBLE);
            mainTitle.setText("Update task");
            title.setText(b.getString("TASK_TITLE"));
            description.setText(b.getString("TASK_DESCR"));
            date.setText(b.getString("TASK_DATE"));
            time.setText(b.getString("TASK_TIME"));
//            temp container for removing old one
            isUpdateTitle = b.getString("TASK_TITLE");
            isUpdateDesc = b.getString("TASK_DESCR");
            isUpdateDate = b.getString("TASK_DATE");
            isUpdateTime = b.getString("TASK_TIME");

        }

        time.setInputType(InputType.TYPE_NULL);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                timePickerDialog = new TimePickerDialog(SaveUpdateActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                time.setText(String.format("%02d:%02d", sHour, sMinute));
                            }
                        }, hour, minutes, true);
                timePickerDialog.show();
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                datePickerDialog = new DatePickerDialog(SaveUpdateActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        saveUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleText = title.getText().toString();
                String descText = description.getText().toString();
                String timeText = time.getText().toString();
                String dateText = date.getText().toString();
                if (!titleText.isEmpty() && !descText.isEmpty() && !timeText.isEmpty() && !dateText.isEmpty()) {
                    try {
                        String xmlString = XmlController.formTask(titleText, descText, dateText, timeText);
                        if (!xmlString.isEmpty()) {
                            File storage = fileController.getStorageFile(getApplicationContext());
                            if (isUpdate) {
                                if (fileController.addTaskToFile(getApplicationContext(), fileController.getStorageContent(getApplicationContext(), isUpdateTitle, isUpdateDesc, isUpdateDate, isUpdateTime), xmlString)) {
                                    Toast.makeText(getApplicationContext(), xmlString, Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(), "Task updated!", Toast.LENGTH_SHORT).show();

//                                redirect to main act
                                    Intent act = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(act);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed to update the task", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (fileController.addTaskToFile(getApplicationContext(), fileController.getStorageContent(getApplicationContext()), xmlString)) {
                                    Toast.makeText(getApplicationContext(), xmlString, Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(), "Task saved!", Toast.LENGTH_SHORT).show();

//                                redirect to main act
                                    Intent act = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(act);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed to save the task", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to save the task", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (SAXException | ParserConfigurationException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SaveUpdateActivity.this)
                        .setTitle("Delete a task").setMessage("Are you sure you want to delete current task?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String xmlString = "";
                                try {
                                    File storage = fileController.getStorageFile(getApplicationContext());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (fileController.addTaskToFile(getApplicationContext(), fileController.getStorageContent(getApplicationContext(), isUpdateTitle, isUpdateDesc, isUpdateDate, isUpdateTime), xmlString)) {
                                        Toast.makeText(getApplicationContext(), xmlString, Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getApplicationContext(), "Task deleted!", Toast.LENGTH_SHORT).show();

//                                redirect to main act
                                        Intent act = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(act);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed to delete the task", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (IOException | ParserConfigurationException | SAXException e) {
                                    e.printStackTrace();
                                }


                            }
                        }).setNegativeButton("No", null).show();
            }
        });

    }

}