import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.*;
import java.io.File;
import java.util.ArrayList;

public class Main {
    private static int minimum(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }

    public static int computeLevenshteinDistance(String str1, String str2) {
        return computeLevenshteinDistance(str1.toCharArray(),
                str2.toCharArray());
    }
    private static int computeLevenshteinDistance(char [] str1, char [] str2) {
        int [][]distance = new int[str1.length+1][str2.length+1];

        for(int i=0;i<=str1.length;i++){
            distance[i][0]=i;
        }
        for(int j=0;j<=str2.length;j++){
            distance[0][j]=j;
        }
        for(int i=1;i<=str1.length;i++){
            for(int j=1;j<=str2.length;j++){
                distance[i][j]= minimum(distance[i-1][j]+1,
                        distance[i][j-1]+1,
                        distance[i-1][j-1]+
                                ((str1[i-1]==str2[j-1])?0:1));
            }
        }
        return distance[str1.length][str2.length];

    }
    public static void main(String[] args) throws Exception {
        /*try{
            // crear un analizador léxico que se alimenta a partir de la entrada (archivo  o consola)
            chocPyLexer lexer;
            if (args.length>0)
                lexer = new chocPyLexer(CharStreams.fromFileName(args[0]));
            else
                lexer = new chocPyLexer(CharStreams.fromStream(System.in));
            // Identificar al analizador léxico como fuente de tokens para el sintactico
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            // Crear el objeto correspondiente al analizador sintáctico que se alimenta a partir del buffer de tokens
            chocPyParser parser = new chocPyParser(tokens);
            ParseTree tree = parser.program(); // Iniciar el analisis sintáctico en la regla inicial: r
            System.out.println(tree.toStringTree(parser)); // imprime el arbol al estilo LISP
            Myvisitors<Object> loader = new Myvisitors<Object>();
            loader.visit(tree);
        } catch (Exception e){
            System.err.println("Error (Test): " + e);
        }*/
        Java8Lexer lexer;
        if (args.length>0)
            lexer = new Java8Lexer(CharStreams.fromFileName(args[0]));
        else
            lexer = new Java8Lexer(CharStreams.fromStream(System.in));
        // Identificar al analizador léxico como fuente de tokens para el sintactico
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Crear el objeto correspondiente al analizador sintáctico que se alimenta a partir del buffer de tokens
        Java8Parser parser = new Java8Parser(tokens);
        ParseTree tree = parser.compilationUnit(); // Iniciar el analisis sintáctico en la regla inicial: r
        System.out.println(tree.toStringTree(parser)); // imprime el arbol al estilo LISP
        MyVisitors<Object> loader = new MyVisitors<Object>();
        loader.visit(tree);
        ArrayList<String> tot= new ArrayList<String>();
        ArrayList<String> fun = new ArrayList<String>();
        ArrayList<function> objfun = new ArrayList<function>();
        loader.tablaFunciones.forEach((k,v) -> {
            fun.add(k);
            tot.add(k);
            objfun.add(v);
        });
        ArrayList<String> cla = new ArrayList<String>();
        ArrayList<Nclase> objcla = new ArrayList<Nclase>();
        loader.tablaClases.forEach((k,v) -> {
            cla.add(k);
            tot.add(k);
            objcla.add(v);
        });
        System.out.println("    ");
        loader.tablaFunciones.forEach((k,v) -> {if(v.calls<1){
            loader.smells.add(new smell(v.getT(),
                    "This Method is not used!.\n", "https://refactoring.guru/smells/speculative-generality"));
        }
        });
        loader.tablaClases.forEach((k,v) -> {if(v.calls<1){
            loader.smells.add(new smell(v.getT(),
                    "This class is not used!.\n", "https://refactoring.guru/smells/lazy-class"));
        }
        });

        for (int i = 0; i <loader.smells.size(); i++) {
            System.out.println(loader.smells.get(i).toString());
        }
        System.out.println("datos");
        for(int c =0;c<fun.size();c++){
            System.out.println("funcion "+objfun.get(c).name+"llamados "+objfun.get(c).calls);
        }
        for(int c =0;c<cla.size();c++){
            System.out.println("clase "+objcla.get(c).name+"llamados "+objcla.get(c).calls);
        }
        for(int c=0;c<tot.size();c++){
            for(int d=c+1;d<tot.size();d++){
                System.out.println("pair "+tot.get(c)+" "+tot.get(d)+" distance "+computeLevenshteinDistance(tot.get(c),tot.get(d)));
            }
        }
    }
}