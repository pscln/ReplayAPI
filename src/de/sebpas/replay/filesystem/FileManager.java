package de.sebpas.replay.filesystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.sebpas.replay.ReplaySystem;

public class FileManager {
	private String fileContent = "";
	private File file;
	private PrintWriter writer;
	public FileManager(){
		
	}
	
	public synchronized boolean save(){
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("YY-MM-D-H-m-s");
		file = new File("plugins/Replays/", format.format(date) + ".rpl");
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e1) {
				ReplaySystem.sendBroadcastError(e1.getMessage());
				e1.printStackTrace();
			}
		try {
			writer = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			ReplaySystem.sendBroadcastError(e.getMessage());
			e.printStackTrace();
			return false;
		}
		writer.print(fileContent);
		System.out.println("[Replay] Saving..." + fileContent);
		writer.flush();
		writer.close();
		return true;
	}
	public synchronized List<String> readFile(String name){
		List<String> rtn = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("plugins/Replays/", name)));
			String t;
			while((t = reader.readLine()) != null)
				rtn.add(t); 
			reader.close();
			return rtn;
		} catch (FileNotFoundException e) {
			ReplaySystem.sendBroadcastError(e.getMessage());
		} catch (IOException e) {
			ReplaySystem.sendBroadcastError(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	public void reset(){
		this.fileContent = "";
	}
	public void appendString(String s){
		fileContent += s + "\n";
	}
}
