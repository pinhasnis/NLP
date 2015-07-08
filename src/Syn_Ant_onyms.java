
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by pinhas on 08/07/2015.
 */
public class Syn_Ant_onyms {

    //The document will contain the data of synonyms and antonyms of the "word"
    private Document d;
    private String word;
    private int thresholdSize;

    /**
     *
     * @param word The word to search in thesaurus.com site.
     * @throws IOException in case of failure to connect to www.thesaurus.com
     */
    public Syn_Ant_onyms(String word) throws IOException {
        thresholdSize = Integer.MAX_VALUE;
        this.word = word;
        Connection con = Jsoup.connect("http://www.thesaurus.com/browse/" + word + "?s=t");

        d = con.get();
    }

    public Syn_Ant_onyms(String word, int thresholdSize) throws IOException {
        this.thresholdSize = thresholdSize;
        this.word = word;
        Connection con = Jsoup.connect("http://www.thesaurus.com/browse/" + word + "?s=t");
        d = con.get();
    }

    /**
     *
     * @return The word from the constructor
     */
    public String getWord(){
        return word;
    }

    /**
     *
     * @param thresholdSize The upper threshold of words in the lists.
     */
    public void setMaxWordThreshold(int thresholdSize){
        this.thresholdSize = thresholdSize;
    }

    /**
     *
     * @return The synonyms of the "word" (no more the the "thresholdSize" value)
     */
    public ArrayList<String> getSynonyms() {
        ArrayList<String> list;
        String synonyms = d.getElementById("filters-0").getElementsByClass("relevancy-list").html();
        list = findwords(synonyms);
        return list;
    }

    /**
     *
     * @return The Antonyms of the "word" (no more the the "thresholdSize" value)
     */
    public ArrayList<String> getAntonyms() {
        ArrayList<String> list;
        String antonyms = d.getElementsByClass("list-holder").html();
        list = findwords(antonyms);
        return list;
    }


    /**
     *
     * @param html The section in the document with the relevant data.
     * @return A list of all the words from the relevant data.
     */
    private ArrayList<String> findwords(String html){
        ArrayList<String> list = new ArrayList<>();
        String key = "\"text\">";
        int step = key.length();
        char endChar = '<';
        String [] lines = html.split("\n");
        for (String l : lines){
            int endLine = l.length() - step;
            for (int i = 0; i <= endLine; i++) {
                if (key.equals(l.substring(i,i+step))){
                    int j = i + step;
                    while(l.charAt(j++) != endChar){}
                    list.add(l.substring(i+step,j-1));
                    i = endLine + 1;
                }
            }
            if(list.size() == thresholdSize){
                break;
            }
        }
        return list;
    }
    public static void main(String [] args) throws IOException {
        Syn_Ant_onyms w = new Syn_Ant_onyms("bad",4);
        ArrayList<String> ant = w.getAntonyms();
        ArrayList<String> syn = w.getSynonyms();
        System.out.println("Synonyms: "+syn);
        System.out.println("Antonyms: "+ant);
    }
}
