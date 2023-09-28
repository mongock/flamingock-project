# Spike to support native image

## Scope
This spike is intended to clarify if and how Flamingock can support native images.

## Output
- Flamingock is able to support native images
- It will require some work that will be specified below but absolutely doable
- For the first draft we'll need to disable the proxy

## How I see it working?
- We'll provide a gradle-task/maven-plugin that takes all the changeUnits full className and drops them in a file
- We'll also provide a graal Feature like `FlamingockReflectionGraalVMFeature` which will load and make the classes available for reflections
- The user just needs to specify the resource with the parameter `-H:IncludeResources` when building the native image

## Discussion
The gradle-task/maven-plugin could just create the configuration file GraalVM needs with the changeUnits classes.
However, in the midterm we want to allow proxies, which will probably require inspecting the changeUnit's methods, which is much easier with features, as they allow using reflections at the building image time.

## How to run it
- Build jar with 
```shell
./gradlew build -x test
```

- build native image executable with 
```shell
native-image --no-fallback --features=io.flamingock.examples.community.FlamingockReflectionGraalVMFeature -H:IncludeResources=".*/flamingock/change-units-list.txt$" -jar ./examples/community/standalone-mongodb-sync/build/libs/standalone-mongodb-sync.jar
```
- run executable with
```shell
./standalone-mongodb-sync
```
