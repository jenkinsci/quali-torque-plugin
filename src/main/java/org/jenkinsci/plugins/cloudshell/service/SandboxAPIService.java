package org.jenkinsci.plugins.cloudshell.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import org.jenkinsci.plugins.cloudshell.api.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by shay-k on 21/06/2017.
 */
public class SandboxAPIService {
    private final SandboxServiceConnection connection;
    private final SandboxAPI sandboxAPI;

    public SandboxAPIService(SandboxServiceConnection connection) {
        this.connection = connection;
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connection.connectionTimeoutSec,TimeUnit.SECONDS);
        OkHttpClient client= builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getURL())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();


        sandboxAPI = retrofit.create(SandboxAPI.class);
    }

    public ResponseData<Sandbox []> getSandboxes() throws IOException {
        try {
            Call<Sandbox[]> sandboxes = sandboxAPI.getSandboxes();
            ResponseData<Sandbox[]> responseData = parseResponse(sandboxes.execute());
            return responseData;
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

    public ResponseData<CreateSandboxResponse> createSandbox(CreateSandboxRequest req) throws IOException {
        try {
            Call<CreateSandboxResponse> call = sandboxAPI.createSandbox(req);
            ResponseData<CreateSandboxResponse> responseData = parseResponse(call.execute());
            return responseData;
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }
    public ResponseData<Void> deleteSandbox(String sandboxId){
        try {
            Call<Void> call = sandboxAPI.deleteSandbox(sandboxId);
            return parseResponse(call.execute());
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

    public void waitForService(String sandboxId, String serviceName,int timeoutSec) throws Exception {
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis()-startTime) < timeoutSec*1000)
        {
            Sandbox sandbox = getSandbox(sandboxId);
            if(sandbox != null)
            {
                Service service = getService(sandbox, serviceName);
                if(service != null)
                {
                    if(healthCheck(service))
                       return;
                }
            }
            Thread.sleep(2000);
        }
        throw new Exception(String.format("waiting for sandbox timed out after %1s sec",timeoutSec));
    }


    private Sandbox getSandbox(String sandboxId) throws Exception {
        ResponseData<Sandbox []> res = this.getSandboxes();
        if(!res.isSuccessful())
            throw new Exception(res.getError());
        for (Sandbox sandbox: res.getData())
        {
            if(sandbox.id.equals(sandboxId))
                return sandbox;
        }
        return null;
    }

    private Service getService(Sandbox sandbox, String serviceName) {
        for(Service service:sandbox.services)
        {
            if(service.name.equals(serviceName))
                return service;
        }
        return null;
    }

    private boolean healthCheck(Service service) throws IOException {

        List<String> addresses = new ArrayList<String>();
        addresses.addAll(0,service.addresses);
        addresses.remove("22");
        List<String> check_addresses = new ArrayList<String>();
        check_addresses.addAll(0,addresses);

        for (String address :addresses)
        {
            HttpURLConnection connection = (HttpURLConnection) new URL(address).openConnection();
            connection.setRequestMethod("HEAD");
            try {
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    check_addresses.remove(address);
                }
            }
            catch (ConnectException ex)
            {
                continue;
            }
        }
        return check_addresses.isEmpty();
    }

    private String getURL(){
        return String.format("http://%1$s:%2$s",connection.address,connection.port);
    }

    public static <T> ResponseData<T> parseResponse(final Response<T> response) throws IOException {
        String message = response.message();
        if (!response.isSuccessful()) {
            final String err = response.errorBody().string();
            return ResponseData.error(response.code(),err).setMessage(message);
        }
        return ResponseData.ok(response.body(),response.code()).setMessage(message);
    }
    public static <T> ResponseData<T> createErrorResponse(Exception e) {
        String errorMessage = e.toString();
        return ResponseData.error(-1,errorMessage);
    }
}
