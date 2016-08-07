package com.lloydtucker.blueproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by lloydtucker on 06/08/2016.
 */
public class AccountAdapter extends ArrayAdapter<Accounts> {

    public AccountAdapter(Context context, Accounts[] a) {
        super(context, R.layout.item_customer, a);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.item_customer, parent, false);

        Accounts account = getItem(position);
        TextView accountTypeView = (TextView) customView.findViewById(R.id.account_type);
        TextView accountDetails = (TextView) customView.findViewById(R.id.account_details);
        TextView accountBalance = (TextView) customView.findViewById(R.id.account_balance);
        ImageView accountImage = (ImageView) customView.findViewById(R.id.account_image);

        String accountType = account.getAccountType();
        accountTypeView.setText(accountType);
        accountDetails.setText(account.getAccountNumber() + "   |   "
                + formatSortCode(account.getSortCode()));
        String currency = "";
        switch(account.getAccountCurrency()){
            case "GBP":
                currency = "£";
                break;
            case "EUR":
                currency = "€";
                break;
            case "CAD":
            case "USD":
                currency = "$";
                break;
            default:
                throw new IllegalArgumentException("Unsupported currency: "
                        + account.getAccountCurrency());
        }
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        accountBalance.setText(currency + formatBalance(account.getAccountBalance()));
        pickAccountImage(accountType, accountImage);
        return customView;
    }

    public static String formatSortCode(String s){
        String formatted = "";
        if(s.length() % 2 != 0){
            throw new IllegalArgumentException("Invalid Sort Code: "
                    + s);
        }
        for(int i = 0; i < s.length(); i+=2){
            formatted += s.charAt(i);
            formatted += s.charAt(i + 1);
            if(i+2 < s.length()){
                formatted += "-";
            }
        }
        return formatted;
    }

    public static String formatBalance(Double d){
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return formatter.format(d);
    }

    public static void pickAccountImage(String s, ImageView i){
        int drawableSource = 0;
        switch(s){
            case "Standard Current Account":
                drawableSource = R.drawable.pound_coin;
                break;
            case "90-day Savings Account":
                drawableSource = R.drawable.piggy_bank;
                break;
            default:
                throw new IllegalArgumentException("Unsupported account type: "
                        + s);
        }
        i.setImageResource(drawableSource);
    }
}
