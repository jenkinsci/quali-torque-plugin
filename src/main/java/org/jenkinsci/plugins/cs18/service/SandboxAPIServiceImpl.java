package org.jenkinsci.plugins.cs18.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import org.jenkinsci.plugins.cs18.api.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SandboxAPIServiceImpl implements SandboxAPIService{
    private SandboxAPISpec sandboxAPI = null;
    public SandboxAPIServiceImpl(SandboxServiceConnection connection) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connection.connectionTimeoutSec,TimeUnit.SECONDS);
        builder.readTimeout(connection.readTimeoutSec, TimeUnit.SECONDS);
        OkHttpClient client= builder.build();

        String baseUrl = String.format("http://%1$s:%2$s",connection.address,connection.port);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();


        sandboxAPI = retrofit.create(SandboxAPISpec.class);
    }

    public ResponseData<Sandbox []> getSandboxes() throws RuntimeException, IOException {
        return execute(sandboxAPI.getSandboxes());
    }

    @Override
    public ResponseData<SingleSandbox> getSandboxById(String sandboxId) throws RuntimeException, IOException {
        return execute(sandboxAPI.getSandboxById(sandboxId));
    }


    public ResponseData<CreateSandboxResponse> createSandbox(final CreateSandboxRequest req) throws IOException {
        return execute(sandboxAPI.createSandbox(req));
    }

    public ResponseData<Void> deleteSandbox(String sandboxId) throws IOException {
        return execute(sandboxAPI.deleteSandbox(sandboxId));
    }

    private static <T> ResponseData<T> parseResponse(final Response<T> response) throws IOException {
        String message = response.message();
        if (!response.isSuccessful()) {
            final String err = response.errorBody().string();
            return ResponseData.error(response.code(),err).setMessage(message);
        }
        return ResponseData.ok(response.body(),response.code()).setMessage(message);
    }
    private static <T> ResponseData<T> createErrorResponse(Exception e) {
        String errorMessage = e.toString();
        return ResponseData.error(-1,errorMessage);
    }
    public <T> ResponseData<T> execute(Call<T> call) throws IOException {
        return parseResponse(call.execute());
    }
}
