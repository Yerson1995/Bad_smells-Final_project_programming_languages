import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.*;
import java.io.File;

public class Main {
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
    }
}
