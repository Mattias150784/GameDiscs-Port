package net.tejty.gamediscs.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.tejty.gamediscs.GameDiscsMod;
import net.tejty.gamediscs.games.util.Game;
import net.tejty.gamediscs.games.controls.Button;
import net.tejty.gamediscs.item.custom.GameDiscItem;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GamingConsoleScreen extends Screen {
    private static final ResourceLocation BACKGROUD = new ResourceLocation(GameDiscsMod.MOD_ID, "textures/gui/gaming_console.png");
    private static final int CONSOLE_WIDTH = 160;
    private static final int CONSOLE_HEIGHT = 198;
    private static final int SCREEN_X = 10;
    private static final int SCREEN_Y = 10;
    private static final int W = 87;
    private static final int S = 83;
    private static final int A = 65;
    private static final int D = 68;
    private static final int SPACE = 32;
    private static final int ENTER = 257;
    private static final VisualButton W_BUTTON = new VisualButton(BACKGROUD, 256, 256, 33, 121, 14, 24, 183, 0, 24);
    private static final VisualButton A_BUTTON = new VisualButton(BACKGROUD, 256, 256, 17, 137, 23, 15, 160, 0, 24);
    private static final VisualButton D_BUTTON = new VisualButton(BACKGROUD, 256, 256, 40, 137, 23, 15, 197, 0, 24);
    private static final VisualButton S_BUTTON = new VisualButton(BACKGROUD, 256, 256, 33, 145, 14, 23, 220, 0, 24);
    private static final VisualButton B1_BUTTON = new VisualButton(BACKGROUD, 256, 256, 96, 136, 16, 16, 234, 0, 24);
    private static final VisualButton B2_BUTTON = new VisualButton(BACKGROUD, 256, 256, 128, 128, 16, 16, 234, 0, 24);

    private int getConsoleX() {
        return (this.width - CONSOLE_WIDTH) / 2;
    }

    private int getConsoleY() {
        return (this.height - CONSOLE_HEIGHT) / 2;
    }

    private List<Game> availableGames = new ArrayList<>();
    private int selected = 0;
    private Game game = new Game();

    public GamingConsoleScreen(Component title) {
        super(title);
        availableGames = scanForGames();
        Timer timer = new Timer(50, e -> {
            if (game != null) {
                game.tick();
            }
        });
        timer.start();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(poseStack);
        renderGameScreen(poseStack, getConsoleX() + SCREEN_X, getConsoleY() + SCREEN_Y);
        renderButtons(poseStack);
        super.render(poseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void renderBackground(PoseStack poseStack) {
        super.renderBackground(poseStack);
        RenderSystem.setShaderTexture(0, BACKGROUD);
        this.blit(poseStack, getConsoleX(), getConsoleY(), 0, 0, CONSOLE_WIDTH, CONSOLE_HEIGHT);
    }

    private void renderGameScreen(PoseStack poseStack, int x, int y) {
        if (!game.isEmpty()) {
            game.render(poseStack, x, y);
        } else {
            renderGameSelection(poseStack, x, y);
        }
    }

    private void renderGameSelection(PoseStack poseStack, int x, int y) {
        if (!availableGames.isEmpty()) {
            this.minecraft.getTextureManager().bindForSetup(new ResourceLocation(GameDiscsMod.MOD_ID, "textures/gui/selected.png"));
            this.blit(poseStack, x, y + 3 + font.lineHeight + 18 * selected - (Math.max(0, selected - 3) * 18), 0, 0, 140, 18);
        }
        font.draw(poseStack, Component.translatable("gui.gamingconsole.select_game").withStyle(ChatFormatting.BOLD).getString(), x + (Game.WIDTH - font.width(Component.translatable("gui.gamingconsole.select_game").withStyle(ChatFormatting.BOLD).getString())) / 2, y + 3 - (Math.max(0, selected - 3) * 18), 0xace53b);
        for (int i = 0; i < availableGames.size(); i++) {
            font.draw(poseStack, Component.literal(availableGames.get(i).getName().getString()).withStyle(availableGames.get(i).getColor(), i == selected ? ChatFormatting.BOLD : ChatFormatting.ITALIC).getString(), x + 22, y + 4 + font.lineHeight + 18 * i + (18 - font.lineHeight) / 2 - (Math.max(0, selected - 3) * 18), availableGames.get(i).getColor().getColor());
            this.minecraft.getTextureManager().bindForSetup(availableGames.get(i).getIcon());
            this.blit(poseStack, x + 3, y + 4 + font.lineHeight + 18 * i - (Math.max(0, selected - 3) * 18), 0, 0, 16, 16);
        }
    }

    private void renderButtons(PoseStack poseStack) {
        W_BUTTON.render(poseStack, getConsoleX(), getConsoleY(), game.controls.isButtonDown(Button.UP));
        A_BUTTON.render(poseStack, getConsoleX(), getConsoleY(), game.controls.isButtonDown(Button.LEFT));
        D_BUTTON.render(poseStack, getConsoleX(), getConsoleY(), game.controls.isButtonDown(Button.RIGHT));
        S_BUTTON.render(poseStack, getConsoleX(), getConsoleY(), game.controls.isButtonDown(Button.DOWN));
        B1_BUTTON.render(poseStack, getConsoleX(), getConsoleY(), game.controls.isButtonDown(Button.BUTTON1));
        B2_BUTTON.render(poseStack, getConsoleX(), getConsoleY(), game.controls.isButtonDown(Button.BUTTON2));
    }

    public List<Game> scanForGames() {
        List<Game> games = new ArrayList<>();
        Player player = Minecraft.getInstance().player;
        for (int i = 0; i < Objects.requireNonNull(player).getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).getItem() instanceof GameDiscItem disc) {
                games.removeIf(game -> game.getClass().equals(disc.getGame().getClass()));
                games.add(disc.getGame());
            }
        }
        return games;
    }

    @Override
    public boolean keyPressed(int key, int pScanCode, int pModifiers) {
        boolean flag = false;
        if (!game.isEmpty()) {
            if (key == 81) {
                game = new Game();
                flag = true;
                game.soundPlayer.playConfirm();
            } else if (key == 82) {
                game.prepare();
                flag = true;
                game.soundPlayer.playConfirm();
            }
        } else {
            if (key == 81) {
                this.minecraft.setScreen(null);
                return true;
            }
        }
        if (game != null) {
            switch (key) {
                case W -> {
                    game.controls.setButton(Button.UP, true);
                    flag = true;
                }
                case S -> {
                    game.controls.setButton(Button.DOWN, true);
                    flag = true;
                }
                case A -> {
                    game.controls.setButton(Button.LEFT, true);
                    flag = true;
                }
                case D -> {
                    game.controls.setButton(Button.RIGHT, true);
                    flag = true;
                }
                case SPACE -> {
                    game.controls.setButton(Button.BUTTON1, true);
                    flag = true;
                }
                case ENTER -> {
                    game.controls.setButton(Button.BUTTON2, true);
                    flag = true;
                }
            }
        }
        if (game.isEmpty()) {
            if (key == W) {
                int newSelected = selected - 1;
                if (newSelected < 0) {
                    newSelected = availableGames.size() - 1;
                }
                selected = newSelected;
                flag = true;
                game.soundPlayer.playSelect();
            }
            if (key == S) {
                int newSelected = selected + 1;
                if (newSelected > availableGames.size() - 1) {
                    newSelected = 0;
                }
                selected = newSelected;
                flag = true;
                game.soundPlayer.playSelect();
            }
            if ((key == SPACE || key == ENTER) && !availableGames.isEmpty()) {
                Game newGame = availableGames.get(selected);
                newGame.prepare();
                game = newGame;
                flag = true;
                game.soundPlayer.playConfirm();
            }
        }
        return (super.keyPressed(key, pScanCode, pModifiers) || flag);
    }

    @Override
    public
    void onClose() {
        super.onClose();
        game = null;
    }
}
