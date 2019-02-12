package io.esuau.bigdata.tweets2graph.repository;

import io.esuau.bigdata.tweets2graph.entity.EdgeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EdgeRepository extends JpaRepository<EdgeData, String> {

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EdgeData e WHERE (e.source = :source AND e.target = :target) OR (e.source = :target AND e.target = :source)")
    boolean existsEdgeDataBySourceAndTarget(@Param("source") String source, @Param("target") String target);

}
