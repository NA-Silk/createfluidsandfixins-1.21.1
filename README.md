
Installation information for Create: Fluid and Fixins
=======

This template repository can be directly cloned to get you started with a new
mod. Simply create a new repository cloned from this one, by following the
instructions provided by [GitHub](https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-repository-from-a-template).

Once you have your clone, simply open the repository in the IDE of your choice. The usual recommendation for an IDE is either IntelliJ IDEA or Eclipse.

If at any point you are missing libraries in your IDE, or you've run into problems you can
run `gradlew --refresh-dependencies` to refresh the local cache. `gradlew clean` to reset everything 
{this does not affect your code} and then start the process again.

Mapping Names:
============
By default, the MDK is configured to use the official mapping names from Mojang for methods and fields 
in the Minecraft codebase. These names are covered by a specific license. All modders should be aware of this
license. For the latest license text, refer to the mapping file itself, or the reference copy here:
https://github.com/NeoForged/NeoForm/blob/main/Mojang.md

Additional Resources: 
==========
Community Documentation: https://docs.neoforged.net/  
NeoForged Discord: https://discord.neoforged.net/

Requirements:
==========
1. Install [IntelliJ IDEA](https://www.jetbrains.com/idea/download/?section=windows)
    - Update PATH variable (restart needed) := CHECK Add "bin" folder to the PATH
2. Install [Temurin jdk-21.0.11+10](https://adoptium.net/temurin/releases/?version=21&os=windows&arch=any&mode=filter)
    - Set or override JAVA_HOME variable := Will be installed on local hard drive
3. Clone repository
4. Set "Project Structure... > Project Settings > Project > Language Level" := 21
5. Set "Project Structure... > Project Settings > Project > SDK" := temurin-21
6. Set "Settings... > Build, Execution, Deployment > Build Tools > Gradle > Gradle JVM" := Project SDK
7. Press "Build"
