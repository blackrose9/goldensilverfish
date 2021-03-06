package com.example.sarah.alkosh.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.sarah.alkosh.model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sarah on 11/30/2017.
 */

public class Database extends SQLiteAssetHelper{
    private static final String DB_NAME= "item.db";
    private static final int DB_VER=1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public List<Order> getCarts()
    {
        SQLiteDatabase db=getReadableDatabase();
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();

        String[] sqlSelect={"ItemID","ItemName","Quantity","Price","Discount","Supplier"};
        String sqlTable="OrderDetail";
        qb.setTables(sqlTable);
        Cursor c=qb.query(db,sqlSelect,null,null,null,null,null);

        final List<Order> result=new ArrayList<>();
        while(c.moveToNext())
        {
            result.add(new Order(c.getString(c.getColumnIndex("ItemID")),
                    c.getString(c.getColumnIndex("ItemName")),
                    c.getString(c.getColumnIndex("Quantity")),
                    c.getString(c.getColumnIndex("Price")),
                    c.getString(c.getColumnIndex("Discount")),
                    c.getString(c.getColumnIndex("Supplier"))
            ));
        }
        return  result;
    }

    public void addToCart(Order order)
    {
        SQLiteDatabase db=getReadableDatabase();
        String query= String.format("INSERT INTO OrderDetail(ItemID,ItemName,Quantity,Price,Discount,Supplier)" +
                        " VALUES('%s','%s','%s','%s','%s','%s');",
                order.getItemID(),
                order.getItemName(),
                order.getPrice(),
                order.getQuantity(),
                order.getDiscount(),
                order.getSupplier()
        );

        db.execSQL(query);
    }
    public void cleanCart()
    {
        SQLiteDatabase db=getReadableDatabase();
        String query= String.format("DELETE FROM OrderDetail");
        db.execSQL(query);
    }
}
