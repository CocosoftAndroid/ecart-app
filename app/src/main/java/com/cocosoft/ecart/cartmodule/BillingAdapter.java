package com.cocosoft.ecart.cartmodule;
/**
 * Created by.dmin on 3/20/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.cocosoft.ecart.R;
import java.util.ArrayList;

public class BillingAdapter extends RecyclerView.Adapter<BillingAdapter.MyViewHolders> {

    private  int flag=1;
    private ArrayList<CartItem> productList;
    private Context context;
    public class MyViewHolders extends RecyclerView.ViewHolder {
        public TextView productName, productPrice, count;
        public MyViewHolders(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.name);
            productPrice = (TextView) view.findViewById(R.id.price);
            count = (TextView) view.findViewById(R.id.quantity);
        }
    }

    public BillingAdapter(Context c, ArrayList<CartItem> list) {
        this.productList = list;
        this.context = c;
    }


    public BillingAdapter(Context c, ArrayList<CartItem> list,int flag) {
        this.productList = list;
        this.context = c;
        this.flag=flag;
    }
    @Override
    public MyViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        if(flag==1) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_billing_item, parent, false);
            return new MyViewHolders(itemView);
        }
        else
        { View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_billing_item2, parent, false);
            return new MyViewHolders(itemView);

        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolders holder, final int position) {
        holder.productName.setText(productList.get(position).getProductId() + " - " + productList.get(position).getProductName());
        holder.count.setText("" + productList.get(position).getCount());
        holder.productPrice.setText("$ " + productList.get(position).getProductPrice());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}