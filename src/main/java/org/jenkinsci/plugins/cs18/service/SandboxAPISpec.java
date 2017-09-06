package org.jenkinsci.plugins.cs18.service;

import org.jenkinsci.plugins.cs18.api.CreateSandboxRequest;
import org.jenkinsci.plugins.cs18.api.CreateSandboxResponse;
import org.jenkinsci.plugins.cs18.api.Sandbox;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by shay-k on 21/06/2017.
 */
public interface SandboxAPISpec {
    @GET("api/Sandboxes")
    Call<Sandbox[]> getSandboxes();

    @POST("api/Sandboxes")
    Call<CreateSandboxResponse> createSandbox(@Body CreateSandboxRequest request);

    @DELETE("api/Sandboxes")
    Call<Void> deleteSandbox(@Query("sandboxId") String sandboxId);
}
