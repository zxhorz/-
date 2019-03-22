package com.hengtiansoft.bluemorpho.workbench.dto;

public class CheckMissingItem {
    private String name;
    private String type;
    
    public CheckMissingItem(String name, String type) {
        super();
        this.name = name;
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }    
}
