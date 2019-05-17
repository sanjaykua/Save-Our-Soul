package com.hash.sos.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hash.sos.R;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout itemLayout;
    public TextView name;
    public TextView number;
    public ImageButton delete;

    public ContactViewHolder(View v) {
        super(v);
        itemLayout = v.findViewById(R.id.item_layout);
        name = v.findViewById(R.id.name);
        number = v.findViewById(R.id.number);
        delete = v.findViewById(R.id.delete);
    }
}
