package de.fuchsi.basal_rate_db.listener;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class EntryListClickListener implements RecyclerView.OnClickListener{
    private int position;
    private OnEntryListClickListener callback;

    public interface OnEntryListClickListener{
        void onEntryListClicked(View aView, int position);
    }

    public EntryListClickListener(OnEntryListClickListener callback, int pos) {
        position = pos;
        this.callback = callback;
    }

    @Override
    public void onClick(View v){
        callback.onEntryListClicked(v, position);
    }
}