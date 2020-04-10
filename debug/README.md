## This module is responsible for help the debugging of the kotlin-processor

First you have to create a `Remote run configuration` in IntelliJ

    Host: localhost
    Port: 5005
    Command line arguments: -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
    
Then run the following command

    gradle --no-daemon -Dorg.gradle.debug=true clean build
    
And run the previously created Remote configuration in `Debug` mode. Please note that without the `kapt.use.worker.api=true` entry in the `gradle.properties` it won't work. 