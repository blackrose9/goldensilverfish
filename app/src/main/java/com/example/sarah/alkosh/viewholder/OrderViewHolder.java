package com.example.sarah.alkosh.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sarah.alkosh.R;

/**
 * Created by Sarah on 11/30/2017.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder{
    public TextView txtOrderId,txtOrderPhone,txtOrderAddress,txtOrderStatus;



    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderAddress=(TextView)itemView.findViewById(R.id.order_address);
        txtOrderId=(TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus=(TextView)itemView.findViewById(R.id.order_status);


    }
}
