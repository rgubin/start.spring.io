package io.spring.start.site.extension.code.java.realpage;

import io.spring.initializr.generator.language.Parameter;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.java.JavaCompilationUnit;
import io.spring.initializr.generator.language.java.JavaExpressionStatement;
import io.spring.initializr.generator.language.java.JavaMethodDeclaration;
import io.spring.initializr.generator.language.java.JavaMethodInvocation;
import io.spring.initializr.generator.language.java.JavaSourceCode;
import io.spring.initializr.generator.language.java.JavaTypeDeclaration;
import io.spring.initializr.generator.spring.code.TestSourceCodeCustomizer;

import java.lang.reflect.Modifier;

public class JavaRPTestsSourceCodeCustomizer implements TestSourceCodeCustomizer<JavaTypeDeclaration, JavaCompilationUnit, JavaSourceCode> {

    //@Override
    public void custyomize(SourceCode sourceCode) {
        JavaTypeDeclaration typeDeclaration;
        typeDeclaration = ((JavaSourceCode) sourceCode).getCompilationUnits().get(0).getTypeDeclarations().get(0);
        typeDeclaration.addMethodDeclaration(
                JavaMethodDeclaration.method("main2").modifiers(Modifier.PUBLIC | Modifier.STATIC).returning("void")
                        .parameters(new Parameter("java.lang.String[]", "args"))
                        .body(new JavaExpressionStatement(
                                new JavaMethodInvocation("org.springframework.boot.SpringApplication", "run",
                                        typeDeclaration.getName() + ".class", "args"))));
    }

    @Override
    public void customize(JavaSourceCode sourceCode) {
        ((JavaSourceCode) sourceCode).getCompilationUnits().get(0);
    }
}
