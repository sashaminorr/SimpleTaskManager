package com.example.todomanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todomanager.adapters.MyRecyclerViewAdapter;
import com.example.todomanager.controllers.FileController;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    private FileController fileController;
    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private MyRecyclerViewAdapter adapter;
    private TextView title;
    private TextView noTasksTitle;


    private List<Map<String, String>> tasks;
    private List<Map<String, String>> tasksByDate;

    /*
    TODO
    find out what's the month getting problem (returns value lower by 1)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.taskList);
        calendarView = findViewById(R.id.calendarView);
        title = findViewById(R.id.title);
        noTasksTitle = findViewById(R.id.noTasksTitle);
        fileController = new FileController();

        try {
            tasks = fileController.getStorageContentAsList(getApplicationContext());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String selectedDate = sdf.format(new Date(calendarView.getDate()));
        title.setText(selectedDate);


        recyclerView.setHasFixedSize(true);

        // set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            tasksByDate = getTasksByDate(selectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        adapter = new MyRecyclerViewAdapter(this, tasksByDate);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        changeVisibilitiesBySize(tasksByDate.size());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int date) {
                month += 1;
                String fullDate = date + "/" + month + "/" + year;

                // clear old list
                tasksByDate.clear();
                // add new list
                List<Map<String, String>> temp = null;
                try {
                    temp = getTasksByDate(fullDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                changeVisibilitiesBySize(temp.size());
                tasksByDate.addAll(temp);
                // notify adapter
                adapter.notifyDataSetChanged();

                title.setText(fullDate);

            }
        });

    }

    private List<Map<String, String>> getTasksByDate(String date) throws ParseException {
        List<Map<String, String>> result = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).get("date").equals(date)) {
                result.add(tasks.get(i));
            }
        }
        result = sortByTime(result);
        return result;
    }

    private List<Map<String, String>> sortByTime(List<Map<String, String>> sortedByDate) throws ParseException {

        for (int i = 0; i < sortedByDate.size(); i++) {
            Map<String, String> min = sortedByDate.get(i);
            int minId = i;
            for (int j = i + 1; j < sortedByDate.size(); j++) {
                if (getDiff(sortedByDate.get(j).get("time"), min.get("time")) < 0) {
                    min = sortedByDate.get(j);
                    minId = j;
                }
            }
            // swapping
            Map<String, String> temp = sortedByDate.get(i);
            sortedByDate.set(i, min);
            sortedByDate.set(minId, temp);
        }
        return sortedByDate;
    }

    private long getDiff(String time1, String time2) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date d1 = sdf.parse(time1);
        Date d2 = sdf.parse(time2);
        return d1.getTime() - d2.getTime();

    }

    private void changeVisibilitiesBySize(int size) {
        if (size <= 0) {
            recyclerView.setVisibility(View.GONE);
            noTasksTitle.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noTasksTitle.setVisibility(View.GONE);
        }
    }

    public void saveUpdateActivityRedirect(View view) {
        Intent act = new Intent(this, SaveUpdateActivity.class);
        startActivity(act);

    }

    @Override
    public void onItemClick(View view, int position) {
//            Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        Intent act = new Intent(this, SaveUpdateActivity.class);
        act.putExtra("TASK_TITLE", adapter.getItem(position).get("title"));
        act.putExtra("TASK_DATE", adapter.getItem(position).get("date"));
        act.putExtra("TASK_TIME", adapter.getItem(position).get("time"));
        act.putExtra("TASK_DESCR", adapter.getItem(position).get("description"));
        startActivity(act);
    }

}