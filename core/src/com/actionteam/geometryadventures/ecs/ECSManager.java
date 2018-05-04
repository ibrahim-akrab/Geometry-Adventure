package com.actionteam.geometryadventures.ecs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Manages all the entities, system and components
 * It's responsible for:
 * - Creating entities and assigning them to concerned systems
 * - Assigning components to entities
 * - Acting as a message bus for communication across systems using events
 * - Updating the systems each frame
 *
 * Created by ibrahim on 3/18/18.
 */

public class ECSManager {

    private static final int MAXIMUM_NUMBER_OF_EVENT_CODES = 64;

    private List<Entity> entities;
    private List<Component> components;
    private List<System> systems;
    private ArrayList<ECSEventListener> listenerLists[];
    private Stack<Integer> entityEmptySlots;
    private Stack<Integer> componentEmptySlots;
    private List<Integer> entitiesToBeRemoved;

    // an instance of the singleton class
    private static final ECSManager instance = new ECSManager();

    private ECSManager() {
        entities = new ArrayList<Entity>();
        components = new ArrayList<Component>();
        systems = new ArrayList<System>();
        entityEmptySlots = new Stack<Integer>();
        componentEmptySlots = new Stack<Integer>();
        listenerLists = new ArrayList[64];
        for (int i = 0; i < 64; i++) {
            listenerLists[i] = new ArrayList<ECSEventListener>();
        }
        entitiesToBeRemoved = new ArrayList<Integer>();
    }

    public static ECSManager getInstance(){
        return instance;
    }

