/*
 * Torque API Reference
 * This page contains information about the Torque APIs and how to use it.<br><br>To be able to test the API methods, you will need an access token to be set in the Authorize section.If you got to this page from your Torque account, we already made it available for you, but you can switch to a different token as needed.<br>To get a different token, use the 'Get token' function available under the 'Access Tokens' section. After running the method, copy the access_token you get in the response and set it in the Authorize fieldas 'Bearer access_token'. For example: Bearer fqSWuw72BbUVFn8AbokF77GJ0r5KEn9MiZjLXF8kBwI.<br><br>The API can be accessed with your account endpoint as will be mentioned in the descriptions and examples or using https://qtorque.io as the prefix instead.
 *
 * OpenAPI spec version: latest
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package org.jenkinsci.plugins.torque.api;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;


public class EnvironmentNameValueResponse {
  @SerializedName("name")
  private String name = null;

  @SerializedName("value")
  private String value = null;

  public EnvironmentNameValueResponse name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public EnvironmentNameValueResponse value(String value) {
    this.value = value;
    return this;
  }

   /**
   * Get value
   * @return value
  **/
  
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EnvironmentNameValueResponse environmentNameValueResponse = (EnvironmentNameValueResponse) o;
    return Objects.equals(this.name, environmentNameValueResponse.name) &&
        Objects.equals(this.value, environmentNameValueResponse.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EnvironmentNameValueResponse {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
