package com.github.pozo

import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import kotlinx.metadata.Flag
import kotlinx.metadata.Flags
import kotlinx.metadata.KmClassVisitor
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import org.slf4j.LoggerFactory
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter


@AutoService(Processor::class)
class BuilderProcessor : AbstractProcessor() {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        logger.info("roundEnv = $roundEnv")

        roundEnv.getElementsAnnotatedWith(KotlinBuilder::class.java)
            .filterIsInstance<TypeElement>()
            .filter { it -> isAnnotatedByKotlin(it) }
            .filter { it -> isDataClass(it) }
            .forEach { generateBuilder(it) }

        return true
    }

    private fun isAnnotatedByKotlin(it: TypeElement): Boolean {
        return it.annotationMirrors
                .stream()
                .filter { Metadata::class.java.canonicalName.equals(it.annotationType.toString()) }
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

    private fun generateBuilder(it: TypeElement) {
        val builderClassName = "${it.simpleName}Builder"
        val builder = TypeSpec.classBuilder(builderClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

        val packageName = processingEnv.elementUtils.getPackageOf(it)
        val constructors = ElementFilter.constructorsIn(it.enclosedElements)
        val fields = mutableListOf<FieldSpec>()

        constructors.stream().flatMap { it.parameters.stream() }
                .forEach {
                    val fieldType = ClassName.get(it.asType())
                    val fieldName = it.simpleName.toString()
                    val field = FieldSpec.builder(fieldType, fieldName, Modifier.PRIVATE).build()
                    builder.addField(field)
                    fields.add(field)

                    val setter = MethodSpec.methodBuilder("set${fieldName.capitalize()}")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(ClassName.get(packageName.toString(), builderClassName))
                            .addParameter(fieldType, fieldName)
                            .addStatement("this.\$N = \$N", fieldName, fieldName)
                            .addStatement("return this")
                            .build()
                    builder.addMethod(setter)
                }
        val guessedReturnValueType = ClassName.bestGuess("${it.simpleName}")
        val createMethod = createMethodSpec(guessedReturnValueType, fields)
        builder.addMethod(createMethod)

        val builderMethod = builderMethodSpec(packageName, builderClassName)
        builder.addMethod(builderMethod)


        val javaFile = JavaFile.builder(
                packageName.toString(),
                builder.build()
        ).build()

        javaFile.writeTo(processingEnv.filer)

    }

    private fun createMethodSpec(guessedReturnValueType: ClassName?, fields: MutableList<FieldSpec>): MethodSpec? {
        return MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC)
                .returns(guessedReturnValueType)
                .addStatement("return new \$T(\$L)", guessedReturnValueType, fields.joinToString { it.name })
                .build()
    }

    private fun builderMethodSpec(packageOf: PackageElement, builderClassName: String): MethodSpec? {
        return MethodSpec.methodBuilder("builder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get(packageOf.toString(), builderClassName))
                .addStatement("return new \$T()", ClassName.get(packageOf.toString(), builderClassName))
                .build()
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

    // https://github.com/JetBrains/kotlin/tree/master/libraries/kotlinx-metadata/jvm
    // https://github.com/square/moshi/pull/570/files
    @Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
    fun Element.readHeader(): KotlinClassHeader {
        return getAnnotation(Metadata::class.java).run {
            KotlinClassHeader(kind, metadataVersion, bytecodeVersion, data1, data2, extraString, packageName, extraInt)
        }
    }
}