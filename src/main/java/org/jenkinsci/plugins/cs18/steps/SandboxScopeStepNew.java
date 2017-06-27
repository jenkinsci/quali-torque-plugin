//package org.jenkinsci.plugins.cs18.steps;

import com.google.gson.Gson;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Util;
import hudson.model.TaskListener;
import jenkins.util.Timer;
import org.jenkinsci.plugins.cs18.Config;
import org.jenkinsci.plugins.cs18.Messages;
import org.jenkinsci.plugins.cs18.PluginConstants;
import org.jenkinsci.plugins.cs18.api.CreateSandboxRequest;
import org.jenkinsci.plugins.cs18.api.CreateSandboxResponse;
import org.jenkinsci.plugins.cs18.api.ResponseData;
import org.jenkinsci.plugins.cs18.api.Sandbox;
import org.jenkinsci.plugins.cs18.service.SandboxAPIService;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by shay-k on 26/06/2017.
 */
//public class SandboxScopeStepNew extends Step {
//
//    private String blueprint;
//
//    @DataBoundConstructor
//    public SandboxScopeStepNew(@Nonnull String blueprint)
//    {
//        this.blueprint = blueprint;
//    }
//
//    @Override
//    public StepExecution start(StepContext stepContext) throws Exception {
//        //return new Execution(stepContext, getBlueprint());
//        return new Execution1(stepContext);
//    }
//
//    public String getBlueprint() {
//        return blueprint;
//    }
//
//    public void setBlueprint(String blueprint) {
//        this.blueprint = blueprint;
//    }
//    public static final class Execution1 extends SynchronousNonBlockingStepExecution{
//
//        protected Execution1(@Nonnull StepContext context) {
//            super(context);
//        }
//
//        @Override
//        protected Object run() throws Exception {
//            TaskListener l = getContext().get(TaskListener.class);
//            l.getLogger().println("creating sandbox...");
//            Thread.sleep(5000);
//            l.getLogger().println("health check...");
//            Thread.sleep(5000);
//
//            getContext().newBodyInvoker().
//                    //withContext(EnvironmentExpander.merge(getContext().get(EnvironmentExpander.class), createEnvironmentExpander())).
//                    withCallback(new Execution.Callback()).
//                    start();
//
//
//            return null;
//        }
//    }
//    public static final class Execution extends AbstractStepExecutionImpl {
//        private static final long serialVersionUID = 1;
//        private final String blueprint;
//        private transient Thread thread;
//
//        public Execution(@Nonnull StepContext context, String blueprint) throws Exception {
//            super(context);
//            this.blueprint = blueprint;
//        }
//
//        @Override
//        public boolean start() throws Exception {
//            doit();
//
//            return false;
//        }
//
////        private EnvironmentExpander createEnvironmentExpander()
////        {
////            final EnvVars env = new EnvVars();
////
////            String sandboxJson = "{}";// new Gson().toJson(sandbox).toString();
////            env.override(PluginConstants.SANDBOX_ENVVAR, sandboxJson);
////            EnvironmentExpander expander = new EnvironmentExpander() {
////                @Override
////                public void expand(@Nonnull EnvVars envVars) throws IOException, InterruptedException {
////                    envVars.overrideAll(env);
////                }
////            };
////            return expander;
////        }
//
//
//        @Override
//        public void stop(@Nonnull Throwable throwable) throws Exception {
//            if (thread!= null) {
//                thread.interrupt();
//            }
//            getContext().onFailure(throwable);
//        }
//
//        @Override
//        public String getStatus() {
//            return "status";
//        }
//
//
////        private void createSandbox() throws Exception {
////            CreateSandboxRequest req = new CreateSandboxRequest(blueprint,stage);
////            ResponseData<CreateSandboxResponse> res = sandboxAPIService.createSandbox(req);
////            if(!res.isSuccessful()){
////                throw new AbortException(res.getMessage());
////            }
////
////            sandboxId = res.getData().id;
////            if(this.serviceNameForHealthCheck != null)
////                sandboxAPIService.waitForService(sandboxId, this.serviceNameForHealthCheck,10);
////
////            ResponseData<Sandbox[]> sandboxesRes = sandboxAPIService.getSandboxes();
////            if(!sandboxesRes.isSuccessful()) {
////                throw new AbortException(res.getMessage());
////            }
////            for(Sandbox _sandbox :sandboxesRes.getData()){
////                if (_sandbox.id.equals(sandboxId)){
////                    sandbox = _sandbox;
////                    return true;
////                }
////            }
////            endSandbox(sandboxId,sandboxAPIService,getContext());
////            throw new AbortException(String.format(Messages.SandboxNotExistsError(),sandboxId));
////        }
//
//        private void doit() {
//            TaskListener listener;
//            try {
//                listener = getContext().get(TaskListener.class);
//            } catch (Exception x) {
//                listener = TaskListener.NULL;
//            }
//            listener.getLogger().println("Sleeping for");
//            thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        TaskListener l = getContext().get(TaskListener.class);
//                        l.getLogger().println("creating sandbox...");
//                        Thread.sleep(5000);
//                        l.getLogger().println("health check...");
//                        Thread.sleep(5000);
//
////                        getContext().newBodyInvoker().
////                                //withContext(EnvironmentExpander.merge(getContext().get(EnvironmentExpander.class), createEnvironmentExpander())).
////                                withCallback(new Execution.Callback()).
////                                start();
//
//
//                        getContext().onSuccess(null);
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        getContext().onFailure(e);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        getContext().onFailure(e);
//                    }
//                }
//            });
//            thread.start();
//        }
//        private static class Callback extends BodyExecutionCallback.TailCall {
//            private static final long serialVersionUID = 1;
//            Callback() {
//
//            }
//            @Override
//            protected void finished(StepContext stepContext) throws Exception {
////                endSandbox(sandboxId, sandboxAPIService, stepContext);
//            }
//
//        }
//
//    }
//    @Extension
//    public static final class DescriptorImpl extends StepDescriptor {
//
//        @Override public String getFunctionName() {
//            return "withIt";
//        }
//
//        @Override public String getDisplayName() {
//            return "withIt";
//        }
//
//        @Override public Set<? extends Class<?>> getRequiredContext() {
//            return Collections.singleton(TaskListener.class);
//        }
//    }
//
//}
