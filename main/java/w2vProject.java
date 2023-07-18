
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;


import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilterFactory;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilterFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;
import org.bytedeco.opencv.presets.opencv_core;
import org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW;
import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

//First you have to set the libs file from DL4J AND LUCENE
public class w2vProject {

    private static Word2Vec vec;

    public static final int TEXTS_RETRIEVED = 50;
    public static final double SIMILARITY=0.65;
    public static final int LAYERS=50;
    public static final int  WINDOW=5;
    public static final String MY_ANSWERS_FILENAME = "myResults50_SKIP_"+LAYERS+"_5_"+WINDOW+"_"+SIMILARITY+".txt";



    public w2vProject() {
        try{

            //DEFINE PATH OF INDEX!!!!!

            String indexLocation = (""); //define where the index is stored
            String field = "contents"; //define which field will be searched

            //FIRST YOU TRAIN THE MODEL
            setUpBefore();

            //Access the index using indexReaderFSDirectory.open(Paths.get(index))
            IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation))); //IndexReader is an abstract class, providing an interface for accessing an index.
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);   //Creates a searcher searching the provided index, Implements search over a single IndexReader.

            indexSearcher.setSimilarity(new BM25Similarity());

            //Search the index using indexSearcher
            search(indexSearcher, field);
            //Close indexReader
            indexReader.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }




    public static void setUpBefore() throws Exception {
        /*
         * WARNING: YOU MUST DEFINE THE PATH ON YOUR PC!!!
         */
        String filePath = "";

        if (!new File(filePath).exists()) {
            System.out.println("Can't find the file");
        }

        // FieldValuesSentenceIterator iterator = new FieldValuesSentenceIterator(reader,"page");
        SentenceIterator iter = new BasicLineIterator(filePath);


        //place comments for the model

        SkipGram MLP = new SkipGram(); // skip gram
        //CBOW MLP = new CBOW();       

        vec = new Word2Vec.Builder()//settings word2vec
                .layerSize(LAYERS)
                .windowSize(WINDOW)
                .epochs(5)
                .elementsLearningAlgorithm(MLP)
                .tokenizerFactory(new LuceneTokenizerFactory(new StandardAnalyzer()))
                .iterate(iter)
                .build();

        vec.fit();//train!!


        //WordVectorSerializer.writeWord2VecModel(vec, "C:\\Users\\User\\deeplearning4j-examples\\dl4j-examples\\model\\model.bin");


    }


    public static void ReadModel() {
        //where you have seted the dlj
        String filePath = "C://Users//User//deeplearning4j-examples//dl4j-examples//model//model128_3_5.bin";
        Word2Vec loadModel=WordVectorSerializer.readWord2VecModel(filePath);
    }





    static Analyzer tempAnalyzer=new Analyzer() {

        @Override
        protected TokenStreamComponents createComponents(String s) {
            CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
            Tokenizer tokenizer = new WhitespaceTokenizer();

            double minAcc = 0.65;
            TokenStream tokenStream = new W2VSynonymFilter(tokenizer, vec, minAcc);
            tokenStream = new LowerCaseFilter(tokenStream);
            tokenStream = new StopFilter(tokenStream, stopWords);
            return new TokenStreamComponents(tokenizer, tokenStream);
        }

    };


     static LuceneTokenizerFactory synonymFilter=new LuceneTokenizerFactory(tempAnalyzer);

    //save the file in format trec_eval!
    public void saveToFile(FileWriter file,Document doc, ScoreDoc[] score, int j, int queryNumber) throws IOException{
        String docNo = doc.get("caption").substring(0, 6);
        String Q0;

        if (queryNumber<10){
            Q0="Q0";
        }else{
            Q0="Q";
        }
        file.write(Q0 + queryNumber +"\t0\t" + docNo + "\t0\t" + score[j].score + "\tSearcherProject\n");

        if (queryNumber==10 && j == TEXTS_RETRIEVED - 1){
            file.flush();
            file.close();
        }
    }


    private void search(IndexSearcher indexSearcher, String field) throws IOException{
        try{

            
            QueryParser parserSynonym=new QueryParser(field,customAnalyzerForQueryExpansion());

            // read user's query from stdin
            FileWriter file = new FileWriter(MY_ANSWERS_FILENAME);

            String[] queriesStrings = new String[]{"multimodal travel services","Big Data for Mobility", "European logistics applications", "Architectures for Big Data Analytics", "Architecture for Industrial IoT",
                    "Mobility-as-a-Service tools", "fragmentation of IoT through federation ", "Seamless Efficient European Travelling", "cross-domain orchestration of services", "communal organisation of network infrastucture"};


            // parse the query according to QueryParser
            for (int i = 1; i < queriesStrings.length + 1; i++) {

                

                String q=queriesStrings[i-1];
                String extra=Synonym(queriesStrings[i-1]);
                if(extra!=null){
                    q+=" " +extra;
                }
                Query query = parserSynonym.parse(q);

                System.out.println("Searching for: " + query.toString()  + "\n");

                // search the index using the indexSearcher
                TopDocs results = indexSearcher.search(query, 100);
                ScoreDoc[] hits = results.scoreDocs;
                long numTotalHits = results.totalHits;
                System.out.println(numTotalHits + " total matching documents \n");

                //display results
                for(int j=0; j<TEXTS_RETRIEVED; j++){
                    Document hitDoc = indexSearcher.doc(hits[j].doc);
                    System.out.println("\tScore "+hits[j].score +"\tcaption:"+hitDoc.get("caption"));
                    saveToFile(file, hitDoc, hits, j, i);
                }

            }

            file.close();

        } catch(Exception e){
            e.printStackTrace();
        }
    }


    public String Synonym(String query){

        String synonym = " ";
        String[] words = query.split(" ");
        for(String word:words){
            Collection<String> nearestWords=vec.wordsNearest(word,2);
            for(String wn:nearestWords){
                double similiraty=vec.similarity(word,wn);
                if(similiraty>SIMILARITY){
                    synonym+=" "+wn;
                    System.out.println(" for word " + word + "  found Synonym   " +wn + " Sim "+ similiraty +"\n");
                }
            }
        }
        return  synonym;
    }



   public static CustomAnalyzer customAnalyzerForQueryExpansion() throws IOException {

       CustomAnalyzer.Builder builder = CustomAnalyzer.builder()
               .withTokenizer(StandardTokenizerFactory.class)
               .addTokenFilter(EnglishPossessiveFilterFactory.class)
               .addTokenFilter(LowerCaseFilterFactory.class)
               .addTokenFilter(PorterStemFilterFactory.class)
               .addTokenFilter(StopFilterFactory.class);


       CustomAnalyzer analyzer = builder.build();
       return analyzer;
   }



    public static void main(String[] args) throws Exception {
        w2vProject w2vProject=new w2vProject();

    }
}