    /**
     * Creates a new entity and returns a handler for it
     *
     * @return entity handler
     */
    public int createEntity() {
        // checks if there are empty slots in the entity arraylist
        // if there is any, the entity is added in that slot
        // otherwise, it's added at the end of the list
        if (entityEmptySlots.empty()) {
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

    /**
     * Removes an entity from the system alongside all of its components
     */
    public boolean removeEntity(int entityId) {
        if (entityId >= entities.size()) return false;
        Entity entity = entities.get(entityId);
        if (entity == null) return false;
        if (!entitiesToBeRemoved.contains(entityId)) {
            entitiesToBeRemoved.add(entityId);
        }
        return true;
    }

    /**
     * remove entities in entitiesToBeRemoved list
     */
    public void updateEntities(){
        Iterator<Entity> entitiesIterator = entities.iterator();
        while (entitiesIterator.hasNext()) {
            Entity entity = entitiesIterator.next();
            if (entity != null) {
                int entityId = entity.getId();
                if (entitiesToBeRemoved.contains(entityId)) {
                    entitiesToBeRemoved.remove(Integer.valueOf(entityId));
                    // removes all components associated with that entity
                    for (int componentCode = 0; componentCode < Entity.MAXIMUM_COMPONENT_NUMBER;
                         componentCode++) {
                        int componentId = entity.getComponentId(componentCode);
                        if (componentId != -1)
                            _removeComponent(getComponent(componentId), entity);
                    }

                    // marks the slot of the removed entity as empty
                    entityEmptySlots.push(entityId);
                    entities.set(entityId, null);

                    for (System system : systems) {
                        if (system.entities.contains(entityId)) {
                            system.entities.remove(Integer.valueOf(entityId));
                        }
                    }

                }
            }
        }
    }

    /**
     * Adds a component to an entity
     */
    public boolean addComponent(Component component, int entityId) {
        Entity entity = getEntity(entityId);
        if (entity == null)
            return false;

        // checks if there are empty slots in the components arraylist
        // if there is any, the component is added in that slot
        // otherwise, it's added at the end of the list
        int componentId;
        if (componentEmptySlots.empty()) {
            componentId = components.size();
            components.add(component);
        } else {
            componentId = componentEmptySlots.pop();
            components.set(componentId, component);
        }
        component.setComponentId(componentId);

        // inform systems of the change of components for the entity
        // the addition of a new component may make a system interested in that entity
        long oldMask = entity.getComponentsMask();
        if (!entity.addComponent(component.getCode(), componentId)) return false;
        long newMask = entity.getComponentsMask();
        updateEntitySystems(entity.getId(), oldMask, newMask);
        return true;
    }

    /**
     * Removes a component from the manager and from the entity it's attached to
     *
     * @param componentId
     * @return true if successful, false otherwise
     */
    public boolean removeComponent(int componentId) {
        Component component = getComponent(componentId);
        if (component == null) return false;
        Entity entity = null;

        // get the entity to which the component is attached
        for (Entity tmpEntity : entities) {
            if (tmpEntity.checkComponentAttached(component.getCode(), componentId)) {
                entity = tmpEntity;
                break;
            }
        }
        if (entity == null) return false;
        return _removeComponent(component, entity);
    }

    /**
     * Internal method that assists the method removeComponent
     */
    private boolean _removeComponent(Component component, Entity entity) {
        if (entity == null) return false;
        if (component == null) return false;

        components.set(component.getId(), null);

        // marks the slot of the removed component as empty
        componentEmptySlots.push(component.getId());

        // inform systems of the change of components for the entity
        // the removal of a component may make a system disinterested in that entity
        long oldMask = entity.getComponentsMask();
        if (!entity.removeComponent(component.getCode())) return false;
        long newMask = entity.getComponentsMask();
        updateEntitySystems(entity.getId(), oldMask, newMask);
        return true;
    }

    /**
     * Adds a system and informs it of all the entities it's interested in
     * A system is interested in all the entities that satisfy all of its component requirements
     *
     * @param system The added system
     * @return true if successful, false otherwise
     */
    public boolean addSystem(System system) {
        if (system == null) return false;
        if (systems.contains(system)) return false;
        if (!systems.add(system)) return false;
        system.setEcsManager();
        updateSystemEntities(system);
        return true;
    }

    /**
     * Adds all the entities that satisfy the system component requirements
     */
    private void updateSystemEntities(System system) {
        for (Entity entity : entities) {
            if (system.isInterested(entity.getComponentsMask())) {
                system.addEntity(entity.getId());
            }
        }
    }

    /**
     * Informs the systems of the change of components for that entity
     * The system might become disinterested, so we remove the entity from the system
     * And it might become interested, so we  add the entity to the system
     */
    private void updateEntitySystems(int entityId, long oldMask, long newMask) {
        for (System system : systems) {
            boolean oldQualify = system.isInterested(oldMask);
            boolean newQualify = system.isInterested(newMask);
            if (oldQualify && !newQualify) {
                system.removeEntity(entityId);
            } else if (newQualify && !oldQualify) {
                system.addEntity(entityId);
            }
        }
    }

    /**
     * Subscribes a listener to all events with the code @eventCode
     *
     * @param eventCode Code of the type of event to which the listener will be subscribed
     * @param listener  The subscribing listener
     * @return true if successful, false otherwise
     */
    public boolean subscribe(int eventCode, ECSEventListener listener) {
        if (eventCode < 0 || eventCode >= MAXIMUM_NUMBER_OF_EVENT_CODES) {
            return false;
        }
        return listenerLists[eventCode].add(listener);
    }

    /**
     * Unsubscribes a listener from all events with the code @eventCode
     *
     * @param eventCode Code of the type of event to which the listener will be unsubscribed
     * @param listener  The unsubscribing listener
     * @return true if successful, false otherwise
     */
    public boolean unsubscribe(int eventCode, ECSEventListener listener) {
        if (eventCode < 0 || eventCode > MAXIMUM_NUMBER_OF_EVENT_CODES) {
            return false;
        }
        return listenerLists[eventCode].remove(listener);
    }

    /**
     * Fires an event by informing all interested listeners
     */
    public void fireEvent(ECSEvent event) {
        int eventCode = event.eventCode;
        for (ECSEventListener listener : listenerLists[eventCode]) {
            listener.handle(event.eventCode, event.message);
        }
    }

    /**
     * Updates all systems
     *
     * @param dt Time difference from last update
     */
    public void update(float dt) {
        for (System system : systems) {
            system.update(dt);
        }
        updateEntities();
    }

    public boolean entityHasComponent(int entityId, int componentCode) {
        Entity entity = getEntity(entityId);
        return entity != null && entity.hasComponent(componentCode);
    }

    public Entity getEntity(int id) {
        if (id >= entities.size() || id < 0)
            return null;
        return entities.get(id);
    }

    public Component getComponent(int id) {
        if (id >= components.size() || id < 0)
            return null;
        return components.get(id);
    }

    public Component getComponent(int entityId, int componentCode) {
        Entity entity = entities.get(entityId);
        if (entity == null) return null;
        int componentId = entity.getComponentId(componentCode);
        if (componentId == -1) return null;
        return components.get(componentId);
    }

}
