package com.hengtiansoft.bluemorpho.workbench.enums;

public enum MissingDocType {
    COPYBOOK("missing_copybooks.txt"),PROGRAM("missing_programs.txt"),TABLE("missing_tables.txt");
    private String messge;

    MissingDocType(String messge) {
        this.messge = messge;
    }
    
    public String getMessge() {
        return messge;
    }

    public void setMessge(String messge) {
        this.messge = messge;
    }

    @Override
    public String toString(){
        return this.messge;
    }
    
    
}
