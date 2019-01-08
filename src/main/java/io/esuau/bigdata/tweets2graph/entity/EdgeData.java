package io.esuau.bigdata.tweets2graph.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "edges", schema = "graph")
public class EdgeData {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "source")
    private String source;

    @Column(name = "target")
    private String target;

    @Column(name = "weight")
    private double weight;

}
