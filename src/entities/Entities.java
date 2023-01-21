package entities;

import javax.servlet.ServletContext;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class Entities {
    private final String fileName = "/WEB-INF/data/entities.db";
    private ConcurrentMap<UUID, Entity> entities;
    private ServletContext srvctx;

    public Entities(){
        entities = new ConcurrentMap<UUID, Entity>() {
            @Override
            public Entity putIfAbsent(UUID key, Entity value) {
                return null;
            }

            @Override
            public boolean remove(Object key, Object value) {
                return false;
            }

            @Override
            public boolean replace(UUID key, Entity oldValue, Entity newValue) {
                return false;
            }

            @Override
            public Entity replace(UUID key, Entity value) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public Entity get(Object key) {
                return null;
            }

            @Override
            public Entity put(UUID key, Entity value) {
                return null;
            }

            @Override
            public Entity remove(Object key) {
                return null;
            }

            @Override
            public void putAll(Map<? extends UUID, ? extends Entity> m) {

            }

            @Override
            public void clear() {

            }

            @Override
            public Set<UUID> keySet() {
                return null;
            }

            @Override
            public Collection<Entity> values() {
                return null;
            }

            @Override
            public Set<Entry<UUID, Entity>> entrySet() {
                return null;
            }
        };
    }

    public void setServletContext(ServletContext srvctx){
        this.srvctx = srvctx;
    }
    public ServletContext getServletContext(){
        return this.srvctx;
    }

    public ConcurrentMap<UUID, Entity> getCMap(){
        if(srvctx == null) return null;
        if(entities.size() < 1) read();
        return this.entities;
    }

    public String toXML(Object obj){
        String xml = null;
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            XMLEncoder encoder = new XMLEncoder(out);
            encoder.writeObject(obj);
            encoder.close();
            xml = out.toString();
        } catch (Exception e){
            System.out.println("Something went wrong with XML encoding!");
            System.exit(1);
        }
        return xml;
    }

    public UUID addEntity(String creator, String name){
        Entity e = new Entity(creator, name);
        UUID eid = e.getId();
        entities.put(eid, e);
        return eid;
    }

    private void read(){
        InputStream in = srvctx.getResourceAsStream(this.fileName);
        if(in != null){
            try{
                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(isr);

                String record = null;
                while ((record = reader.readLine()) != null){
                    String[] parts = record.split("!");
                    if (parts.length == 2){
                        addEntity(parts[0], parts[1]);
                    }
                }
                in.close();
            }
            catch (Exception e){
                System.out.println("Something went wrong while reading files!");
                System.exit(1);
            }
        }
    }
}
