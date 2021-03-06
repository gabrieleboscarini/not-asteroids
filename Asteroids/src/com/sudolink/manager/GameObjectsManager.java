/*
*    Copyright 2014 Matthew MacGregor
*
*    This file is part of NotAsteroids.
*
*    NotAsteroids is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Foobar is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with NotAsteroids.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.sudolink.manager;

import com.sudolink.entities.Asteroid;
import com.sudolink.entities.Bullet;
import com.sudolink.entities.GameObject;
import com.sudolink.entities.Ship;
import static com.sudolink.enumeration.EntityState.Active;
import static com.sudolink.enumeration.EntityState.Killed;
import com.sudolink.game.GameTimer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * This class handles all of the in-game entities. 
 * 
 * @author Matthew MacGregor
 */
public class GameObjectsManager {
    
    // Singleton
    private GameObjectsManager() {
        gameObjects = new ArrayList<>();
        gameObjectQueue = new ArrayList<>();
        zcomp = new ZOrderComparator();
        
        gcTimer = new GameTimer(150) {
            @Override
            public void action() {
                cleanup();
            }

        };
        
    }
   
    public static synchronized GameObjectsManager getInstance() {
        if ( instance == null ) {
            instance = new GameObjectsManager();
        }
        
        return instance;
    }
    
    /**
     * Called on each iteration of the game loop.
     */
    public void update() {
        
        asteroidCount = 0;
        gcTimer.tick();
        
        for (GameObject o : gameObjects) {
            if( o instanceof Asteroid && o.getState() == Active) { asteroidCount++; }
            switch (o.getState()) {
                
                // This is the normal state of an object.
                case Active:
                    o.update();
                    checkCollisions(o);
                    break;
                    
                // Impervious objects update but don't collide.
                case Impervious:
                    o.update();
                    break;
                
                // Dead or passive objects do nothing
                case Killed:
                case Passive:
                default:
                        
            }
        }
        
        // At the end of each iteration, insert any queued objects.
        emptyGameObjectQueue();
    }
    
    public void add(GameObject o) {
        // Adding new objects adds them first to the queue. They will be inserted
        // into the primary arraylist at the end of an update.
        this.gameObjectQueue.add(o);
    }
    
    public List<GameObject> get() {
        return Collections.unmodifiableList(gameObjects);
    }
    
    /**
     * @return the asteroidCount
     */
    public int getAsteroidCount() {
        
        return asteroidCount;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Private Methods">
    private void checkCollisions(GameObject o1) {
        //Only certain types matter for collisions
        if (o1 instanceof Ship || o1 instanceof Bullet) {
            for (GameObject o2 : gameObjects) {
                o1.collide(o2);
            }
        }

    }
    
    private void emptyGameObjectQueue() {
        if( gameObjectQueue.isEmpty() ) return;
        
        synchronized (gameObjectQueue) {
            gameObjects.addAll(gameObjectQueue);
            gameObjectQueue.clear();
        }

        sortGameObjectsByZ();
    }
    
    private void sortGameObjectsByZ() {
        //Sort into Z order. Only needs to be done when new objects are added.
        Collections.sort(gameObjects, zcomp);
    }
    
    /**
     * Removes "dead" objects from the game object queue.
     */
    private void cleanup() {

        int count = 0;

        for (Iterator<GameObject> o = gameObjects.iterator(); o.hasNext();) {
            GameObject obj = (GameObject) o.next();

            if (Killed == obj.getState()) {
                //It would be better to reuse these
                o.remove();
                count++;

            }
        }
        // TODO: Implement logging class.
        System.out.println("Game objects: " + count + " removed. Remaining: " + gameObjects.size());
    }
    
    // At some point, reimplement with lambda
    private class ZOrderComparator implements Comparator<GameObject> {
        @Override
        public int compare(GameObject o1, GameObject o2) {
            return o1.getZ() - o2.getZ();
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Private Members">
    private final List<GameObject> gameObjects;
    private final List<GameObject> gameObjectQueue;
    private static GameObjectsManager instance;
    private final ZOrderComparator zcomp;
    private final GameTimer gcTimer;
    private int asteroidCount;
    // </editor-fold>
}



