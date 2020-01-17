package com.soushetty.grocerylist.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.soushetty.grocerylist.R;
import com.soushetty.grocerylist.data.DatabaseHandler;
import com.soushetty.grocerylist.model.GroceryList;

import java.text.MessageFormat;
import java.util.List;

/*override two main methods: one to inflate the view and its view holder, and another one to bind data to the view.
The good thing about RecyclerViewAdapter is that the first method is called only when we really need to create a new view*/
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<GroceryList> groceryLists;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;

    /* from the MainActivity context and Grocery list as array list is passed*/
    public RecyclerViewAdapter(Context context, List<GroceryList> groceryLists) {
        this.context = context;
        this.groceryLists=groceryLists;
    }
    /* Implement all the methods from the super class RecyclerView.Adapter*/
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*to show only the data that a particular user desired to see,create a data object*/
        //creating an object 'view' to inflate all the contacts information from the contacts_row_view.xml file
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row,parent,false);//converting the data entered into xml file into an object
        return new ViewHolder(view,context);    //passing the object created which contains the entered data
    }

    /* this method binds view with the actual data to display*/
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GroceryList item=groceryLists.get(position); //object item created
        holder.itemname.setText(MessageFormat.format("Item: {0}", item.getItemname()));
        holder.quantity.setText(MessageFormat.format("Quantity: {0}", String.valueOf(item.getQuantity())));
        holder.color.setText(MessageFormat.format("Color: {0}", item.getColor()));
        holder.size.setText(MessageFormat.format("Size: {0}", String.valueOf(item.getSize())));
        holder.brand.setText(MessageFormat.format("Brand: {0}", item.getBrand()));
        holder.date.setText(MessageFormat.format("Date: {0}", item.getDate_item_added()));

    }

    @Override
    public int getItemCount() {
        return groceryLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView itemname;
        public TextView quantity;
        public TextView color;
        public TextView size;
        public TextView brand;
        public TextView date;
        public Button editButton;
        public Button deleteButton;
        public  int id;

        public ViewHolder(@NonNull View itemView,Context conx) {
            super(itemView);
            context=conx;

            itemname=itemView.findViewById(R.id.name);
            quantity=itemView.findViewById(R.id.quantity);
            color=itemView.findViewById(R.id.color);
            size=itemView.findViewById(R.id.size);
            brand=itemView.findViewById(R.id.brand);
            date=itemView.findViewById(R.id.date);
            editButton=itemView.findViewById(R.id.edit_button);
            deleteButton=itemView.findViewById(R.id.delete_button);

            //calling onclick listener's method
            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
                int position;
                position = getAdapterPosition();  //to get the item where delete or insert has to be performed
                GroceryList items=groceryLists.get(position);

            switch (v.getId()){
                case R.id.edit_button:
                    edititem(items);
                    break;
                case R.id.delete_button: //when the user clicks delete image
                    deleteitem(items.getId()); //passing the id of the item to be deleted from database
                    break;
            }

        }

        private void deleteitem(final int id) {
            //poping up the confirmation page before deleting
            builder=new AlertDialog.Builder(context);
            inflater=LayoutInflater.from(context);
            View view=inflater.inflate(R.layout.confirmation_pop,null);

            Button yesbutton=view.findViewById(R.id.yes_button);
            Button nobutton=view.findViewById(R.id.no_button);

            builder.setView(view); //passing the object 'view' which has all the details from confirmation_pop.xml
            dialog=builder.create(); //creating the alert dialog on the screen
            dialog.show();

            //when user selects YES button -proceed to delete the item
            yesbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler db=new DatabaseHandler(context);
                    db.deleteitem(id);
                    groceryLists.remove(getAdapterPosition());        //to remove the correct object from the recycler view
                    notifyItemRemoved(getAdapterPosition());        // once removed ,we need to notify adapter that the card was removed -by using the helper method
                    dialog.dismiss(); //after deleting in both front end view and backend database we no more need the pop up screen
                }
            });

            //when user selects NO - just return to original screen and nothing needs to changed
            nobutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss(); //dismissing th confirmation popup
                }
            });

        }
        private void edititem(final GroceryList selected_item) {

            //final GroceryList selected_item=groceryLists.get(getAdapterPosition());
            builder=new AlertDialog.Builder(context);
            inflater=LayoutInflater.from(context);
            View view=inflater.inflate(R.layout.popup,null);
             Button save;
             final EditText itemName;
             final EditText itemQuantity;
             final EditText itemColor;
             final EditText itemSize;
             final EditText itemBrand;
             TextView title;

            itemName=view.findViewById(R.id.item_name);
            itemQuantity=view.findViewById(R.id.item_quantity);
            itemColor=view.findViewById(R.id.item_color);
            itemSize=view.findViewById(R.id.item_size);
            itemBrand=view.findViewById(R.id.item_brand);
            title=view.findViewById(R.id.title);
            save=view.findViewById(R.id.save_button);

            title.setText(R.string.edit_title);
            //to display already existing data for this Item to be edited
            itemName.setText(selected_item.getItemname());
            itemQuantity.setText(String.valueOf(selected_item.getQuantity()));
            itemColor.setText(selected_item.getColor());
            itemSize.setText(String.valueOf(selected_item.getSize()));
            itemBrand.setText(selected_item.getBrand());
            save.setText(R.string.saving_edit);

            builder.setView(view);
            dialog=builder.create();
            dialog.show();

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {      //saving the updated items values
                    DatabaseHandler db=new DatabaseHandler(context);
                    //getting the items from the updated User end screen
                    selected_item.setItemname(itemName.getText().toString());
                    selected_item.setQuantity(Integer.parseInt(itemQuantity.getText().toString()));
                    selected_item.setColor(itemColor.getText().toString());
                    selected_item.setSize(Integer.parseInt(itemSize.getText().toString()));
                    selected_item.setBrand(itemBrand.getText().toString());

                    if(itemName.getText().toString().isEmpty() || itemQuantity.getText().toString().isEmpty() ||
                            itemColor.getText().toString().isEmpty() || itemSize.getText().toString().isEmpty() || itemBrand.getText().toString().isEmpty())
                    {
                        Snackbar.make(v,"Fields can't be empty!!",Snackbar.LENGTH_SHORT).show();

                    }else{

                        db.updateitem(selected_item);
                        notifyItemChanged(getAdapterPosition(),selected_item); //calling the method/inner call which tells the adapter that items have been changed
                        //or else the changes will be reflected when user terminates that session and opens the app again
                    }

                    dialog.dismiss(); //to remove the pop up screen
                }

            });





        }
    }




}
