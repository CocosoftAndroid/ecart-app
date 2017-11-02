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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.cocosoft.ecart.R;
import com.cocosoft.ecart.cartmodule.BillingAdapter;
import com.cocosoft.ecart.cartmodule.CartItem;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.MyViewHolders> {

    private final List<OrderMaster> orderHistoryList;
    private Context context;
    private Activity activity;
    private ArrayList<CartItem> cartlist = new ArrayList<>();
    double tax = 5;
    double tt;
    double ordertotal;
    double purcheseprice;

    public class MyViewHolders extends RecyclerView.ViewHolder {

        public TextView transactionIdTxt, dateTxt, countTxt, totalTxt, invoiceIdTxt;
        public CardView cardView;

        public MyViewHolders(View view) {
            super(view);
            transactionIdTxt = (TextView) view.findViewById(R.id.transactionid_txt);
            dateTxt = (TextView) view.findViewById(R.id.date_txt);
            countTxt = (TextView) view.findViewById(R.id.count_txt);
            totalTxt = (TextView) view.findViewById(R.id.total_txt);
            cardView = (CardView) view.findViewById(R.id.card_view);
            invoiceIdTxt = (TextView) view.findViewById(R.id.invoiceid_txt);
        }

        private String getDate(long timeStamp) {

            try {
                DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
                Date netDate = (new Date(timeStamp));
                return sdf.format(netDate);
            } catch (Exception ex) {
                return "sd";
            }
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
        holder.invoiceIdTxt.setText(orderHistoryList.get(position).getTransactionId().substring(11));
        holder.countTxt.setText("" + orderHistoryList.get(position).getTotalItems());

        double d = (double) orderHistoryList.get(position).getTotalPrice();
        tt = purcheseprice * (tax / 100.0);
        double dd = d + tt;
        holder.totalTxt.setText("$" + dd + "0");
        holder.dateTxt.setText(holder.getDate(orderHistoryList.get(position).getCreated()));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<OrderList> olist = orderHistoryList.get(position).getOrderList();
                cartlist.clear();
                cartlist = new ArrayList<CartItem>();
                double total = 0.0;
                for (int y = 0; y < olist.size(); y++) {
                    cartlist.add(new CartItem(olist.get(y).getProductId(), olist.get(y).getProductName(), "", olist.get(y).getProductPrice(), olist.get(y).getProductCount(), 0, false));
                    total = total + (olist.get(y).getProductPrice() * olist.get(y).getProductCount());
                }
                showDialog(cartlist, orderHistoryList.get(position).getTransactionId().substring(11), holder.getDate(orderHistoryList.get(position).getCreated()), orderHistoryList.get(position).getTotalItems(), (Double) total);
            }
        });
        //holder.dateTxt.setText(""+orderHistoryList.get(position).getOrderDate());
    }

    private void showDialog(ArrayList<CartItem> listContent, String invoiceId, String date, int items, double total) {
        final Dialog dialog = new Dialog(context);
        View view = activity.getLayoutInflater().inflate(R.layout.dialoglayout, null);
        RecyclerView lv = (RecyclerView) view.findViewById(R.id.dialoglist);
        LinearLayoutManager lmanager = new LinearLayoutManager(context);
        BillingAdapter adapter = new BillingAdapter(context, listContent, 2);


        TextView invoiceTxt = (TextView) view.findViewById(R.id.invoiceid_txt1);
        TextView idate = (TextView) view.findViewById(R.id.date_txt1);
        TextView subtotal = (TextView) view.findViewById(R.id.subt);
        TextView countt = (TextView) view.findViewById(R.id.countt);
        ImageView dialogcancel = (ImageView) view.findViewById(R.id.dialogcancel);
        TextView totalamt = (TextView) view.findViewById(R.id.totalamt);
        purcheseprice = total;

        DecimalFormat decim = new DecimalFormat("0.00");
        subtotal.setText("$" + decim.format(purcheseprice));

        countt.setText("(" + items + ")");
        invoiceTxt.setText(invoiceId);
        idate.setText(date);
//Tax
        TextView taxxx = (TextView) view.findViewById(R.id.tax);
        tt = ((purcheseprice * tax) / 100.0);
        ordertotal = purcheseprice + tt;
        taxxx.setText("$" + decim.format(tt));
        totalamt.setText("$" + decim.format(ordertotal));


        lv.setLayoutManager(lmanager);
        lv.setAdapter(adapter);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        window.setLayout(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        dialog.show();
//madhu changed
        dialogcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    @Override
    public int getItemCount() {
        return orderHistoryList.size();
    }
}
