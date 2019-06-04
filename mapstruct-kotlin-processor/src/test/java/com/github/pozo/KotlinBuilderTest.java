package com.github.pozo;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.Arrays;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KotlinBuilderTest {

    protected TypeElement typeElement(AnnotationMirror... annotationMirror) {
        TypeElement typeElement = mock(TypeElement.class);
        doReturn(Arrays.asList(annotationMirror)).when(typeElement).getAnnotationMirrors();
        return typeElement;
    }

    protected AnnotationMirror annotationMirror(DeclaredType declaredType) {
        AnnotationMirror kotlinBuilder = mock(AnnotationMirror.class);
        when(kotlinBuilder.getAnnotationType()).thenReturn(declaredType);
        return kotlinBuilder;
    }

    protected DeclaredType declaredType(String toString) {
        DeclaredType kotlinBuilderType = mock(DeclaredType.class);
        when(kotlinBuilderType.toString()).thenReturn(toString);
        return kotlinBuilderType;
    }

}
