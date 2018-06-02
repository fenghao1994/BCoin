package com.fh.bcoin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fh.bcoin.R;
import com.fh.bcoin.model.CoinContractsModel;

import java.util.List;

public class SearchResultAdapter extends BaseAdapter {

    private List<CoinContractsModel> list;
    private Context mContext;
    private LayoutInflater inflater;

    public SearchResultAdapter(Context context, List<CoinContractsModel> list) {
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
            convertView = inflater.inflate(R.layout.item_search_result, null);
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.contract = convertView.findViewById(R.id.contract);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(list.get(position).getName());
        viewHolder.contract.setText(list.get(position).getContracts());
        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView contract;
    }
}
