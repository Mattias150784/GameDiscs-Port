package net.tejty.gamediscs.games.controls;

import org.lwjgl.glfw.GLFW;
import net.tejty.gamediscs.games.util.Game;

public class Controls {
    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;
    private boolean button1;
    private boolean button2;
    private boolean was_up;
    private boolean was_down;
    private boolean was_left;
    private boolean was_right;
    private boolean was_b1;
    private boolean was_b2;

    private final Game game;
    public Controls(Game game) {
        this.game = game;
    }

    public void setButton(Button button, boolean isDown) {
        boolean callEvent = false;
        switch (button) {
            case UP -> {
                if (!up && isDown) {
                    callEvent = true;
                    was_up = true;
                }
                up = isDown;
            }
            case DOWN -> {
                if (!down && isDown) {
                    callEvent = true;
                    was_down = true;
                }
                down = isDown;
            }
            case LEFT -> {
                if (!left && isDown) {
                    callEvent = true;
                    was_left = true;
                }
                left = isDown;
            }
            case RIGHT -> {
                if (!right && isDown) {
                    callEvent = true;
                    was_right = true;
                }
                right = isDown;
            }
            case BUTTON1 -> {
                if (!button1 && isDown) {
                    callEvent = true;
                    was_b1 = true;
                }
                button1 = isDown;
            }
            case BUTTON2 -> {
                if (!button2 && isDown) {
                    callEvent = true;
                    was_b2 = true;
                }
                button2 = isDown;
            }
        }
        if (isDown) {
            if (callEvent) {
                game.buttonDown(button);
            }
        } else {
            game.buttonUp(button);
        }
    }

    public boolean isButtonDown(Button button) {
        return switch (button) {
            case UP -> up;
            case DOWN -> down;
            case LEFT -> left;
            case RIGHT -> right;
            case BUTTON1 -> button1;
            case BUTTON2 -> button2;
        };
    }

    public boolean wasButtonDown(Button button) {
        boolean toReturn = false;
        switch (button) {
            case UP -> {
                toReturn = was_up;
                was_up = false;
            }
            case DOWN -> {
                toReturn = was_down;
                was_down = false;
            }
            case LEFT -> {
                toReturn = was_left;
                was_left = false;
            }
            case RIGHT -> {
                toReturn = was_right;
                was_right = false;
            }
            case BUTTON1 -> {
                toReturn = was_b1;
                was_b1 = false;
            }
            case BUTTON2 -> {
                toReturn = was_b2;
                was_b2 = false;
            }
        }
        return toReturn;
    }

    public void handleKey(int keyCode, boolean isDown) {
        switch (keyCode) {
            case GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_UP -> setButton(Button.UP, isDown);
            case GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_DOWN -> setButton(Button.DOWN, isDown);
            case GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_LEFT -> setButton(Button.LEFT, isDown);
            case GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_RIGHT -> setButton(Button.RIGHT, isDown);
            default -> { }
        }
    }
}
