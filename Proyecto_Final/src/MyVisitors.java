import org.antlr.v4.runtime.tree.ParseTree;

import org.antlr.v4.runtime.RuleContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class MyVisitors<T> extends Java8ParserBaseVisitor {
    public HashMap<String,function> tablaFunciones = new HashMap<>();
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
        //System.out.println("ControlFlow: "+controlFlow);
            if(ctx.whileStatement()!=null){
                controlFlow=controlFlow+15;
                //System.out.println("while");
            }else if(ctx.forStatement()!=null){
                controlFlow=controlFlow+10;
                //System.out.println("for");
            }else if(ctx.ifThenElseStatement()!=null){
                controlFlow=controlFlow+8;
                //System.out.println("if else then");
            }else if(ctx.ifThenStatement()!=null){
                controlFlow=controlFlow+5;
                //System.out.println("if then");
            }else if(ctx.statementWithoutTrailingSubstatement()!=null){
                controlFlow++;
                //System.out.println("statementWithoutTrailing");
            }else if(ctx.labeledStatement()!=null){
                controlFlow++;
                //System.out.println("labeledStatement");
            }
        return (T) visitChildren(ctx);
    }

    @Override
    public T visitMethodBody(Java8Parser.MethodBodyContext ctx) {
        controlFlow = 0;
        List<Java8Parser.BlockStatementContext> statements = ctx.block().blockStatements().blockStatement();
        count++;//contando cantidad de metodos declarados
        //System.out.println("Count "+count+"de tamaño"+statements.size());
        for(int c=0;c<statements.size();c++){
            if(ctx.block().blockStatements().blockStatement(c).statement()!=null){
                if(ctx.block().blockStatements().blockStatement(c).statement().whileStatement()!=null){
                    controlFlow=controlFlow+15;
                    //System.out.println("while");
                }else if(ctx.block().blockStatements().blockStatement(c).statement().forStatement()!=null){
                    controlFlow=controlFlow+10;
                    //System.out.println("for");
                }else if(ctx.block().blockStatements().blockStatement(c).statement().ifThenElseStatement()!=null){
                    controlFlow=controlFlow+8;
                    //System.out.println("if else then");
                }else if(ctx.block().blockStatements().blockStatement(c).statement().ifThenStatement()!=null){
                    controlFlow=controlFlow+5;
                    //System.out.println("if then");
                }else if(ctx.block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement()!=null){
                    //System.out.println("statementWithoutTrailing");
                }else if(ctx.block().blockStatements().blockStatement(c).statement().labeledStatement()!=null){
                    //System.out.println("labeledStatement");
                }
            }else if(ctx.block().blockStatements().blockStatement(c).classDeclaration()!=null){
                controlFlow=controlFlow+25;
                //System.out.println("class");
            }else if(ctx.block().blockStatements().blockStatement(c).localVariableDeclarationStatement()!=null){
                controlFlow=controlFlow+1;
                //System.out.println("variable");
            }
        }
        String methodName = ((Java8Parser.MethodDeclarationContext)ctx.getParent()).methodHeader().methodDeclarator().Identifier().getText();
        if(statements.size()> 10 ){
            smells.add(new smell(((Java8Parser.MethodDeclarationContext)ctx.parent).start,
                    "This method is too long!.\n"
                            + "Method name: " + methodName , "https://refactoring.guru/smells/long-method"));

        }
        visitChildren(ctx.block());
        System.out.println("ControlFlow method "+controlFlow);
        if(controlFlow > 70){
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
        if(ctx.methodName()!=null) {
            if(tablaFunciones.containsKey( ctx.methodName().getText())){
                function f = tablaFunciones.get(ctx.methodName().getText());
                f.setCalls(1);
                tablaFunciones.replace(ctx.methodName().getText(),f);
                System.out.println("metodo invocado declarado" + ctx.methodName().getText());
            }
        }
        else if(ctx.Identifier()!=null){
            if(tablaFunciones.containsKey( ctx.Identifier().getText())){
                function f = tablaFunciones.get(ctx.Identifier().getText());
                f.setCalls(1);
                tablaFunciones.replace(ctx.Identifier().getText(),f);
                System.out.println("metodo invocado declarado" + ctx.Identifier().getText());
            }
        }
        return (T) visitChildren(ctx);
    }
    @Override public T visitMethodInvocation_lf_primary(Java8Parser.MethodInvocation_lf_primaryContext ctx)
    {
        if(ctx.Identifier()!=null){
            if(tablaFunciones.containsKey( ctx.Identifier().getText())){
                function f = tablaFunciones.get(ctx.Identifier().getText());
                f.setCalls(1);
                tablaFunciones.replace(ctx.Identifier().getText(),f);
                System.out.println("metodo invocado ad declarado" + ctx.Identifier().getText());
            }
        }
        return (T) visitChildren(ctx);
    }
    @Override public T visitMethodInvocation_lfno_primary(Java8Parser.MethodInvocation_lfno_primaryContext ctx)
    {
        if(ctx.methodName()!=null) {
            if(tablaFunciones.containsKey( ctx.methodName().getText())){
                function f = tablaFunciones.get(ctx.methodName().getText());
                f.setCalls(1);
                tablaFunciones.replace(ctx.methodName().getText(),f);
                System.out.println("metodo invocado declarado" + ctx.methodName().getText());
            }
        }
        else if(ctx.Identifier()!=null){
            if(tablaFunciones.containsKey( ctx.Identifier().getText())){
                function f = tablaFunciones.get(ctx.Identifier().getText());
                f.setCalls(1);
                tablaFunciones.replace(ctx.Identifier().getText(),f);
                System.out.println("metodo invocado declarado" + ctx.Identifier().getText());
            }
        }
        return (T) visitChildren(ctx);
    }
    @Override
    public T visitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx){
        System.out.println("metodo declarado"+ctx.methodHeader().methodDeclarator().Identifier().toString());
        function f = new function(ctx.methodHeader().methodDeclarator().Identifier().toString(),0, ctx.methodHeader().methodDeclarator().Identifier().getSymbol());
        if(!f.name.equals("main"))
        tablaFunciones.put(ctx.methodHeader().methodDeclarator().Identifier().toString(),f);
        return (T) visitChildren(ctx);
    }
    @Override
    public T visitMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx) {
        String methodName = ctx.Identifier().toString();
        String[] parameters = ctx.formalParameterList().getText().split(",");
        System.out.println("nombre metodo"+methodName);
        for (int i=0;i >= parameters.length;i++){
            System.out.println("parametro:"+parameters[i]);
        }
        int c = parameters.length;
        //evalua si tiene mas de 4 parametros
        if (c > 4){
            smells.add(new smell(((Java8Parser.MethodDeclarationContext)ctx.parent.parent).start,
                    "This method have a lot of parameters!.\n"
                            + "Method name: " + methodName , "https://refactoring.guru/smells/long-parameter-list"));
        }
        return null;
    }
}
