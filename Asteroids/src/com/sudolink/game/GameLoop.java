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
package com.sudolink.game;

/**
 *
 * @author matsu
 */
public class GameLoop implements Runnable {

    private Thread gameloop;
    private final GameMain main;
 

    public GameLoop(GameMain c) {
        main = c;
    }

    @Override
    public void run() {

        Thread t = Thread.currentThread();
        while (t == gameloop) {
            main.updateGame();
            main.drawGame();
            timeWarp();
            
        }

    }

    private void timeWarp() {
        long sleepTime = 50000000; //nanoseconds?
        long now = System.nanoTime();
        long diff;
        while ((diff = System.nanoTime() - now) < sleepTime) {
            if (diff < sleepTime * 0.8) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException exc) {
                }
            } else {
                Thread.yield();
            }
        }
    }

    public void stop() {
        gameloop = null;
    }

    public void start() {
        gameloop = new Thread(this);
        gameloop.start();
        run();
    }

}
