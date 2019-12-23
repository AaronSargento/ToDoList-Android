package com.example.sargento.finalproject;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sargento.finalproject.db.TaskContract;
import com.example.sargento.finalproject.db.TaskDBHelper;

import java.util.ArrayList;

// Input: The user can tap on the plus button to add an item to the list. The user can tap on the DONE button beside a list item to remove an item from the list.
// Output: The list will be refreshed to reflect an item added or removed.

public class MainActivity extends AppCompatActivity {

    //Declare objects
    private static final String TAG = "MainActivity"; //this will be used for logging
    private TaskDBHelper dbHelper; //this will be used to write/read from the database
    private ListView listView; //this will the UI for the ToDolist
    private ArrayAdapter<String> arrayAdapterToDo; //populate the ListView with data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the database helper at each initial load of the application
        dbHelper = new TaskDBHelper(this);

        //initialize the list with the information from the database
        listView = (ListView)findViewById(R.id.listView);
        updateUI();
    }

    /*
        This function will place the add button (from main_menu.xml) in the menu bar
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
        This function will allow the user to add an item to the list when the add button is pressed
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionAddTask:
                //set up an Alert Dialog box, so the user can add a list item
                final EditText taskEditText = new EditText(this);

                //create a builder for an alert dialog that uses the default alert dialog theme
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new task")
                        .setMessage("What do you want to do next?")

                        //incorporated an EditText into the Alert Dialogue
                        .setView(taskEditText)

                        //Provide a cancel button
                        .setNegativeButton("Cancel", null)

                        //Provide an add button
                        .setPositiveButton("Add", new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //place text from EditText box into database and update UI of ListView
                                String task = String.valueOf(taskEditText.getText());

                                //a read/write database object will be returned
                                SQLiteDatabase db = dbHelper.getWritableDatabase();

                                //used to store a set of values
                                ContentValues values = new ContentValues();

                                //add the task to the set
                                values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);

                                //general method for inserting a row into the database
                                db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                db.close(); //close the database object
                                updateUI(); //update the look of the ListView
                            }
                        })
                        .create(); //create an Alert Dialog with the arguments supplied to the builder
                dialog.show(); //this will show the Alert Dialogue over the regular view
                return true;
            default:
                return super.onOptionsItemSelected(item); //user can also exit out of the Alert Dialogue if he/she taps anywhere outside the Alert Dialogue box
        }
    }

    /*
        This function will update the look of the ListView when app is loaded or when an item is added/deleted.
    */
    private void updateUI() {
        ArrayList<String> taskList = new ArrayList<>();

        //a read-only database object will be returned
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //query the table, returning a cursor over the set with all the columns
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE},
                null, null, null, null, null);

        //Place the items in an ArrayList of Strings
        while (cursor.moveToNext()) {
            int columnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(columnIndex));
        }

        //If the adapter is not created or null, then set it as the adapter of the ListView
        if (arrayAdapterToDo == null) {
            arrayAdapterToDo = new ArrayAdapter<String>(this,
                    R.layout.item_todo, //what view to user for the items
                    R.id.taskTitle, //where to put the String of data
                    taskList); //where to get all the data
            listView.setAdapter(arrayAdapterToDo); //set it as the adapter of the ListView Instance
        } else {
            arrayAdapterToDo.clear(); //remove all the elements from the list
            arrayAdapterToDo.addAll(taskList); //add all the items from the database, came from the while loop populating taskList
            arrayAdapterToDo.notifyDataSetChanged(); //alert the ListView that the data changed and it should refresh itself
        }
        cursor.close(); //close the cursor object
        db.close(); //close the database object
    }

    /*
        This function subclasses the onClick() function.
        When the user taps the DONE button beside each list item (from item_todo.xml), the item will be removed from database and the list will be updated
    */
    public void deleteTask(View v) {
        //grab the parent view of the DONE button
        View parent = (View)v.getParent();

        //grab the TextView from item_todo.xml
        TextView taskTextView = (TextView)parent.findViewById(R.id.taskTitle);
        String task = String.valueOf(taskTextView.getText());

        //a read/write database object will be returned
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //delete the entry in the database that matches the selected task
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry.COL_TASK_TITLE + " =?",
                new String[]{task});
        db.close(); //close the database object
        updateUI(); //update the look of the ListView
    }
}
