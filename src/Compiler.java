import lex.Lexer;
import lex.Source;
import lex.Token;
import parse.Parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Compiler {
    public static void main(String[] argv) {
        Source source = new Source();
        Lexer lexer = new Lexer(source);


        File file = new File("output.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter("output.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedWriter bw = new BufferedWriter(fw);

        Parser parser = new Parser(lexer, bw);

//        for (String str : strings) {
//            try {
//                bw.write(str);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

        try {
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

