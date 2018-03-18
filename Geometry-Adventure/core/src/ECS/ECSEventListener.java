package ECS;

/**
 * Created by ibrahim on 3/18/18.
 */

public interface ECSEventListener {
    public boolean update(int eventCode, Object message);
}
