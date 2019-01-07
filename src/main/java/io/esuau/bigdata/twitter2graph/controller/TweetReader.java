package io.esuau.bigdata.twitter2graph.controller;

import io.esuau.bigdata.twitter2graph.operation.TweetProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
@Controller
public class TweetReader {

    private final TweetProcessor tweetProcessor;

    @Autowired
    public TweetReader(TweetProcessor tweetProcessor) {
        this.tweetProcessor = tweetProcessor;
    }

    public void readFile(File file) {
        try (FileReader reader = new FileReader(file); BufferedReader bufferedReader = new BufferedReader(reader)) {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                tweetProcessor.processLine(currentLine);
            }
        } catch (IOException e) {
            log.error("Error during file processing.", e);
        }
    }

}
