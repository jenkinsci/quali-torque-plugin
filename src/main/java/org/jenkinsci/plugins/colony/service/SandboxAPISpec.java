package org.jenkinsci.plugins.colony.service;

import org.jenkinsci.plugins.colony.api.CreateSandboxRequest;
import org.jenkinsci.plugins.colony.api.CreateSandboxResponse;
import org.jenkinsci.plugins.colony.api.SingleSandbox;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by shay-k on 21/06/2017.
 */
public interface SandboxAPISpec {

    @POST("api/spaces/{spaceName}/sandboxes")
    Call<CreateSandboxResponse> createSandbox(@Header("Authorization") String token,
                                              @Path("spaceName") String spaceName,
                                              @Body CreateSandboxRequest request);

    @DELETE("api/spaces/{spaceName}/sandboxes/{sandboxId}")
    Call<Void> deleteSandbox(@Header("Authorization") String token,
                             @Path("spaceName") String spaceName,
                             @Path("sandboxId") String sandboxId);

    @GET("api/spaces/{spaceName}/sandboxes/{sandboxId}")
    Call<SingleSandbox> getSandboxById(@Header("Authorization") String token,
                                       @Path("spaceName") String spaceName,
                                       @Path("sandboxId") String sandboxId);
}
