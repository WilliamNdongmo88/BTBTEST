package will.dev.BTBTEST.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "files")
public class FileParams {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String temp;
    @Transient
    private String content;

    public FileParams() {
    }

    public FileParams(int id, String name, String temp, String content) {
        this.id = id;
        this.name = name;
        this.temp = temp;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}


