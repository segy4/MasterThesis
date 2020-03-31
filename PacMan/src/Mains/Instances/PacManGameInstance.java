package Mains.Instances;

public enum PacManGameInstance {

    INSTANCE00("Instances/instance00.txt"),
    INSTANCE01("Instances/instance01.txt"),
    INSTANCE02("Instances/instance02.txt"),
    INSTANCE03("Instances/instance03.txt"),
    INSTANCE04("Instances/instance04.txt"),

    TEST00("Tests/test00.txt");

    private String path;

    PacManGameInstance(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
