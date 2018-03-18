package ECS;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by ibrahim on 3/18/18.
 */

public class ECSManager {
    private List<Entity> entities;
    private List<Component> components;
    private List<System> systems;
    private ArrayList<ECSEventListener> listenerLists[];
    private Stack<Integer> entityEmptySlots;
    private Stack<Integer> componentEmptySlots;

    public ECSManager(){
        entities = new ArrayList<Entity>();
        components = new ArrayList<Component>();
        systems = new ArrayList<System>();
        entityEmptySlots = new Stack<Integer>();
        componentEmptySlots = new Stack<Integer>();
        listenerLists = new ArrayList[64];
        for(int i = 0; i < 64; i++){
            listenerLists[i] = new ArrayList<ECSEventListener>();
        }
    }

    public int createEntity(){
        if(entityEmptySlots.empty()) {
            Entity entity = new Entity(entities.size());
            entities.add(entity);
            return entity.getId();
        } else {
            int id = entityEmptySlots.pop();
            Entity entity = new Entity(id);
            entities.set(id, entity);
            return id;
        }
    }

    public boolean removeEntity(int entityId){
        if(entityId >= entities.size()) return false;
        if(entities.get(entityId) == null) return false;
        entityEmptySlots.push(entityId);
        entities.set(entityId, null);
        return true;
    }

    private Entity getEntity(int id){
        if(id >= entities.size() || id < 0)
            return null;
        return entities.get(id);
    }

    public Component getComponent(int id){
        if(id >= components.size() || id < 0)
            return null;
        return components.get(id);
    }

    public Component getComponent(int entityId, int componentCode){
        Entity entity = entities.get(entityId);
        int componentId = entity.getComponentId(componentCode);
        if(componentId == -1) return null;
        return components.get(componentId);
    }

    public boolean addComponent(Component component, int entityId){
        Entity entity = getEntity(entityId);
        if(entity == null)
            return false;

        int componentId = 0;
        if(componentEmptySlots.empty()) {
            componentId = components.size();
            components.add(component);
        } else {
            componentId = componentEmptySlots.pop();
            components.set(componentId, component);
        }
        component.setId(componentId);
        long oldMask = entity.getComponentsMask();
        boolean returnValue = entity.addComponent(component.getComponentCode(),componentId);
        long newMask = entity.getComponentsMask();
        updateEntitySystems(entity.getId(), oldMask, newMask);
        return returnValue;
    }

    public boolean removeComponent(int componentId) {
        Component component = getComponent(componentId);
        if(component == null) return false;
        Entity entity = null;
        for(Entity tmpEntity : entities){
            if(tmpEntity.checkComponentAttached(component.getComponentCode(), componentId)){
                entity = tmpEntity;
                break;
            }
        }
        if(entity == null) return false;
        _removeComponent(component, entity);
        return true;
    }

    private boolean _removeComponent(Component component, Entity entity){
        if(entity == null) return false;
        if(component == null) return false;
        componentEmptySlots.push(component.getId());
        components.set(component.getId(), null);
        long oldMask = entity.getComponentsMask();
        boolean returnValue = entity.removeComponent(component.getComponentCode());
        long newMask = entity.getComponentsMask();
        updateEntitySystems(entity.getId(), oldMask, newMask);
        return returnValue;
    }

    public boolean addSystem(System system){
        if(system == null) return false;
        systems.add(system);
        system.setEcsManager(this);
        updateSystemEntities(system);
        return true;
    }

    private void updateSystemEntities(System system){
        for(Entity entity : entities){
            if((entity.getComponentsMask() & system.getComponentsMask()) == system.getComponentsMask()){
                system.addEntity(entity.getId());
            }
        }
    }

    private void updateEntitySystems(int entityId, long oldMask, long newMask){
        for(System system : systems){
            boolean oldQualify = ((system.getComponentsMask() & oldMask) == system.getComponentsMask());
            boolean newQualify = ((system.getComponentsMask() & newMask) == system.getComponentsMask());
            if(oldQualify && !newQualify){
                system.removeEntity(entityId);
            } else if(newQualify && !oldQualify){
                system.addEntity(entityId);
            }
        }
    }


    public boolean subscribe(int eventCode, ECSEventListener listener) {
        if(eventCode < 0 || eventCode > 63) {
            return false;
        }
        return listenerLists[eventCode].add(listener);
    }

    public boolean unsubscribe(int eventCode, ECSEventListener listener) {
        if(eventCode < 0 || eventCode > 63) {
            return false;
        }
        return listenerLists[eventCode].remove(listener);
    }

    public void fireEvent(ECSEvent event) {
        int eventCode = event.eventCode;
        for(ECSEventListener listener : listenerLists[eventCode]) {
            listener.update(eventCode, event.message);
        }
    }

    public void update(float dt){
        for(System system : systems){
            system.update(dt);
        }
    }
}
