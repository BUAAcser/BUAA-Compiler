import Ir.Function;
import lex.Lexer;
import lex.Source;
import lex.Token;
import parse.Parser;
import parse.TreeNode.CompUnit;
import symbol.SymbolTable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Compiler {
    public static void main(String[] argv) {
        Source source = new Source();
        Lexer lexer = new Lexer(source);


//        File file = new File("output.txt");
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

        FileWriter fw = null;
        try {
            fw = new FileWriter("parserOutput.txt");
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

        FileWriter fw1 = null;
        try {
            fw1 = new FileWriter("IrOutput.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedWriter bw1 = new BufferedWriter(fw1);

        CompUnit compUnit = parser.getCompUnit();
        Visitor visitor = new Visitor(compUnit, bw1);
        visitor.visit();
        // visitor.printIr();

        try {
            bw1.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileWriter fw2 = null;
        try {
            fw2 = new FileWriter("mips.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedWriter bw2 = new BufferedWriter(fw2);



        ArrayList<Function> functions = visitor.getFunctions();
        Function main = visitor.getMainFunc();
        SymbolTable globalTable = visitor.getGlobal();
        ArrayList<String> strs = visitor.getStrs();
        Generator generator = new Generator(functions, main, globalTable, strs, bw2);
        generator.generateMips();
        generator.printMips();

        try {
            bw2.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}

