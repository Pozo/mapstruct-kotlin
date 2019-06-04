package com.github.pozo

import com.github.pozo.BuilderGenerator.Builder.addBuilderMethod
import com.github.pozo.BuilderGenerator.Create.addCreateMethod
import com.github.pozo.BuilderGenerator.Field.addPrivateField
import com.github.pozo.BuilderGenerator.Setter.addSetterMethod
import com.github.pozo.BuilderGenerator.readHeader
import com.google.auto.service.AutoService
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import kotlinx.metadata.Flag
import kotlinx.metadata.Flags
import kotlinx.metadata.KmClassVisitor
import kotlinx.metadata.jvm.KotlinClassMetadata
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter
import kotlin.streams.toList


@AutoService(Processor::class)
class BuilderProcessor : AbstractProcessor() {

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(KotlinBuilder::class.java)
            .filterIsInstance<TypeElement>()
            .filter { isAnnotatedByKotlin(it) }
            .filter { isDataClass(it) }
            .forEach { generateBuilder(it) }

        return true
    }

    private fun isAnnotatedByKotlin(it: TypeElement): Boolean {
        return it.annotationMirrors
            .stream()
            .filter { Metadata::class.java.canonicalName == it.annotationType.toString() }
            .count() > 0
    }

    private fun isDataClass(it: TypeElement): Boolean {
        val kotlinClassHeader = it.readHeader()
        val kotlinClassMetadata = KotlinClassMetadata.read(kotlinClassHeader)

        var isDataClass = false
        when (kotlinClassMetadata) {
            is KotlinClassMetadata.Class -> {
                kotlinClassMetadata.accept(object : KmClassVisitor() {
                    override fun visit(flags: Flags, name: kotlinx.metadata.ClassName) {
                        isDataClass = Flag.Class.IS_DATA(flags)
                    }
                })
            }
        }
        return isDataClass
    }

    private fun generateBuilder(typeElement: TypeElement) {
        val packageName = processingEnv.elementUtils.getPackageOf(typeElement).toString()
        val builderClassName = "${typeElement.simpleName}Builder"
        val classBuilder = TypeSpec.classBuilder(builderClassName)

        with(classBuilder) {
            this.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            val constructors = ElementFilter.constructorsIn(typeElement.enclosedElements)

            constructors.stream()
                .flatMap { it.parameters.stream() }
                .peek { this.addSetterMethod(it, packageName, builderClassName) }
                .map { this.addPrivateField(it) }
                .toList()
                .apply {
                    this@with.addCreateMethod(typeElement, this)
                    this@with.addBuilderMethod(packageName, builderClassName)
                }
            this
        }.let {
            JavaFile.builder(
                packageName,
                it.build()
            )
        }.apply {
            this.build().writeTo(processingEnv.filer)
        }
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(KotlinBuilder::class.java.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_8
    }

    override fun getSupportedOptions(): Set<String> {
        return setOf("kapt.kotlin.generated")
    }
}