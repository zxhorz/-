package com.hengtiansoft.bluemorpho.workbench.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hengtiansoft.bluemorpho.workbench.domain.Project;

/**
 * ProjectListResult
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-27T19:55:23.216+08:00")

public class ProjectListResponse extends BaseResponse  {

  @JsonProperty("data")
  @Valid
  private List<Project> data = null;

  public ProjectListResponse code(String code) {
	    this.code = code;
	    return this;
  }

  public ProjectListResponse message(String message) {
	    this.message = message;
	    return this;
}

  public ProjectListResponse data(List<Project> data) {
    this.data = data;
    return this;
  }

  public ProjectListResponse addDataItem(Project dataItem) {
    if (this.data == null) {
      this.data = new ArrayList<Project>();
    }
    this.data.add(dataItem);
    return this;
  }

  /**
   * list of all projects
   * @return data
  **/
  @ApiModelProperty(value = "list of all projects")

  @Valid

  public List<Project> getData() {
    return data;
  }

  public void setData(List<Project> data) {
    this.data = data;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectListResponse projectListResult = (ProjectListResponse) o;
    return Objects.equals(this.message, projectListResult.message) &&
        Objects.equals(this.code, projectListResult.code) &&
        Objects.equals(this.data, projectListResult.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, code, data);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProjectListResult {\n");
    
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

