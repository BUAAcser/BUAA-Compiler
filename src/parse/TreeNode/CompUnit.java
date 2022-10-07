package parse.TreeNode;

import java.util.ArrayList;

public class CompUnit {
    private ArrayList<Decl> decls;
    private ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;

    public CompUnit(ArrayList<Decl> decls, ArrayList<FuncDef> functions, MainFuncDef main) {
        this.decls = decls;
        this.funcDefs = functions;
        this.mainFuncDef = main;
    }

}
