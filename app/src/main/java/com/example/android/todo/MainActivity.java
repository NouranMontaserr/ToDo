package com.example.android.todo;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.content.Intent;

import java.util.Date;

import android.icu.text.DateFormat;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.widget.Toast;

import com.example.android.todo.database.TODODBHelper;
import com.example.android.todo.database.todolist;
import com.example.android.todo.TODOCursorAdapter;

import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View;
import android.app.Activity;
import android.os.Handler;
import android.text.style.ClickableSpan;
import android.text.TextUtils;
import android.widget.SearchView;

public class MainActivity extends AppCompatActivity {

    private TODODBHelper tododbHelper = new TODODBHelper(MainActivity.this);
    private ListView display;
    private TextView t1;
    private TextView t2;
    private TextView t3;
    private EditText edActionSearch;
    Cursor cursor;
    TODOCursorAdapter todoCursorAdapter;
    private SearchView e;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        e =(SearchView) findViewById(R.id.action0search);
        display = (ListView) findViewById(R.id.listitem);
        t1 = (TextView) findViewById(R.id.txt);
        t2 = (TextView) findViewById(R.id.date);
        t3 = (TextView) findViewById(R.id.timee);
        View emptyView = findViewById(R.id.empty_view);
        display.setEmptyView(emptyView);
        displayinfo();


        display.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                String selection = todolist.todoEntry._ID + " = ?";
                String d = ((String) ("" + display.getItemIdAtPosition(position)));
                String[] selectionArgs = {d};
                SQLiteDatabase sqLiteDatabase = tododbHelper.getReadableDatabase();
                String[] projection = {todolist.todoEntry._ID, todolist.todoEntry.COLUMN_TODO_TEXT};
                Cursor cursor = sqLiteDatabase.query(todolist.todoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                cursor.moveToFirst();
                int tColumnIndex = cursor.getColumnIndex(todolist.todoEntry.COLUMN_TODO_TEXT);
                String tit = cursor.getString(tColumnIndex);
                final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                final AlertDialog.Builder alertt = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle(tit);
                String[] items = {"Edit", "Delete"};
                alert.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 1) {
                            alertt.setTitle("Delete").setMessage("Are you sure you want to Delete it ?");
                            alertt.setNegativeButton("No", null);
                            alertt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deltodo(position);
                                }
                            });
                            alertt.show();
                        }else if (which == 0)
                            edittodo(position);
                    }
                });
                alert.show();
                return true;
            }
        });
    }

    private void edittodo(int po) {
        final int p = po;
        final EditText editText = new EditText(this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Edit an existing todo").setIcon(R.drawable.t).setMessage("What do you want to do next?").setView(editText);
        dialog.setNegativeButton("Cancel", null);
        dialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String task = String.valueOf(editText.getText().toString().trim());
                SQLiteDatabase sqLiteDatabase = tododbHelper.getWritableDatabase();
                String selection = todolist.todoEntry._ID + " = ?";
                String d = ((String) ("" + display.getItemIdAtPosition(p)));
                String[] selectionArgs = {d};
                ContentValues cv = new ContentValues();
                cv.put(todolist.todoEntry.COLUMN_TODO_TEXT, task);
                sqLiteDatabase.update(todolist.todoEntry.TABLE_NAME, cv, selection, selectionArgs);
                displayinfo();
            }
        });
        dialog.create();
        dialog.show();
    }

    private void deltodo(int po) {
        String selection = todolist.todoEntry._ID + " = ?";
        String d = ((String) ("" + display.getItemIdAtPosition(po)));
        String[] selectionArgs = {d};
        SQLiteDatabase sqLiteDatabase = tododbHelper.getWritableDatabase();
        int iid = sqLiteDatabase.delete(todolist.todoEntry.TABLE_NAME, selection, selectionArgs);
        displayinfo();
    }

    private void displayinfo() {
        SQLiteDatabase sqLiteDatabase = tododbHelper.getReadableDatabase();
        String[] projection = {todolist.todoEntry._ID, todolist.todoEntry.COLUMN_TODO_TEXT, todolist.todoEntry.COLUMN_TODO_DAATE, todolist.todoEntry.COLUMN_TODO_TIIME};
        cursor = sqLiteDatabase.query(todolist.todoEntry.TABLE_NAME, projection, null, null, null, null, null);
        todoCursorAdapter = new TODOCursorAdapter(this, cursor);
        display.setAdapter(todoCursorAdapter);
    }

    public void inserttodo(String t) {
        SQLiteDatabase sqLiteDatabase = tododbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String[] datetime = currentDateTimeString.split(",");
        values.put(todolist.todoEntry.COLUMN_TODO_TEXT, t);
        String DatE = datetime[0] + "," + datetime[1];
        String TimE = datetime[2];
        values.put(todolist.todoEntry.COLUMN_TODO_DAATE, DatE);
        values.put(todolist.todoEntry.COLUMN_TODO_TIIME, TimE);
        long rowid = sqLiteDatabase.insert(todolist.todoEntry.TABLE_NAME, null, values);
//        if (rowid == -1) {
//            Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Done" + datetime[0] + "," + datetime[1] + "     " + datetime[2], Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

        public void searchh(String c) {
//            SQLiteDatabase sqLiteDatabase = tododbHelper.getReadableDatabase();
//            String[] projection = {todolist.todoEntry._ID, todolist.todoEntry.COLUMN_TODO_TEXT, todolist.todoEntry.COLUMN_TODO_DAATE, todolist.todoEntry.COLUMN_TODO_TIIME};
//            String selection = todolist.todoEntry.COLUMN_TODO_TEXT + "=?";
//            String[] selectionArgs = {cc};
//            Cursor cursor = sqLiteDatabase.query(todolist.todoEntry.TABLE_NAME, projection, null, null, null, null, null);
//            TODOCursorAdapter todoCursorAdapter = new TODOCursorAdapter(this, cursor);
//            display.setAdapter(todoCursorAdapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                final EditText editText = new EditText(this);
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Add a new todo").setIcon(R.drawable.t).setMessage("What do you want to do next?").setView(editText);
                dialog.setNegativeButton("Cancel", null);
                dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {// 	DialogInterface: the dialog that received the click,,which => the button that was clicked
                        String task = String.valueOf(editText.getText().toString().trim());
                        inserttodo(task);
                        displayinfo();
                    }
                });
                dialog.create();
                dialog.show();
                break;
            case R.id.action0search:
//                e.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                    @Override
//                    public boolean onQueryTextSubmit(String query) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onQueryTextChange(String newText) {
//                        searchh(newText);
//                        return false;
//                    }
//                });
//                e.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        //searchh(s);
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//
//                    }
//                });
              //  todoCursorAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);


    }
}
