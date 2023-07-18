
package txtparsing;

import utils.IO;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TXTParsingProject {

    public static List<MyDocProject> parse(String file) throws Exception {
        List<MyDocProject> parsed_docs= new ArrayList<MyDocProject>();
        try{
            //Parse txt file
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder current_doc = new StringBuilder();
            while ((line = br.readLine()) != null) {
                if (line.trim().equals("///")) {
                    // Create a new MyDocProject object and add it to the list
                    parsed_docs.add(new MyDocProject(current_doc.toString()));
                    // Reset the current_doc StringBuilder
                    current_doc.setLength(0);
                } else {
                    // Append the current line to the current_doc StringBuilder
                    current_doc.append(line).append("\n");
                }
            }
            /*
            String txt_file = IO.ReadEntireFileIntoAString(file);
            System.out.println(txt_file);
            String[] docs = txt_file.split("///");
            System.out.println("Read: "+docs.length + " docs");
            //Parse each document from the txt file
            for (String doc:docs){
                MyDocProject mydocproject = new MyDocProject(doc);
                parsed_docs.add(mydocproject);
            }
            */
            parsed_docs.add(new MyDocProject(current_doc.toString()));
            
        } catch (Throwable err) {
            err.printStackTrace();
            return null;
        }
        System.out.println("Read: " + parsed_docs.size() + " docs");
        return parsed_docs;
    }

}
