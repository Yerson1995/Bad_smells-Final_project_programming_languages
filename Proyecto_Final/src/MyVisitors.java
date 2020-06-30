import org.antlr.v4.runtime.tree.ParseTree;

import org.antlr.v4.runtime.RuleContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class MyVisitors<T> extends Java8ParserBaseVisitor {
    public HashMap<String,function> tablaFunciones = new HashMap<>();
    public HashMap<String,Nclase> tablaClases = new HashMap<>();
    public HashMap<String,VField> tablaVariables = new HashMap<>();
    ArrayList<smell> smells=new ArrayList<>();
    int count = 0;
    private int methods = 0;
    private int methodsw = 0;
    private int field = 0;
    private int innerClasses = 0;
    private int innerInterfaces = 0;
    private int controlFlow = 0;

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

    @Override
    public T visitStatement(Java8Parser.StatementContext ctx) {
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
                    controlFlow=controlFlow+1;
                    //System.out.println("statementWithoutTrailing");
                }else if(ctx.block().blockStatements().blockStatement(c).statement().labeledStatement()!=null){
                    controlFlow=controlFlow+1;
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
                f.AddCalls();
                tablaFunciones.replace(ctx.methodName().getText(),f);
                System.out.println("Metodo invocado declaradoM" + ctx.methodName().getText());
            }
            else{
                function f = new function(ctx.methodName().getText());
                tablaFunciones.put(ctx.methodName().getText(),f);
            }
        }
        else if(ctx.Identifier()!=null){
            if(tablaFunciones.containsKey( ctx.Identifier().getText())){
                function f = tablaFunciones.get(ctx.Identifier().getText());
                f.AddCalls();
                tablaFunciones.replace(ctx.Identifier().getText(),f);
                System.out.println("Metodo invocado declaradoI" + ctx.Identifier().getText());
            }
            else{
                function f = new function(ctx.Identifier().getText());
                tablaFunciones.put(ctx.Identifier().getText(),f);
            }
        }
        return (T) visitChildren(ctx);
    }
    @Override public T visitMethodInvocation_lf_primary(Java8Parser.MethodInvocation_lf_primaryContext ctx)
    {
        if(ctx.Identifier()!=null){
            if(tablaFunciones.containsKey( ctx.Identifier().getText())){
                function f = tablaFunciones.get(ctx.Identifier().getText());
                f.AddCalls();
                tablaFunciones.replace(ctx.Identifier().getText(),f);
                System.out.println("Metodo invocado ad declarado" + ctx.Identifier().getText());
            }
            else{
                function f = new function(ctx.Identifier().getText());
                tablaFunciones.put(ctx.Identifier().getText(),f);
            }
        }
        return null;
    }
    @Override public T visitMethodInvocation_lfno_primary(Java8Parser.MethodInvocation_lfno_primaryContext ctx)
    {
        if(ctx.methodName()!=null) {
            if(tablaFunciones.containsKey( ctx.methodName().getText())){
                function f = tablaFunciones.get(ctx.methodName().getText());
                f.AddCalls();
                tablaFunciones.replace(ctx.methodName().getText(),f);
                System.out.println("Metodo invocado declaradoM" + ctx.methodName().getText());
            }
            else{
                function f = new function(ctx.methodName().getText());
                tablaFunciones.put(ctx.methodName().getText(),f);
            }
        }
        else if(ctx.Identifier()!=null){
            if(tablaFunciones.containsKey( ctx.Identifier().getText())){
                function f = tablaFunciones.get(ctx.Identifier().getText());
                f.AddCalls();
                tablaFunciones.replace(ctx.Identifier().getText(),f);
                System.out.println("Metodo invocado declaradoI" + ctx.Identifier().getText());
            }
            else{
                function f = new function(ctx.Identifier().getText());
                tablaFunciones.put(ctx.Identifier().getText(),f);
            }
        }
        return null;
    }
    @Override
    public T visitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx){
        System.out.println("Metodo declarado"+ctx.methodHeader().methodDeclarator().Identifier().toString());
        function f = new function(ctx.methodHeader().methodDeclarator().Identifier().toString(),0, ctx.methodHeader().methodDeclarator().Identifier().getSymbol());
        if(tablaFunciones.containsKey( f.name)){
            f = tablaFunciones.get(f.name);
            f.setT(ctx.methodHeader().methodDeclarator().Identifier().getSymbol());
            tablaFunciones.replace(f.name,f);
            System.out.println("Metodo Creado" + f.name);
        }
        else{
            if(!f.name.equals("main")){
                tablaFunciones.put(ctx.methodHeader().methodDeclarator().Identifier().toString(),f);
            }
        }
        return (T) visitChildren(ctx);
    }
    @Override
    public T visitMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx) {
        String methodName = ctx.Identifier().toString();
        if( ctx.formalParameterList()!=null){
            String[] parameters = ctx.formalParameterList().getText().split(",");
            System.out.println("Nombre metodo"+methodName);
            for (int i=0;i < parameters.length;i++){
                System.out.println("parametro:"+parameters[i]);
            }
            int c = parameters.length;
            //evalua si tiene mas de 4 parametros
            if (c > 4){
                smells.add(new smell(((Java8Parser.MethodDeclarationContext)ctx.parent.parent).start,
                        "This method have a lot of parameters!.\n"
                                + "Method name: " + methodName , "https://refactoring.guru/smells/long-parameter-list"));
            }
        }
        return null;
    }
    //-----------------------------------------------------------------------------------
    @Override public T visitClassDeclaration(Java8Parser.ClassDeclarationContext ctx)
    {
        if(ctx.normalClassDeclaration()!=null){
            String cl=ctx.normalClassDeclaration().Identifier().toString();
            Nclase Oclase=new Nclase(cl,0,ctx.start);
            if(tablaClases.containsKey(cl)){
                Oclase=tablaClases.get(cl);
                Oclase.setT(ctx.start);
                tablaClases.replace(cl,Oclase);
            }
            else {
                tablaClases.put(cl, Oclase);
            }
            if(ctx.normalClassDeclaration().superclass()!=null){
                cl=ctx.normalClassDeclaration().superclass().classType().Identifier().toString();
                System.out.println(cl+" Extendida");
                if(tablaClases.containsKey(cl)){
                    Oclase=tablaClases.get(cl);
                    Oclase.AddCalls();
                    tablaClases.replace(cl,Oclase);
                }else{
                    Oclase=new Nclase(cl);
                    tablaClases.put(cl,Oclase);
                }
            }
            System.out.println("Clase declarada "+ctx.normalClassDeclaration().Identifier().toString());
            for(int c=0;c<ctx.normalClassDeclaration().classBody().classBodyDeclaration().size();c++){
                if(ctx.normalClassDeclaration().classBody().classBodyDeclaration(c).classMemberDeclaration()!=null){
                    if(ctx.normalClassDeclaration().classBody().classBodyDeclaration(c).classMemberDeclaration().methodDeclaration()!=null)
                    {
                        String met=ctx.normalClassDeclaration().classBody().classBodyDeclaration(c).classMemberDeclaration().methodDeclaration().methodHeader().methodDeclarator().Identifier().toString();
                        System.out.println("clase "+ctx.normalClassDeclaration().Identifier().toString()+"metod "+met);
                        if(met.equals("main")){
                            Oclase=tablaClases.get(ctx.normalClassDeclaration().Identifier().toString());
                            Oclase.AddCalls();
                            tablaClases.replace(cl,Oclase);
                        }
                        /*if(ctx.normalClassDeclaration().classBody().classBodyDeclaration(c).classMemberDeclaration().methodDeclaration().methodBody().block().blockStatements()!=null){
                            for (int i = 0; i <ctx.normalClassDeclaration().classBody().classBodyDeclaration(c).classMemberDeclaration().methodDeclaration().methodBody().block().blockStatements().blockStatement().size() ; i++) {
                                ctx.normalClassDeclaration().classBody().classBodyDeclaration(c).classMemberDeclaration().methodDeclaration().methodBody().block().blockStatements().blockStatement(i).statement().forStatement().basicForStatement().statement().forStatement()statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().statement().forStatement().basicForStatement().
                            }
                        }
                    }
                    else if(ctx.normalClassDeclaration().classBody().classBodyDeclaration(c).classMemberDeclaration().fieldDeclaration()!=null){
                        for (int i = 0; i <ctx.normalClassDeclaration().classBody().classBodyDeclaration(c).classMemberDeclaration().fieldDeclaration().variableDeclaratorList().variableDeclarator().size(); i++) {
                            if(ctx.normalClassDeclaration().classBody().classBodyDeclaration(c).classMemberDeclaration().fieldDeclaration().variableDeclaratorList().variableDeclarator(i).variableDeclaratorId().Identifier()!=null){
                                String name=ctx.normalClassDeclaration().classBody().classBodyDeclaration(c).classMemberDeclaration().fieldDeclaration().variableDeclaratorList().variableDeclarator(i).variableDeclaratorId().Identifier().getText();
                                if(!tablaVariables.containsKey(name)){
                                    VField vf=new VField(name,0,ctx.normalClassDeclaration().classBody().classBodyDeclaration(c).classMemberDeclaration().fieldDeclaration().variableDeclaratorList().variableDeclarator(i).variableDeclaratorId().Identifier().getSymbol(),cl);
                                    tablaVariables.put(name,vf);
                                    System.out.println("Variable creada");
                                }
                                else{
                                    VField vf=tablaVariables.get(name);
                                    vf.setT(ctx.normalClassDeclaration().classBody().classBodyDeclaration(c).classMemberDeclaration().fieldDeclaration().variableDeclaratorList().variableDeclarator(i).variableDeclaratorId().Identifier().getSymbol());
                                    tablaVariables.replace(name,vf);
                                    System.out.println("Variable indicada");
                                }
                            }
                        }*/
                    }
                }
            }
        }
        return (T) visitChildren(ctx);
    }
    @Override public T visitClassInstanceCreationExpression_lf_primary(Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx)
    {
        System.out.println(ctx.Identifier().getText()+"instanced");
        String c=ctx.Identifier().getText();
        Nclase Oclase=new Nclase(c,0,ctx.start);
        if(tablaClases.containsKey(c)){
            Oclase=tablaClases.get(c);
            Oclase.AddCalls();
            tablaClases.replace(c,Oclase);
        }else{
            Oclase=new Nclase(c);
            tablaClases.put(c,Oclase);
        }
        return null;
    }
    @Override public T visitClassInstanceCreationExpression_lfno_primary(Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx)
    {
        System.out.println(ctx.Identifier().get(0).getText()+"instanced");
        String c=ctx.Identifier().get(0).getText();
        Nclase Oclase=new Nclase(c,0,ctx.start);
        if(tablaClases.containsKey(c)){
            Oclase=tablaClases.get(c);
            Oclase.AddCalls();
            tablaClases.replace(c,Oclase);
        }else{
            Oclase=new Nclase(c);
            tablaClases.put(c,Oclase);
        }
        return null;
    }
    @Override public T visitClassInstanceCreationExpression(Java8Parser.ClassInstanceCreationExpressionContext ctx)
    {
        System.out.println(ctx.Identifier().get(0).getText()+"instanced");
        String c=ctx.Identifier().get(0).getText();
        Nclase Oclase=new Nclase(c,0,ctx.start);
        if(tablaClases.containsKey(c)){
            Oclase=tablaClases.get(c);
            Oclase.AddCalls();
            tablaClases.replace(c,Oclase);
        }else{
            Oclase=new Nclase(c);
            tablaClases.put(c,Oclase);
        }
        return null;
    }
    //-----------------------------------------------------------------------------------

    @Override
    public Object visitVariableDeclaratorId(Java8Parser.VariableDeclaratorIdContext ctx) {
        System.out.println("Variable declarada "+ctx.Identifier().getText());
        VField f = new VField(ctx.Identifier().getText(),0, ctx.Identifier().getSymbol(),null);
        if(tablaVariables.containsKey( f.name)){
            f = tablaVariables.get(f.name);
            f.setT(ctx.Identifier().getSymbol());
            tablaVariables.replace(f.name,f);
            System.out.println("Variable declarada " + f.name);
        }
        else{
            tablaVariables.put(ctx.Identifier().getText(),f);
        }
        return null;
    }
    @Override
    public T visitWhileStatement(Java8Parser.WhileStatementContext ctx) {
        System.out.println("im in while");
        if(ctx.statement().statementWithoutTrailingSubstatement().returnStatement()!=null){
            System.out.println("i have a simple return");
            smells.add(new smell(ctx.statement().statementWithoutTrailingSubstatement().returnStatement().start,
                    "This while has a return.\n", "https://refactoring.guru/refactoring/smells"));
            return null;
        }
        if(ctx.statement().statementWithoutTrailingSubstatement().block().blockStatements()!=null) {
            if (ctx.statement().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement() != null) {
                int t = ctx.statement().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement().size();
                for (int c = 0; c < t; c++) {
                    if(ctx.statement().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement()!=null)
                    if (ctx.statement().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement()!= null) {
                        if (ctx.statement().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement().returnStatement() != null) {
                            System.out.println("i have a return");
                            smells.add(new smell(ctx.statement().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement().returnStatement().start,
                                    "This while has a return.\n", "https://refactoring.guru/refactoring/smells"));
                        }
                    }
                }

            }
        }
        return null;
    }

    @Override
    public Object visitWhileStatementNoShortIf(Java8Parser.WhileStatementNoShortIfContext ctx) {
        System.out.println("im in while");
        if(ctx.statementNoShortIf().statementWithoutTrailingSubstatement().returnStatement()!=null){
            System.out.println("i have a simple return");
            smells.add(new smell(ctx.statementNoShortIf().statementWithoutTrailingSubstatement().returnStatement().start,
                    "This while has a return.\n", "https://refactoring.guru/refactoring/smells"));
        }
        if(ctx.statementNoShortIf().statementWithoutTrailingSubstatement().block().blockStatements()!=null)
        if(ctx.statementNoShortIf().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement()!=null){
            int t=ctx.statementNoShortIf().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement().size();
            for(int c=0;c<t;c++){
                if(ctx.statementNoShortIf().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement()!=null)
                if(ctx.statementNoShortIf().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement()!=null)
                    if(ctx.statementNoShortIf().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement().returnStatement()!=null){
                        System.out.println("i have a return");
                        smells.add(new smell(ctx.statementNoShortIf().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement().returnStatement().start,
                                "This while has a return.\n", "https://refactoring.guru/refactoring/smells"));
                    }
            }

        }
        return (T) visitChildren(ctx);
    }

    @Override
    public Object visitForStatement(Java8Parser.ForStatementContext ctx) {
        //System.out.println(ctx.start.getLine());
        if(ctx.basicForStatement()!=null){
            if(ctx.basicForStatement().statement().statementWithoutTrailingSubstatement().returnStatement()!=null){
                System.out.println("i have a simple return");
                smells.add(new smell(ctx.basicForStatement().statement().statementWithoutTrailingSubstatement().returnStatement().start,
                        "This for has a return.\n", "https://refactoring.guru/refactoring/smells"));
            }
            else if(ctx.basicForStatement().statement().statementWithoutTrailingSubstatement().block().blockStatements()!=null){
                int t=ctx.basicForStatement().statement().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement().size();
                for(int c=0;c<t;c++){
                    if(ctx.basicForStatement().statement().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement()!=null)
                    if(ctx.basicForStatement().statement().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement()!=null) {
                        if (ctx.basicForStatement().statement().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement().returnStatement() != null) {
                            System.out.println("i have a return");
                            smells.add(new smell(ctx.basicForStatement().statement().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement().returnStatement().start,
                                    "This for has a return.\n", "https://refactoring.guru/refactoring/smells"));
                        }
                    }
                }
            }
        }
        else if(ctx.enhancedForStatement()!=null){
            if(ctx.enhancedForStatement().statement().statementWithoutTrailingSubstatement().returnStatement()!=null){
                System.out.println("i have a simple return");
                smells.add(new smell(ctx.enhancedForStatement().statement().statementWithoutTrailingSubstatement().returnStatement().start,
                        "This for has a return.\n", "https://refactoring.guru/refactoring/smells"));
            }
            else if(ctx.enhancedForStatement().statement().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement()!=null){
                int t=ctx.enhancedForStatement().statement().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement().size();
                for(int c=0;c<t;c++){
                    if(ctx.enhancedForStatement().statement().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement()!=null)
                        if(ctx.enhancedForStatement().statement().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement().returnStatement()!=null){
                            System.out.println("I have a return");
                            smells.add(new smell(ctx.enhancedForStatement().statement().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement().returnStatement().start,
                                    "This for has a return.\n", "https://refactoring.guru/refactoring/smells"));
                        }
                }
            }
        }
        return (T) visitChildren(ctx);
    }

    @Override
    public Object visitForStatementNoShortIf(Java8Parser.ForStatementNoShortIfContext ctx) {
        if(ctx.basicForStatementNoShortIf()!=null){
            if(ctx.basicForStatementNoShortIf().statementNoShortIf().statementWithoutTrailingSubstatement().returnStatement()!=null){
                System.out.println("i have a simple return");
                smells.add(new smell(ctx.basicForStatementNoShortIf().statementNoShortIf().statementWithoutTrailingSubstatement().returnStatement().start,
                        "This for has a return.\n", "https://refactoring.guru/refactoring/smells"));
            }
            else if(ctx.basicForStatementNoShortIf().statementNoShortIf().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement()!=null){
                int t=ctx.basicForStatementNoShortIf().statementNoShortIf().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement().size();
                for(int c=0;c<t;c++){
                    if(ctx.basicForStatementNoShortIf().statementNoShortIf().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement()!=null)
                        if(ctx.basicForStatementNoShortIf().statementNoShortIf().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement().returnStatement()!=null){
                            System.out.println("i have a return");
                            smells.add(new smell(ctx.basicForStatementNoShortIf().statementNoShortIf().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement().returnStatement().start,
                                    "This for has a return.\n", "https://refactoring.guru/refactoring/smells"));
                        }
                }
            }
        }
        else if(ctx.enhancedForStatementNoShortIf()!=null){
            if(ctx.enhancedForStatementNoShortIf().statementNoShortIf().statementWithoutTrailingSubstatement().returnStatement()!=null){
                System.out.println("i have a simple return");
                smells.add(new smell(ctx.enhancedForStatementNoShortIf().statementNoShortIf().statementWithoutTrailingSubstatement().returnStatement().start,
                        "This for has a return.\n", "https://refactoring.guru/refactoring/smells"));
            }
            else if(ctx.enhancedForStatementNoShortIf().statementNoShortIf().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement()!=null){
                int t=ctx.enhancedForStatementNoShortIf().statementNoShortIf().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement().size();
                for(int c=0;c<t;c++){
                    if(ctx.enhancedForStatementNoShortIf().statementNoShortIf().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement()!=null)
                        if(ctx.enhancedForStatementNoShortIf().statementNoShortIf().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement().returnStatement()!=null){
                            System.out.println("i have a return");
                            smells.add(new smell(ctx.enhancedForStatementNoShortIf().statementNoShortIf().statementWithoutTrailingSubstatement().block().blockStatements().blockStatement(c).statement().statementWithoutTrailingSubstatement().returnStatement().start,
                                    "This for has a return.\n", "https://refactoring.guru/refactoring/smells"));
                        }
                }
            }
        }
        return (T) visitChildren(ctx);
    }
}