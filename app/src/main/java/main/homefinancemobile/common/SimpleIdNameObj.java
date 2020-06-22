package main.homefinancemobile.common;

public class SimpleIdNameObj {
    private String id;
    private String name;

    public SimpleIdNameObj() {}
    public SimpleIdNameObj(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
}
