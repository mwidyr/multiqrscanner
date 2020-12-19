package com.multiqrscanner.qrcode;

import com.google.gson.annotations.SerializedName;

public class QrCodePalletValue {
    @SerializedName("pallet_no")
    private Long pallet_no;

    public QrCodePalletValue(Long pallet_no) {
        this.pallet_no = pallet_no;
    }

    public QrCodePalletValue() {
    }

    public Long getPallet_no() {
        return pallet_no;
    }

    public void setPallet_no(Long pallet_no) {
        this.pallet_no = pallet_no;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("QrCodePalletValue{");
        sb.append("pallet_no=").append(pallet_no);
        sb.append('}');
        return sb.toString();
    }
}
