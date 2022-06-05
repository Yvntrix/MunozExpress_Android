package com.android.munozexpress;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    Context context;
    ArrayList<OrderList> orders;



    public OrderAdapter(Context context, ArrayList<OrderList> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders,parent,false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

        OrderList orderList = orders.get(position);
        if(orderList.getCompleted() == 1){
            holder.status.setText("  Order Completed");
            holder.status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_24,0,0,0);
        }
        if(orderList.getCancelled() == 1){
            holder.status.setText("  Order Cancelled");
            holder.status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_cancel_24,0,0,0);
        }
        if(orderList.getOngoing() == 1){
            holder.status.setText("  Delivering Order");
            holder.status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_sports_motorsports_24,0,0,0);
        }
        holder.serviceType.setText(orderList.getServiceType());
        holder.transactionId.setText(orderList.getTransactionId());
        holder.customerPhone.setText("+63"+orderList.getCustomerNumber());
        holder.customerName.setText(orderList.getCustomerName());
        holder.distance.setText(String.format( "%.2f Km",orderList.getDistance()));
        if(orderList.getDuration()>3600){
            holder.duration.setText(String.format( "%.0f Hour %.0f minutes",orderList.getDuration()/3600,(orderList.getDuration()% 3600) / 60));
        }else{
            holder.duration.setText(String.format( "%.0f minutes",(orderList.getDuration() % 3600) / 60));
        }

        holder.dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+63"+orderList.getCustomerNumber()));
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (orderList.getServiceType().equals("Pabili")) {
                    Intent intent = new Intent(context.getApplicationContext(), Pabili_details.class);
                    intent.putExtra("transId", orderList.getTransactionId());
                    context.startActivity(intent);
                }
                if (orderList.getServiceType().equals("Pahatid")) {
                    Intent intent = new Intent(context.getApplicationContext(), Pahatid_details.class);
                    intent.putExtra("transId", orderList.getTransactionId());
                    context.startActivity(intent);
                }
                if (orderList.getServiceType().equals("Pakuha")) {
                    Intent intent = new Intent(context.getApplicationContext(), Pakuha_details.class);
                    intent.putExtra("transId", orderList.getTransactionId());
                    context.startActivity(intent);
                }
                if (orderList.getServiceType().equals("Pasundo")) {
                    Intent intent = new Intent(context.getApplicationContext(), Pasundo_details.class);
                    intent.putExtra("transId", orderList.getTransactionId());
                    context.startActivity(intent);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{
        TextView serviceType , transactionId , customerName , customerPhone ,status,distance,duration ;
        ImageView dial;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            distance = itemView.findViewById(R.id.distance);
            duration = itemView.findViewById(R.id.duration);
            status = itemView.findViewById(R.id.status);
            serviceType = itemView.findViewById(R.id.serviceType);
            transactionId = itemView.findViewById(R.id.transactionId);
            customerName = itemView.findViewById(R.id.cName);
            customerPhone = itemView.findViewById(R.id.cNumber);
            dial = itemView.findViewById(R.id.dialPhone);

        }
    }



}
