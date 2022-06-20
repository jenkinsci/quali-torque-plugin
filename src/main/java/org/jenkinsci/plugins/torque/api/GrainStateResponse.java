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


public class GrainStateResponse {
  @SerializedName("current_state")
  private String currentState = null;

  @SerializedName("stages")
  private List<GrainStageResponse> stages = null;

  public GrainStateResponse currentState(String currentState) {
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

  public GrainStateResponse stages(List<GrainStageResponse> stages) {
    this.stages = stages;
    return this;
  }

  public GrainStateResponse addStagesItem(GrainStageResponse stagesItem) {
    if (this.stages == null) {
      this.stages = new ArrayList<GrainStageResponse>();
    }
    this.stages.add(stagesItem);
    return this;
  }

   /**
   * Get stages
   * @return stages
  **/
  
  public List<GrainStageResponse> getStages() {
    return stages;
  }

  public void setStages(List<GrainStageResponse> stages) {
    this.stages = stages;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GrainStateResponse grainStateResponse = (GrainStateResponse) o;
    return Objects.equals(this.currentState, grainStateResponse.currentState) &&
        Objects.equals(this.stages, grainStateResponse.stages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currentState, stages);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EnvironmentGrainStateResponse {\n");
    
    sb.append("    currentState: ").append(toIndentedString(currentState)).append("\n");
    sb.append("    stages: ").append(toIndentedString(stages)).append("\n");
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
