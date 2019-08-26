package app.mcsl.windows.elements.label;

public enum LabelType {

    DEFAULT(""), H1("h1"), H2("h2"), H3("h3");

    String id;

    LabelType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
