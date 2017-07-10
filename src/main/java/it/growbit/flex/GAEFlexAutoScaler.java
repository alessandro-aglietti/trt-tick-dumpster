package it.growbit.flex;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.appengine.v1.Appengine;
import com.google.api.services.appengine.v1.AppengineScopes;
import com.google.api.services.appengine.v1.model.Operation;
import com.google.api.services.appengine.v1.model.Version;
import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.apphosting.api.ApiProxy;
import com.google.gson.Gson;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by name on 09/07/17.
 */
public class GAEFlexAutoScaler extends HttpServlet implements Callable<Boolean> {

    private static final String SERVLET_MAPPING_URL_PATTERN = "servlet-mapping-url-pattern";
    private static String servlet_mapping_url_pattern = null;
    private static final String GAE_SERVICE_VERSION = "gae_service_version";
    private String gae_service_version = null;
    private static final String GAE_SERVICE_NAME = "gae_service_name";
    private String gae_service_name = null;
    private Operations current_operation;
    private static Properties props = null;

    public String get_servlet_mapping_url_pattern() {
        if (servlet_mapping_url_pattern == null) {
            /**
             * per qualche motivo la warmup request
             * non si e' fatta viva
             */
            log.warning("Where are you warmup?");
            this.load_properties();
            servlet_mapping_url_pattern = props.getProperty(SERVLET_MAPPING_URL_PATTERN);

        }
        return servlet_mapping_url_pattern;
    }

    private void load_properties() {
        if (this.props == null) {
            props = new Properties();
            if (GAEFlexAutoScaler.class.getResource("/gae-flex-auto-scaler.properties") != null) {
                InputStream is = GAEFlexAutoScaler.class.getResourceAsStream("/gae-flex-auto-scaler.properties");
                try {
                    props.load(is);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                log.warning("gae-flex-auto-scaler.properties not found using defaults");
                props.put(SERVLET_MAPPING_URL_PATTERN, "/gae-flex-auto-scaler");
            }
        }
    }

    public enum Operations {START, STOP}

    private static GAEFlexAutoScaler singleton = null;
    private ExecutorService executor = null;
    private static final Logger log = Logger.getLogger(GAEFlexAutoScaler.class.getName());

    /**
     * Don't use, @see singleton()
     * it's there because
     * Im also a servlet
     */
    public GAEFlexAutoScaler() {
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.info("init with ServletConfig");
        servlet_mapping_url_pattern = config.getInitParameter(SERVLET_MAPPING_URL_PATTERN);
        if (servlet_mapping_url_pattern != null) {
            log.info("servlet_mapping_url_pattern: " + servlet_mapping_url_pattern);
        } else {
            log.severe("servlet_mapping_url_pattern null :(");
        }
    }

    /**
     * @param gae_service_name
     * @param gae_service_version null for default version
     */
    private GAEFlexAutoScaler(String gae_service_name, String gae_service_version) {
        this.gae_service_name = gae_service_name;
        this.gae_service_version = gae_service_version;
        log.info("init executor");
        this.executor = Executors.newCachedThreadPool(ThreadManager.currentRequestThreadFactory());
        log.info("init executor done");
        log.info("servlet_mapping_url_pattern: " + servlet_mapping_url_pattern);
    }

    public static GAEFlexAutoScaler singleton(String gae_service_name, String gae_service_version) {
        if (singleton == null) {
            log.info("singleton empty, new it!");
            singleton = new GAEFlexAutoScaler(gae_service_name, gae_service_version);
        } else {
            log.info("singleton exist use it!");
        }

        return singleton;
    }

    public Future<Boolean> start() {
        log.info("start future");
        this.current_operation = Operations.START;
        return this.executor.submit(this);
    }

    /**
     * Allowed only on manual scaling
     * or basic scaling
     *
     * @return
     */
    public Future<Boolean> stop_as_thread() {
        log.info("stop future");
        this.current_operation = Operations.STOP;
        return this.executor.submit(this);
    }

    /**
     * Using App Engine defalt queue
     */
    public void stop_as_task() {
        log.info("stop no future");
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder
                .withUrl(this.get_servlet_mapping_url_pattern())
                .method(TaskOptions.Method.DELETE)
                .param(GAE_SERVICE_NAME, this.gae_service_name)
                .param(GAE_SERVICE_VERSION, this.gae_service_version)
        );
    }

