package com.cocosoft.ecart.orderHistory;
/**
 * Created by.dmin on 3/20/2017.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import com.cocosoft.ecart.R;
import com.cocosoft.ecart.cartmodule.BillingAdapter;
import com.cocosoft.ecart.cartmodule.CartItem;

import java.util.ArrayList;
import java.util.List;


public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.MyViewHolders> {

    private final List<OrderMaster> orderHistoryList;
    private Context context;
    private Activity activity;
    private ArrayList<CartItem> cartlist = new ArrayList<>();

    public class MyViewHolders extends RecyclerView.ViewHolder {

        public TextView transactionIdTxt, dateTxt, countTxt, totalTxt;
        public CardView cardView;

        public MyViewHolders(View view) {
            super(view);
            transactionIdTxt = (TextView) view.findViewById(R.id.transactionid_txt);
            dateTxt = (TextView) view.findViewById(R.id.date_txt);
            countTxt = (TextView) view.findViewById(R.id.count_txt);
            totalTxt = (TextView) view.findViewById(R.id.total_txt);
            cardView = (CardView) view.findViewById(R.id.card_view);
        }
    }

    public OrderHistoryAdapter(Activity a, Context c, List<OrderMaster> list) {
        this.orderHistoryList = list;
        this.context = c;
        this.activity = a;
    }

    @Override
    public MyViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_orderhistory_item, parent, false);
        return new MyViewHolders(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolders holder, final int position) {
        holder.transactionIdTxt.setText(orderHistoryList.get(position).getTransactionId());
        holder.countTxt.setText("" + orderHistoryList.get(position).getTotalItems());
        holder.totalTxt.setText("$ " + orderHistoryList.get(position).getTotalPrice());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<OrderList> olist = orderHistoryList.get(position).getOrderList();
                cartlist = null;
                cartlist = new ArrayList<CartItem>();
                for (int y = 0; y < olist.size(); y++) {
                    cartlist.add(new CartItem(olist.get(y).getProductId(), olist.get(y).getProductName(), "", olist.get(y).getProductPrice(), olist.get(y).getProductCount(), 0, false));
                }
                showDialog(cartlist);
            }
        });
        //holder.dateTxt.setText(""+orderHistoryList.get(position).getOrderDate());
    }

    private void showDialog(ArrayList<CartItem> listContent) {
        final Dialog dialog = new Dialog(context);
        View view = activity.getLayoutInflater().inflate(R.layout.dialoglayout, null);
        RecyclerView lv = (RecyclerView) view.findViewById(R.id.dialoglist);
        LinearLayoutManager lmanager = new LinearLayoutManager(context);
        BillingAdapter adapter = new BillingAdapter(context, listContent, 2);
        lv.setLayoutManager(lmanager);
        lv.setAdapter(adapter);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        window.setLayout(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return orderHistoryList.size();
    }
}
