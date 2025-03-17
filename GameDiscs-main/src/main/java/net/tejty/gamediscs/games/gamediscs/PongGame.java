package net.tejty.gamediscs.games.gamediscs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.tejty.gamediscs.GameDiscsMod;
import net.tejty.gamediscs.games.controls.Button;
import net.tejty.gamediscs.games.graphics.MultiImage;
import net.tejty.gamediscs.games.util.Game;
import net.tejty.gamediscs.games.util.Sprite;
import net.tejty.gamediscs.games.util.VecUtil;

public class PongGame extends Game {
    private Sprite player = new Sprite(
            new Vec2(10, HEIGHT / 2 - 10),
            new Vec2(5, 20),
            new ResourceLocation("minecraft:textures/block/white_concrete.png")
    );
    private Sprite oponent = new Sprite(
            new Vec2(WIDTH - 15, HEIGHT / 2 - 10),
            new Vec2(5, 20),
            new ResourceLocation("textures/block/white_concrete.png")
    );
    private Sprite ball = new Sprite(
            new Vec2(WIDTH / 2 - 2, HEIGHT / 2 - 2),
            new Vec2(4, 4),
            new ResourceLocation("textures/block/white_concrete.png")
    );
    private Sprite numberRenderer = new Sprite(
            new Vec2(0, 0),
            new Vec2(8, 12),
            new MultiImage(
                    new ResourceLocation(GameDiscsMod.MOD_ID, "textures/games/sprite/numbers.png"),
                    8,
                    120,
                    10
            )
    );

    private static final int SPEED = 3;
    private int oponentScore = 0;
    private int ballTimer;
    private float ballSpeed = 3;

    public PongGame() {
        super();
    }

    @Override
    public synchronized void prepare() {
        super.prepare();
        player = new Sprite(
                new Vec2(10, HEIGHT / 2 - 10),
                new Vec2(5, 20),
                new ResourceLocation("minecraft:textures/block/white_concrete.png")
        );
        oponent = new Sprite(
                new Vec2(WIDTH - 15, HEIGHT / 2 - 10),
                new Vec2(5, 20),
                new ResourceLocation("minecraft:textures/block/white_concrete.png")
        );
        ballSpeed = 4;
        ball = new Sprite(
                new Vec2(WIDTH / 2 - 2, HEIGHT / 2 - 2),
                new Vec2(4, 4),
                new ResourceLocation("minecraft:textures/block/white_concrete.png")
        ).setVelocity(new Vec2((random.nextInt(2) * 2 - 1) * 2, (random.nextInt(2) * 2 - 1) * 2));
        oponentScore = 0;
    }

    public void resetBall() {
        ballSpeed = 4;
        ball.setPos(new Vec2(WIDTH / 2 - 2, HEIGHT / 2 - 2));
        ball.setVelocity(new Vec2(random.nextInt(2) * 2 - 1, random.nextInt(2) * 2 - 1).normalized().scale(ballSpeed));
        ballTimer = 60;
    }

    @Override
    public synchronized void start() {
        super.start();
        ballTimer = 60;
    }

    @Override
    public synchronized void tick() {
        super.tick();
        if (ballTimer > 0) {
            ballTimer--;
        }
    }

