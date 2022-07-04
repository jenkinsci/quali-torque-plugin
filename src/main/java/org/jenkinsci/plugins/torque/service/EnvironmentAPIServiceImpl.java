package org.jenkinsci.plugins.torque.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import org.jenkinsci.plugins.torque.api.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class EnvironmentAPIServiceImpl implements EnvironmentAPIService {
    private EnvironmentAPISpec sandboxAPI;
    private EnvironmentServiceConnection connection;
    public EnvironmentAPIServiceImpl(EnvironmentServiceConnection connection) {
        this.connection = connection;

        Gson gson = new GsonBuilder()
                .create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connection.connectionTimeoutSec,TimeUnit.SECONDS);
        builder.readTimeout(connection.readTimeoutSec, TimeUnit.SECONDS);

        OkHttpClientBuilderExtensions.injectHeader(builder,
                "User-Agent", "Torque-Plugin-Jenkins/" + VersionUtils.PackageVersion);

        OkHttpClient client= builder.build();

        String baseUrl = String.format("%1$s",connection.address);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();


        sandboxAPI = retrofit.create(EnvironmentAPISpec.class);
    }

    public ResponseData<EnvironmentResponse> getEnvironmentById(String spaceName, String environmentId) throws RuntimeException, IOException {
        return execute(sandboxAPI.getEnvironmentById(this.connection.getAuthorizationHeader(), spaceName, environmentId));
    }

    public ResponseData<CreateEnvResponse> createEnvironment(String spaceName, final CreateSandboxRequest req) throws IOException {
        return execute(sandboxAPI.createEnvironment(this.connection.getAuthorizationHeader(), spaceName, req));
    }

    public ResponseData<Void> deleteEnvironment(String spaceName, String environmentId) throws IOException {
        return execute(sandboxAPI.deleteEnvironment(this.connection.getAuthorizationHeader(), spaceName, environmentId));
    }

    private static <T> ResponseData<T> parseResponse(final Response<T> response) throws IOException {
        String message = response.message();
        if (!response.isSuccessful()) {
            final String err = response.errorBody().string();
            return ResponseData.error(response.code(),err).setMessage(message);
        }
        String rawBodyJson  = new GsonBuilder().setPrettyPrinting().create().toJson(response.body());
        return ResponseData.ok(response.body(),response.code(), rawBodyJson).setMessage(message);
    }
    private static <T> ResponseData<T> createErrorResponse(Exception e) {
        String errorMessage = e.toString();
        return ResponseData.error(-1,errorMessage);
    }
    public <T> ResponseData<T> execute(Call<T> call) throws IOException {
        return parseResponse(call.execute());
    }
}
