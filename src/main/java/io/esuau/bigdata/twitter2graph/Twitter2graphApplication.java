package io.esuau.bigdata.twitter2graph;

import io.esuau.bigdata.twitter2graph.controller.TweetReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.File;

@Slf4j
@SpringBootApplication
public class Twitter2graphApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = SpringApplication.run(Twitter2graphApplication.class, args);

		if (args.length == 0 || args[0] == null) {
			throw new Exception("No file provided.");
		}

		File file = new File(args[0]);
		if (!file.exists()) {
			throw new Exception("File " + args[0] + " does not exist.");
		}

		TweetReader tweetReader = (TweetReader) ctx.getBean("tweetReader");
		tweetReader.readFile(file);
	}

}

