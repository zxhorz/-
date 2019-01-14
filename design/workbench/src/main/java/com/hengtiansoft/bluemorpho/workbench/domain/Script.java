package com.hengtiansoft.bluemorpho.workbench.domain;

public class Script {
    private String name;
    private String description;
    
    public Script(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }



}
