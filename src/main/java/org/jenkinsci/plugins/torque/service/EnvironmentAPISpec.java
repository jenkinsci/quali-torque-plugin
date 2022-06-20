package org.jenkinsci.plugins.torque.service;

import org.jenkinsci.plugins.torque.api.CreateSandboxRequest;
import org.jenkinsci.plugins.torque.api.CreateEnvResponse;
import org.jenkinsci.plugins.torque.api.EnvironmentResponse;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by shay-k on 21/06/2017.
 */
public interface EnvironmentAPISpec {

    @POST("api/spaces/{spaceName}/environments")
    Call<CreateEnvResponse> createEnvironment(@Header("Authorization") String token,
                                              @Path("spaceName") String spaceName,
                                              @Body CreateSandboxRequest request);

    @DELETE("api/spaces/{spaceName}/environments/{environmentId}")
    Call<Void> deleteEnvironment(@Header("Authorization") String token,
                                 @Path("spaceName") String spaceName,
                                 @Path("environmentId") String environmentId);

    @GET("api/spaces/{spaceName}/environments/{environmentId}")
    Call<EnvironmentResponse> getEnvironmentById(@Header("Authorization") String token,
                                                 @Path("spaceName") String spaceName,
                                                 @Path("environmentId") String environmentId);
}
