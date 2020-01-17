package com.soushetty.grocerylist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.util.UniversalTimeScale;

import androidx.annotation.Nullable;

import com.soushetty.grocerylist.R;
import com.soushetty.grocerylist.model.GroceryList;
import com.soushetty.grocerylist.util.Util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private final Context context;
    public DatabaseHandler(Context context) {
        super(context, Util.DATABASE_NAME,null,Util.DATABASE_VERSION);
        this.context=context;
    }
//method to create the sql database table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE="CREATE TABLE " +Util.TABLE_NAME+ "( "+
                Util.KEY_ID + " INTEGER PRIMARY KEY," +
                Util.KEY_NAME + " TEXT," +
                Util.KEY_QUANTITY + " INTEGER," +
                Util.KEY_COLOR + " TEXT," +
                Util.KEY_SIZE + " INTEGER," +
                Util.KEY_BRAND + " TEXT," +
                Util.KEY_DATE + " LONG" + " ) ";
            db.execSQL(CREATE_TABLE);
    }
//sqlite query to drop an existing table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE=String.valueOf(R.string.droptable); //query to drop table is added as a string resource
        db.execSQL(DROP_TABLE,new String[]{Util.DATABASE_NAME});
        //create table again,after drop of the older one
        onCreate(db);
    }

    /* CRUD OPERATIONS : add item,get an item,update an item,delete an item*/
    /* Inserting an item(row) to already existing database table*/
    public void addItem(GroceryList groceryList){

        SQLiteDatabase db=this.getWritableDatabase();//getting permisssion to add data to already existing table

        ContentValues values=new ContentValues();  //to store the values to be added to database table
        values.put(Util.KEY_NAME,groceryList.getItemname());
        values.put(Util.KEY_QUANTITY,groceryList.getQuantity());
        values.put(Util.KEY_COLOR,groceryList.getColor());
        values.put(Util.KEY_SIZE,groceryList.getSize());
        values.put(Util.KEY_BRAND,groceryList.getBrand());
        values.put(Util.KEY_DATE,java.lang.System.currentTimeMillis());
        //insert the row
        db.insert(Util.TABLE_NAME,null,values); //insert query where we pass the table name where data should be added ans the contentvalues variable
        db.close(); //should always close the table after it's opened to make some changes
    }

    /*Retrieving a row from the database table*/
    public GroceryList getitem(int id){

        SQLiteDatabase db=this.getReadableDatabase();//calling this inbuilt method as we want to read already existing database and retrieve a row

        Cursor cursor=db.query(Util.TABLE_NAME,
                new String[]{Util.KEY_ID ,Util.KEY_NAME,Util.KEY_QUANTITY,Util.KEY_COLOR,Util.KEY_SIZE,Util.KEY_BRAND,Util.KEY_DATE},
                Util.KEY_ID+"=?",
                new String[]{String.valueOf(id)},
                null,null,null);

        if(cursor!=null) {      //validating whether that row exists or not
            cursor.moveToFirst();
        }

        //adding all items that a cursor parses through to a variable of table
        GroceryList groceryList=new GroceryList();
        groceryList.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Util.KEY_ID))));
        groceryList.setItemname(cursor.getString(cursor.getColumnIndex(Util.KEY_NAME)));
        groceryList.setQuantity(Integer.parseInt((cursor.getString(cursor.getColumnIndex(Util.KEY_QUANTITY)))));
        groceryList.setColor(cursor.getString(cursor.getColumnIndex(Util.KEY_COLOR)));
        groceryList.setSize(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Util.KEY_SIZE))));
        groceryList.setBrand(cursor.getString(cursor.getColumnIndex(Util.KEY_BRAND)));

        //converting TIMESTAMP to something readable
        DateFormat dateFormat=DateFormat.getDateInstance();
        String formateddate=dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Util.KEY_DATE))).getTime());
        groceryList.setDate_item_added(formateddate);
        return groceryList;
    }
    /*method to get all the items stored*/
    public List<GroceryList> getallitems(){
        List<GroceryList> groceryList=new ArrayList<>(); //using a list to store the retrieved data
        SQLiteDatabase db=this.getReadableDatabase(); //to parse through/read the stored values

        String select_query="SELECT * FROM " + Util.TABLE_NAME +
                " ORDER BY "+Util.KEY_DATE + " DESC";

        Cursor cursor=db.rawQuery(select_query,null);
        if (cursor.moveToFirst()){
            do {
                GroceryList gl=new GroceryList();
                gl.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Util.KEY_ID))));
                gl.setItemname(cursor.getString(cursor.getColumnIndex(Util.KEY_NAME)));
                gl.setQuantity(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Util.KEY_QUANTITY))));
                gl.setColor(cursor.getString(cursor.getColumnIndex(Util.KEY_COLOR)));
                gl.setSize(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Util.KEY_SIZE))));
                gl.setBrand(cursor.getString(cursor.getColumnIndex(Util.KEY_BRAND)));

                DateFormat dateFormat=DateFormat.getDateInstance();
                String formateddate=dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Util.KEY_DATE))).getTime());
                gl.setDate_item_added(formateddate);

                //Add to array List
                groceryList.add(gl);
            }while (cursor.moveToNext());
        }
        return groceryList;
    }
    /*method to update an already existing item*/
    public int updateitem(GroceryList groceryList){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();//since we are going to write into database
        values.put(Util.KEY_ID,groceryList.getId());
        values.put(Util.KEY_NAME,groceryList.getItemname());
        values.put(Util.KEY_QUANTITY,groceryList.getQuantity());
        values.put(Util.KEY_COLOR,groceryList.getColor());
        values.put(Util.KEY_SIZE,groceryList.getSize());
        values.put(Util.KEY_BRAND,groceryList.getBrand());
        values.put(Util.KEY_DATE,java.lang.System.currentTimeMillis());
        //update the row
        return db.update(Util.TABLE_NAME,values,Util.KEY_ID + " =?",new String[]{String.valueOf(groceryList.getId())});
    }

    /*method to delete an item */
    public void deleteitem(int id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(Util.TABLE_NAME,Util.KEY_ID+" =?",new String[]{String.valueOf(id)});
        db.close();
    }

    //method to return the count of items in the table
    public int getitemscount(){

        SQLiteDatabase db=this.getReadableDatabase();
        String query=" SELECT * FROM "+Util.TABLE_NAME;
        Cursor cursor=db.rawQuery(query,null);
        return cursor.getCount();

    }

}
