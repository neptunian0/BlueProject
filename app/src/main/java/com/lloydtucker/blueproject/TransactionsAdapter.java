package com.lloydtucker.blueproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by lloydtucker on 07/08/2016.
 */
public class TransactionsAdapter extends ArrayAdapter<Transactions> {

    public TransactionsAdapter(Context context, Transactions[] t) {
        super(context, R.layout.item_transaction, t);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.item_transaction, parent, false);

        Transactions transaction = getItem(position);
        TextView transactionDateDescription = (TextView) customView.findViewById(R.id.transactionDateDesc);
        TextView transactionAmount = (TextView) customView.findViewById(R.id.transactionAmount);

        transactionDateDescription.setText(transaction.getTransactionDateTime()
                + " " + transaction.getTransactionDescription());
        transactionAmount.setText(formatTransactionAmount(transaction.getTransactionAmount()));
        return customView;
    }

    public static String formatTransactionAmount(Double d){
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return formatter.format(d);
    }
}
