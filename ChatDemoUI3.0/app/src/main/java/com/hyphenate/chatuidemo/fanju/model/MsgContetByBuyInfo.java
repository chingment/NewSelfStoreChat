package com.hyphenate.chatuidemo.fanju.model;

import java.util.List;

public class MsgContetByBuyInfo {

    private  String machineId;
    private  String storeName;
    private List<ProductSkuBean> skus;

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public List<ProductSkuBean> getSkus() {
        return skus;
    }

    public void setSkus(List<ProductSkuBean> skus) {
        this.skus = skus;
    }
}
