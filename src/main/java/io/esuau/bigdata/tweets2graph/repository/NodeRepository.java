package io.esuau.bigdata.tweets2graph.repository;

import io.esuau.bigdata.tweets2graph.entity.NodeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository extends JpaRepository<NodeData, String> {

    @Modifying
    @Query("UPDATE NodeData n SET n.score = :score WHERE n.id = :id")
    void setScore(@Param("id") String id, @Param("score") double score);

}
