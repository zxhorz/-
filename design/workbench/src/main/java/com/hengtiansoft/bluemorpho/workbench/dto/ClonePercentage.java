package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.hengtiansoft.bluemorpho.model.ParagraphClone;

/**
 * @Description: clone analysis后得到的段之间的clone关系
 * json文件结构如下：
 * [
    {
     	"name": "PROGRAM1.PARAGRAPH1",
        "cloneMembers": [
            {
                "cloneLines": 46,
                "clonePercentage": 1,
                "name": "PROGRAM2.PARAGRAPH2"
            },
            {
                "cloneLines": 46,
                "clonePercentage": 1,
                "name": "PROGRAM3.PARAGRAPH3"
            },
            {
                "cloneLines": 46,
                "clonePercentage": 1,
                "name": "PROGRAM4.PARAGRAPH3"
            }
        ]        
    }
   ]
 * @author gaochaodeng
 * @date Aug 7, 2018
 */
public class ClonePercentage implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private List<ParagraphClone> cloneMembers = new ArrayList<ParagraphClone>();

	public List<ParagraphClone> getCloneMembers() {
		return cloneMembers;
	}

	public void setCloneMembers(List<ParagraphClone> cloneMembers) {
		this.cloneMembers = cloneMembers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
