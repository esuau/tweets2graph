package io.esuau.bigdata.tweets2graph;

import io.esuau.bigdata.tweets2graph.controller.GraphWriter;
import io.esuau.bigdata.tweets2graph.controller.TweetReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.File;

@Slf4j
@SpringBootApplication
public class Twitter2graphApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = SpringApplication.run(Twitter2graphApplication.class);

		if (args.length < 2) {
			throw new Exception("No input file and no output path provided.");
		} else if (args[0] == null) {
			throw new Exception("No input file provided.");
		} else if (args[1] == null) {
			throw new Exception("No output path provided.");
		}

		File input = new File(args[0]);
		if (!input.exists()) {
			throw new Exception("File " + args[0] + " does not exist.");
		}

		File output = new File(args[1]);
		if (!output.getParentFile().exists()) {
			throw new Exception("Invalid output path: " + args[1] + ".");
		}

		TweetReader tweetReader = (TweetReader) ctx.getBean("tweetReader");
		tweetReader.readFile(input);

		GraphWriter graphWriter = (GraphWriter) ctx.getBean("graphWriter");
		graphWriter.write(output);
	}

}

