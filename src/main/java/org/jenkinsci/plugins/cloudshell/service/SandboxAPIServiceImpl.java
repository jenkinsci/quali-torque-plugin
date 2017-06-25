package org.jenkinsci.plugins.cloudshell.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jenkins.util.Timer;
import okhttp3.OkHttpClient;
import org.jenkinsci.plugins.cloudshell.Messages;
import org.jenkinsci.plugins.cloudshell.api.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SandboxAPIServiceImpl implements SandboxAPIService{
    private SandboxAPISpec sandboxAPI = null;
    public SandboxAPIServiceImpl(SandboxServiceConnection connection) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connection.connectionTimeoutSec,TimeUnit.SECONDS);
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

    public ResponseData<CreateSandboxResponse> createSandbox(final CreateSandboxRequest req) throws IOException {
        return execute(sandboxAPI.createSandbox(req));
    }

    public ResponseData<CreateSandboxResponse> createSandbox(final CreateSandboxRequest req, String healthCheckService,double timeoutMinutes) throws IOException, TimeoutException, InterruptedException {
        ResponseData<CreateSandboxResponse> res = createSandbox(req);
        if(res.isSuccessful()){
            String sandboxId = res.getData().id;
            waitForService(sandboxId, healthCheckService,timeoutMinutes);
        }
        return res;
    }
    public ResponseData<Void> deleteSandbox(String sandboxId) throws IOException {
        return execute(sandboxAPI.deleteSandbox(sandboxId));
    }
    public void waitForService_temp(final String sandboxId, final String serviceName, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Future<Void> task = null;
        try {
            task = Timer.get().submit(new Callable<Void>() {
                @Override public Void call() throws Exception {
                    while (true)
                    {
                        Sandbox sandbox = getSandbox(sandboxId);
                        if(sandbox != null)
                        {
                            Service service = getService(sandbox, serviceName);
                            if(service != null)
                            {
                                if(healthCheck(service))
                                    return null;
                            }
                        }
                        Thread.sleep(2000);
                    }
                }
            });
            task.get(timeout, unit);
        } catch (Exception x) { // ExecutionException, RejectedExecutionException, CancellationException, TimeoutException, InterruptedException
            if (task != null) {
                task.cancel(true); // in case of TimeoutException especially, we do not want this thread continuing
            }
            throw x;
        }
    }
    public void waitForService(String sandboxId, String serviceName,double timeoutMinutes) throws TimeoutException, IOException, InterruptedException {

        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis()-startTime) < timeoutMinutes*1000*60)
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
        throw new TimeoutException(String.format(Messages.WaitingForSandboxTimeoutError(),timeoutMinutes));
    }
    private Sandbox getSandbox(String sandboxId) throws RuntimeException, IOException {
        ResponseData<Sandbox []> res = this.getSandboxes();
        if(!res.isSuccessful())
            throw new RuntimeException(res.getError());
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

        List<String> addresses = new ArrayList<>();
        addresses.addAll(0,service.addresses);
        addresses.remove("22");
        List<String> check_addresses = new ArrayList<>();
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
            catch (ConnectException ignored)
            {
            }
        }
        return check_addresses.isEmpty();
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
