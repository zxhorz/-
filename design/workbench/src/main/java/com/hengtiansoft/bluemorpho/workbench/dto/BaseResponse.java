package com.hengtiansoft.bluemorpho.workbench.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

/**
 * BaseResponse
 */
@Validated

public class BaseResponse   {
	@JsonProperty("code")
	protected String code = null;
	
	@JsonProperty("message")
	protected String message = null;

	public BaseResponse message(String message) {
		this.message = message;
		return this;
  }

  /**
   * Response message for an API all, usually the error message.
   * @return message
  **/
  @ApiModelProperty(value = "Response message for an API all, usually the error message.")


  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public BaseResponse code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Response code for an API call
   * @return code
  **/
  @ApiModelProperty(value = "Response code for an API call")


  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseResponse resultDto = (BaseResponse) o;
    return Objects.equals(this.message, resultDto.message) &&
        Objects.equals(this.code, resultDto.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, code);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResultDto {\n");
    
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
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

