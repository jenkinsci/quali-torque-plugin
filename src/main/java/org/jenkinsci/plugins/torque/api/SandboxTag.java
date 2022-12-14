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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SandboxTag {
  @SerializedName("name")
  private String name = null;

  @SerializedName("value")
  private String value = null;

  @SerializedName("modified_by")
  private String modifiedBy = null;

  @SerializedName("last_modified")
  private String lastModified = null;

  @SerializedName("created_by")
  private String createdBy = null;

  @SerializedName("created_date")
  private String createdDate = null;

  @SerializedName("possible_values")
  private List<String> possibleValues = null;

  @SerializedName("description")
  private String description = null;

  public SandboxTag name(String name) {
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

  public SandboxTag value(String value) {
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

  public SandboxTag modifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
    return this;
  }

   /**
   * Get modifiedBy
   * @return modifiedBy
  **/
  
  public String getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public SandboxTag lastModified(String lastModified) {
    this.lastModified = lastModified;
    return this;
  }

   /**
   * Get lastModified
   * @return lastModified
  **/
  
  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public SandboxTag createdBy(String createdBy) {
    this.createdBy = createdBy;
    return this;
  }

   /**
   * Get createdBy
   * @return createdBy
  **/
  
  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public SandboxTag createdDate(String createdDate) {
    this.createdDate = createdDate;
    return this;
  }

   /**
   * Get createdDate
   * @return createdDate
  **/
  
  public String getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  public SandboxTag possibleValues(List<String> possibleValues) {
    this.possibleValues = possibleValues;
    return this;
  }

  public SandboxTag addPossibleValuesItem(String possibleValuesItem) {
    if (this.possibleValues == null) {
      this.possibleValues = new ArrayList<String>();
    }
    this.possibleValues.add(possibleValuesItem);
    return this;
  }

   /**
   * Get possibleValues
   * @return possibleValues
  **/
  
  public List<String> getPossibleValues() {
    return possibleValues;
  }

  public void setPossibleValues(List<String> possibleValues) {
    this.possibleValues = possibleValues;
  }

  public SandboxTag description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Get description
   * @return description
  **/
  
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SandboxTag sandboxTag = (SandboxTag) o;
    return Objects.equals(this.name, sandboxTag.name) &&
        Objects.equals(this.value, sandboxTag.value) &&
        Objects.equals(this.modifiedBy, sandboxTag.modifiedBy) &&
        Objects.equals(this.lastModified, sandboxTag.lastModified) &&
        Objects.equals(this.createdBy, sandboxTag.createdBy) &&
        Objects.equals(this.createdDate, sandboxTag.createdDate) &&
        Objects.equals(this.possibleValues, sandboxTag.possibleValues) &&
        Objects.equals(this.description, sandboxTag.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value, modifiedBy, lastModified, createdBy, createdDate, possibleValues, description);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SandboxTag {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    modifiedBy: ").append(toIndentedString(modifiedBy)).append("\n");
    sb.append("    lastModified: ").append(toIndentedString(lastModified)).append("\n");
    sb.append("    createdBy: ").append(toIndentedString(createdBy)).append("\n");
    sb.append("    createdDate: ").append(toIndentedString(createdDate)).append("\n");
    sb.append("    possibleValues: ").append(toIndentedString(possibleValues)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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
