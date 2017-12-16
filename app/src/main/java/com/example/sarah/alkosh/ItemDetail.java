package com.example.sarah.alkosh;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.sarah.alkosh.database.Database;
import com.example.sarah.alkosh.model.Item;
import com.example.sarah.alkosh.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by Sarah on 11/30/2017.
 */

public class ItemDetail  extends AppCompatActivity{
    private static final String TAG = "ItemDetail";
    TextView item_name,item_price,item_description;
    ImageView item_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    DatabaseReference mDatabase;
    private boolean clicked=false;
    private String itemID="";
    Item currentItem;
    private String key;
    private String id="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        initViews();
        mDatabase= FirebaseDatabase.getInstance().getReference("Items");

        if(getIntent().getExtras()!=null)
        {
            Bundle bundle=getIntent().getExtras();
            final String name=bundle.get("name").toString();
            final String price=bundle.get("price").toString();
            final String img=bundle.get("image").toString();
            id=bundle.get("ItemID").toString();

            mDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    currentItem=dataSnapshot.getValue(Item.class);
                    item_name.setText(currentItem.getItemName());
                    item_price.setText("KES "+currentItem.getItemPrice());
                    item_description.setText(currentItem.getItemDescription());
                    Picasso
                            .with(getBaseContext())
                            .load(currentItem.getItemPhotoUrl())
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .into(item_image, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(ItemDetail.this).load(currentItem.getItemPhotoUrl()).into(item_image);
                                }
                            });


                    collapsingToolbarLayout.setTitle(currentItem.getItemName());

                }



                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




            btnCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!clicked)
                    {
                        new Database(getBaseContext()).addToCart(new Order(
                                id,
                                currentItem.getItemName(),
                                numberButton.getNumber(),
                                currentItem.getItemPrice(),
                                currentItem.getItemDiscount(),
                                currentItem.getItemSupplier()
                        ));
                        Toast.makeText(ItemDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ItemDetail.this, "You have already added", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    private void initViews() {
        collapsingToolbarLayout=findViewById(R.id.collapsing);
        numberButton=(ElegantNumberButton)findViewById(R.id.number_button);
        btnCart=(FloatingActionButton)findViewById(R.id.btnCart);
        item_description=(TextView)findViewById(R.id.itemDescription);
        item_name=(TextView)findViewById(R.id.item_name);
        item_price=(TextView)findViewById(R.id.item_price);
        item_image=(ImageView)findViewById(R.id.img_item);

    }
}
