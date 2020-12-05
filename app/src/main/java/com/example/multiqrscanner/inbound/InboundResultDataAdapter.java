package com.example.multiqrscanner.inbound;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

public class InboundResultDataAdapter extends TableDataAdapter<InboundResult> {

    public InboundResultDataAdapter(Context context, List<InboundResult> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        InboundResult car = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
//            case 0:
//                renderedView = renderProducerLogo(car);
//                break;
//            case 1:
//                renderedView = renderCatName(car);
//                break;
//            case 2:
//                renderedView = renderPower(car);
//                break;
        }

        return renderedView;
    }

}
