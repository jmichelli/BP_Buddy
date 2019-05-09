package com.example.BPBuddy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecycleListAdapter extends RecyclerView.Adapter<RecycleListAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener{

    private ArrayList<String> timestamps = new ArrayList<>();
    private ArrayList<String> pumpYields = new ArrayList<>();
    private ArrayList<String> freezerAdds = new ArrayList<>();
    private Context listContext;
    private OnEntryListener mOnEntryListener;

    public RecycleListAdapter(ArrayList<String> timestamps, ArrayList<String> pumpYields, ArrayList<String> freezerAdds, Context listContext, OnEntryListener onEntryListener) {
        this.listContext = listContext;
        this.timestamps = timestamps;
        this.pumpYields = pumpYields;
        this.freezerAdds = freezerAdds;
        this.mOnEntryListener = onEntryListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem,viewGroup, false);

        ViewHolder holder = new ViewHolder(view, mOnEntryListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.ts.setText(timestamps.get(i));
        viewHolder.ozPm.setText(pumpYields.get(i));
        viewHolder.ozSt.setText(freezerAdds.get(i));
    }

    @Override
    public int getItemCount() {
        return timestamps.size();
    }

    @Override
    public void onClick(View v) {
        
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView ts;
        TextView ozPm;
        TextView ozSt;
        LinearLayout container;

        OnEntryListener onEntryListener;

        public ViewHolder(@NonNull View itemView, OnEntryListener onEntryListener) {
            super(itemView);
            ts = itemView.findViewById(R.id.pumped_timestamp);
            ozPm = itemView.findViewById(R.id.pumped_list_val);
            ozSt = itemView.findViewById(R.id.stored_list_val);
            this.onEntryListener = onEntryListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onEntryListener.OnEntryClick(getAdapterPosition());
        }
    }

    public interface OnEntryListener{
        void OnEntryClick(int position);

    }

}
