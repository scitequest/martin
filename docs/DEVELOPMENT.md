# Development

## Working with the project

### Run the standalone directly

Maven can automatically start MARTin in standalone mode.
To do so, run Maven with the following arguments:

```bash
mvn -Pexec
```

### Build the plugin

Building the plugin and its dependencies can be accomplished by using the Maven:

```bash
mvn -Dscijava.app.directory=path/to/imagej/directory
```

After executing this command, the plugin JAR and all dependency JARs are in the
ImageJ plugin directory.

When using Linux the following `-Dscijava.app.directory="$HOME/.imagej"` can be
used to specify the plugin directory.

Note: since ImageJ2 still uses Java 8, we have to use our own Java 11 JRE to use
out plugin:

```bash
./ImageJ-linux64 --system --default-gc
```

Alternatively, a standalone JAR file can be built. This fully contains ImageJ
with around 150 MB. This is archived with the following command:

```bash
mvn package -P uberjar
```

The resulting standalone JAR is in the `target` directory.

### Build the standalone as JAR

The standalone is integrated in the plugin uberjar. Therefore, the same commands can be used:

```bash
mvn package -P uberjar
```

### Build an installer for the current operating system

We can create installers for the operating systems currently in use.
A Java 11 runtime is mandatory to build working installers.

#### Example on Fedora

Install the latest OpenJDK and version 11 as JRE using:

```bash
sudo dnf install java-11-openjdk-jmods
sudo dnf install java-latest-openjdk
```

Once installed we must tell Maven where to find the runtime and to use the
latest JDK:

```bash
export JAVA_HOME=/etc/alternatives/java_sdk_19_openjdk/
export JRE_HOME=/etc/alternatives/java_sdk_11_openjdk/
```

Then we can build the actual installer:

```bash
mvn clean install -P build-linux
```

The installer is in the `target/installer` directory.

### Viewing project metrics such as JavaDoc

To view JavaDoc/Checkstyle/SpotBugs/JaCoCo results first create the Maven
project information site. To do so run:

```bash
mvn test site

# Conveniently start Firefox and open the webpage
firefox target/site/index.html
```

The `index.html` residing in `target/site` can then be opened with a browser of
your choice.

## Troubleshooting

Maven is just an abomination of software. Don't be surprised if it craps out. If
it does or something unexpected happens let it clean its mess up and try again:

```bash
mvn clean
```
