package com.soushetty.grocerylist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.soushetty.grocerylist.data.DatabaseHandler;
import com.soushetty.grocerylist.model.GroceryList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AlertDialog.Builder builder; //these variables used to send the popup.xml details as alert message screen
    private AlertDialog dialog;
    private Button save;
    private EditText itemName;
    private EditText itemQuantity;
    private EditText itemColor;
    private EditText itemSize;
    private EditText itemBrand;
    private DatabaseHandler databaseHandler;

// the application starts here and in onCreate function-it holds everything that's required in the first user view application page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //support all versions of android

        //databaseHandler=new DatabaseHandler.getInstance(this);//instantiating the database handler class
        databaseHandler=DatabaseHandler.getInstance(getApplicationContext());
        bypassactivity();

        List<GroceryList> lists=databaseHandler.getallitems();
        for (GroceryList groceryList:lists){
            Log.d("Main","item added: "+groceryList.getItemname() +" id: "+groceryList.getId());
        }


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //just like Toast ,we are using here snackbar
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                createpopup();
            }
        });
    }

    private void bypassactivity() {
        if(databaseHandler.getitemscount()>0){
            startActivity(new Intent(MainActivity.this,ListActivity.class));
            finish();
        }
    }


    //defining the method createpopup() inorder to get the data to display from popup.xml
    private void createpopup() {
        //instantiating the Alert dialog builder
        builder=new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.popup,null);//creating a view inflating all the data in popup.xml
        itemName=view.findViewById(R.id.item_name);
       // itemName.setSelection(0);
       /* itemName.setFocusable(true);
        itemName.requestFocus();
        itemName.setFocusableInTouchMode(true);
        InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(itemName,InputMethodManager.SHOW_IMPLICIT);*/
        /*_searchText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(_searchText, InputMethodManager.SHOW_IMPLICIT);*/
        itemQuantity=view.findViewById(R.id.item_quantity);
        itemColor=view.findViewById(R.id.item_color);
        itemSize=view.findViewById(R.id.item_size);
        itemBrand=view.findViewById(R.id.item_brand);

        builder.setView(view); //now view has all the entered data together and hence seting it to builder
        dialog=builder.create();//creating the Alert dialog here
        dialog.show(); //the important step to show the created view

        save=view.findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking that all the items details are filled
                if(itemName.getText().toString().isEmpty())
                {
                    Snackbar.make(v,"Name can't be left empty!!",Snackbar.LENGTH_SHORT).show();
                }
                else {
                    saveItem(v); //calling the method to save the entered data
                }
            }
        });

    }
    /* --to save each grocery item to the list
    --then move to next screen*/
    private void saveItem(View view) {

        GroceryList item=new GroceryList();
        //getting the data from the Ui
        String newitem=itemName.getText().toString().trim();
        String quantity=(itemQuantity.getText().toString().trim());
        String color=itemColor.getText().toString().trim();
        String size= (itemSize.getText().toString().trim());
        String brand=itemBrand.getText().toString().trim();
        //setting the data retrieved to class variables
        item.setItemname(newitem);
        item.setQuantity(quantity);
        item.setColor(color);
        item.setSize(size);
        item.setBrand(brand);

      /*  if(!quantity.isEmpty()){
        ;}

        if(!color.isEmpty()){
       }

        if(!size.isEmpty()){
       }
        if(!brand.isEmpty()){
        }*/

        databaseHandler.addItem(item); //passing it to data base
        Snackbar.make(view,"Item added to the list",Snackbar.LENGTH_SHORT).show();
        //Log.d("id_is",""+item.getId());

        /* to dismiss the popup screen and go to next Activity*/
        // we want to delay something and pass a runnable interface after it.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                //to move to next screen
                startActivity(new Intent(MainActivity.this,ListActivity.class));
                


            }
        },100); //1 sec=12000


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            return true;
        }else if (id==R.id.action_delete){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        //call close() of the helper class
        databaseHandler.close();
    }
}
