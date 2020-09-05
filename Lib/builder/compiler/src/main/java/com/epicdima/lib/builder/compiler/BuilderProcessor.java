package com.epicdima.lib.builder.compiler;

import com.epicdima.lib.builder.annotation.Builder;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.sun.tools.javac.code.Flags.*;

/**
 * @author EpicDima
 */
public final class BuilderProcessor extends AbstractProcessor {

    private Trees trees;
    private Names names;
    private TreeMaker treeMaker;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Builder.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
        JavacProcessingEnvironment javacEnv = (JavacProcessingEnvironment) processingEnv;
        names = Names.instance(javacEnv.getContext());
        treeMaker = TreeMaker.instance(javacEnv.getContext());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element clsElement : roundEnv.getElementsAnnotatedWith(Builder.class)) {
            List<Element> fields = new ArrayList<>();
            for (Element innerElement : clsElement.getEnclosedElements()) {
                if (innerElement.getKind().isField()) {
                    fields.add(innerElement);
                }
            }
            generateBuilderClass(clsElement, fields);
        }
        return true;
    }

    private void generateBuilderClass(Element clientElement, List<Element> fields) {
        JCTree.JCClassDecl clientClass = ((JCTree.JCClassDecl) trees.getTree(clientElement));
        Name clientClassName = names.fromString(clientElement.getSimpleName().toString());
        removeBuilderAnnotation(clientClass);
        Name builderClassName = names.fromString("Builder");

        treeMaker.at(clientClass.pos);
        JCTree.JCClassDecl builderClass = createBuilderClass(builderClassName);

        JCTree.JCNewClass clientConstructorCall = createConstructorCall(treeMaker,
                treeMaker.Ident(clientClassName), nil());
        treeMaker.at(builderClass.pos);
        JCTree.JCMethodDecl builderBuildMethod = createBuilderBuildMethod(clientClassName, clientConstructorCall);

        JCTree.JCExpression clientToBuilderMethodReturnFieldSetterCall = null;

        treeMaker.at(builderBuildMethod.pos);
        JCTree.JCNewClass builderConstructorCall = createConstructorCall(treeMaker,
                treeMaker.Ident(builderClassName), nil());

        for (int i = 0; i < fields.size(); i++) {
            Element field = fields.get(i);
            Name fieldName = names.fromString(field.getSimpleName().toString());
            JCTree.JCExpression fieldType = treeMaker.Type((Type) field.asType());

            treeMaker.at(builderClass.pos);
            processField(fieldName, fieldType, fieldName, builderClassName, builderClass,
                    clientConstructorCall);
            treeMaker.at(builderClass.pos);
            clientToBuilderMethodReturnFieldSetterCall = processField2(clientToBuilderMethodReturnFieldSetterCall,
                    builderConstructorCall, fieldName, i);
        }

        builderClass.defs = builderClass.defs.append(builderBuildMethod);

        treeMaker.at(clientClass.pos);
        JCTree.JCMethodDecl clientBuilderMethod = createClientBuilderMethod(builderClassName);
        treeMaker.at(clientClass.pos);
        JCTree.JCMethodDecl clientToBuilderMethod = createClientToBuilderMethod(builderClassName,
                clientToBuilderMethodReturnFieldSetterCall);

        clientClass.defs = clientClass.defs.append(builderClass);
        clientClass.defs = clientClass.defs.append(clientBuilderMethod);
        clientClass.defs = clientClass.defs.append(clientToBuilderMethod);
    }

    private void removeBuilderAnnotation(JCTree.JCClassDecl clientClass) {
        com.sun.tools.javac.util.List<JCTree.JCAnnotation> annotations = nil();
        for (JCTree.JCAnnotation annotation : clientClass.mods.annotations) {
            if (!Builder.class.getCanonicalName().equals(annotation.annotationType.type.toString())) {
                annotations = annotations.append(annotation);
            }
        }
        clientClass.mods.annotations = annotations;
    }

    private JCTree.JCClassDecl createBuilderClass(Name builderClassName) {
        return treeMaker.ClassDef(treeMaker.Modifiers(PUBLIC | STATIC | FINAL),
                builderClassName, nil(), null, nil(),
                of(createMethod(treeMaker, names.init, PRIVATE, null, nil(), nil())));
    }

    private JCTree.JCMethodDecl createBuilderBuildMethod(Name clientClassName,
                                                         JCTree.JCNewClass clientConstructorCall) {
        Name builderBuildMethodName = names.fromString("build");
        return createMethod(treeMaker, builderBuildMethodName, PUBLIC, treeMaker.Ident(clientClassName),
                nil(), of(treeMaker.Return(clientConstructorCall)));
    }

    private void processField(Name fieldName, JCTree.JCExpression fieldType, Name fieldParameterName,
                              Name builderClassName, JCTree.JCClassDecl builderClass,
                              JCTree.JCNewClass clientConstructor) {
        builderClass.defs = builderClass.defs.appendList(of(
                createVariable(treeMaker, fieldName, PRIVATE, fieldType),
                createBuilderSetterMethod(fieldName, builderClassName,
                        fieldParameterName, fieldType)));
        clientConstructor.args = clientConstructor.args.append(treeMaker.Ident(fieldName));
    }

    private JCTree.JCExpression processField2(JCTree.JCExpression clientToBuilderMethodReturnFieldSetterCall,
                                              JCTree.JCNewClass builderConstructorCall, Name fieldName,
                                              int fieldNumber) {
        if (fieldNumber == 0) {
            return treeMaker.Exec(
                    treeMaker.Apply(nil(),
                            treeMaker.Select(builderConstructorCall, fieldName),
                            of(treeMaker.Ident(fieldName)))).expr;
        } else {
            return treeMaker.Exec(
                    treeMaker.Apply(nil(),
                            treeMaker.Select(clientToBuilderMethodReturnFieldSetterCall, fieldName),
                            of(treeMaker.Ident(fieldName)))).expr;
        }
    }

    private JCTree.JCMethodDecl createBuilderSetterMethod(Name fieldName, Name builderClassName,
                                                          Name fieldParameterName,
                                                          JCTree.JCExpression fieldType) {
        return createMethod(treeMaker, fieldName, PUBLIC, treeMaker.Ident(builderClassName),
                of(createVariable(treeMaker, fieldParameterName, PARAMETER, fieldType)),
                of(treeMaker.Exec(
                        treeMaker.Assign(
                                treeMaker.Select(
                                        treeMaker.Ident(names._this),
                                        fieldName),
                                treeMaker.Ident(fieldParameterName))),
                        treeMaker.Return(treeMaker.Ident(names._this))));
    }

    private JCTree.JCMethodDecl createClientBuilderMethod(Name builderClassName) {
        Name clientBuilderMethodName = names.fromString("builder");
        return createMethod(treeMaker, clientBuilderMethodName, PUBLIC | STATIC,
                treeMaker.Ident(builderClassName), nil(),
                of(treeMaker.Return(
                        createConstructorCall(treeMaker, treeMaker.Ident(builderClassName), nil()))));
    }

    private JCTree.JCMethodDecl createClientToBuilderMethod(Name builderClassName,
                                                            JCTree.JCExpression clientToBuilderMethodReturnFieldSetterCall) {
        Name clientToBuilderMethodName = names.fromString("toBuilder");
        return createMethod(treeMaker, clientToBuilderMethodName, PUBLIC,
                treeMaker.Ident(builderClassName), nil(),
                of(treeMaker.Return(clientToBuilderMethodReturnFieldSetterCall)));
    }

    private static JCTree.JCVariableDecl createVariable(TreeMaker treeMaker, Name name, long modifiers,
                                                        JCTree.JCExpression type) {
        return treeMaker.VarDef(treeMaker.Modifiers(modifiers), name, type, null);
    }

    private static JCTree.JCMethodDecl createMethod(TreeMaker treeMaker, Name name, long modifiers,
                                                    JCTree.JCExpression returnType,
                                                    com.sun.tools.javac.util.List<JCTree.JCVariableDecl> params,
                                                    com.sun.tools.javac.util.List<JCTree.JCStatement> bodyStatements) {
        return treeMaker.MethodDef(treeMaker.Modifiers(modifiers), name, returnType, nil(),
                params, nil(), treeMaker.Block(0, bodyStatements), null);
    }

    private static JCTree.JCNewClass createConstructorCall(TreeMaker treeMaker, JCTree.JCExpression cls,
                                                           com.sun.tools.javac.util.List<JCTree.JCExpression> params) {
        return treeMaker.NewClass(null, nil(), cls, params, null);
    }

    // just shorter name
    private static <A> com.sun.tools.javac.util.List<A> nil() {
        return com.sun.tools.javac.util.List.nil();
    }

    // just shorter name
    private static <A> com.sun.tools.javac.util.List<A> of(A x1) {
        return com.sun.tools.javac.util.List.of(x1);
    }

    // just shorter name
    private static <A> com.sun.tools.javac.util.List<A> of(A x1, A x2) {
        return com.sun.tools.javac.util.List.of(x1, x2);
    }
}