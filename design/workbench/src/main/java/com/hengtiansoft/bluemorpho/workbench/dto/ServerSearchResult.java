package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.List;


public class ServerSearchResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<HitResultDto> hits;
	private double max_score;
	private int total;
	
	public double getMax_score() {
		return max_score;
	}
	public void setMax_score(double max_score) {
		this.max_score = max_score;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<HitResultDto> getHits() {
		return hits;
	}
	public void setHits(List<HitResultDto> hits) {
		this.hits = hits;
	}	
}
