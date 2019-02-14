package io.esuau.bigdata.tweets2graph.operation;

import io.esuau.bigdata.tweets2graph.entity.EdgeData;
import io.esuau.bigdata.tweets2graph.entity.NodeData;
import io.esuau.bigdata.tweets2graph.repository.EdgeRepository;
import io.esuau.bigdata.tweets2graph.repository.NodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.*;

import javax.transaction.Transactional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
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
        this.createNode(user);

        if (status.isRetweet() || status.getQuotedStatus() != null) {
            User referencedUser = null;
            if (status.isRetweet()) {
                referencedUser = status.getRetweetedStatus().getUser();
            } else if (status.getQuotedStatus() != null) {
                referencedUser = status.getQuotedStatus().getUser();
            }
            if (referencedUser != null) {
                this.createNode(referencedUser);
                createEdge(user.getScreenName(), referencedUser.getScreenName());
            }
        }
        if (status.getUserMentionEntities() != null && status.getUserMentionEntities().length > 0) {
            for (UserMentionEntity userMentionEntity : status.getUserMentionEntities()) {
                createNode(userMentionEntity);
                createEdge(user.getScreenName(), userMentionEntity.getScreenName());
            }
        }
    }

    private void createNode(User user) {
        if (!nodeRepository.existsById(user.getScreenName())) {
            NodeData nodeData = new NodeData();
            nodeData.setId(user.getScreenName());
            nodeData.setName(user.getName());
            nodeData.setScore(user.getFollowersCount());
            nodeRepository.save(nodeData);
        } else {
            if (nodeRepository.getOne(user.getScreenName()).getScore() == 0) {
                nodeRepository.setScore(user.getScreenName(), user.getFollowersCount());
            }
        }
    }

    private void createNode(UserMentionEntity user) {
        if (!nodeRepository.existsById(user.getScreenName())) {
            NodeData nodeData = new NodeData();
            nodeData.setId(user.getScreenName());
            nodeData.setName(user.getName());
            nodeRepository.save(nodeData);
        }
    }

    private void createEdge(String sourceUserId, String targetUserId) {
        if (!sourceUserId.equals(targetUserId) && !edgeRepository.existsEdgeDataBySourceAndTarget(sourceUserId, targetUserId)) {
            EdgeData edgeData = new EdgeData(UUID.randomUUID().toString(), sourceUserId, targetUserId, 0.5);
            edgeRepository.save(edgeData);
        }
    }

}
