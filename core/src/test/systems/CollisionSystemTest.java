package test.systems;
import com.actionteam.geometryadventures.*;
import com.actionteam.geometryadventures.systems.CollisionSystem;

import org.junit.Assert;
import org.junit.Test;
import java.util.regex.Pattern;
import static org.junit.Assert.*;


/**
 * Created by Omniia  on 25/04/2018.
 */

public class CollisionSystemTest {
    @Test
    public void circRectCollisionTest()
    {
        CollisionSystem cS = new CollisionSystem();
        boolean collided = cS.circRectCollision(10,10,10,10,20,10,5);
        assertTrue( collided);
        collided = cS.circRectCollision(10,10,10,10,5,5,5);
        assertFalse(collided );

    }
    @Test
    public void circCircCollisionTest()
    {
        CollisionSystem cS = new CollisionSystem();
        boolean collided = cS.circCircCollision(5,5,5,15,-5,15);
        assertTrue( collided);
        collided = cS.circCircCollision(2,2,2,10,10,1);
        assertFalse(collided);

    }


}
