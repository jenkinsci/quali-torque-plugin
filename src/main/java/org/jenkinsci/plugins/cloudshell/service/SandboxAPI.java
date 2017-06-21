package org.jenkinsci.plugins.cloudshell.service;

import org.jenkinsci.plugins.cloudshell.api.CreateSandboxRequest;
import org.jenkinsci.plugins.cloudshell.api.CreateSandboxResponse;
import org.jenkinsci.plugins.cloudshell.api.Sandbox;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by shay-k on 21/06/2017.
 */
public interface SandboxAPI {
    @GET("api/Sandbox")
    Call<Sandbox[]> getSandboxes();

    @POST("api/Sandbox")
    Call<CreateSandboxResponse> createSandbox(@Body CreateSandboxRequest request);

    @DELETE("api/Sandbox")
    Call<Void> deleteSandbox(@Query("sandboxId") String sandboxId);
}
