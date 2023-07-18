
// tested for lucene 7.7.2 and jdk13
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;


/**
 *
 * @author Tonia Kyriakopoulou
 */
public class SearcherProject{
    public final int TEXTS_RETRIEVED = 50;
    public final String MY_ANSWERS_FILENAME = "myResults50.txt";

    public SearcherProject(){
        try{
            String indexLocation = ("index"); //define where the index is stored
            String field = "contents"; //define which field will be searched            
            
            //Access the index using indexReaderFSDirectory.open(Paths.get(index))
            IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation))); //IndexReader is an abstract class, providing an interface for accessing an index.
            IndexSearcher indexSearcher = new IndexSearcher(indexReader); //Creates a searcher searching the provided index, Implements search over a single IndexReader.
            indexSearcher.setSimilarity(new BM25Similarity());
            
            //Search the index using indexSearcher
            search(indexSearcher, field);
            
            //Close indexReader
            indexReader.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Searches the index given a specific user query.
     */
    private void search(IndexSearcher indexSearcher, String field) throws IOException{
        try{
            // define which analyzer to use for the normalization of user's query
            Analyzer analyzer = new EnglishAnalyzer();
            
            // create a query parser on the field "contents"
            QueryParser parser = new QueryParser(field, analyzer);
            
            // read user's query from stdin
            FileWriter file = new FileWriter(MY_ANSWERS_FILENAME);
            //Queries
            String[] queriesStrings = new String[]{"multimodal travel services", "Big Data for Mobility", "European logistics applications", "Architectures for Big Data Analytics", "Architecture for Industrial IoT",
        "Mobility-as-a-Service tools", "fragmentation of IoT through federation ", "Seamless Efficient European Travelling", "cross-domain orchestration of services", "communal organisation of network infrastucture"};
            
            // parse the query according to QueryParser
            for (int i = 1; i < queriesStrings.length + 1; i++) {
                Query query = parser.parse(queriesStrings[i-1]);
                System.out.println("Searching for: " + query.toString(field));
                
                // search the index using the indexSearcher
                TopDocs results = indexSearcher.search(query, 100);
                ScoreDoc[] hits = results.scoreDocs;
                long numTotalHits = results.totalHits;
                System.out.println(numTotalHits + " total matching documents");

                //display results
                for(int j=0; j<TEXTS_RETRIEVED; j++){
                    Document hitDoc = indexSearcher.doc(hits[j].doc);
                    System.out.println("\tScore "+hits[j].score +"\tcaption:"+hitDoc.get("caption"));
                    saveToFile(file, hitDoc, hits, j, i);
                }
                                    
            }
        
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    
    public void saveToFile(FileWriter file,Document doc, ScoreDoc[] score, int j, int queryNumber) throws IOException{
        String docNo = doc.get("caption").substring(0, 6);
        file.write("Q0" + queryNumber +"\t0\t" + docNo + "\t0\t" + score[j].score + "\tSearcherProject\n");
        if (queryNumber==10 && j == TEXTS_RETRIEVED - 1){
            file.close();
        }
    }

    /*
     * Initialize a SearcherDemo
     */
    public static void main(String[] args){

        SearcherProject searcherDemo = new SearcherProject();
    }
}
