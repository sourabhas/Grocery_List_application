package com.soushetty.grocerylist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.soushetty.grocerylist.adapter.RecyclerViewAdapter;
import com.soushetty.grocerylist.data.DatabaseHandler;
import com.soushetty.grocerylist.model.GroceryList;

import java.util.ArrayList;
import java.util.List;

/* activity_list.xml & this java class together make - Recycler view*/
public class ListActivity extends AppCompatActivity {
    //declaring the required class variables
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<GroceryList> groceryLists;
    private DatabaseHandler databaseHandler;
    private FloatingActionButton floatingActionButton; //to get the entering items pop screen on viewing all items page too
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private EditText itemName;
    private EditText itemQuantity;
    private EditText itemColor;
    private EditText itemSize;
    private EditText itemBrand;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView=findViewById(R.id.recyclerview);
        floatingActionButton=findViewById(R.id.fab_button);
        //assigning or defining the declared variables
        databaseHandler=new DatabaseHandler(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        groceryLists=new ArrayList<>();

        //getting items from db
        groceryLists=databaseHandler.getallitems();
        for (GroceryList items:groceryLists){
            Log.d("added",""+items.getItemname());
        }

        recyclerViewAdapter=new RecyclerViewAdapter(this,groceryLists);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.notifyDataSetChanged();//to let the adapter know by itself when ever data stored changes
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createpopdialog();
            }
        });
    }
    //to create the same popup screen. on popup.xml  values are entered by user and we need to convert into holadble view objects
    private void createpopdialog() {
        builder=new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.popup,null);//creating a view inflating all the data in popup.xml
        itemName=view.findViewById(R.id.item_name);
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
                if(itemName.getText().toString().isEmpty()
                        || itemQuantity.getText().toString().isEmpty()
                        || itemColor.getText().toString().isEmpty() ||
                        itemSize.getText().toString().isEmpty() ||
                        itemBrand.getText().toString().isEmpty()){
                    Snackbar.make(v,"Fields can't be empty!!",Snackbar.LENGTH_SHORT).show();
                }
                else {
                    saveItem(v); //calling the method to save the entered data
                }
            }


    });
    }

    private void saveItem(View v) {
        GroceryList item=new GroceryList();
        //getting the data from the Ui
        String newitem=itemName.getText().toString().trim();
        int quantity= Integer.parseInt(itemQuantity.getText().toString().trim());
        String color=itemColor.getText().toString().trim();
        int size= Integer.parseInt(itemSize.getText().toString().trim());
        String brand=itemBrand.getText().toString().trim();
        //setting the data retrieved to class variables
        item.setItemname(newitem);
        item.setQuantity(quantity);
        item.setColor(color);
        item.setSize(size);
        item.setBrand(brand);

        databaseHandler.addItem(item); //passing it to data base
        Snackbar.make(v,"Item added to the list",Snackbar.LENGTH_SHORT).show();

        /* to dismiss the popup screen and go to next Activity*/
        // we want to delay something and pass a runnable interface after it.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                //to move to next screen
                startActivity(new Intent(ListActivity.this,ListActivity.class));

                finish();//killing the previous activity

            }
        },1200); //1 sec


    }



    }

