## mapstruct-kotlin

Since mapstruct [1.3.0.Beta2](http://mapstruct.org/news/2018-07-15-mapstruct-1_3_0_Beta1-is-out-with-builder-support/) it's possible to use builders for immutable classes. [According to the documentation](http://mapstruct.org/documentation/dev/reference/html/#mapping-with-builders) you can implement your custom builder provider logic. This project take advantage of this and provide a custom `BuilderProvider` for kotlin data classes.
 
So instead of this ([source](https://github.com/mapstruct/mapstruct-examples/tree/master/mapstruct-kotlin))

    data class PersonDto(var firstName: String?, var lastName: String?, var phone: String?, var birthdate: LocalDate?) {
        // Necessary for MapStruct
        constructor() : this(null, null, null, null)
    } 

We can do this

    @KotlinBuilder
    data class PersonDto(val firstName: String, val lastName: String, val phone: String, val birthdate: LocalDate)
    
With a mapper

    @Mapper
    interface PersonMapper {
        fun map(person: Person): PersonDto
    }
    
#### Jitpack release

__Please keep in your mind that this repository still a proof of concept, and absolutely not ready for production. A contribution, idea, discussion is always welcomed__

A version which is using the mapstruct 1.3.0.Beta2 is available on [jitpack.io](https://jitpack.io/#Pozo/mapstruct-kotlin).  

#### Jitpack howto
    
First of all add `maven { url 'https://jitpack.io' }` to your repositories, and then add these to your dependencies

    api("com.github.pozo.mapstruct-kotlin:mapstruct-kotlin:1.3.0.Beta2")
    
    kapt("com.github.pozo.mapstruct-kotlin:mapstruct-kotlin-processor:1.3.0.Beta2")

#### Project structure

 - `annotation` contains only the `KotlinBuilder` annotation
 - `builder-processor` responsible for generating the builders for the kotlin data classes
 - `processor` responsible for extending the `DefaultBuilderProvider` functionality according to the [documentation](http://mapstruct.org/documentation/dev/reference/html/#mapping-with-builders)
 - `test` responsible for demonstrating this library usage

#### Build and run the example application

The building order is important since the `processor` project depends on `builder-processor` and mapstruct. 

    ./gradlew clean build publishToMavenLocal

    ./gradlew -p example clean build

# TODO 

 - ~~Map with custom types are not working~~
 - ~~Look over [kotlin-builder-annotation](https://github.com/ThinkingLogic/kotlin-builder-annotation) project and replace with class generating module (builder-processor)~~
 - Writing tests
 - Versioning and release process 

# Licensing 

Please see LICENSE file

# Contact

Zoltan Polgar - pozo@gmx.com

Please do not hesitate to contact me if you have any further questions.