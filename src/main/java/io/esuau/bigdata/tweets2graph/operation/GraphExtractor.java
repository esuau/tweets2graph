package io.esuau.bigdata.tweets2graph.operation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

            String extension = output.getName().substring(output.getName().lastIndexOf("."));
            switch (extension) {
                case ".json":
                    jsonWrite(out);
                    break;
                case ".graphml":
                    graphMLWrite(out);
                    break;
            }

            stopwatch.stop();
            log.info("Exporting took {} ms.", stopwatch.getTime(TimeUnit.MILLISECONDS));

            return AsyncResult.forValue(true);
        } catch (Exception ex) {
            log.error("Failed to export data", ex);
            return AsyncResult.forValue(false);
        }
    }

    private void jsonWrite(FileOutputStream out) throws IOException {
        ObjectWriter writer = jsonMapper.writer().withDefaultPrettyPrinter();
        try (SequenceWriter sequenceWriter = writer.writeValues(out)) {
            sequenceWriter.init(true);
            int edgePage = 0;
            while (true) {
                Pageable pageable = PageRequest.of(edgePage++, 100);
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
                Pageable pageable = PageRequest.of(nodePage++, 100);
                List<NodeData> batch = nodeRepository.findAll(pageable).getContent();
                for (NodeData nodeData : batch) {
                    sequenceWriter.write(new Node(nodeData));
                }
                if (batch.isEmpty()) {
                    break;
                }
            }
        }
    }

    private void graphMLWrite(FileOutputStream out) throws XMLStreamException {
        XMLStreamWriter writer = XMLStreamWriterFactory.create(out);
        try {
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement("graphml");
            writer.writeStartElement("graph");
            int nodePage = 0;
            while (true) {
                Pageable pageable = PageRequest.of(nodePage++, 100);
                List<NodeData> batch = nodeRepository.findAll(pageable).getContent();
                for (NodeData nodeData : batch) {
                    writer.writeEmptyElement("node");
                    writer.writeAttribute("id", nodeData.getId());
                }
                if (batch.isEmpty()) {
                    break;
                }
            }
            int edgePage = 0;
            while (true) {
                Pageable pageable = PageRequest.of(edgePage++, 100);
                List<EdgeData> batch = edgeRepository.findAll(pageable).getContent();
                for (EdgeData edgeData : batch) {
                    writer.writeEmptyElement("edge");
                    writer.writeAttribute("source", edgeData.getSource());
                    writer.writeAttribute("target", edgeData.getTarget());
                }
                if (batch.isEmpty()) {
                    break;
                }
            }
            writer.writeEndElement();
            writer.writeEndElement();
            writer.writeEndDocument();
        } finally {
            writer.flush();
            writer.close();
        }
    }

}
