package com.github.pozo;

import org.junit.Before;
import org.junit.Test;

import javax.lang.model.element.TypeElement;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.spy;

public class KotlinBuilderProviderTest extends KotlinBuilderTest {

    private KotlinBuilderProvider underTest;

    @Before
    public void init() {
        underTest = spy(KotlinBuilderProvider.class);
    }

    @Test
    public void isKotlinBuilder_When_There_Is_Just_Kotlin_Data_Class() {
        // GIVEN
        TypeElement typeElement = typeElement(
                annotationMirror(
                        declaredType("kotlin.Metadata")
                )
        );
        // WHEN
        final boolean builderInfo = underTest.isKotlinBuilder(typeElement);

        // THEN
        assertFalse(builderInfo);
    }

    @Test
    public void isKotlinBuilder_When_There_Is_Just_Kotlin_Builder_Annotation() {
        // GIVEN
        TypeElement typeElement = typeElement(
                annotationMirror(
                        declaredType("com.github.pozo.KotlinBuilder")
                )
        );

        // WHEN
        final boolean builderInfo = underTest.isKotlinBuilder(typeElement);

        // THEN
        assertFalse(builderInfo);
    }

    @Test
    public void isKotlinBuilderTest() {
        // GIVEN
        TypeElement typeElement = typeElement(
                annotationMirror(
                        declaredType("kotlin.Metadata")
                ),
                annotationMirror(
                        declaredType("com.github.pozo.KotlinBuilder")
                )
        );
        // WHEN
        final boolean builderInfo = underTest.isKotlinBuilder(typeElement);

        // THEN
        assertTrue(builderInfo);
    }
}