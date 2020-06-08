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
        System.out.println("control en este momento"+controlFlow);
        return (T) visitChildren(ctx);
    }

    @Override
    public T visitMethodBody(Java8Parser.MethodBodyContext ctx) {
        controlFlow = 0;
        List<Java8Parser.BlockStatementContext> statements = ctx.block().blockStatements().blockStatement();
        count++;
        System.out.println("asdads"+count+"tamaño"+statements.size());
        for(int c=0;c<statements.size();c++){
            if(ctx.block().blockStatements().blockStatement(c).statement()!=null){
                if(ctx.block().blockStatements().blockStatement(c).statement().whileStatement()!=null){
                    System.out.println("while");
                }else if(ctx.block().blockStatements().blockStatement(c).statement().forStatement()!=null){
                    System.out.println("for");
                }else if(ctx.block().blockStatements().blockStatement(c).statement().ifThenElseStatement()!=null){
                    System.out.println("if else then");
                }else if(ctx.block().blockStatements().blockStatement(c).statement().ifThenStatement()!=null){
                    System.out.println("if then");
                }else if(ctx.block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement()!=null){
                    System.out.println("statementWithoutTrailing");
                }else if(ctx.block().blockStatements().blockStatement(c).statement().labeledStatement()!=null){
                    System.out.println("labeledStatement");
                }
            }else if(ctx.block().blockStatements().blockStatement(c).classDeclaration()!=null){
                System.out.println("class");
            }else if(ctx.block().blockStatements().blockStatement(c).localVariableDeclarationStatement()!=null){
                System.out.println("variable");
            }
        }
        String methodName = ((Java8Parser.MethodDeclarationContext)ctx.getParent()).methodHeader().methodDeclarator().Identifier().getText();
        if(statements.size()> 10 ){
            smells.add(new smell(((Java8Parser.MethodDeclarationContext)ctx.parent).start,
                    "This method is too long!.\n"
                            + "Method name: " + methodName , "https://refactoring.guru/smells/long-method"));

        }
        visitChildren(ctx.block());
        System.out.println("control++"+controlFlow);
        if(controlFlow > 15){
            controlFlow = 0;
            smells.add(new smell(((Java8Parser.MethodDeclarationContext)ctx.parent).start,
                    "This method is too long!.\n"
                            + "Method name: " + methodName , "https://refactoring.guru/smells/long-method"));


        }
        return null;
    }
    //-----------------------------------------------------------------------------
    @Override
    public T visitMethodInvocation(Java8Parser.MethodInvocationContext ctx) {
        if(ctx.methodName()!=null)
        System.out.println("metodo invocado"+ctx.methodName().getText());
        return (T) visitChildren(ctx);
    }
    @Override
    public T visitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx){
        System.out.println("metodo declarado"+ctx.methodHeader().methodDeclarator().Identifier().toString());
        return (T) visitChildren(ctx);
    }
}

