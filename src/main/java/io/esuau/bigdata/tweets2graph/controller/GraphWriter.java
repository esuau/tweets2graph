package io.esuau.bigdata.tweets2graph.controller;

import io.esuau.bigdata.tweets2graph.operation.GraphExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Controller
public class GraphWriter {

    private final GraphExtractor graphExtractor;

    @Autowired
    public GraphWriter(GraphExtractor graphExtractor) {
        this.graphExtractor = graphExtractor;
    }

    public void write() {
        graphExtractor.export().addCallback(new ListenableFutureCallback<Boolean>() {
            public void onSuccess(Boolean explosion) {
                log.info("Success.");
            }
            public void onFailure(Throwable thrown) {
                log.error("Failure.");
            }
        });
    }

}
