package com.example.sarah.alkosh.supplier;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.sarah.alkosh.EditImage.EditActivity;
import com.example.sarah.alkosh.HomeFragment;
import com.example.sarah.alkosh.R;
import com.example.sarah.alkosh.model.Item;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.io.ByteArrayOutputStream;

/**
 * Created by Sarah on 11/30/2017.
 */

public class SupplierItemsFragment extends Fragment {
    private static final String TAG = "SupplierItemsFragment";
    RecyclerView itemList;
    DatabaseReference mDatabase;

    ImageButton editact;
    ImageButton editacth;
    ImageButton editacts;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
//        return inflater.inflate(R.layout.fragment_supplier_items, container, false);

        View view = inflater.inflate(R.layout.fragment_supplier_items, container, false);

        editact = (ImageButton)view.findViewById(R.id.hoodiebtn);
        editacth = (ImageButton)view.findViewById(R.id.cupbtn);
        editacts = (ImageButton)view.findViewById(R.id.shirtbtn);
        editact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hoodie1);
                Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.cup);
                Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.shirt);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra("hoodie", byteArray);
                intent.putExtra("cup", byteArray);
                intent.putExtra("shirt", byteArray);
                startActivity(intent);

            }
        });


        return view;
    }
//
//    public void editact(View view){
//        Intent intent = new Intent(SupplierItemsFragment.this.getActivity(), EditActivity.class);
//        startActivity(intent);
//    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("My Items");

        String id= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference("Items");
        mDatabase.keepSynced(true);
        mDatabase.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue((Long) mutableData.getValue() + 1);
                }
                return Transaction.success(mutableData); //we can also abort by calling Transaction.abort()
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }

        });
        itemList=(RecyclerView)view.findViewById(R.id.itemList);
        itemList.setHasFixedSize(true);
        itemList.setLayoutManager(new GridLayoutManager(getActivity(),2));
    }

    @Override
    public void onStart() {
        super.onStart();
        String name=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        FirebaseRecyclerAdapter<Item,HomeFragment.ItemsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Item, HomeFragment.ItemsViewHolder>(
                Item.class,
                R.layout.single_item,
                HomeFragment.ItemsViewHolder.class,
                mDatabase.orderByChild("itemSupplier").equalTo(name)
        ) {
            @Override
            protected void populateViewHolder(HomeFragment.ItemsViewHolder viewHolder, Item model, int position) {
                viewHolder.setItemName(model.getItemName());
                viewHolder.setItemPrice(model.getItemPrice());
                viewHolder.setItemImage(getActivity(),model.getItemPhotoUrl());

                Log.d(TAG, "Photo Url"+model.getItemPhotoUrl());
            }
        };

        itemList.setAdapter(firebaseRecyclerAdapter);
    }
}
