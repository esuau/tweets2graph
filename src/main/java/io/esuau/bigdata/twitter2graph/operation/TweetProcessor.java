package io.esuau.bigdata.twitter2graph.operation;

import io.esuau.bigdata.twitter2graph.entity.Edge;
import io.esuau.bigdata.twitter2graph.entity.EdgeData;
import io.esuau.bigdata.twitter2graph.entity.Node;
import io.esuau.bigdata.twitter2graph.entity.NodeData;
import io.esuau.bigdata.twitter2graph.repository.EdgeRepository;
import io.esuau.bigdata.twitter2graph.repository.NodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.User;

import java.util.UUID;

@Slf4j
@Service
public class TweetProcessor {

    private final EdgeRepository edgeRepository;
    private final NodeRepository nodeRepository;

    @Autowired
    public TweetProcessor(EdgeRepository edgeRepository, NodeRepository nodeRepository) {
        this.edgeRepository = edgeRepository;
        this.nodeRepository = nodeRepository;
    }

    public void processLine(String tweetStr) {
        Status status = null;
        try {
            status = TwitterObjectFactory.createStatus(tweetStr);
        } catch (TwitterException e) {
            log.error("Could not extract tweet from string.", e);
        }
        if (status != null) {
            processTweet(status);
        }
    }

    private void processTweet(Status status) {
        User user = status.getUser();
        if (!nodeRepository.existsById(String.valueOf(user.getId()))) {
            this.createNode(user);
        }

        if (status.isRetweet() || status.getQuotedStatus() != null) {
            User referencedUser = null;
            if (status.isRetweet()) {
                referencedUser = status.getRetweetedStatus().getUser();
            } else if (status.getQuotedStatus() != null) {
                referencedUser = status.getQuotedStatus().getUser();
            }
            if (!nodeRepository.existsById(String.valueOf(referencedUser.getId()))) {
                this.createNode(referencedUser);
            }
            createEdge(user.getId(), referencedUser.getId());
        }
        if (status.getInReplyToUserId() > 0L) {
            createEdge(user.getId(), status.getInReplyToUserId());
        }
    }

    private void createNode(User user) {
        NodeData nodeData = new NodeData();
        nodeData.setId(String.valueOf(user.getId()));
        nodeData.setName(user.getName());
        nodeData.setScore(user.getFollowersCount() / 100000.0);
        nodeRepository.save(nodeData);
    }

    private void createEdge(long sourceUserId, long targetUserId) {
        EdgeData edgeData = new EdgeData(UUID.randomUUID().toString(), String.valueOf(sourceUserId), String.valueOf(targetUserId), 0.5);
        edgeRepository.save(edgeData);
    }

}
