package com.multiqrscanner.inventory.model;

public class PalletDetail {
    private Long palletNo;

    public PalletDetail(Long palletNo) {
        this.palletNo = palletNo;
    }

    public PalletDetail() {
    }

    public Long getPalletNo() {
        return palletNo;
    }

    public void setPalletNo(Long palletNo) {
        this.palletNo = palletNo;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PalletDetail{");
        sb.append("palletNo=").append(palletNo);
        sb.append('}');
        return sb.toString();
    }
}
