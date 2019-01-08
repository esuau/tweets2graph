package io.esuau.bigdata.tweets2graph.repository;

import io.esuau.bigdata.tweets2graph.entity.EdgeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EdgeRepository extends JpaRepository<EdgeData, String> {
}
