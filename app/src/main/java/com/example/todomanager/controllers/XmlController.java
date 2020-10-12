package com.example.todomanager.controllers;

import androidx.appcompat.app.AppCompatActivity;

public class XmlController extends AppCompatActivity {
    public static final String ROOT_ELEMENT_START = "<tasks>";
    public static final String ROOT_ELEMENT_END = "</tasks>";

    public static String formTask(String title, String description, String date, String time) {
        return "<task><title>" + title + "</title><description>" + description + "</description><date>" + date + "</date><time>" + time + "</time></task>";
    }

}
