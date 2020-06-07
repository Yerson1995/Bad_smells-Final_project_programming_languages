import org.antlr.v4.runtime.tree.ParseTree;

import org.antlr.v4.runtime.RuleContext;
import java.util.ArrayList;
import java.util.List;
public class MyVisitors<T> extends Java8ParserBaseVisitor {
    ArrayList<smell> smells=new ArrayList<>();
    int count = 0;
    private int methods = 0;
    private int methodsw = 0;
    private int field = 0;
    private int innerClasses = 0;
    private int innerInterfaces = 0;

    @Override
    public T visitClassBody(Java8Parser.ClassBodyContext ctx){
        visitChildren(ctx);
        double tot=0;
        if(methods<5){
            tot=tot+methods*0.20;
        }else{
            tot=tot+methods*0.25;
        }
        if(field<7){
            tot=tot+methods*0.10;
        }else{
            tot=tot+methods*0.15;
        }
        if(innerInterfaces<5){
            tot=tot+innerInterfaces * 0.25;
        }else{
            tot=tot+innerInterfaces * 0.30;
        }
        if(innerClasses<5){
            tot=tot+innerClasses * 0.25;
        }else{
            tot=tot+innerClasses * 0.30;
        }
        tot=tot/5;
        System.out.println("total"+tot);
        if(tot > 1){
            smells.add(new smell(((Java8Parser.NormalClassDeclarationContext)ctx.parent).start,
                    "This class is to enormous!.\n", "https://refactoring.guru/smells/large-class"));
        }
        if(methods < 3){
            smells.add(new smell(((Java8Parser.NormalClassDeclarationContext)ctx.parent).start,
                    "This class is just a bunch of data!.\n", "https://refactoring.guru/smells/data-class"));
        }
        return null;
    }
    @Override
    public T visitClassMemberDeclaration(Java8Parser.ClassMemberDeclarationContext ctx) {

        if(ctx.fieldDeclaration() != null){
            field += ctx.fieldDeclaration().variableDeclaratorList().variableDeclarator().size();

        }else if (ctx.methodDeclaration() != null) {
            methodsw+=ctx.methodDeclaration().methodBody().block().blockStatements().blockStatement().size();
            String methodName = ctx.methodDeclaration().methodHeader().methodDeclarator().Identifier().getText();
            boolean isGet = methodName.startsWith("get");
            boolean isSet = methodName.startsWith("set");
            if(isGet || isSet){
                //GettersAndSetters++;
            }
            else{
                methods++;
                System.out.println("estoy sumando");
            }
        } else if (ctx.classDeclaration() != null) {
            innerClasses++;
        } else if (ctx.interfaceDeclaration() != null) {
            innerInterfaces++;
        }
        return(T)visitChildren(ctx);
    }
//---------------------------------------------------------------------

    private int controlFlow = 0;

    @Override
    public T visitStatement(Java8Parser.StatementContext ctx) {

        controlFlow++;
        return (T) visitChildren(ctx);
    }

    @Override
    public T visitMethodBody(Java8Parser.MethodBodyContext ctx) {
        List<Java8Parser.BlockStatementContext> statements = ctx.block().blockStatements().blockStatement();
        count++;
        System.out.println("asdads"+count+"tamaño"+statements.size());

        String methodName = ((Java8Parser.MethodDeclarationContext)ctx.getParent()).methodHeader().methodDeclarator().Identifier().getText();
        if(statements.size()> 10 ){
            smells.add(new smell(((Java8Parser.MethodDeclarationContext)ctx.parent).start,
                    "This method is too long!.\n"
                            + "Method name: " + methodName , "https://refactoring.guru/smells/long-method"));

        }
        visitChildren(ctx.block());
        if(controlFlow > 15){
            controlFlow = 0;
            smells.add(new smell(((Java8Parser.MethodDeclarationContext)ctx.parent).start,
                    "This method is too long!.\n"
                            + "Method name: " + methodName , "https://refactoring.guru/smells/long-method"));


        }
        return null;
    }
    //-----------------------------------------------------------------------------
    public void nestedStatement(RuleContext ctx, int line, int column){
        RuleContext parentStructure = ctx;
        String parentString;
        int nestedCounter = 1;
        int i;
        while(true){
            i = 0;
            while(parentStructure != null && i < 4){
                parentStructure = parentStructure.parent;
                i++;
            }
            if(parentStructure != null){
                parentString = parentStructure.getClass().toString();
                if(parentString.equals("class Java8Parser$If_elsif_statementContext")){
                    parentStructure = parentStructure.parent.parent;
                    parentString = parentStructure.getClass().toString();
                }
                if(parentString.equals("class RubyParser$If_statementContext")
                        || parentString.equals("class RubyParser$Unless_statementContext")
                        || parentString.equals("class RubyParser$While_statementContext")
                        || parentString.equals("class RubyParser$For_statementContext")){
                    nestedCounter++;
                }
                if(nestedCounter > 4){
                    String message = "\nMal olor encontrado, estructura profundamente anidada, Linea: " + line + ", Columna: " + column + "\n"
                            + "Se recomienda reestructurar la logica del codigo para evitar la complejidad de lectura.\n";
                   //manager.AddCodeSmell(SMELL.DeeplyNestedCode, line, column, message);
                    break;
                }
            }
            else{
                break;
            }
        }
    }
}

