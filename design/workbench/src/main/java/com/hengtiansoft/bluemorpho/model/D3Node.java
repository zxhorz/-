package com.hengtiansoft.bluemorpho.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class D3Node implements Serializable {

	private String id;
	private String group;

	public D3Node() {
		super();
	}

	public D3Node(String id, String group) {
		super();
		this.id = id;
		this.group = group;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
	
    @Override  
    public boolean equals(Object o) {  
        if (o instanceof D3Node) {  
            D3Node question = (D3Node) o;  
            return this.id.equals(question.id)  
                    && this.group.equals(question.group);
        }  
        return super.equals(o);  
    }  

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
