package com.fh.bcoin.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fh.bcoin.R;
import com.fh.bcoin.extra.ExtraType;
import com.fh.bcoin.model.CoinContractsModel;
import com.fh.bcoin.model.TokenHolderModel;

import java.text.DecimalFormat;
import java.util.List;

public class TokenHolderAdapter extends BaseAdapter {

    private List<TokenHolderModel> list;
    private Context mContext;
    private LayoutInflater inflater;

    public TokenHolderAdapter(Context context, List<TokenHolderModel> list) {
        this.list = list;
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    public void clear() {
        list.clear();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_token_holder, null);
            viewHolder.rank = convertView.findViewById(R.id.rank);
            viewHolder.address = convertView.findViewById(R.id.address);
            viewHolder.quantity = convertView.findViewById(R.id.quantity);
            viewHolder.percentage = convertView.findViewById(R.id.percentage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.rank.setText(list.get(position).getRank());
        viewHolder.address.setText(list.get(position).getAddress());
        viewHolder.quantity.setText(dealWithQuantity(list.get(position).getQuantity()));
        viewHolder.percentage.setText(list.get(position).getPercentage());
        return convertView;
    }

    private String dealWithQuantity(String quantity) {
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(Double.parseDouble(quantity));
        }catch (Exception e) {
            Log.e(mContext.getClass().getSimpleName(), e.getMessage());
        }
        return quantity;
    }

    static class ViewHolder {
        TextView rank;
        TextView address;
        TextView quantity;
        TextView percentage;
    }
}
