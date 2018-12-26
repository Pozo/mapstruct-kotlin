package com.github.pozo

import com.google.auto.service.AutoService
import com.squareup.javapoet.*
import org.slf4j.LoggerFactory
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter


@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(BuilderProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class BuilderProcessor : AbstractProcessor() {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        logger.info("roundEnv = $roundEnv")

        val annotatedKotlinClasses = roundEnv.getElementsAnnotatedWith(KotlinBuilder::class.java)
                .filterIsInstance<TypeElement>()
                .filter { it -> isAnnotatedByKotlin(it) }
                .filter { it -> isDataClass(it) }


        generateBuilders(annotatedKotlinClasses)
        return true
    }

    private fun isAnnotatedByKotlin(it: TypeElement): Boolean {
        return it.annotationMirrors
                .stream()
                .filter { Metadata::class.java.canonicalName.equals(it.annotationType.toString()) }
                .count() > 0
    }

    private fun isDataClass(it: TypeElement): Boolean {
        // TODO Improve data class filtering
        return ElementFilter.methodsIn(it.enclosedElements)
                .stream()
                .filter {
                    it.simpleName
                            .toString()
                            .startsWith("component")
                }
                .count() > 0
    }

    private fun generateBuilders(typeElements: List<TypeElement>) {
        typeElements.forEach {
            generateBuilder(it)
        }
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
                    val fieldType = parseType(it.asType().toString())
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

    private fun parseType(className: String): Class<*> {
        when (className) {
            "boolean" -> return Boolean::class.javaPrimitiveType!!
            "byte" -> return Byte::class.javaPrimitiveType!!
            "short" -> return Short::class.javaPrimitiveType!!
            "int" -> return Int::class.javaPrimitiveType!!
            "long" -> return Long::class.javaPrimitiveType!!
            "float" -> return Float::class.javaPrimitiveType!!
            "double" -> return Double::class.javaPrimitiveType!!
            "char" -> return Char::class.javaPrimitiveType!!
            "void" -> return Void.TYPE
            else -> {
                try {
                    return Class.forName(className)
                } catch (ex: ClassNotFoundException) {
                    throw IllegalArgumentException("Class not found: $className")
                }

            }
        }
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(KotlinBuilder::class.java.canonicalName)
    }
}