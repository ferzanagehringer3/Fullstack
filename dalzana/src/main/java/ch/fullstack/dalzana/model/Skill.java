package ch.fullstack.dalzana.model;

public enum Skill {
    JAVA("Java"),
    PYTHON("Python"),
    JAVASCRIPT("JavaScript"),
    TYPESCRIPT("TypeScript"),
    SPRING_BOOT("Spring Boot"),
    REACT("React"),
    ANGULAR("Angular"),
    VUE("Vue.js"),
    SQL("SQL"),
    MONGODB("MongoDB"),
    DOCKER("Docker"),
    KUBERNETES("Kubernetes"),
    GIT("Git"),
    LINUX("Linux"),
    AGILE("Agile"),
    REST_API("REST API"),
    GRAPHQL("GraphQL"),
    MICROSERVICES("Microservices"),
    AWS("AWS"),
    AZURE("Azure"),
    GCP("Google Cloud"),
    TESTING("Testing/JUnit"),
    MAVEN("Maven"),
    GRADLE("Gradle"),
    HTML_CSS("HTML/CSS"),
    DEVOPS("DevOps"),
    CI_CD("CI/CD"),
    FIGMA("Figma"),
    UX_UI("UX/UI Design");

    private final String displayName;

    Skill(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
