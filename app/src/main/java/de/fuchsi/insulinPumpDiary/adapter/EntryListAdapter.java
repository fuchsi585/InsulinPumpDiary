package de.fuchsi.insulinPumpDiary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.List;

import de.fuchsi.insulinPumpDiary.R;
import de.fuchsi.insulinPumpDiary.database.Entry;
import de.fuchsi.insulinPumpDiary.listener.EntryListClickListener;
import de.fuchsi.insulinPumpDiary.listener.EntryListClickListener.OnEntryListClickListener;

public class EntryListAdapter extends RecyclerView.Adapter<EntryListAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private OnEntryListClickListener mClickListener;
    private List<Entry> mModel;

    public EntryListAdapter(Context context, List<Entry> model,
                            OnEntryListClickListener callback){
        mInflater = LayoutInflater.from(context);
        this.mModel = model;
        this.mClickListener = callback;
    }
    public void add(int position, Entry item) {
        mModel.add(position, item);
        notifyItemInserted(position);
    }
    public void update(Entry item){
        int position = mModel.indexOf(item);
        mModel.set(position, item);
        notifyItemChanged(position);
    }
    public void remove(Entry item) {
        int position = mModel.indexOf(item);
        mModel.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount(){
        return mModel.size();
    }

    public Entry getItem(int position) {
        return mModel.get(position);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Entry entry = mModel.get(position);

        holder.title.setText(entry.getName());
        holder.total.setText("24-Total: " + String.valueOf(entry.getBasalRateSum()) + " Units");
        if (entry.getActive()) holder.active.setVisibility(TextView.VISIBLE);
        else holder.active.setVisibility(TextView.INVISIBLE);

        holder.itemView.setOnClickListener(new EntryListClickListener(mClickListener,position));
    }
    @Override
    public EntryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.content_list_view, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    public void updateAdapter(List<Entry> list){
        mModel = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView total;
        TextView active;

        ViewHolder(View v){
            super(v);
            title = (TextView) v.findViewById(R.id.item_title);
            total = (TextView) v.findViewById(R.id.item_total);
            active= (TextView) v.findViewById(R.id.item_active);
        }
    }
}