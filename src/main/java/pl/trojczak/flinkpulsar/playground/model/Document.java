package pl.trojczak.flinkpulsar.playground.model;

import java.io.Serializable;
import java.util.Objects;

public class Document implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String content;

    public Document() {
    }

    public Document(String id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof Document)) {return false;}
        Document document = (Document) o;
        return Objects.equals(id, document.id) &&
                Objects.equals(name, document.name) &&
                Objects.equals(content, document.content);
    }

    @Override public int hashCode() {
        return Objects.hash(id, name, content);
    }
}