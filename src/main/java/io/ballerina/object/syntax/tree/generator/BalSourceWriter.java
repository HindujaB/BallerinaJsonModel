package io.ballerina.object.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.object.model.BallerinaPackage;
import io.ballerina.projects.util.FileUtils;
import org.ballerinalang.formatter.core.Formatter;
import org.ballerinalang.formatter.core.FormatterException;
import org.wso2.ballerinalang.util.RepoUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static io.ballerina.object.syntax.tree.generator.ModelConstants.BALLERINA_TOML;
import static io.ballerina.object.syntax.tree.generator.ModelConstants.DEFAULT_FILENAME;
import static io.ballerina.object.syntax.tree.generator.ModelConstants.DEVCONTAINER;
import static io.ballerina.object.syntax.tree.generator.ModelConstants.DIST_VERSION;
import static io.ballerina.object.syntax.tree.generator.ModelConstants.GITIGNORE;
import static io.ballerina.object.syntax.tree.generator.ModelConstants.GITIGNORE_FILE_NAME;
import static io.ballerina.object.syntax.tree.generator.ModelConstants.ORG_NAME;
import static io.ballerina.object.syntax.tree.generator.ModelConstants.OUTPUT_PATH;
import static io.ballerina.object.syntax.tree.generator.ModelConstants.PKG_DEFAULTS;
import static io.ballerina.object.syntax.tree.generator.ModelConstants.PKG_NAME;
import static io.ballerina.object.syntax.tree.generator.ModelConstants.USER_DIR;

public class BalSourceWriter {

    private final BallerinaPackage.DefaultPackage defaultPackage;
    private final Path outputPath;

    public BalSourceWriter(BallerinaPackage.DefaultPackage pkg, Path outputPath) {
        this.defaultPackage = pkg;
        this.outputPath = outputPath == null ? OUTPUT_PATH : outputPath;
    }

    public void writeDefaultModule(SyntaxTree syntaxTree) {

        try {
            String sourceCode = Formatter.format(syntaxTree).toSourceCode();
            generatePackageDetails(sourceCode);
//            writeFile(filePath, sourceCode);
        } catch (FormatterException e) {
            throw new RuntimeException(e);
        }

    }

    private void generatePackageDetails(String sourceCode) {
        Path packagePath = Paths.get(outputPath.toString(), defaultPackage.name());
        Path currentDir = Paths.get(System.getProperty(USER_DIR));
        if (!packagePath.isAbsolute()) {
            packagePath = Paths.get(currentDir.toString(), packagePath.toString()).normalize();
        }
        String packageName;
        Optional<Path> optionalPackageName = Optional.ofNullable(packagePath.getFileName());
        if (optionalPackageName.isEmpty()) {
            System.err.println("package name could not be derived" + packagePath);
            return;
        }
        packageName = optionalPackageName.get().toString();
        try {
            Path filePath = Paths.get(packagePath.resolve(DEFAULT_FILENAME).toFile().getCanonicalPath());
            writeFile(filePath, sourceCode);

            Path ballerinaToml = packagePath.resolve(BALLERINA_TOML);
            Files.createFile(ballerinaToml);

            String defaultManifest = FileUtils.readFileAsString( PKG_DEFAULTS + "/" + "manifest-app.toml");
            defaultManifest = defaultManifest
                    .replace(ORG_NAME, defaultPackage.org())
                    .replace(PKG_NAME, packageName)
                    .replace(DIST_VERSION, RepoUtils.getBallerinaShortVersion());
            Files.writeString(ballerinaToml, defaultManifest);
            createDefaultGitignore(packagePath);
            createDefaultDevContainer(packagePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void createDefaultGitignore(Path path) throws IOException {
        Path gitignore = path.resolve(GITIGNORE_FILE_NAME);
        if (Files.notExists(gitignore)) {
            Files.createFile(gitignore);
        }
        if (Files.size(gitignore) == 0) {
            String defaultGitignore = FileUtils.readFileAsString(PKG_DEFAULTS + "/" + GITIGNORE);
            Files.writeString(gitignore, defaultGitignore);
        }
    }

    private static void createDefaultDevContainer(Path path) throws IOException {
        Path devContainer = path.resolve(DEVCONTAINER);
        if (Files.notExists(devContainer)) {
            Files.createFile(devContainer);
        }
        if (Files.size(devContainer) == 0) {
            String defaultDevContainer = FileUtils.readFileAsString(PKG_DEFAULTS + "/" + DEVCONTAINER);
            defaultDevContainer = defaultDevContainer.replace("latest", RepoUtils.getBallerinaVersion());
            Files.writeString(devContainer, defaultDevContainer);
        }
    }

    private static void writeFile(Path filePath, String content) throws IOException {
        if (!Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }
        try (FileWriter writer = new FileWriter(filePath.toString(), StandardCharsets.UTF_8)) {
            writer.write(content);
        }
    }

    public void writeNonDefaultModule(SyntaxTree syntaxTree) {
        // TODO: Implement this
    }
}
