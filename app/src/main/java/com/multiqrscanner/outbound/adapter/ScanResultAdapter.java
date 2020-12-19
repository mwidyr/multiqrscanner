package com.multiqrscanner.outbound.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.multiqrscanner.R;
import com.multiqrscanner.outbound.GoodsShipmentScanResultActivity;
import com.multiqrscanner.qrcode.QrCodeProductValue;

import java.util.List;

class ScanResultItemViewHolder extends RecyclerView.ViewHolder {
    public TextView serialNo, sku, status;

    public ScanResultItemViewHolder(View itemView) {
        super(itemView);
        serialNo = itemView.findViewById(R.id.serial_no_val);
        sku = itemView.findViewById(R.id.sku_val);
        status = itemView.findViewById(R.id.status_val);
    }

}

public class ScanResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;
    ILoadMore loadMore;
    boolean isLoading;
    Activity activity;
    List<QrCodeProductValue> items;
    int visibleThreshold = 5;
    int lastVisibleItem, totalItemCount;

    public ScanResultAdapter(RecyclerView recyclerView, Activity activity, List<QrCodeProductValue> items) {
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
            View view = LayoutInflater.from(activity).inflate(R.layout.scan_result_item_layout, viewGroup, false);
            return new ScanResultItemViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_loading, viewGroup, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ScanResultItemViewHolder) {
            QrCodeProductValue item = items.get(position);
            ScanResultItemViewHolder itemViewHolder = (ScanResultItemViewHolder) viewHolder;
            String serialNo = item.getSerialNo()+"";
            itemViewHolder.serialNo.setText(serialNo);
            itemViewHolder.sku.setText(item.getSku());
            itemViewHolder.status.setText(item.getValid());
            itemViewHolder.status.setTextColor(Color.parseColor("#1E90FF"));
            if(item.getValid().trim().equalsIgnoreCase(GoodsShipmentScanResultActivity.InvalidInbound)){
                itemViewHolder.status.setTextColor(Color.parseColor("#FF0000"));
            }
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
