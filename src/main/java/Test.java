package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.infiauto.datastr.auto.DictionaryAutomaton;
import com.infiauto.datastr.auto.LevenshteinAutomaton;

public class Test {
	public static void main(String[] args) {
	
		ArrayList<String> words = new ArrayList<String>();
		
		try {
			BufferedReader fr = new BufferedReader (new InputStreamReader(new FileInputStream("/home/mech/bktree/wordsEn.txt")));
			
			while (true) {
				String s = fr.readLine();
				if (s == null) break;
				words.add(s);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 15.86 queries per second
		
		DictionaryAutomaton da = new DictionaryAutomaton(words);
		LevenshteinAutomaton leva = LevenshteinAutomaton.generateLevenshtein(2);
		
		while (true) {
			long t = System.currentTimeMillis();
			for (int i=0; i<100; i++)
				leva.recognize("aple", da);
			System.out.println (String.format("%.2f queries per second", 100000.0 / (System.currentTimeMillis() - t)));			
		}
	}
}
