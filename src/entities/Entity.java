package entities;

import java.io.Serializable;
import java.util.UUID;

public class Entity implements Serializable, Comparable<Entity>{
    private final UUID uid;
    private String creator;
    private String name;

    public Entity(){
        this.uid = UUID.randomUUID();
    }
    public Entity(String creator, String name){
        this.uid = UUID.randomUUID();
        this.creator = creator;
        this.name = name;
    }

    public void setCreator(String creator){
        this.creator = creator;
    }

    public String getCreator(){
        return this.creator;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public UUID getId(){
        return this.uid;
    }

    public int compareTo(final Entity other){
        return uid.compareTo(other.getId());
    }
}
