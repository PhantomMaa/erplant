package io.erplant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = true)
public class GencodeMojo extends AbstractMojo {

    @Parameter(defaultValue = "${basedir}")
    private String basedir;

    @Parameter(name = "srcPuml")
    private String srcPuml;

    @Parameter(name = "packageName")
    private String packageName;

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Parameter(name = "generateTypes")
    private String generateTypes;

    @Parameter(name = "tableConfigs")
    private List<TableConfig> tableConfigs;

    @Parameter(name = "jdkVersion")
    private Integer jdkVersion;

    private static final String TARGET_GENERATED_SOURCES = "/target/generated-sources/erplant";

    private static final String TARGET_CLASSES = "/target/classes";

    public void execute() {
        String outputJava = basedir + TARGET_GENERATED_SOURCES + "/java";
        String outputMapper = basedir + TARGET_CLASSES;
        String testOutputDir = project.getBasedir().getAbsolutePath() + "/src/test/java";
        String pumlFile = basedir + "/" + srcPuml;
        String tableConfigsStr = toJsonString(tableConfigs);
        int jdkVersionVal = this.jdkVersion == null ? 17 : this.jdkVersion;

        Command.Companion.run(pumlFile, packageName, filterGenerateTypes("DO,DAO,BK,PageNumQuery,OffsetQuery,Converter,Entity,Repository"), tableConfigsStr, outputJava, jdkVersionVal);
        Command.Companion.run(pumlFile, packageName, filterGenerateTypes("Mapper"), tableConfigsStr, outputMapper, jdkVersionVal);
        Command.Companion.run(pumlFile, packageName, filterGenerateTypes("DaoTest"), tableConfigsStr, testOutputDir, jdkVersionVal);
        Command.Companion.run(pumlFile, packageName, filterGenerateTypes("Ddl"), tableConfigsStr, basedir + "/src/test/resources", jdkVersionVal);

        /*
         * tell to maven-compiler-plugin the generated dir is SourceRoot
         * https://stackoverflow.com/questions/19953551/custom-maven-plugin-programmatically-adding-source-directory-to-project/19953920#19953920
         */
        project.addCompileSourceRoot(basedir + TARGET_GENERATED_SOURCES);
    }

    private Set<String> convertGenerateTypes() {
        return Arrays.stream(generateTypes.replaceAll(" ", "").split(",")).collect(Collectors.toSet());
    }

    private String filterGenerateTypes(String inputTypes) {
        if (StringUtils.isBlank(generateTypes)) {
            return inputTypes;
        }

        Set<String> generateTypeSet = convertGenerateTypes();
        return Arrays.stream(inputTypes.split(",")).filter(generateTypeSet::contains).collect(Collectors.joining(","));
    }

    public static String toJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
