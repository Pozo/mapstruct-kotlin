package com.github.pozo;

import org.mapstruct.ap.spi.BuilderInfo;
import org.mapstruct.ap.spi.BuilderProvider;
import org.mapstruct.ap.spi.DefaultBuilderProvider;
import org.mapstruct.ap.spi.MapStructProcessingEnvironment;
import org.mapstruct.ap.spi.TypeHierarchyErroneousException;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.List;
import java.util.stream.Collectors;

public class KotlinBuilderProvider extends DefaultBuilderProvider implements BuilderProvider {

    @Override
    public void init(MapStructProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    @Override
    public BuilderInfo findBuilderInfo(TypeMirror type) {
        final TypeElement typeElement = getTypeElement(type);
        if (typeElement == null) {
            return null;
        }
        if (isAnnotatedByKotlin(typeElement) && isAnnotatedByKotlinBuilder(typeElement)) {
            return findBuilder(typeElement);
        } else {
            return super.findBuilderInfo(type);
        }
    }

    private BuilderInfo findBuilder(TypeElement typeElement) {
        final TypeElement builder = asBuilderElement(typeElement);
        if (builder == null) {
            throw new TypeHierarchyErroneousException(typeElement);
        }

        List<ExecutableElement> builderMethods = ElementFilter.methodsIn(builder.getEnclosedElements())
                .stream()
                .filter(executableElement -> "create".equals(executableElement.getSimpleName().toString()))
                .collect(Collectors.toList());
        List<ExecutableElement> builderCreatonMethods = ElementFilter.methodsIn(builder.getEnclosedElements())
                .stream()
                .filter(executableElement -> "builder".equals(executableElement.getSimpleName().toString()))
                .collect(Collectors.toList());

        return new BuilderInfo.Builder()
                .builderCreationMethod(builderCreatonMethods.get(0))
                .buildMethod(builderMethods)
                .build();
    }

    private TypeElement asBuilderElement(TypeElement typeElement) {
        Element enclosingElement = typeElement.getEnclosingElement();
        StringBuilder builderQualifiedName = new StringBuilder();
        if (enclosingElement.getKind() == ElementKind.PACKAGE) {
            builderQualifiedName.append(((PackageElement) enclosingElement).getQualifiedName().toString());
        } else {
            builderQualifiedName.append(((TypeElement) enclosingElement).getQualifiedName().toString());
        }

        if (builderQualifiedName.length() > 0) {
            builderQualifiedName.append(".");
        }

        builderQualifiedName.append(typeElement.getSimpleName()).append("Builder");
        return elementUtils.getTypeElement(builderQualifiedName);
    }

    private boolean isAnnotatedByKotlin(TypeElement typeElement) {
        return typeElement.getAnnotationMirrors()
                .stream()
                .anyMatch(o -> "kotlin.Metadata".equals(o.getAnnotationType().toString()));
    }

    private boolean isAnnotatedByKotlinBuilder(TypeElement typeElement) {
        return typeElement.getAnnotationMirrors()
                .stream()
                .anyMatch(o -> "com.github.pozo.KotlinBuilder".equals(o.getAnnotationType().toString()));
    }
}

