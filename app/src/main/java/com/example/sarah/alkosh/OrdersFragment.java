package com.example.sarah.alkosh;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sarah.alkosh.common.Common;
import com.example.sarah.alkosh.model.OrderRequest;
import com.example.sarah.alkosh.viewholder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rey.material.widget.FloatingActionButton;

/**
 * Created by Sarah on 11/30/2017.
 */

public class OrdersFragment extends Fragment{
    RecyclerView mOrderList;
    DatabaseReference mDatabase;
    FirebaseRecyclerAdapter<OrderRequest,OrderViewHolder> firebaseRecyclerAdapter;

    android.support.design.widget.FloatingActionButton fabChat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_orders, container, false);
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        fabChat = (android.support.design.widget.FloatingActionButton) view.findViewById(R.id.fabChat);
        fabChat.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChatAct.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("My Orders");

        initViews(view);
        mOrderList.setHasFixedSize(true);
        mOrderList.setLayoutManager(new LinearLayoutManager(getContext()));


        mDatabase= FirebaseDatabase.getInstance().getReference("OrderRequests");

    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<OrderRequest, OrderViewHolder>(
                OrderRequest.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                mDatabase.orderByChild("phone").equalTo(Common.userPhoneNo)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, OrderRequest model, int position) {
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderStatus.setText(model.getStatus());
                viewHolder.txtOrderStatus.setTextColor(setColor(model.getStatus()));
                viewHolder.txtOrderId.setText(firebaseRecyclerAdapter.getRef(position).getKey());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };
        mOrderList.setAdapter(firebaseRecyclerAdapter);
    }

    private int setColor(String status) {
        if(status.equals("0"))
        {
            return android.R.color.holo_blue_dark;
        }
        else if(status.equals("1"))
        {
            return R.color.colorAccent;
        }
        else
        {
            return  android.R.color.holo_green_light;
        }
    }

    private String convertCodeToStatus(String status) {
        if(status.equals("0"))
        {
            return "Shipped";
        }
        else if(status.equals("1"))
        {
            return "On its way";
        }
        else
        {
            return  "Arrived";
        }
    }

    private void initViews(View view) {
        mOrderList=view.findViewById(R.id.listOrders);
    }
}
