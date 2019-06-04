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

    private static final String BUILDER_CLASS_POSTFIX = "Builder";

    private static final String CREATE_METHOD_NAME = "create";

    private static final String BUILDER_METHOD_NAME = "builder";

    private static final String KOTLIN_METADATA_CLASS_NAME = "kotlin.Metadata";

    private static final String KOTLIN_BUILDER_ANNOTATION_CLASS_NAME = "com.github.pozo.KotlinBuilder";

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
        if (isKotlinBuilder(typeElement)) {
            return findBuilder(typeElement);
        } else {
            return super.findBuilderInfo(type);
        }
    }

    boolean isKotlinBuilder(TypeElement typeElement) {
        return isAnnotatedByKotlin(typeElement) && isAnnotatedByKotlinBuilder(typeElement);
    }

    BuilderInfo findBuilder(TypeElement typeElement) {
        final TypeElement builder = asBuilderElement(typeElement);
        if (builder == null) {
            throw new TypeHierarchyErroneousException(typeElement);
        }

        List<ExecutableElement> builderMethods = ElementFilter.methodsIn(builder.getEnclosedElements())
                .stream()
                .filter(executableElement -> CREATE_METHOD_NAME.equals(executableElement.getSimpleName().toString()))
                .collect(Collectors.toList());

        List<ExecutableElement> builderCreatonMethods = ElementFilter.methodsIn(builder.getEnclosedElements())
                .stream()
                .filter(executableElement -> BUILDER_METHOD_NAME.equals(executableElement.getSimpleName().toString()))
                .collect(Collectors.toList());

        return new BuilderInfo.Builder()
                .builderCreationMethod(builderCreatonMethods.get(0))
                .buildMethod(builderMethods)
                .build();
    }

    // from org.mapstruct.ap.spi.ImmutablesBuilderProvider
    TypeElement asBuilderElement(TypeElement typeElement) {
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

        builderQualifiedName.append(typeElement.getSimpleName()).append(BUILDER_CLASS_POSTFIX);
        return elementUtils.getTypeElement(builderQualifiedName);
    }

    private boolean isAnnotatedByKotlin(TypeElement typeElement) {
        return typeElement.getAnnotationMirrors()
                .stream()
                .anyMatch(annotationMirror -> KOTLIN_METADATA_CLASS_NAME.equals(annotationMirror.getAnnotationType().toString()));
    }

    private boolean isAnnotatedByKotlinBuilder(TypeElement typeElement) {
        return typeElement.getAnnotationMirrors()
                .stream()
                .anyMatch(annotationMirror -> KOTLIN_BUILDER_ANNOTATION_CLASS_NAME.equals(annotationMirror.getAnnotationType().toString()));
    }
}

