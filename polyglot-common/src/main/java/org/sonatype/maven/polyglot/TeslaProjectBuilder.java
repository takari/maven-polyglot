package org.sonatype.maven.polyglot;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.project.*;
import org.eclipse.sisu.Priority;

@Singleton
@Named
@Priority(10)
public class TeslaProjectBuilder implements ProjectBuilder {

    private final TeslaModelProcessor teslaModelProcessor;

    private final DefaultProjectBuilder defaultProjectBuilder;

    @Inject
    public TeslaProjectBuilder(TeslaModelProcessor teslaModelProcessor, DefaultProjectBuilder defaultProjectBuilder) {
        this.teslaModelProcessor = requireNonNull(teslaModelProcessor);
        this.defaultProjectBuilder = requireNonNull(defaultProjectBuilder);
    }

    @Override
    public ProjectBuildingResult build(File pomFile, ProjectBuildingRequest request) throws ProjectBuildingException {
        return convert(defaultProjectBuilder.build(pomFile, request));
    }

    @Override
    public ProjectBuildingResult build(ModelSource modelSource, ProjectBuildingRequest request)
            throws ProjectBuildingException {
        return convert(defaultProjectBuilder.build(modelSource, request));
    }

    @Override
    public ProjectBuildingResult build(Artifact artifact, ProjectBuildingRequest request)
            throws ProjectBuildingException {
        return convert(defaultProjectBuilder.build(artifact, request));
    }

    @Override
    public ProjectBuildingResult build(Artifact artifact, boolean allowStubModel, ProjectBuildingRequest request)
            throws ProjectBuildingException {
        return convert(defaultProjectBuilder.build(artifact, allowStubModel, request));
    }

    @Override
    public List<ProjectBuildingResult> build(List<File> pomFiles, boolean recursive, ProjectBuildingRequest request)
            throws ProjectBuildingException {
        List<ProjectBuildingResult> results = defaultProjectBuilder.build(pomFiles, recursive, request);
        return results.stream().map(this::convert).collect(Collectors.toList());
    }

    private ProjectBuildingResult convert(ProjectBuildingResult result) {
        if (result.getPomFile() == null) return result;
        String projectId = result.getProjectId();
        MavenProject project = result.getProject();
        List<ModelProblem> problems = result.getProblems();
        DependencyResolutionResult dependencyResolutionResult = result.getDependencyResolutionResult();

        // When running with the argument `-f <pomFile>`, we must restore the location of the generated pom xml file.
        // Otherwise, it retains a reference to the polyglot pom, which causes a `409 Conflict` error when deployed.
        File pomFile = teslaModelProcessor.getPomXmlFile(result.getPomFile()).orElse(result.getPomFile());
        project.setPomFile(pomFile);
        project.getModel().setPomFile(pomFile);

        return new TeslaProjectBuildingResult(projectId, pomFile, project, problems, dependencyResolutionResult);
    }

    private static class TeslaProjectBuildingResult implements ProjectBuildingResult {

        private final String projectId;
        private final File pomFile;
        private final MavenProject project;
        private final List<ModelProblem> problems;
        private final DependencyResolutionResult dependencyResolutionResult;

        public TeslaProjectBuildingResult(
                String projectId,
                File pomFile,
                MavenProject project,
                List<ModelProblem> problems,
                DependencyResolutionResult dependencyResolutionResult) {
            this.projectId = projectId;
            this.pomFile = pomFile;
            this.project = project;
            this.problems = problems;
            this.dependencyResolutionResult = dependencyResolutionResult;
        }

        @Override
        public String getProjectId() {
            return projectId;
        }

        @Override
        public File getPomFile() {
            return pomFile;
        }

        @Override
        public MavenProject getProject() {
            return project;
        }

        @Override
        public List<ModelProblem> getProblems() {
            return problems;
        }

        @Override
        public DependencyResolutionResult getDependencyResolutionResult() {
            return dependencyResolutionResult;
        }
    }
}
