package com.multiqrscanner.inventory.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.multiqrscanner.R;

import java.util.List;

// The adapter class which
// extends RecyclerView Adapter
public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyView> {
    private String TAG = "HorizontalAdapter";
    // List with String type
    private List<String> list;
    private Context context;

    // View Holder class which
    // extends RecyclerView.ViewHolder
    public class MyView extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Text View
        TextView textView;
//        TextView buttonAdd;

        // parameterised constructor for View Holder class
        // which takes the view as a parameter
        public MyView(View view) {
            super(view);

            // initialise TextView with id
            textView = view.findViewById(R.id.textview);
            view.setOnClickListener(this);
//            buttonAdd = view.findViewById(R.id.button_add);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(v, getAdapterPosition());
            }
        }
    }

    // Constructor for adapter class
    // which takes a list of String type
    public HorizontalAdapter(Context context, List<String> horizontalList) {
        this.list = horizontalList;
        this.context = context;
    }

    private onRecyclerViewItemClickListener mItemClickListener;

    public void setOnItemClickListener(onRecyclerViewItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onRecyclerViewItemClickListener {
        void onItemClickListener(View view, int position);
    }

    // Override onCreateViewHolder which deals
    // with the inflation of the card layout
    // as an item for the RecyclerView.
    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate item.xml using LayoutInflator
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_horizontal, parent, false);
//            itemView.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Log.d(TAG, "onCreateViewHolder: "+parent);
//        if (viewType == R.layout.custom_item_horizontal) {
//            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_horizontal, parent, false);
//            itemView.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }
//        else {
//            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_button_add, parent, false);
//            itemView.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }

        // return itemView
        return new MyView(itemView);
    }

    // Override onBindViewHolder which deals
    // with the setting of different data
    // and methods related to clicks on
    // particular items of the RecyclerView.
    @Override
    public void onBindViewHolder(final MyView holder, final int position) {
//        if (position == list.size()) {
//            holder.buttonAdd.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(context, "Button Clicked", Toast.LENGTH_LONG).show();
//                    list.add("Add #"+list.size());
//                    notifyItemRangeChanged(list.size(),1,1);
////                    holder.setIsRecyclable(false);
//                }
//            });
//        } else {
            // Set the text of each item of
            // Recycler view with the list items
            holder.textView.setText(list.get(position));
//        }
    }

    // Override getItemCount which Returns
    // the length of the RecyclerView.
    @Override
    public int getItemCount() {
        return list.size()
//                +1 //for +pallet in view
                ;
    }

//    @Override
//    public int getItemViewType(int position) {
//        Log.d(TAG, "getItemViewType: "+position);
//        Log.d(TAG, "getItemViewType: list.size "+list.size());
//        return (position == list.size()) ? R.layout.custom_button_add : R.layout.custom_item_horizontal;
//    }
}
