# Learn Maven on Windows: build and run a banking app

This guide takes you from an empty Windows laptop to a running banking app built with Maven.
Do the steps in order. Each step ends with a check so you know it worked.

---

## Part 1. What Maven is (5 minute read)

Maven is a **build tool** for Java. Instead of running `javac` and `jar` by hand, you describe
your project once in a file called **`pom.xml`**, and Maven does the rest:

| Without Maven | With Maven |
|---|---|
| Download library JARs yourself (JUnit, etc.) | List them in `pom.xml`; Maven downloads them |
| Run `javac` on every file | `mvn compile` |
| Run tests by hand | `mvn test` |
| Build a JAR by hand | `mvn package` |

Four ideas are all you need to start:

1. **The POM (`pom.xml`)**: the project's description. It has *coordinates*
   (`groupId`, `artifactId`, `version`), *dependencies* (libraries you use), and *plugins* (tools that do the building).
2. **Standard folder layout**: Maven expects code in fixed places, so you never configure paths:
   ```
   banking-app/
   ├─ pom.xml
   └─ src/
      ├─ main/java/...   ← your application code
      └─ test/java/...   ← your tests
   ```
   Maven writes everything it builds into `target/`.
3. **The lifecycle**: a fixed sequence of *phases*. Running a phase runs every phase before it:
   `validate → compile → test → package → verify → install → deploy`
   So `mvn package` also compiles and tests. `clean` is separate and deletes `target/`.
4. **The local repository**: downloaded libraries are cached in
   `C:\Users\<you>\.m2\repository`, so the first build is slow and later builds are fast.

---

## Part 2. Install the JDK (Java)

1. Download **Eclipse Temurin JDK 21 (LTS)** for Windows x64, the `.msi` installer:
   https://adoptium.net/temurin/releases/?os=windows&package=jdk
2. Run the installer. On the "Custom Setup" screen, turn on
   **"Set JAVA_HOME variable"** and **"Add to PATH"** (click each and pick *"Will be installed on local hard drive"*).
3. **Check:** open a **new** Command Prompt (Win key, type `cmd`, Enter) and run:
   ```
   java -version
   ```
   You should see `openjdk version "21..."`.

   Also run `echo %JAVA_HOME%`. It should print a folder like
   `C:\Program Files\Eclipse Adoptium\jdk-21...`. If it prints `%JAVA_HOME%`, see Part 3 step 3.

> The project compiles for Java 17, so JDK 17 or any newer JDK also works.

---

## Part 3. Install Maven

1. Download the **Binary zip archive** (`apache-maven-3.9.x-bin.zip`) from
   https://maven.apache.org/download.cgi
2. Unzip it to a simple path, for example `C:\tools\apache-maven-3.9.11`
   (that folder should directly contain `bin`, `conf`, `lib`).
3. Set environment variables:
   - Press the Win key, type **"environment variables"**, open
     **"Edit the system environment variables"** → **Environment Variables…**
   - Under *User variables*, click **New**: name `MAVEN_HOME`, value `C:\tools\apache-maven-3.9.11`
   - If `JAVA_HOME` is missing, add it the same way, pointing at your JDK folder.
   - Select **Path** → **Edit** → **New** → type `%MAVEN_HOME%\bin` → OK, OK, OK.
4. **Check:** close every Command Prompt, open a new one, and run:
   ```
   mvn -v
   ```
   You should see `Apache Maven 3.9.x` and your Java version. 

**If it fails:**
- `'mvn' is not recognized` → the Path entry is wrong or you didn't open a new window.
- `JAVA_HOME environment variable is not defined correctly` → JAVA_HOME must point to the JDK folder itself, **not** its `bin` subfolder.

---

## Part 4. Get the banking project

Download the `maven-banking` folder from this project (or `maven-banking.zip` and unzip it),
and put it somewhere like `C:\projects\maven-banking`. Then:

```
cd C:\projects\maven-banking
dir
```
You should see `pom.xml`, `src`, and this guide.

What's inside:

