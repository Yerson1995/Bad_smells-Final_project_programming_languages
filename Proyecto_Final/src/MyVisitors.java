import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;

public class MyVisitors<T> extends Java8ParserBaseVisitor {
    ArrayList<smell> smells;
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
        double largeRate = (methods * 0.24 + field * 0.16 + innerInterfaces * 0.30 + innerClasses * 0.30)/5;
        if(largeRate > 1){
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
            boolean isGet = methodName.startsWith("get") || methodName.startsWith("is");
            boolean isSet = methodName.startsWith("set");
            if(isGet || isSet){
                //GettersAndSetters++;
            }
            else{
                methods++;
            }
        } else if (ctx.classDeclaration() != null) {
            innerClasses++;
        } else if (ctx.interfaceDeclaration() != null) {
            innerInterfaces++;
        }
        return null;
    }
}
