package io.esuau.bigdata.twitter2graph.definition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeData {
    private String id;
    private String name;
    private double score;
}