| File | What it does |
|---|---|
| `pom.xml` | Project coordinates, JUnit dependency, and the plugin that makes the JAR runnable |
| `src/main/java/com/example/bank/Account.java` | One account: deposit, withdraw, balance |
| `src/main/java/com/example/bank/Bank.java` | Holds accounts, opens new ones, transfers money |
| `src/main/java/com/example/bank/InsufficientFundsException.java` | Error for overdrawing |
| `src/main/java/com/example/bank/BankApp.java` | The console menu (the `main` method) |
| `src/test/java/com/example/bank/BankTest.java` | 8 JUnit tests |

Open `pom.xml` and read the comments. Every line is explained.

---

## Part 5. Build it with Maven, one phase at a time

Run each command from the `maven-banking` folder and look at what changes in `target\`.

```
mvn compile
```
Downloads plugins the first time (lots of "Downloading…" lines, that's normal), then compiles
`src/main/java` into `target\classes`. Ends with **BUILD SUCCESS**.

```
mvn test
```
Compiles the tests and runs them. Look for:
```
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

```
mvn package
```
Creates **`target\banking-app-1.0.0.jar`**. The name comes from `artifactId` + `version` in the POM.

```
mvn clean package
```
The command you'll use most: wipe `target\` and rebuild from scratch.

**Try breaking a test** to see Maven protect you: in `BankTest.java`, change `"60"` to `"61"`
in `withdrawDecreasesBalance`, run `mvn package`, and watch it fail with **BUILD FAILURE**
and no JAR. Change it back.

---

## Part 6. Run ("deploy") the app on your laptop

```
java -jar target\banking-app-1.0.0.jar
```

A sample session:
```
=== Simple Bank ===
1) Open account   2) Deposit   3) Withdraw
4) Transfer       5) Balance   6) List accounts   0) Exit
Choose: 1
Owner name: Rahul
Opened ACC1001 for Rahul
Choose: 2
Account number: ACC1001
Amount: 500
New balance: 500
Choose: 3
Account number: ACC1001
Amount: 9999
Error: Account ACC1001 has only 500, cannot withdraw 9999
Choose: 0
Goodbye!
```

To "deploy" it like a real install, copy the JAR to a folder of its own and run it from there:
```
mkdir C:\apps\bank
copy target\banking-app-1.0.0.jar C:\apps\bank\
java -jar C:\apps\bank\banking-app-1.0.0.jar
```
The JAR is self-contained (it has no runtime dependencies), so it runs anywhere Java is installed.

Optionally run `mvn install`: it copies the JAR into your local repository
(`%USERPROFILE%\.m2\repository\com\example\banking-app\1.0.0\`), which is how other Maven
projects on your laptop could use it as a dependency.

> Accounts live in memory, so they disappear when you exit. Saving to a file or database is a good next exercise.

---

## Part 7. Exercises to make it stick

1. **Change the version** in `pom.xml` to `1.1.0`, run `mvn clean package`, and notice the new JAR name.
2. **Add a feature**: an `Account.getOwnerInitials()` method plus a test for it. Run `mvn test`.
3. **Add a dependency**: add Google Gson (`com.google.code.gson:gson`) to the POM, run
   `mvn dependency:tree`, and find it in `%USERPROFILE%\.m2\repository`.
4. **Run one test only**: `mvn test -Dtest=BankTest#transferMovesMoney`
5. **Skip tests** (useful, but don't make it a habit): `mvn package -DskipTests`

---

## Cheat sheet

| Command | What it does |
|---|---|
| `mvn -v` | Show Maven and Java versions |
| `mvn compile` | Compile main code |
| `mvn test` | Compile and run tests |
| `mvn package` | Compile, test, build the JAR |
| `mvn clean` | Delete `target\` |
| `mvn clean package` | Fresh full build |
| `mvn install` | Package and copy to `~\.m2` |
| `mvn dependency:tree` | Show all libraries and where they come from |
| `mvn -q ...` / `mvn -X ...` | Quieter / very detailed (debug) output |

## Next step: a web version

Once this feels comfortable, the same banking logic can become a **Spring Boot** web app
(REST endpoints like `POST /accounts/{id}/deposit`) built with the same `mvn package`
and run with `java -jar`. That's a natural Part 2 when you're ready.
