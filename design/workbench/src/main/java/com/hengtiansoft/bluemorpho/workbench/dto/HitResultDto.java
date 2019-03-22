package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class HitResultDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JSONField(name = "_type")
	private String _type;
	private HighLightDto highlight;
	@JSONField(name = "_score")
	private String _score;
	
	@JSONField(name = "_index")
	private String _index;
	
	@JSONField(name = "_id")
	private String _id;
	
	@JSONField(name = "_type")
	public String getType() {
		return _type;
	}
	
	@JSONField(name = "_type")
	public void setType(String _type) {
		this._type = _type;
	}
	public HighLightDto getHighlight() {
		return highlight;
	}
	public void setHighlight(HighLightDto highlight) {
		this.highlight = highlight;
	}
	
	@JSONField(name = "_score")
	public String getScore() {
		return _score;
	}
	
	@JSONField(name = "_score")
	public void setScore(String _score) {
		this._score = _score;
	}
	
	@JSONField(name = "_index")
	public String getIndex() {
		return _index;
	}
	
	@JSONField(name = "_index")
	public void setIndex(String _index) {
		this._index = _index;
	}
	
	@JSONField(name = "_id")
	public String get_id() {
		return _id;
	}
	
	@JSONField(name = "_id")
	public void set_id(String _id) {
		this._id = _id;
	}
}
