//CASEY LORIA Homework 2
package com.dkg.casey.caseyloria_hw2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences savedData;
    ListView list;
    EditText searchText, tagText;
    Button saveBtn,clearButton,tagButton;

    private LinearLayout linearLayout;

    private static final String SAVED_INFO = "SearchInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        saveBtn = (Button)findViewById(R.id.button);
        clearButton= (Button)findViewById(R.id.button2);

        list = (ListView)findViewById(R.id.list);

        searchText = (EditText) findViewById(R.id.editText);
        tagText= (EditText) findViewById(R.id.editText2);

        linearLayout = findViewById(R.id.list_Linear_Layout);

        savedData = getSharedPreferences(SAVED_INFO, MODE_PRIVATE);

        refreshButton("",true);


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(searchText.getText()) && TextUtils.isEmpty(tagText.getText()))
                {
                    Log.d("Message:", "Please Enter A Search Term AND a Tag");
                    AlertDialog.Builder bld = new AlertDialog.Builder(MainActivity.this);
                    bld.setTitle("Missing Text");
                    bld.setMessage("Please enter a search term and tag.");
                    bld.setPositiveButton("OK", null);
                    AlertDialog missingDialog = bld.create();
                    missingDialog.show();
                }

                else if(TextUtils.isEmpty(searchText.getText()))
                {
                    Log.d("Message:", "Please Enter A Search Term");
                    AlertDialog.Builder bld = new AlertDialog.Builder(MainActivity.this);
                    bld.setTitle("Missing Text");
                    bld.setMessage("Please enter a search term.");
                    bld.setPositiveButton("OK", null);
                    AlertDialog missingDialog = bld.create();
                    missingDialog.show();
                }

                else if(TextUtils.isEmpty(tagText.getText()))
                {
                    Log.d("Message:", "Please Enter A Tag For Your Search");
                    AlertDialog.Builder bld = new AlertDialog.Builder(MainActivity.this);
                    bld.setTitle("Missing Text");
                    bld.setMessage("Please enter a tag.");
                    bld.setPositiveButton("OK", null);
                    AlertDialog missingDialog = bld.create();
                    missingDialog.show();
                }

                else
                {

                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.generated_tag, null);
                    boolean tagExists = savedData.contains(tagText.getText().toString());
                    SharedPreferences.Editor editor = savedData.edit();
                    editor.putString(tagText.getText().toString(),searchText.getText().toString());
                    editor.apply();
                    if (!tagExists) {
                        refreshButton(tagText.getText().toString(), false);
                    }
                    Button tagButton = (Button)rowView.findViewById(R.id.tag_button);

                    tagButton.setText(tagText.getText());

                    Button editButton = (Button)rowView.findViewById(R.id.edit_button);

                    //linearLayout.addView(rowView);

                    searchText.setText("");
                    tagText.setText("");
                    Log.d("Message:", "Success!");

                }


            }
        });


    }




    public void onClick(View v)
    {
       switch (v.getId())
       {
           case R.id.button:
               if(searchText.getText().length() > 0 && tagText.getText().length()>0) {
                   String name = searchText.getText().toString();
                   String tag = tagText.getText().toString();
                   boolean tagAlreadySaved = savedData.contains(tag);
                   SharedPreferences.Editor myEditor = savedData.edit();
                   myEditor.putString(tag, name);
                   myEditor.apply();
                   if (!tagAlreadySaved) {
                       refreshButton(tag, false);
                   }
                   searchText.setText("");
                   tagText.setText("");
               }
               else{
                   AlertDialog.Builder bld = new AlertDialog.Builder(MainActivity.this);
                   bld.setTitle("Missing Text");
                   bld.setMessage("Please enter your name and age.");
                   bld.setPositiveButton("OK", null);
                   AlertDialog missingDialog = bld.create();
                   missingDialog.show();
               }
               break;
       case R.id.tag_button:
            String tag = ((Button)v).getText().toString();
            String query = savedData.getString(tag,"");
            String url = "https://twitter.com/search?q=(%23" + query + ")&src=typed_query";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            break;

           case R.id.button2:
               AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
               // set title and message using string resources
               // The  set methods look up the actual values of the resources
               adb.setTitle("Are you sure?");
               adb.setMessage("This will delete all saved searches.");
               adb.setCancelable(true);
               // negative button does nothing other than dismiss the dialog
               adb.setNegativeButton("Cancel", null);
               // positive button will carry out the deletion
               adb.setPositiveButton("Erase", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       // get rid of the rows in the GUI
                       linearLayout.removeAllViews();
                       // clear the stored queries
                       SharedPreferences.Editor myEditor = savedData.edit();
                       myEditor.clear();
                       myEditor.apply();
                   }
               });
               // create the dialog
               AlertDialog confirmDialog = adb.create();
               // show the dialog
               // if the user selects to erase, the queries will be erased before the show
               //  method returns
               confirmDialog.show();
               break;

           case R.id.edit_button:
               Log.d("Action: ", "Edit Tag");

               Button tagButton = (Button)linearLayout.findViewById(R.id.tag_button);
               String tag1 = tagButton.getText().toString();
               // get the query from the saved searches
               String query1 = savedData.getString(tag1,"");
               tagText.setText(tag1);
               searchText.setText(query1);
               break;
       }
        Log.d("Action: ", "Searching Twitter.....");

    }

    private void refreshButton(String tag, boolean applytoAll) {
        // get the map of tags to queries
        Map<String, ?> queryMap = savedData.getAll();
        // get the keys from the map
        Set<String> tagSet = queryMap.keySet();
        // convert the set to an array
        String[] tags = tagSet.toArray(new String[0]);
        // sort the tags
        Arrays.sort(tags, String.CASE_INSENSITIVE_ORDER);
        // determine where the new tag should go
        int index = Arrays.binarySearch(tags, tag);

        if(applytoAll){
            for(int i = 0; i < tags.length; i++ ) {
                makeTagGUI(tags[i], i);
            }
        }
        else
            makeTagGUI(tag, index);
    }

    public void onDelete (View v)
    {
        linearLayout.removeView((View) v.getParent());
    }

    private void makeTagGUI(String tag, int index) {
        // create a new row by inflating the layout file
        LayoutInflater li =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = li.inflate(R.layout.generated_tag, null);

        // get the tag button
        Button tagBTN = (Button)row.findViewById(R.id.tag_button);
        //tagBTN.setText(tag + " " + index);
        tagBTN.setText(tag);


        // set the edit listener on the edit button
        Button editBTN = (Button)row.findViewById(R.id.edit_button);

        linearLayout.removeView(row);
        linearLayout.addView(row,index);


    }


}
