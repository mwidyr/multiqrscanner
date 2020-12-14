package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;

public class InboundItemDetail {
    @SerializedName("id")
    private String id;
    @SerializedName("iditem")
    private String iditem;
    @SerializedName("documentno")
    private String documentno;
    @SerializedName("serialno")
    private String serialno;
    @SerializedName("sku")
    private String sku;
    @SerializedName("qty")
    private Long qty;
    @SerializedName("line")
    private Long line;
    @SerializedName("verif")
    private String verif;
    @SerializedName("subkey")
    private String subkey;
    @SerializedName("productname")
    private String productname;

    public InboundItemDetail() {
    }

    public InboundItemDetail(String id, String iditem, String documentno, String serialno, String sku, Long qty, Long line, String verif, String subkey, String productname) {
        this.id = id;
        this.iditem = iditem;
        this.documentno = documentno;
        this.serialno = serialno;
        this.sku = sku;
        this.qty = qty;
        this.line = line;
        this.verif = verif;
        this.subkey = subkey;
        this.productname = productname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIditem() {
        return iditem;
    }

    public void setIditem(String iditem) {
        this.iditem = iditem;
    }

    public String getDocumentno() {
        return documentno;
    }

    public void setDocumentno(String documentno) {
        this.documentno = documentno;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public Long getLine() {
        return line;
    }

    public void setLine(Long line) {
        this.line = line;
    }

    public String getVerif() {
        return verif;
    }

    public void setVerif(String verif) {
        this.verif = verif;
    }

    public String getSubkey() {
        return subkey;
    }

    public void setSubkey(String subkey) {
        this.subkey = subkey;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InboundItemDetail{");
        sb.append("id=").append(id);
        sb.append(", iditem='").append(iditem).append('\'');
        sb.append(", documentno='").append(documentno).append('\'');
        sb.append(", serialno='").append(serialno).append('\'');
        sb.append(", sku='").append(sku).append('\'');
        sb.append(", qty=").append(qty);
        sb.append(", line=").append(line);
        sb.append(", verif='").append(verif).append('\'');
        sb.append(", subkey='").append(subkey).append('\'');
        sb.append(", productname='").append(productname).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
