package com.multiqrscanner.qrcode;

import boofcv.alg.fiducial.qrcode.QrCode;

public class QrCodeWrapper {
    private QrCode qrCode;
    private Long count;

    public QrCode getQrCode() {
        return qrCode;
    }

    public void setQrCode(QrCode qrCode) {
        this.qrCode = qrCode;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public QrCodeWrapper() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("QrCodeWrapper{");
        sb.append("qrCode=").append(qrCode);
        sb.append(", count=").append(count);
        sb.append('}');
        return sb.toString();
    }
}
