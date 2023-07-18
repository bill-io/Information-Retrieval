
// tested for lucene 7.7.3 and jdk13

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import txtparsing.*;

/**
 * Creates a lucene's inverted index from an xml file.
 * 
 */
public class IndexProject {
    
    /**
     * Configures IndexWriter.
     * Creates a lucene's inverted index.
     *
     */
    public IndexProject() throws Exception{
        
        String txtfile =  ""; //txt file to be parsed and indexed, it contains one document per line
        String indexLocation = (""); //define were to store the index
        
        Date start = new Date();
        try {
            System.out.println("Indexing to directory '" + indexLocation + "'...");
            
            Directory dir = FSDirectory.open(Paths.get(indexLocation));
            // define which analyzer to use for the normalization of documents
            Analyzer analyzer = new EnglishAnalyzer();
            // define retrieval model 
            Similarity similarity = new ClassicSimilarity();
            // configure IndexWriter
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setSimilarity(similarity);

            // Create a new index in the directory, removing any
            // previously indexed documents:
            iwc.setOpenMode(OpenMode.CREATE);

            // create the IndexWriter with the configuration as above 
            IndexWriter indexWriter = new IndexWriter(dir, iwc);
            
            // parse txt document using TXT parser and index it
            List<MyDocProject> docs = TXTParsingProject.parse(txtfile);
            for (MyDocProject doc : docs){
                indexDoc(indexWriter, doc);
            }
            indexWriter.close();
            
            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");
            
        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
        }
        
        
    }
    
    private void indexDoc(IndexWriter indexWriter, MyDocProject mydocproject){
        
        try {
            
            // make a new, empty document
            Document doc = new Document();
            
            // create the fields of the document and add them to the document
            
            StoredField caption = new StoredField("caption", mydocproject.getCaption());
            doc.add(caption);
            String fullSearchableText = mydocproject.getCaption() ;            
            TextField contents = new TextField("contents", fullSearchableText, Field.Store.NO);
            doc.add(contents);
            
            if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE) {
                // New index, so we just add the document (no old document can be there):
                indexWriter.addDocument(doc);
            } 
        } catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    /**
     * Initializes an IndexerDemo
     */
    public static void main(String[] args) {
        try {
            IndexProject indexProject = new IndexProject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
