import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.IOUtils;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

/**
 * DL4J {@link TokenizerFactory} based on Lucene's {@link Analyzer}s
 */
public class LuceneTokenizerFactory  implements TokenizerFactory {

    private final Analyzer analyzer;

    public LuceneTokenizerFactory(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public Tokenizer create(String toTokenize) {
        return new LuceneTokenizer(analyzer, toTokenize);
    }

    @Override
    public Tokenizer create(InputStream toTokenize) {
        return new LuceneTokenizer(analyzer, toTokenize);
    }

    @Override
    public void setTokenPreProcessor(TokenPreProcess preProcessor) {
        // do nothing
    }

    public TokenPreProcess getTokenPreProcessor() {
        return null;
    }

    private class LuceneTokenizer implements Tokenizer {
        private List<String> tokens;
        private Iterator<String> iterator;

        LuceneTokenizer(Analyzer analyzer, String toTokenize) {
            try {
                TokenStream tokenStream = analyzer.tokenStream(null, toTokenize);
                tokens = new LinkedList<>();
                final CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
                try {
                    tokenStream.reset();
                    while (tokenStream.incrementToken()) {
                        tokens.add(charTermAttribute.toString());
                    }
                    tokenStream.end();
                } catch (IOException ioe) {
                    throw new RuntimeException("Error occurred while iterating over tokenstream", ioe);
                } finally {
                    IOUtils.closeWhileHandlingException(tokenStream);
                }

                iterator = tokens.iterator();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        LuceneTokenizer(Analyzer analyzer, InputStream toTokenize) {
            try {
                TokenStream tokenStream = analyzer.tokenStream(null, new InputStreamReader(toTokenize));
                tokens = new LinkedList<>();
                final CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
                try {
                    tokenStream.reset();
                    while (tokenStream.incrementToken()) {
                        tokens.add(charTermAttribute.toString());
                    }
                    tokenStream.end();
                } catch (IOException ioe) {
                    throw new RuntimeException("Error occured while iterating over tokenstream", ioe);
                } finally {
                    IOUtils.closeWhileHandlingException(tokenStream);
                }

                iterator = tokens.iterator();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean hasMoreTokens() {
            return iterator.hasNext();
        }

        @Override
        public int countTokens() {
            return tokens.size();
        }

        @Override
        public String nextToken() {
            return iterator.next();
        }

        @Override
        public List<String> getTokens() {
            return tokens;
        }

        @Override
        public void setTokenPreProcessor(TokenPreProcess tokenPreProcessor) {
            // do nothing
        }
    }
}