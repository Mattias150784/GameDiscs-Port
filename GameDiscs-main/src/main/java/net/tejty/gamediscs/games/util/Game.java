package net.tejty.gamediscs.games.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.tejty.gamediscs.GameDiscsMod;
import net.tejty.gamediscs.games.audio.SoundPlayer;
import net.tejty.gamediscs.games.controls.Button;
import net.tejty.gamediscs.games.controls.Controls;
import net.tejty.gamediscs.games.graphics.ParticleColor;
import net.tejty.gamediscs.games.graphics.Renderer;
import net.tejty.gamediscs.item.ItemRegistry;
import net.tejty.gamediscs.item.custom.GamingConsoleItem;
import net.tejty.gamediscs.sounds.SoundRegistry;
import net.tejty.gamediscs.util.networking.ModMessages;
import net.tejty.gamediscs.util.networking.packet.SetBestScoreC2SPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public class Game {
    public GameStage stage = GameStage.START;
    public Controls controls = new Controls(this);
    public SoundPlayer soundPlayer = new SoundPlayer();
    public static final int WIDTH = 140;
    public static final int HEIGHT = 100;
    public final Random random = new Random();
    public int ticks = 0;
    public int score = 0;
    public int lives = maxLives();

    public int maxLives() {
        return 1;
    }

    private List<Particle> particles = new ArrayList<>();
    public Game() {
    }

    public synchronized void prepare() {
        score = 0;
        lives = maxLives();
        respawn();
    }

    public synchronized void start() {
        stage = GameStage.PLAYING;
        ticks = 1;
    }

    public synchronized void die() {
        if (getConsole().getItem() instanceof GamingConsoleItem) {
            String gameName = this.getClass().getName().substring(this.getClass().getPackageName().length() + 1);
            if (GamingConsoleItem.getBestScore(getConsole(), gameName, Minecraft.getInstance().player) < score) {
                ModMessages.sendToServer(new SetBestScoreC2SPacket(gameName, score));
                soundPlayer.playNewBest();
                spawnConfetti();
            } else {
                soundPlayer.playGameOver();
            }
        }
        stage = GameStage.DIED;
        ticks = 1;
    }

    public synchronized void lostLife() {
        lives--;
        soundPlayer.play(SoundRegistry.EXPLOSION.get());
        respawn();
        if (lives <= 0) {
            die();
        }
    }

    public synchronized void respawn() {
        stage = GameStage.START;
        ticks = 1;
        particles.clear();
    }

    public synchronized void win() {
        soundPlayer.playNewBest();
        spawnConfetti();
        if (getConsole().getItem() instanceof GamingConsoleItem) {
            String gameName = this.getClass().getName().substring(this.getClass().getPackageName().length() + 1);
            if (GamingConsoleItem.getBestScore(getConsole(), gameName, Minecraft.getInstance().player) < score) {
                ModMessages.sendToServer(new SetBestScoreC2SPacket(gameName, score));
            }
        }
        stage = GameStage.WON;
        ticks = 1;
    }

    private ItemStack getConsole() {
        Player player = Minecraft.getInstance().player;
        assert player != null;
        ItemStack item = player.getMainHandItem();
        if (item.getItem() instanceof GamingConsoleItem) {
            return item;
        } else {
            item = player.getOffhandItem();
            if (item.getItem() instanceof GamingConsoleItem) {
                return item;
            }
        }
        return new ItemStack(ItemRegistry.GAMING_CONSOLE.get());
    }

    public synchronized void tick() {
        if (stage == GameStage.PLAYING && ticks % gameTickDuration() == 0) {
            gameTick();
        }
        int i = 0;
        while (i < particles.size()) {
            Particle particle = particles.get(i);
            particle.tick();
            if (particle.isDead()) {
                particles.remove(i);
                i--;
            }
            i++;
        }
        ticks++;
    }

    public synchronized void gameTick() {
    }

    public synchronized void render(PoseStack poseStack, int posX, int posY) {
        if (getBackground() != null) {
            RenderSystem.setShaderTexture(0, getBackground());
            GuiComponent.blit(poseStack, posX, posY, 0, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
        }
        renderOverlay(poseStack, posX, posY);
    }

    public synchronized void renderOverlay(PoseStack poseStack, int posX, int posY) {
        Font font = Minecraft.getInstance().font;
        if (stage != GameStage.PLAYING) {
            if (showPressAnyKey()) {
                font.draw(poseStack, Component.translatable("gui.gamingconsole.press_any_key").getString(), posX + (WIDTH - font.width(Component.translatable("gui.gamingconsole.press_any_key").getString())) / 2 + 1, posY + HEIGHT - font.lineHeight - 1 - (ticks % 40 <= 20 ? 0 : 1), 0x373737);
                font.draw(poseStack, Component.translatable("gui.gamingconsole.press_any_key").getString(), posX + (WIDTH - font.width(Component.translatable("gui.gamingconsole.press_any_key").getString())) / 2, posY + HEIGHT - font.lineHeight - 2 - (ticks % 40 <= 20 ? 0 : 1), 0xFFFFFF);
            }
            if (stage == GameStage.DIED || stage == GameStage.WON) {
                RenderSystem.setShaderTexture(0, new ResourceLocation(GameDiscsMod.MOD_ID, "textures/gui/score_board.png"));
                GuiComponent.blit(poseStack, posX, posY, 0, 0, 0, 140, 100, 140, 100);
                Component component = stage == GameStage.DIED ? Component.translatable("gui.gamingconsole.died").withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_RED) : Component.translatable("gui.gamingconsole.won").withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_GREEN);
                font.draw(poseStack, component.getString(), posX + (WIDTH - font.width(component.getString())) / 2, posY + 29, Objects.requireNonNull(component.getStyle().getColor()).getValue());
                font.draw(poseStack, component.getString(), posX + (WIDTH - font.width(component.getString())) / 2, posY + 31, component.getStyle().getColor().getValue());
                font.draw(poseStack, component.getString(), posX + (WIDTH - font.width(component.getString())) / 2 + 1, posY + 30, component.getStyle().getColor().getValue());
                font.draw(poseStack, component.getString(), posX + (WIDTH - font.width(component.getString())) / 2 - 1, posY + 30, component.getStyle().getColor().getValue());
                component = stage == GameStage.DIED ? Component.translatable("gui.gamingconsole.died").withStyle(ChatFormatting.BOLD, ChatFormatting.RED) : Component.translatable("gui.gamingconsole.won").withStyle(ChatFormatting.BOLD, ChatFormatting.GREEN);
                font.draw(poseStack, component.getString(), posX + (WIDTH - font.width(component.getString())) / 2, posY + 30, Objects.requireNonNull(component.getStyle().getColor()).getValue());
                component = Component.translatable("gui.gamingconsole.score").append(": ").append(String.valueOf(score)).withStyle(ChatFormatting.YELLOW);
                font.draw(poseStack, component.getString(), posX + (WIDTH - font.width(component.getString())) / 2, posY + 35 + font.lineHeight, Objects.requireNonNull(component.getStyle().getColor()).getValue());
                int bestScore = GamingConsoleItem.getBestScore(getConsole(), this.getClass().getName().substring(this.getClass().getPackageName().length() + 1), Minecraft.getInstance().player);
                component = Component.translatable(score >= bestScore ? "gui.gamingconsole.new_best_score" : "gui.gamingconsole.best_score").append(": ").append(String.valueOf(bestScore)).withStyle(score >= bestScore ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
                font.draw(poseStack, component.getString(), posX + (WIDTH - font.width(component.getString())) / 2, posY + 50 + font.lineHeight, Objects.requireNonNull(component.getStyle().getColor()).getValue());
            }
        } else {
            if (showScoreBox() && showScore()) {
                RenderSystem.setShaderTexture(0, new ResourceLocation(GameDiscsMod.MOD_ID, "textures/gui/score_box.png"));
                GuiComponent.blit(poseStack, posX, posY, 0, 0, 0, 140, 100, 140, 100);
            }
            if (showScore()) {
                Component comp = scoreText() ? Component.translatable("gui.gamingconsole.score").append(": ") : Component.empty();
                comp = ((MutableComponent) comp).append(String.valueOf(score));
                font.draw(poseStack, comp.getString(), posX + 2, posY + 2, 0x373737);
                font.draw(poseStack, comp.getString(), posX + 1, posY + 1, scoreColor());
            }
        }
        for (Particle particle : particles) {
            if (particle.isForOverlay()) {
                particle.render(poseStack, posX, posY, stage);
            }
        }
    }

    public synchronized void renderParticles(PoseStack poseStack, int posX, int posY) {
        for (Particle particle : particles) {
            particle.render(poseStack, posX, posY, stage);
        }
    }

    public synchronized void buttonDown(Button button) {
        soundPlayer.playClick(true);
        if ((stage == GameStage.START || stage == GameStage.RETRY) && ticks > 8) {
            start();
        } else if ((stage == GameStage.WON || stage == GameStage.DIED) && ticks > 8) {
            prepare();
        }
    }

    public Particle addParticle(Particle particle) {
        particles.add(particle);
        return particle;
    }

    public void spawnParticleExplosion(Supplier<Renderer> renderer, Vec2 pos, int count, int speed, int lifetime, ParticleLevel level) {
        for (int i = 0; i < count; i++) {
            Particle particle = new Particle(pos, renderer.get(), random.nextInt(lifetime / 2, lifetime), level);
            particle.setVelocity(new Vec2(random.nextFloat(-speed, speed), random.nextFloat(-speed, speed)));
            particles.add(particle);
        }
    }

    public void spawnParticleExplosion(Vec2 pos, int count, int speed, int lifetime, ParticleLevel level) {
        soundPlayer.play(SoundEvents.GENERIC_EXPLODE, 1.5f, 0.1f);
        for (int i = 0; i < count; i++) {
            Particle particle = new ExplosionParticle(pos, random.nextInt(lifetime / 2, lifetime), level);
            particle.setVelocity(new Vec2(random.nextFloat(-speed, speed), random.nextFloat(-speed, speed)));
            particles.add(particle);
        }
    }

    public void spawnConfetti() {
        for (int i = 0; i < 30; i++) {
            Particle particle = new ConfettiParticle(new Vec2(0, HEIGHT), ParticleColor.random(random), random.nextInt(50, 70), ParticleLevel.OVERLAY);
            particle.setVelocity(new Vec2(random.nextFloat(1, 10), random.nextFloat(-25, -10)));
            particles.add(particle);
        }
        for (int i = 0; i < 30; i++) {
            Particle particle = new ConfettiParticle(new Vec2(WIDTH, HEIGHT), ParticleColor.random(random), random.nextInt(50, 70), ParticleLevel.OVERLAY);
            particle.setVelocity(new Vec2(random.nextFloat(-10, -1), random.nextFloat(-25, -10)));
            particles.add(particle);
        }
    }

    public synchronized void buttonUp(Button button) {
        soundPlayer.playClick(false);
    }

    public int gameTickDuration() {
        return 1;
    }

    public ResourceLocation getBackground() {
        return null;
    }

    public boolean showScoreBox() {
        return true;
    }

    public boolean showScore() {
        return true;
    }

    public boolean showPressAnyKey() {
        return true;
    }

    public int scoreColor() {
        return 0xFFFFFF;
    }

    public boolean scoreText() {
        return true;
    }

    public Component getName() {
        return Component.empty();
    }

    public ResourceLocation getIcon() {
        return null;
    }

    public ChatFormatting getColor() {
        return ChatFormatting.YELLOW;
    }

    public boolean isEmpty() {
        return this.getClass().equals(Game.class);
    }
}
