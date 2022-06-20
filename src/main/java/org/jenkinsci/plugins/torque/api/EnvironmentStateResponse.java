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

public class EnvironmentStateResponse {
  @SerializedName("current_state")
  public String currentState = null;

  @SerializedName("grains")
  private List<EnvironmentGrainResponse> grains = null;

  @SerializedName("execution")
  private EnvironmentExecutionResponse execution = null;

  @SerializedName("outputs")
  private List<EnvironmentOutputResponse> outputs = null;

  @SerializedName("errors")
  private List<EnvironmentErrorResponse> errors = null;

  public EnvironmentStateResponse currentState(String currentState) {
    this.currentState = currentState;
    return this;
  }

   /**
   * Get currentState
   * @return currentState
  **/
  
  public String getCurrentState() {
    return currentState;
  }

  public void setCurrentState(String currentState) {
    this.currentState = currentState;
  }

  public EnvironmentStateResponse grains(List<EnvironmentGrainResponse> grains) {
    this.grains = grains;
    return this;
  }

  public EnvironmentStateResponse addGrainsItem(EnvironmentGrainResponse grainsItem) {
    if (this.grains == null) {
      this.grains = new ArrayList<EnvironmentGrainResponse>();
    }
    this.grains.add(grainsItem);
    return this;
  }

   /**
   * Get grains
   * @return grains
  **/
  
  public List<EnvironmentGrainResponse> getGrains() {
    return grains;
  }

  public void setGrains(List<EnvironmentGrainResponse> grains) {
    this.grains = grains;
  }

  public EnvironmentStateResponse execution(EnvironmentExecutionResponse execution) {
    this.execution = execution;
    return this;
  }

   /**
   * Get execution
   * @return execution
  **/
  
  public EnvironmentExecutionResponse getExecution() {
    return execution;
  }

  public void setExecution(EnvironmentExecutionResponse execution) {
    this.execution = execution;
  }

  public EnvironmentStateResponse outputs(List<EnvironmentOutputResponse> outputs) {
    this.outputs = outputs;
    return this;
  }

  public EnvironmentStateResponse addOutputsItem(EnvironmentOutputResponse outputsItem) {
    if (this.outputs == null) {
      this.outputs = new ArrayList<EnvironmentOutputResponse>();
    }
    this.outputs.add(outputsItem);
    return this;
  }

   /**
   * Get outputs
   * @return outputs
  **/
  
  public List<EnvironmentOutputResponse> getOutputs() {
    return outputs;
  }

  public void setOutputs(List<EnvironmentOutputResponse> outputs) {
    this.outputs = outputs;
  }

  public EnvironmentStateResponse errors(List<EnvironmentErrorResponse> errors) {
    this.errors = errors;
    return this;
  }

  public EnvironmentStateResponse addErrorsItem(EnvironmentErrorResponse errorsItem) {
    if (this.errors == null) {
      this.errors = new ArrayList<EnvironmentErrorResponse>();
    }
    this.errors.add(errorsItem);
    return this;
  }

   /**
   * Get errors
   * @return errors
  **/
  
  public List<EnvironmentErrorResponse> getErrors() {
    return errors;
  }

  public void setErrors(List<EnvironmentErrorResponse> errors) {
    this.errors = errors;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EnvironmentStateResponse environmentStateResponse = (EnvironmentStateResponse) o;
    return Objects.equals(this.currentState, environmentStateResponse.currentState) &&
        Objects.equals(this.grains, environmentStateResponse.grains) &&
        Objects.equals(this.execution, environmentStateResponse.execution) &&
        Objects.equals(this.outputs, environmentStateResponse.outputs) &&
        Objects.equals(this.errors, environmentStateResponse.errors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currentState, grains, execution, outputs, errors);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EnvironmentStateResponse {\n");
    
    sb.append("    currentState: ").append(toIndentedString(currentState)).append("\n");
    sb.append("    grains: ").append(toIndentedString(grains)).append("\n");
    sb.append("    execution: ").append(toIndentedString(execution)).append("\n");
    sb.append("    outputs: ").append(toIndentedString(outputs)).append("\n");
    sb.append("    errors: ").append(toIndentedString(errors)).append("\n");
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
