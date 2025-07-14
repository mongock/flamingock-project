# GraalVM Native Image: Build‑Time Class Initialization vs. Reflection Registration

*Version 2025‑07‑14*

---

## 1. Why does GraalVM care?

When GraalVM turns your JVM application into a native executable it **closes the world**:  
only the classes, resources and metadata that the build step can _see_ are kept.  
Anything the program tries to load or reflect on at **run‑time** must therefore be
declared _in advance_.  
Two main levers exist for that purpose:

| Lever                               | Purpose                                                                             | One‑liner                                 |
|-------------------------------------|-------------------------------------------------------------------------------------|-------------------------------------------|
| **Build‑time class initialisation** | Decide **_when_** a class’s static initialiser (`<clinit>`) runs.                   | “Freeze my static state into the binary.” |
| **Reflection registration**         | Decide **_whether_** run‑time reflection will work for the listed program elements. | “Keep metadata so I can call it later.”   |

---

## 2. `RuntimeClassInitialization.initializeAtBuildTime(...)`

### What it does
* Runs the class’s static block **while the image is being built**.  
* Serialises the resulting static fields & caches into the binary.  
* Marks the class as *already initialised* so `<clinit>` is _skipped_ on start‑up.

### Use it when …

| Symptom                                                                       | Why build‑time init helps                      |
|-------------------------------------------------------------------------------|------------------------------------------------|
| Static block touches reflection, files, environment, security‑sensitive APIs. | All of these work freely during the build.     |
| Static block does heavy work (JSON parsing, regex compilation, data loading). | Pushes the cost to CI/CD, shortens cold‑start. |
| Native‑image build aborts with `UnsupportedFeatureError: ClassInitializer`.   | GraalVM refuses to run that code later.        |

### Avoid it when …

| Scenario                                                              | Reason                                                   |
|-----------------------------------------------------------------------|----------------------------------------------------------|
| Static fields must read run‑time values (time‑zone, config, secrets). | You would freeze the _wrong_ values into the executable. |
| Code is part of a library used by others.                             | Consumers may want different rules.                      |

### Mini example

```java
public class CountryData {
    static final Map<String, Pattern> ISO_PATTERNS = load();
    private static Map<String, Pattern> load() {
        // Heavy reflection & regex compilation
    }
}
```

```java
class MyFeature implements Feature {
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        RuntimeClassInitialization.initializeAtBuildTime(CountryData.class);
    }
}
```

---

## 3. `RuntimeReflection.register(...)`

### What it does
* Preserves *metadata* (names, modifiers, generics, annotations) for the listed
  classes, methods or fields.
* Allows ordinary reflection at run‑time (`Class.forName`, `getMethod`, `invoke`, …).

### Use it when …

| Symptom                                                                              | Cause                           |
|--------------------------------------------------------------------------------------|---------------------------------|
| `NoSuchMethodException`, `IllegalAccessException` **only inside** the native binary. | GraalVM discarded the metadata. |
| Framework scans annotations at run‑time (Spring, Micro‑Profile, migration tools).    | The scan relies on reflection.  |

### Avoid it when …

| Scenario                                                                         | Fix instead                                                    |
|----------------------------------------------------------------------------------|----------------------------------------------------------------|
| Reflection happens only in a static initialiser that you can move to build‑time. | Use `initializeAtBuildTime` to remove the need for reflection. |
| You can switch to direct (non‑reflective) calls.                                 | Prefer compile‑time safety and smaller binaries.               |

### Mini example

```java
// during Feature.beforeAnalysis or reflection‑config JSON
RuntimeReflection.register(
        MyEntity.class,
        MyEntity.class.getDeclaredField("id"),
        MyEntity.class.getDeclaredMethod("toString")
);
```

---

## 4. Decision matrix (cheat‑sheet)

| Question                                                      | Recommended action                                                              |
|---------------------------------------------------------------|---------------------------------------------------------------------------------|
| Does the code path run **before `main`** (static init)?       | Prefer `initializeAtBuildTime`.                                                 |
| Does the code path run **after `main`** and needs reflection? | Register the elements.                                                          |
| Static state depends on run‑time data?                        | Do **not** use build‑time init; register instead.                               |
| Unsure what is missing?                                       | Build with `--trace-class-initialization` or the **Agent** to generate configs. |

---

## 5. Real‑world scenario: Migration framework

| Phase               | Old version                                       | After refactor                                                                 |
|---------------------|---------------------------------------------------|--------------------------------------------------------------------------------|
| Reflection executed | Inside `getExecutionMethod()` (run‑time).         | In a `static final` field of helper (build‑time).                              |
| Needed fix          | `RuntimeReflection.register(ChangeUnit.class, …)` | `RuntimeClassInitialization.initializeAtBuildTime(CodeLoadedChangeUnit.class)` |
| Why                 | Metadata needed later.                            | Work finished early; reflection no longer required at run‑time.                |

Code sketch:

```java
// build‑time feature
RuntimeClassInitialization.initializeAtBuildTime(CodeLoadedChangeUnit.class);
```

The heavy annotation scan runs once during the image build; the resulting
`Method` objects are embedded in the executable.

---

## 6. Practical tips for teams

1. **Start with the tracing agent**  
   ```bash
   java -agentlib:native-image-agent=config-output-dir=graal
   ```  
   Run your tests, then feed the generated JSON to the native‑image build.  
2. **Keep your _feature_ layer thin**  
   Apply rules only for your own classes; let libraries ship their own configs.  
3. **Measure binary size periodically**  
   Over‑registering classes quickly bloats the executable.  
4. **Document your choice**  
   A single comment in the feature explaining *why* a class is build‑time
   initialised saves hours of debugging later.

---

## 7. One‑page summary

> *“Initialise at build‑time if the static block is tricky or expensive.  
> Register for reflection if you actually call it at run‑time.  
> Do both only when necessary, and never freeze values that must stay dynamic.”*

---

