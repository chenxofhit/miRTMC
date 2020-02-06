package com.csu.webapp.util;

import org.apache.log4j.Logger;

/**
 * 
 * Sequence 处理类
 * 
 * @author chenx
 * @since 2020-02-06 19:27:11
 * 
 * 
 *
 */
public class SequenceExtractor {

	private static final Logger logger = Logger.getLogger(SequenceExtractor.class);

	public static String getWrappedSequence(String sequence) {
		
		
		String newSequence = sequence;
		String[] lines = sequence.split(System.lineSeparator());

		if(lines.length > 2 || lines.length == 0) {
			logger.warn("Sequence split error ! ");
		}
		else{
			newSequence = lines[0] + System.lineSeparator() + lines[1].substring(1,7);
			logger.info("Splitted sequence: " + newSequence);
		}
		
		return newSequence;
		
	}

	public static String getWrappedSequenceContent(String sequence) {
	
		String content = sequence;
		String[] lines = sequence.split(System.lineSeparator());

		if(lines.length > 2 || lines.length == 0) {
			logger.warn("Sequence split error ! ");
		}
		else{
			content = lines[0].substring(1,lines[0].length());
			logger.info("Sequence content: " + content);
		}
		
		return content;
		
	}
	
}
