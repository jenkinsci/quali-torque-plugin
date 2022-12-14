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


public class BlueprintInputParameterResponse {
  @SerializedName("name")
  private String name = null;

  @SerializedName("default_value")
  private String defaultValue = null;

  @SerializedName("has_default_value")
  private Boolean hasDefaultValue = null;

  @SerializedName("display_style")
  private String displayStyle = null;

  @SerializedName("sensitive")
  private Boolean sensitive = null;

  @SerializedName("description")
  private String description = null;

  @SerializedName("optional")
  private Boolean optional = null;

  public BlueprintInputParameterResponse name(String name) {
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

  public BlueprintInputParameterResponse defaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

   /**
   * Get defaultValue
   * @return defaultValue
  **/
  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public BlueprintInputParameterResponse hasDefaultValue(Boolean hasDefaultValue) {
    this.hasDefaultValue = hasDefaultValue;
    return this;
  }

   /**
   * Indicates whether default value was defined in the yaml, even if it is not returned in the default_value field (e.g. when sensitive)
   * @return hasDefaultValue
  **/
  public Boolean isHasDefaultValue() {
    return hasDefaultValue;
  }

  public void setHasDefaultValue(Boolean hasDefaultValue) {
    this.hasDefaultValue = hasDefaultValue;
  }

  public BlueprintInputParameterResponse displayStyle(String displayStyle) {
    this.displayStyle = displayStyle;
    return this;
  }

   /**
   * Get displayStyle
   * @return displayStyle
  **/
  public String getDisplayStyle() {
    return displayStyle;
  }

  public void setDisplayStyle(String displayStyle) {
    this.displayStyle = displayStyle;
  }

  public BlueprintInputParameterResponse sensitive(Boolean sensitive) {
    this.sensitive = sensitive;
    return this;
  }

   /**
   * Get sensitive
   * @return sensitive
  **/
  public Boolean isSensitive() {
    return sensitive;
  }

  public void setSensitive(Boolean sensitive) {
    this.sensitive = sensitive;
  }

  public BlueprintInputParameterResponse description(String description) {
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

  public BlueprintInputParameterResponse optional(Boolean optional) {
    this.optional = optional;
    return this;
  }

   /**
   * Get optional
   * @return optional
  **/
  public Boolean isOptional() {
    return optional;
  }

  public void setOptional(Boolean optional) {
    this.optional = optional;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlueprintInputParameterResponse blueprintInputParameterResponse = (BlueprintInputParameterResponse) o;
    return Objects.equals(this.name, blueprintInputParameterResponse.name) &&
        Objects.equals(this.defaultValue, blueprintInputParameterResponse.defaultValue) &&
        Objects.equals(this.hasDefaultValue, blueprintInputParameterResponse.hasDefaultValue) &&
        Objects.equals(this.displayStyle, blueprintInputParameterResponse.displayStyle) &&
        Objects.equals(this.sensitive, blueprintInputParameterResponse.sensitive) &&
        Objects.equals(this.description, blueprintInputParameterResponse.description) &&
        Objects.equals(this.optional, blueprintInputParameterResponse.optional);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, defaultValue, hasDefaultValue, displayStyle, sensitive, description, optional);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QualiColonyContractsBlueprintsV2BlueprintInputParameterResponse {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    defaultValue: ").append(toIndentedString(defaultValue)).append("\n");
    sb.append("    hasDefaultValue: ").append(toIndentedString(hasDefaultValue)).append("\n");
    sb.append("    displayStyle: ").append(toIndentedString(displayStyle)).append("\n");
    sb.append("    sensitive: ").append(toIndentedString(sensitive)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    optional: ").append(toIndentedString(optional)).append("\n");
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
