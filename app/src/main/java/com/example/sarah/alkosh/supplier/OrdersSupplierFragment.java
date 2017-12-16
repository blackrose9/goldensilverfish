package com.example.sarah.alkosh.supplier;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * Created by Sarah on 11/30/2017.
 */

public class OrdersSupplierFragment extends Fragment {
    private static final String TAG = "SupplierOrderFragment";
    RecyclerView itemList;
    DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("My Orders");



        String id= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference("OrderRequests");
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
//        itemList.setLayoutManager(new GridLayoutManager(getActivity(),2));
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
