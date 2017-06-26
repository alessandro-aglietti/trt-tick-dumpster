package it.growbit.servlet;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.*;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import it.growbit.model.trt.Trades;
import it.growbit.model.trt.TradesMetaHref;
import org.apache.http.client.utils.URIBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class CronHandler extends HttpServlet {

    private static final Logger log = Logger.getLogger(CronHandler.class.getName());

    static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
    static final JsonFactory JSON_FACTORY = new GsonFactory();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String request_path = req.getPathInfo();
        switch (request_path) {
            case "/hourly":
            default:
                DateTime after = new DateTime();
                after = after.minusHours(1);
                this.trt_dump(after);
        }
        PrintWriter out = resp.getWriter();
        out.println("Hello, world");
    }

    private void trt_dump(DateTime after) {
        String dft_patter = "yyyy-MM-dd'T'HH:mm:ssZZ";
        DateTimeFormatter dtf = DateTimeFormat.forPattern(dft_patter);
        String after_str = after.toString(dtf);
        log.info("after_str " + after_str);

        URIBuilder uri_template = new URIBuilder()
                .setScheme("https")
                .setHost("api.therocktrading.com")
                .setPath("/v1/funds/BTCEUR/trades")
                .setParameter("after", after_str)
                .setParameter("per_page", "200")
                .setParameter("page", "1");

        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) {
                        request.setParser(new JsonObjectParser(JSON_FACTORY));
                    }
                });

        Trades trades;
        TradesMetaHref next = null;
        do {
            if (next != null && next.getPage() != null) {
                uri_template.setParameter("page", next.getPage().toString());
            }

            URI uri;
            try {
                uri = uri_template.build();
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return;
            }

            GenericUrl generic_url = new GenericUrl(uri);

            HttpRequest request;
            try {
                request = requestFactory.buildGetRequest(generic_url);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            try {
                trades = request.execute().parseAs(Trades.class);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            trades.persist();

            next = trades.getMeta().getNext();
        } while (next != null && next.getPage() != null);

        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/tasks/daily-hour-avg").method(TaskOptions.Method.POST));
    }
}