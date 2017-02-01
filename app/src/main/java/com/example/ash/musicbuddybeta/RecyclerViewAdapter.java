package com.example.ash.musicbuddybeta;

import android.content.Context;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ash on 28-May-16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    CursorAdapter cursorAdapter;

    public RecyclerViewAdapter(Context mContext, CursorAdapter mCursorAdapter) {
        context = mContext;
        cursorAdapter = mCursorAdapter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = cursorAdapter.newView(context, cursorAdapter.getCursor(), parent);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursorAdapter.getCursor().moveToPosition(position);
        cursorAdapter.bindView(holder.itemView, context, cursorAdapter.getCursor());

    }

    @Override
    public int getItemCount() {
        return cursorAdapter.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //public TextView track, artist, name, timestamp,id;
        public ViewHolder(View itemView) {
            super(itemView);
            /*track=(TextView) itemView.findViewById(R.id.tvTrack);
            artist=(TextView) itemView.findViewById(R.id.tvArtist);
            name=(TextView) itemView.findViewById(R.id.tvName);
            timestamp=(TextView) itemView.findViewById(R.id.tvTimestamp);*/
        }
    }
}
