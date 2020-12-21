package com.multiqrscanner.outbound.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.multiqrscanner.R;
import com.multiqrscanner.outbound.model.OutboundDetail;

import java.util.List;

class LoadingViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;

    public LoadingViewHolder(View itemView) {
        super(itemView);
        progressBar = itemView.findViewById(R.id.progressBar);
    }

}

class ItemViewHolder extends RecyclerView.ViewHolder {
    public TextView lineNo, sku, serialNo, product, quantity, subkey;

    public ItemViewHolder(View itemView) {
        super(itemView);
        lineNo = itemView.findViewById(R.id.line_no_val);
        sku = itemView.findViewById(R.id.sku_val);
        serialNo = itemView.findViewById(R.id.serial_no_val);
        product = itemView.findViewById(R.id.product_val);
        quantity = itemView.findViewById(R.id.quantity_val);
        subkey = itemView.findViewById(R.id.subkey_val);
    }

}

public class OutboundsDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;
    ILoadMore loadMore;
    boolean isLoading;
    Activity activity;
    List<OutboundDetail> items;
    int visibleThreshold = 5;
    int lastVisibleItem, totalItemCount;

    public OutboundsDetailsAdapter(RecyclerView recyclerView, Activity activity, List<OutboundDetail> items) {
        this.activity = activity;
        this.items = items;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (loadMore != null) {
                        loadMore.onLoadMore();
                    }
                }
                isLoading = true;
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_layout, viewGroup, false);
            return new ItemViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_loading, viewGroup, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemViewHolder) {
            OutboundDetail item = items.get(position);
            ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
            itemViewHolder.lineNo.setText(item.getLineNo());
            itemViewHolder.sku.setText(item.getSku());
            itemViewHolder.serialNo.setText(item.getSerialNo());
            itemViewHolder.product.setText(item.getProductName());
            itemViewHolder.quantity.setText(item.getQty());
            itemViewHolder.subkey.setText(item.getSubkey());
        } else if (viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setLoaded() {
        isLoading = false;
    }
}
