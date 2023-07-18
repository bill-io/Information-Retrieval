# Information-Retrieval
Information Retrieval with Word 2 Vec


– Extend query with synonyms from word2vec
The model expands IR2023 collection queries with synonyms Terms that  gets from Word2vec.
The model estimates the probability of being selected a word (output) based on its context.
Extracts the closest neighbors of a word Looking at the context, the context of the word and determines when two words are semantically relevant (when occurring in the same or similar context)

First you have to follow these Prerequisites for deeplearning4java in order to set the libs of word2vec model:
https://deeplearning4j.konduit.ai/deeplearning4j/tutorials/quick-start
After that you have to include the libs file of Lucene that are in the lib file to your Project(import packages).

You can create your own index or you can use the index that is already places in the index file.
If you choose to create your own index take into consideration which analyzer you are going to use and use the same one to thw queries (IndexProject.java)
 
The results are in a format that trec_Eval can evaluate the answers you can see the fomrat in the results file
For evaluation you can use Cygwin that you can download and set from here:https://www.cygwin.com/

--Vasilis Ioannidis
Model: CBOW or SKIP GRAM
Document : IR2023
