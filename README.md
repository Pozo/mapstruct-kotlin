## mapstruct-kotlin

Since mapstruct [1.3.0.Beta2](http://mapstruct.org/news/2018-07-15-mapstruct-1_3_0_Beta1-is-out-with-builder-support/) it's possible to use builders for immutable classes. [According to the documentation](http://mapstruct.org/documentation/dev/reference/html/#mapping-with-builders) you can implement your custom builder provider logic. This project take advantage of this and provide a custom `BuilderProvider` for kotlin data classes.
 
So instead of this ([source](https://github.com/mapstruct/mapstruct-examples/tree/master/mapstruct-kotlin))
```kotlin
data class PersonDto(var firstName: String?, var lastName: String?, var phone: String?, var birthdate: LocalDate?) {
    // Necessary for MapStruct
    constructor() : this(null, null, null, null)
} 
```
We can do this
```kotlin
@KotlinBuilder
data class PersonDto(val firstName: String, val lastName: String, val phone: String, val birthdate: LocalDate)
```   
With a mapper
```kotlin
@Mapper
interface PersonMapper {
    fun map(person: Person): PersonDto
}
```    
#### Usage
First apply kapt plugin
```groovy
apply plugin: 'kotlin-kapt'
```
Then add these to your project as dependency
```groovy
api("com.github.pozo:mapstruct-kotlin:1.3.1.0")
kapt("com.github.pozo:mapstruct-kotlin-processor:1.3.1.0")
```
Check out the directory `example` for a basic usage example.
#### Versioning

For example in case of `1.3.1.0` the first part `1.3.1` is the mapstruct version number and the last digit `0` reserved for future patches.
#### Project structure

 - `mapstruct-kotlin-builder` contains only the `KotlinBuilder` annotation
 - `mapstruct-kotlin-processor` responsible for generating the builders for the kotlin data classes with the help of a custom `DefaultBuilderProvider`
 - `example` responsible for demonstrating this library usage

#### Build and run the example application

    ./gradlew -p example clean build

# TODO 

 - ~~Map with custom types are not working~~
 - ~~Look over [kotlin-builder-annotation](https://github.com/ThinkingLogic/kotlin-builder-annotation) project and replace with class generating module (builder-processor)~~
 - ~~Writing tests~~
 - ~~Versioning and release process~~ 

# Licensing 

Please see LICENSE file

# Contact

Zoltan Polgar - pozo@gmx.com

Please do not hesitate to contact me if you have any further questions.
