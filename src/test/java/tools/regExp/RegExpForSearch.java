package tools.regExp;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import tools.file.FileUtils;

public class RegExpForSearch {
	
	@Test
	public void simplePhoneMatchTest() throws Exception {
	        String phones1 = 
	              "Justin's phone number 0939-100391\n" + 
	              "momor's phone number 0939-666888\n"; 
	        
	        Pattern pattern = Pattern.compile(".*0939-\\d{6}"); 
	        Matcher matcher = pattern.matcher(phones1); 

	        while(matcher.find()) { 
	            System.out.println(matcher.group()); 
	        } 
	        
	        String phones2 = 
	             "caterpillar's phone number 0952-600391\n" + 
	             "bush's phone number 0939-550391"; 
	        
	        matcher = pattern.matcher(phones2); 

	        while(matcher.find()) { 
	            System.out.println(matcher.group()); 
	        } 
	}
	
	@Test
	public void testURLMatch() throws Exception {
		String content = FileUtils.readFile("/RawMaterials.txt");
		Pattern pattern; 
	    Matcher matcher; 
	    
	    pattern = Pattern.compile("<(a|image).*href=\"http://www\\.ford\\.com.*>.*</(a|image)>"); 
	    matcher = pattern.matcher(content);
	    while(matcher.find()) { 
            System.out.println(matcher.group()); 
        } 
	}

}
