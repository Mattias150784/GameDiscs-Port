package net.tejty.gamediscs.games.graphics;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;

public class ParticleRenderer extends Renderer {
    private ResourceLocation file;
    private final int fileWidth;
    private final int fileHeight;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    public ParticleRenderer(ResourceLocation file, int fileWidth, int fileHeight, int x, int y, int width, int height) {
        this.file = file;
        this.fileWidth = fileWidth;
        this.fileHeight = fileHeight;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    @Override
    public void render(PoseStack poseStack, int posX, int posY) {
        Minecraft.getInstance().getTextureManager().bindForSetup(file);
        GuiComponent.blit(poseStack, posX - (int)(0.5 * width), posY - (int)(0.5 * height), 0, x, y, width, height, fileWidth, fileHeight);
    }
}
