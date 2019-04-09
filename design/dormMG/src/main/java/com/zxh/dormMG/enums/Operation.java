package com.zxh.dormMG.enums;

public enum Operation {
    HANDLE("处理"), REJECT("拒绝"), FINISH("完成");
    private String operation;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    Operation(String operation) {
        this.operation = operation;
    }
}