    @Override
    public synchronized void gameTick() {
        super.gameTick();
        if (ticks % 20 == 0) {
            ballSpeed += 0.1f;
        }
        if (controls.isButtonDown(Button.UP)) {
            player.moveBy(VecUtil.VEC_UP.scale(SPEED));
        }
        if (controls.isButtonDown(Button.DOWN)) {
            player.moveBy(VecUtil.VEC_DOWN.scale(SPEED));
        }
        if (ball.getCenterPos().y < oponent.getCenterPos().y) {
            oponent.moveBy(VecUtil.VEC_UP.scale(SPEED));
        }
        if (ball.getCenterPos().y > oponent.getCenterPos().y) {
            oponent.moveBy(VecUtil.VEC_DOWN.scale(SPEED));
        }
        player.setY(Math.min(Math.max(player.getY(), 0), HEIGHT - player.getHeight()));
        oponent.setY(Math.min(Math.max(oponent.getY(), 0), HEIGHT - oponent.getHeight()));
        if (ballTimer <= 0) {
            ball.moveBy(new Vec2(ball.getVelocity().x, 0));
            if (ball.getX() < 0) {
                oponentScore++;
                resetBall();
            }
            if (ball.getX() + ball.getWidth() > WIDTH) {
                score++;
                soundPlayer.playPoint();
                resetBall();
            }
            if (ballTimer <= 0) {
                ball.moveBy(new Vec2(0, ball.getVelocity().y));
                if (ball.getY() < 0 || ball.getY() + ball.getHeight() > HEIGHT) {
                    ball.moveBy(new Vec2(0, -ball.getVelocity().y));
                    ball.setVelocity(new Vec2(ball.getVelocity().x, -ball.getVelocity().y));
                    soundPlayer.playJump();
                }
                if (ball.isTouching(player)) {
                    ball.setVelocity(ball.getCenterPos().add(player.getCenterPos().add(new Vec2(-2, 0)).negated()).normalized().scale(ballSpeed));
                    soundPlayer.playJump();
                }
                if (ball.isTouching(oponent)) {
                    ball.setVelocity(ball.getCenterPos().add(oponent.getCenterPos().add(new Vec2(2, 0)).negated()).normalized().scale(ballSpeed));
                    soundPlayer.playJump();
                }
            }
        }
        if (score >= 10) {
            win();
        }
        if (oponentScore >= 10) {
            die();
        }
    }

    @Override
    public synchronized void die() {
        super.die();
    }

    @Override
    public synchronized void render(PoseStack graphics, int posX, int posY) {
        super.render(graphics, posX, posY);
        player.render(graphics, posX, posY);
        oponent.render(graphics, posX, posY);
        ball.render(graphics, posX, posY);
        renderParticles(graphics, posX, posY);
        if (score < 10) {
            if (numberRenderer.getImage() instanceof MultiImage image) {
                image.setImage(score);
            }
            numberRenderer.setPos(new Vec2(WIDTH / 2 - numberRenderer.getWidth() - 4, 4));
            numberRenderer.render(graphics, posX, posY);
        }
        else {
            if (numberRenderer.getImage() instanceof MultiImage image) {
                image.setImage(1);
            }
            numberRenderer.setPos(new Vec2(WIDTH / 2 - numberRenderer.getWidth() * 2 - 4 * 2, 4));
            numberRenderer.render(graphics, posX, posY);
            if (numberRenderer.getImage() instanceof MultiImage image) {
                image.setImage(0);
            }
            numberRenderer.setPos(new Vec2(WIDTH / 2 - numberRenderer.getWidth() - 4, 4));
            numberRenderer.render(graphics, posX, posY);
        }
        if (oponentScore < 10) {
            if (numberRenderer.getImage() instanceof MultiImage image) {
                image.setImage(oponentScore);
            }
            numberRenderer.setPos(new Vec2(WIDTH / 2 + 4, 4));
            numberRenderer.render(graphics, posX, posY);
        }
        else {
            if (numberRenderer.getImage() instanceof MultiImage image) {
                image.setImage(1);
            }
            numberRenderer.setPos(new Vec2(WIDTH / 2 + 4, 4));
            numberRenderer.render(graphics, posX, posY);
            if (numberRenderer.getImage() instanceof MultiImage image) {
                image.setImage(0);
            }
            numberRenderer.setPos(new Vec2(WIDTH / 2 + numberRenderer.getWidth() + 4 * 2, 4));
            numberRenderer.render(graphics, posX, posY);
        }
        renderOverlay(graphics, posX, posY);
    }

    @Override
    public synchronized void buttonDown(Button button) {
        super.buttonDown(button);
    }

    @Override
    public ResourceLocation getBackground() {
        return new ResourceLocation(GameDiscsMod.MOD_ID, "textures/games/background/pong_background.png");
    }

    @Override
    public boolean showScore() {
        return false;
    }

    @Override
    public Component getName() {
        return Component.translatable("gamediscs.pong");
    }

    @Override
    public ResourceLocation getIcon() {
        return new ResourceLocation(GameDiscsMod.MOD_ID, "textures/item/game_disc_pong_no_anim.png");
    }
}
