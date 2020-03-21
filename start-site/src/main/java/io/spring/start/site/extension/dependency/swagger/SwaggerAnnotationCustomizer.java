package io.spring.start.site.extension.dependency.swagger;

import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.java.JavaSourceCode;
import io.spring.initializr.generator.spring.code.MainSourceCodeCustomizer;

public class SwaggerAnnotationCustomizer implements MainSourceCodeCustomizer {

    @Override
    public void customize(SourceCode sourceCode) {
        ((JavaSourceCode) sourceCode).getCompilationUnits().get(0)
                .getTypeDeclarations().get(0).annotate(Annotation.name("springfox.documentation.swagger2.annotations.EnableSwagger2"));
    }
}
