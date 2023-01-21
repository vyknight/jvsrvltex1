package entities;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class EntityServlet extends HttpServlet {
    static final long serialVersionUID = 1L;
    private Entities entities;

    // load into container
    @Override
    public void init(){
        this.entities = new Entities();
        entities.setServletContext(this.getServletContext());
    }

    @Override
    public void doGet(HttpServletRequest rq, HttpServletResponse rs){
        String param = rq.getParameter("id");
        String StringKey = (param == null) ? null : param.trim();
        UUID id = UUID.fromString(StringKey);

        if (StringKey == null){
            ConcurrentMap<UUID, Entity> map = entities.getCMap();;
            Object[] list = map.values().toArray();
            Arrays.sort(list);
            sendResponse(rs, entities.toXML(list));
        } else {
            Entity entity = entities.getCMap().get(id);
            if (entity == null){
                String msg = "There are no entities with the id " + StringKey + "\n";
                sendResponse(rs, entities.toXML(msg));
            } else {
                sendResponse(rs, entities.toXML(entity));
            }
        }
    }

    @Override
    public void doPost(HttpServletRequest rq, HttpServletResponse rs){
        String creator = rq.getParameter("creator");
        String name = rq .getParameter("name");

        if (creator == null || name == null){
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_BAD_REQUEST));
        }

        UUID eid = entities.addEntity(creator, name);

        String msg = "Entity " + eid.toString() + " created.\n";
        sendResponse(rs, msg);
    }

    public void doPut(HttpServletRequest rq, HttpServletResponse rs){
        // work around needeed because of tomcat
        String key = null;
        String rest = null;
        boolean creator = false;

        // the  work around
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(rq.getInputStream()));
            String data = br.readLine();
            String[] args = data.split("#");
            String[] parts1 = args[0].split("=");
            key = parts1[1];
            String[] parts2 = args[1].split("=");
            if(parts2[0].contains("creator")) creator = true;
            rest = parts2[2];
        } catch (Exception e) {
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
        }

        if(key == null){
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_BAD_REQUEST));
        }

        Entity e = entities.getCMap().get(UUID.fromString(key));
        if(e == null) {
            String msg = "There are no entities with the id " + key + "\n";
            sendResponse(rs, entities.toXML(msg));
        } else {
            if (creator) e.setCreator(rest);
            else e.setName(rest);

            String msg = "Entity with id " + key + " has been edited.\n";
            sendResponse(rs, entities.toXML(msg));
        }
    }

    @Override
    public void doDelete(HttpServletRequest rq, HttpServletResponse rs){
        String param = rq.getParameter("id");
        String StringKey = (param == null) ? null : param.trim();
        UUID eid = UUID.fromString(StringKey);

        if (StringKey == null){
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_BAD_REQUEST));
        }
        try{
            entities.getCMap().remove(eid);
            String msg = "Entity " + StringKey + " removed.\n";
            sendResponse(rs, entities.toXML(msg));
        } catch (Exception e) {
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
        }
    }

    @Override
    public void doTrace(HttpServletRequest request, HttpServletResponse response) {
        throw new RuntimeException(Integer.toString(HttpServletResponse.SC_METHOD_NOT_ALLOWED));
    }

    @Override
    public void doHead(HttpServletRequest request, HttpServletResponse response) {
        throw new RuntimeException(Integer.toString(HttpServletResponse.SC_METHOD_NOT_ALLOWED));
    }

    @Override
    public void doOptions(HttpServletRequest request, HttpServletResponse response) {
        throw new RuntimeException(Integer.toString(HttpServletResponse.SC_METHOD_NOT_ALLOWED));
    }

    private void sendResponse(HttpServletResponse rs, String payload){
        try {
            OutputStream out = rs.getOutputStream();
            out.write(payload.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (Exception e){
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
        }
    }
}
