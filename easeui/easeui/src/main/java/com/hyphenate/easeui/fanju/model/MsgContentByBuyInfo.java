package com.hyphenate.easeui.fanju.model;

import java.util.List;

public class MsgContentByBuyInfo {

    private  String machineId;
    private  String storeName;
    private  int handleStatus;
    private  String handleDescribe;
    private String operateUserName;

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

    public int getHandleStatus() {
        return handleStatus;
    }

    public void setHandleStatus(int handleStatus) {
        this.handleStatus = handleStatus;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getHandleDescribe() {
        return handleDescribe;
    }

    public void setHandleDescribe(String handleDescribe) {
        this.handleDescribe = handleDescribe;
    }
}
