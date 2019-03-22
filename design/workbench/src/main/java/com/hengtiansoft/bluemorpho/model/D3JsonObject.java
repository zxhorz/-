package com.hengtiansoft.bluemorpho.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class D3JsonObject implements Serializable {

	private List<D3Node> nodes = new ArrayList<D3Node>();
	private List<D3Link> links = new ArrayList<D3Link>();
	private List<D3Mark> mark = new ArrayList<D3Mark>();

	public D3JsonObject() {
		super();
	}

	public D3JsonObject(List<D3Node> nodes, List<D3Link> links, List<D3Mark> mark) {
		super();
		this.nodes = nodes;
		this.links = links;
		this.mark = mark;
	}

	public List<D3Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<D3Node> nodes) {
		this.nodes = nodes;
	}

	public List<D3Link> getLinks() {
		return links;
	}

	public void setLinks(List<D3Link> links) {
		this.links = links;
	}

	public List<D3Mark> getMark() {
		return mark;
	}

	public void setMark(List<D3Mark> mark) {
		this.mark = mark;
	}

}