    @Override
    public Boolean call() throws Exception {
        log.info("_call");
        switch (this.current_operation) {
            case STOP:
                return this._stop();
            case START:
                return this._start();
            default:
                log.info("call no operations matched");
        }
        return false;
    }

    private Boolean _stop() throws InterruptedException, IOException {
        return this.set_gae_serving_status("STOPPED", false);
    }

    private Boolean _start() throws InterruptedException, IOException {
        return this.set_gae_serving_status("SERVING");
    }

    private Boolean set_gae_serving_status(String serving_status) throws IOException, InterruptedException {
        return this.set_gae_serving_status(serving_status, true);
    }

    private Boolean set_gae_serving_status(String serving_status, Boolean wait_untile_done) throws IOException, InterruptedException {
        AppIdentityCredential credential = new AppIdentityCredential(AppengineScopes.all());
        String service_account_name = credential.getAppIdentityService().getServiceAccountName();
        log.info(service_account_name);

        HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
        JsonFactory JSON_FACTORY = new GsonFactory();

        ApiProxy.Environment env = ApiProxy.getCurrentEnvironment();
        Gson gson = new Gson();
        log.info("ENV FROM API PROXY " + gson.toJson(env.getAttributes()));
        /**
         * {appId}.appspot.com
         */
        String default_version_hostname = (String) env.getAttributes().get("com.google.appengine.runtime.default_version_hostname");
        Matcher m = Pattern.compile("([a-z0-9-_]+)\\.appspot\\.com").matcher(default_version_hostname);
        m.find();
        String appsId = m.group(1);
//        for local dev
//        appsId = "growbit-0";

        /**
         * Application name is not set. Call Builder#setApplicationName
         */
        Appengine gae_client = new Appengine.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(appsId).build();

        String servicesId = this.gae_service_name;
        String versionsId = this.gae_service_version;
        Version content = new Version();
        content.setServingStatus(serving_status);
        Operation operation = gae_client.apps().services().versions().patch(appsId, servicesId, versionsId, content).setUpdateMask("servingStatus").execute();
        if (!wait_untile_done) {
            return true;
        }
        String operation_name = operation.getName();
//        JsonParser parser = new JsonParser();
//        InputStream response_content = gae_client.apps().services().versions().patch(appsId, servicesId, versionsId, content).setUpdateMask("servingStatus").executeAsInputStream();
//        JsonObject operation = parser.parse(new InputStreamReader(response_content)).getAsJsonObject();
//        String operation_name = operation.get("name").getAsString();
        /**
         * operation has no getId()
         * we can get from the name in the form of
         * apps/{appsId}/operations/2341b6b7-0110-4888-a857-0c9e0163269d
         */
        m = Pattern.compile("apps\\/" + appsId + "\\/operations\\/([a-z0-9-]+)").matcher(operation_name);
        m.find();
        String operation_id = m.group(1);
        Boolean operation_done;
        do {
            Thread.sleep(2000);
            operation = gae_client.apps().operations().get(appsId, operation_id).execute();
//            operation = parser.parse(new InputStreamReader(gae_client.apps().operations().get(appsId, operation_id).executeAsInputStream())).getAsJsonObject();
//            operation_done = operation.has("done") ? operation.get("done").getAsBoolean() : false;
            operation_done = operation.containsKey("done") ? operation.getDone() : false;
            if (!operation_done) {
                log.info(operation_name + " not done yet for serving status " + content.getServingStatus());
            }
        } while (!operation_done);

        return operation_done;
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.gae_service_name = req.getParameter(GAE_SERVICE_NAME);
        this.gae_service_version = req.getParameter(GAE_SERVICE_VERSION);
        try {
            this._stop();
        } catch (InterruptedException e) {
            log.severe("InterruptedException: " + e.getMessage());
            e.printStackTrace();
        }
        resp.setStatus(200);
    }
}
