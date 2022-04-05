package org.jenkinsci.plugins.torque.service;

import org.jenkinsci.plugins.torque.api.CreateSandboxRequest;
import org.jenkinsci.plugins.torque.api.CreateSandboxResponse;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by shay-k on 21/06/2017.
 */
public interface SandboxAPISpec {

    @Headers("User-Agent: Jenkins-Torque-Plugin/1.0.0")
    @POST("api/spaces/{spaceName}/sandbox")
    Call<CreateSandboxResponse> createSandbox(@Header("Authorization") String token,
                                              @Path("spaceName") String spaceName,
                                              @Body CreateSandboxRequest request);

    @Headers("User-Agent: Jenkins-Torque-Plugin/1.0.0")
    @DELETE("api/spaces/{spaceName}/sandbox/{sandboxId}")
    Call<Void> deleteSandbox(@Header("Authorization") String token,
                             @Path("spaceName") String spaceName,
                             @Path("sandboxId") String sandboxId);

    @Headers("User-Agent: Jenkins-Torque-Plugin/1.0.0")
    @GET("api/spaces/{spaceName}/sandbox/{sandboxId}")
    Call<Object> getSandboxById(@Header("Authorization") String token,
                                       @Path("spaceName") String spaceName,
                                       @Path("sandboxId") String sandboxId);
}
