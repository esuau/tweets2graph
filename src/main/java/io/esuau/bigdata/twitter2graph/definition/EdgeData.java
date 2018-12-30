package io.esuau.bigdata.twitter2graph.definition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EdgeData {
    private String id;
    private String source;
    private String target;
    private double weight = 0.012590342;
}
