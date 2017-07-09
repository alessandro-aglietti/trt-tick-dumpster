package it.growbit.flex;

import com.google.appengine.api.ThreadManager;

import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Created by name on 09/07/17.
 */
public class GAEFlexAutoScaler implements Callable<Boolean> {

    private Operations current_operation;

    public enum Operations {START, STOP}

    private static GAEFlexAutoScaler singleton = null;
    private final ExecutorService executor;
    private static final Logger log = Logger.getLogger(GAEFlexAutoScaler.class.getName());

    private GAEFlexAutoScaler() {
        log.info("init executor");
        this.executor = Executors.newCachedThreadPool(ThreadManager.backgroundThreadFactory());
        log.info("init executor done");
    }

    public static GAEFlexAutoScaler singleton() {
        if (singleton == null) {
            log.info("singleton empty, new it!");
            singleton = new GAEFlexAutoScaler();
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

    public Future<Boolean> stop() {
        log.info("stop future");
        this.current_operation = Operations.STOP;
        return this.executor.submit(this);
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

    private Boolean _stop() throws InterruptedException {
        log.info("_stop start sleep");
        Thread.sleep(10 * 1000);
        log.info("_stop end sleep");
        return false;
    }

    private Boolean _start() {
        log.info("_start");
        return false;
    }
}
