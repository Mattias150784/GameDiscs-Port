package net.tejty.gamediscs.games.graphics;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import java.util.Random;

public class BreakParticleRenderer extends Renderer {
    private static final int SIZE = 3;
    private final ResourceLocation file;
    private final int fileWidth;
    private final int fileHeight;
    private final int x;
    private final int y;
    public BreakParticleRenderer(ResourceLocation file, int fileWidth, int fileHeight) {
        this.file = file;
        this.fileWidth = fileWidth;
        this.fileHeight = fileHeight;
        Random random = new Random();
        x = random.nextInt(0, fileWidth - SIZE);
        y = random.nextInt(0, fileHeight - SIZE);
    }
    @Override
    public void render(PoseStack poseStack, int posX, int posY) {
        Minecraft.getInstance().getTextureManager().bindForSetup(file);
        GuiComponent.blit(poseStack, posX - SIZE / 2, posY - SIZE / 2, 0, x, y, SIZE, SIZE, fileWidth, fileHeight);
    }
}
