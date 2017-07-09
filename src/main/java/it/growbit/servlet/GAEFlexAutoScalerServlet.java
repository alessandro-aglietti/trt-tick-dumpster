package it.growbit.servlet;

import it.growbit.flex.GAEFlexAutoScaler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * Created by name on 09/07/17.
 */
public class GAEFlexAutoScalerServlet extends HttpServlet {

    private static final GAEFlexAutoScaler fas = GAEFlexAutoScaler.singleton();
    private static final Logger log = Logger.getLogger(GAEFlexAutoScalerServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Future<Boolean> start = fas.start();
        log.info("Start call init");
        try {
            start.get();
            log.info("Start call done");
        } catch (InterruptedException e) {
            this.fail("Start call", e, resp);
            return;
        } catch (ExecutionException e) {
            this.fail("Start call", e, resp);
            return;
        }

        fas.stop();
        log.info("Stop call init");
        resp.setStatus(200);
        resp.getOutputStream().println("Detached successful!!!");
    }

    private void fail(String s, Exception e, HttpServletResponse resp) throws IOException {
        log.severe(s + e.getMessage());
        e.printStackTrace();
        resp.setStatus(500);
        resp.getOutputStream().println(e.getMessage());
    }
}
