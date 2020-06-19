package com.soushetty.grocerylist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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
    private LayoutInflater clear_inflater;
    private android.app.AlertDialog.Builder builder_clear;
    private android.app.AlertDialog dialog_clear;
    public CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.recyclerview);
        floatingActionButton = findViewById(R.id.fab_button);
        //assigning or defining the declared variables
        //databaseHandler=new DatabaseHandler(this);
        databaseHandler = DatabaseHandler.getInstance(getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        groceryLists = new ArrayList<>();

        //getting items from db
        groceryLists = databaseHandler.getallitems();
        for (GroceryList items : groceryLists) {
            Log.d("added", "" + items.getItemname());
        }

        recyclerViewAdapter = new RecyclerViewAdapter(this, groceryLists);
        recyclerView.setAdapter(recyclerViewAdapter);

        //viewHolder=new RecyclerViewAdapter.ViewHolder(recyclerView,this);
        recyclerViewAdapter.notifyDataSetChanged();//to let the adapter know by itself when ever data stored changes

        //viewHolder=new RecyclerViewAdapter.ViewHolder(groceryLists,this);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createpopdialog();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //respond to menu item selection

        switch (item.getItemId()) {
            case R.id.action_share:
                shareitems();
                return true;

            case R.id.action_delete:
                clearwhole();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    //to clear the whole list of items
    public void clearwhole() {
        if (groceryLists.size() <= 0) {
            Toast.makeText(ListActivity.this, "Empty List!", Toast.LENGTH_SHORT).show();
        } else {

            //poping up the confirmation page before deleting
            builder_clear = new android.app.AlertDialog.Builder(this);
            clear_inflater = LayoutInflater.from(this);
            View view = clear_inflater.inflate(R.layout.clearall_confirmation_pop, null);

            Button proceed = view.findViewById(R.id.proceed_button);
            Button cancel = view.findViewById(R.id.cancel_button);

            builder_clear.setView(view); //passing the object 'view' which has all the details from confirmation_pop.xml
            dialog_clear = builder_clear.create(); //creating the alert dialog on the screen
            dialog_clear.show();


            proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatabaseHandler db = DatabaseHandler.getInstance(v.getContext());
                    db.clearitems();
                    groceryLists.clear();
                    recyclerViewAdapter.notifyDataSetChanged();
                    dialog_clear.dismiss();

                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_clear.dismiss();

                }
            });

        }


    }


    //when the user clicks SHare button in the menubar, the list of items with its details must be shared

    private void shareitems() {
        StringBuffer sb = new StringBuffer();
        // CheckBox checkBox = (CheckBox)View;
        checkBox = findViewById(R.id.checkbox);
        if (recyclerViewAdapter.checkeditems.size() <= 0) {
            Toast.makeText(ListActivity.this, "Please select the items!", Toast.LENGTH_SHORT).show();
        } else {
            int i = 1;

            sb.append("Grocery Items to buy:");
            for (GroceryList checked : recyclerViewAdapter.checkeditems) {
                sb.append("\n");
                sb.append(i + ")Item: " + checked.getItemname());
                sb.append("\n");
                if (!checked.getQuantity().isEmpty()) {
                    sb.append("Quantity: " + checked.getQuantity());
                    sb.append("\n");
                }
                if (!checked.getColor().isEmpty()) {
                    sb.append("Color/flavor: " + checked.getColor());
                    sb.append("\n");
                }

                if (!checked.getSize().isEmpty()) {
                    sb.append("Size: " + checked.getSize());
                    sb.append("\n");
                }

                if (!checked.getBrand().isEmpty()) {
                    sb.append("brand: " + checked.getBrand());
                    sb.append("\n");
                }
                i++;
                // to uncheck the boxes after sending and notify the adapter about changed view data
               // checkBox.setChecked(false);
                //recyclerViewAdapter.notifyDataSetChanged();
                //recyclerViewAdapter.notifyItemChanged(checked.getId());

            }

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Items to Buy ");
            intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            startActivity(intent);

            //recyclerViewAdapter.notifyDataSetChanged();

           recyclerViewAdapter.checkeditems.clear(); // removing all the checked/selected items stored in the List

        }

    }


    //to create the same popup screen. on popup.xml  values are entered by user and we need to convert into holdable view objects
    private void createpopdialog() {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup, null);//creating a view inflating all the data in popup.xml
        itemName = view.findViewById(R.id.item_name);
        itemName.setSelection(0);
        itemQuantity = view.findViewById(R.id.item_quantity);
        itemColor = view.findViewById(R.id.item_color);
        itemSize = view.findViewById(R.id.item_size);
        itemBrand = view.findViewById(R.id.item_brand);

        builder.setView(view); //now view has all the entered data together and hence seting it to builder
        dialog = builder.create();//creating the Alert dialog here
        dialog.show(); //the important step to show the created view
        save = view.findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking that all the items details are filled
                if (itemName.getText().toString().isEmpty()) {
                    Snackbar.make(v, "Item name can't be empty!!", Snackbar.LENGTH_SHORT).show();
                } else {
                    saveItem(v); //calling the method to save the entered data
                }
            }
        });
    }

    private void saveItem(View v) {
        GroceryList item = new GroceryList();
        //getting the data from the Ui
        String newitem = itemName.getText().toString().trim();
        String quantity = (itemQuantity.getText().toString().trim());
        String color = itemColor.getText().toString().trim();
        String size = (itemSize.getText().toString().trim());
        String brand = itemBrand.getText().toString().trim();
        //setting the data retrieved to class variables
        item.setItemname(newitem);
        item.setQuantity(quantity);
        item.setColor(color);
        item.setSize(size);
        item.setBrand(brand);

        databaseHandler.addItem(item); //passing it to data base
        Snackbar.make(v, "Item added to the list", Snackbar.LENGTH_SHORT).show();

        /* to dismiss the popup screen and go to next Activity*/
        // we want to delay something and pass a runnable interface after it.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                //to move to next screen
                startActivity(new Intent(ListActivity.this, ListActivity.class));

                finish();//killing the previous activity

            }
        }, 100); //1 sec


    }

}



