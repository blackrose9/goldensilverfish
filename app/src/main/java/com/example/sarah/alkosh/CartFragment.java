package com.example.sarah.alkosh;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarah.alkosh.common.Common;
import com.example.sarah.alkosh.database.Database;
import com.example.sarah.alkosh.model.Order;
import com.example.sarah.alkosh.model.OrderRequest;
import com.example.sarah.alkosh.viewholder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sarah on 11/30/2017.
 */

public class CartFragment extends Fragment {
    private static final String TAG = "Cart";
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;


    DatabaseReference mDatabase;

    TextView txtTotalPrice;

    Button btnPlace;

    List<Order> cart=new ArrayList<>();

    CartAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Cart");

        initViews(view);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cart.size()>0)
                {
                    showAlertDialog();
                }
                else
                {
                    Toast.makeText(getActivity(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
                }

            }


        });

        loadListItem();

    }

    private void loadListItem() {
        cart=new Database(getContext()).getCarts();
        adapter=new CartAdapter(cart,getContext());

        recyclerView.setAdapter(adapter);
        //Calculate total price


        int total=0;
        for(Order order:cart)
        {
            Log.d(TAG, "Order Name :"+order.getItemName());

            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));


            txtTotalPrice.setText(String.valueOf(total));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("Delete")) {
            deleteCart(item.getOrder());
        }
        return true;
    }

    private void deleteCart(int position) {
        //Remove item at List<Order> by position
        cart.remove(position);
        //After that, we will delete all old data from SQLite
        new Database(getContext()).cleanCart();
        //Update data from List<Order> to SQLite
        for(Order item:cart)
        {
            new Database(getContext()).addToCart(item);
        }
        loadListItem();
    }



    private void showAlertDialog() {
        new LovelyTextInputDialog(getContext(), R.style.AppTheme)
                .setTopColorRes(R.color.colorAccent)
                .setTitle("Delivery Location")
                .setMessage("Where do you want it delivered")
                .setIcon(R.drawable.ic_action_location)
                .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {


                        OrderRequest request=new OrderRequest(
                                Common.userPhoneNo,
                                text,
                                txtTotalPrice.getText().toString(),
                                Common.userName,
                                cart);
                        //            Submit to Firebase Use System.currentMillis
                        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("OrderRequests");
                        ref.child(String.valueOf(System.currentTimeMillis())).setValue(request);
//                Delete Cart
                        new Database(getContext()).cleanCart();

                        Toast.makeText(getActivity(), "Thank you!, Order Placed", Toast.LENGTH_SHORT).show();


                    }
                })
                .show();


    }

    private void initViews(View view) {
        recyclerView=(RecyclerView)view.findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        txtTotalPrice=(TextView)view.findViewById(R.id.total);
        btnPlace=view.findViewById(R.id.btnPlaceOrder);


//        Firebase
        mDatabase= FirebaseDatabase.getInstance().getReference("OrderRequests");

//        Init
        recyclerView=(RecyclerView)view.findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

    }
}
