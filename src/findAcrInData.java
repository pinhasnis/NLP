import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class findAcrInData {

	public static String path = "C:\\Users\\pinhas\\Desktop\\nlp ex2\\";
	public final static boolean allWord = true; 
	public static final ArrayList<Character> abc = new ArrayList<>();

	public static void main(String[] args) {
		fillABC();
		HashMap<String, String> acList = new HashMap<>();
		HashMap<String, HashMap<String, Integer>> acr = readAcr(path + "DataSet.txt", acList);
		ArrayList<File> filesList = new ArrayList<>();
		listFilesForFolder(new File(path), filesList);
		ArrayList<location> listW = findAcr(acList, acr.keySet(), filesList, allWord);
		ArrayList<location> listAc = findAcr(acList, acr.keySet(), filesList, !allWord);
		//showAcr(acr);
		System.out.println(listW.size());
		System.out.println(listAc.size());


	}

	private static void fillABC() {
		for (char i = 'a'; i <= 'z'; i++) {
			abc.add(i);
		}
		for (char i = 'A'; i <= 'Z'; i++) {
			abc.add(i);
		}
		System.out.println();
	}

	private static ArrayList<location> findAcr(
			HashMap<String, String> wordNacr, Set<String> acr,
			ArrayList<File> filesList, boolean allWord) {
		ArrayList<location> list = new ArrayList<>();

		for(File f : filesList){
			int lineN = 0;
			try (BufferedReader br = new BufferedReader(new FileReader(f.getPath())))
			{
				String line;
				while ((line = br.readLine()) != null) {
					lineN++;
					if(allWord){
						for (String s : wordNacr.keySet()) {
							if(contains(line, s)){
								location l = new location(f.getPath(),lineN,s,wordNacr.get(s)); 
								list.add(l);
							}
						}
					}
					else{
						for (String s : acr) {
							if(contains(line, s)){
								location l = new location(f.getPath(),lineN,"?",s); 
								list.add(l);
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		return list;
	}

	private static boolean contains(String line, String s) {
		boolean contain = false;
		int end = line.length() - s.length();
		if(line.length() >= s.length()){
			if(line.substring(0,s.length()).equals(s)){
				if(line.length() > s.length()){
					if(!abc.contains(line.charAt(s.length()))){
						contain = true;
					}
				}
				else{
					contain = true;
				}
			}
			if(!contain){
				if(line.substring(end,line.length()).equals(s)){
					if(line.length() > s.length()){
						if(!abc.contains(line.charAt(end-1))){
							contain = true;
						}
					}
					else{
						contain = true;
					}	
				}
				if(!contain){
					for(int i = 1; i < end; i++){
						if(line.substring(i,i+s.length()).equals(s)){
							if(!abc.contains(line.charAt(i-1)) && !abc.contains(line.charAt(i + s.length()))){
								contain = true;
								i = end;
							}
						}
					}
				}
			}
		}
		return contain;
	}

	private static void showAcr(HashMap<String, HashMap<String, Integer>> acr) {
		System.out.println("Anonymized Clinical Abbreviations And Acronyms Size: "+acr.size());
		for(String s : acr.keySet()){
			System.out.println("-----------------------------------------------");
			System.out.println(s + " - Different meanings = "+ acr.get(s).size());
			int count = 1;
			for (String st : acr.get(s).keySet()) {
				System.out.println("("+(count++)+") "+st+" - Count = "+acr.get(s).get(st));
			}
		}

		System.out.println("-----------------------------------------------");
		System.out.println();
	}

	public static void listFilesForFolder(final File folder, ArrayList<File> files) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry, files);
			} else {
				if(fileEntry.getName().endsWith(".xml") || fileEntry.getName().endsWith(".txt")){
					files.add(fileEntry);
				}
			}
		}
	}

	public static HashMap<String, HashMap<String, Integer>> readAcr(String path, HashMap<String, String> allAc){
		HashMap<String, HashMap<String, Integer>> map = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(path)))
		{
			String line;
			while ((line = br.readLine()) != null) {
				String[] sep = line.split("\\|");
				if(map.get(sep[0]) == null){
					map.put(sep[0], new HashMap<String, Integer>());
				}
				if(map.get(sep[0]).get(sep[1]) == null){
					allAc.put(sep[1], sep[0]);
					map.get(sep[0]).put(sep[1], 1);
				}
				else{
					map.get(sep[0]).put(sep[1], map.get(sep[0]).get(sep[1])+1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return map;
	}

}
class location{
	private String fileName;
	private String word;
	private String acWord;
	private int lineNumber;

	public location(String fileName, int line, String word, String acWord){
		this.fileName = fileName;
		this.word = word;
		this.acWord = acWord;
		this.lineNumber = line;
	}

	public int getLine(){
		return lineNumber;
	}
	public String getFileName(){
		return fileName;
	}
	public String getWord(){
		return word;
	}
	public String getacWord(){
		return acWord;
	}
	public void printLocation(){
		System.out.println("------------------------------------");
		System.out.println("File Name: "+fileName);
		System.out.println("Line: "+lineNumber);
		System.out.println("Word found: "+acWord +" - "+ word);
	}
}