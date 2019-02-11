package io.esuau.bigdata.tweets2graph.operation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import io.esuau.bigdata.tweets2graph.entity.Edge;
import io.esuau.bigdata.tweets2graph.entity.EdgeData;
import io.esuau.bigdata.tweets2graph.entity.Node;
import io.esuau.bigdata.tweets2graph.entity.NodeData;
import io.esuau.bigdata.tweets2graph.repository.EdgeRepository;
import io.esuau.bigdata.tweets2graph.repository.NodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class GraphExtractor {

    private final EdgeRepository edgeRepository;
    private final NodeRepository nodeRepository;

    private ObjectMapper jsonMapper = new ObjectMapper();

    @Autowired
    public GraphExtractor(EdgeRepository edgeRepository, NodeRepository nodeRepository) {
        this.edgeRepository = edgeRepository;
        this.nodeRepository = nodeRepository;
    }

    @Async
    public ListenableFuture<Boolean> export(File output) {
        try (FileOutputStream out = new FileOutputStream(output)) {

            StopWatch stopwatch = StopWatch.createStarted();

            ObjectWriter writer = jsonMapper.writer().withDefaultPrettyPrinter();

            try (SequenceWriter sequenceWriter = writer.writeValues(out)) {
                sequenceWriter.init(true);

                int edgePage = 0;
                while (true) {
                    Pageable pageable = new PageRequest(edgePage++, 100);
                    List<EdgeData> batch = edgeRepository.findAll(pageable).getContent();
                    for (EdgeData edgeData : batch) {
                        sequenceWriter.write(new Edge(edgeData));
                    }
                    if (batch.isEmpty()) {
                        break;
                    }
                }
                int nodePage = 0;
                while (true) {
                    Pageable pageable = new PageRequest(nodePage++, 100);
                    List<NodeData> batch = nodeRepository.findAll(pageable).getContent();
                    for (NodeData nodeData : batch) {
                        sequenceWriter.write(new Node(nodeData));
                    }
                    if (batch.isEmpty()) {
                        break;
                    }
                }

            }

            stopwatch.stop();
            log.info("Exporting took {} seconds", stopwatch.getTime(TimeUnit.MILLISECONDS));

            return AsyncResult.forValue(true);
        } catch (Exception ex) {
            log.error("Failed to export data", ex);
            return AsyncResult.forValue(false);
        }
    }

}
