package net.tejty.gamediscs.games.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;

public class Image extends Renderer {
    private final ResourceLocation file;
    private final int fileWidth, fileHeight, x, y, width, height;

    public Image(ResourceLocation file, int width, int height) {
        this(file, width, height, 0, 0, width, height);
    }

    public Image(ResourceLocation file, int fileWidth, int fileHeight, int x, int y, int width, int height) {
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
        RenderSystem.setShaderTexture(0, file);

        GuiComponent.blit(poseStack, posX, posY, 0, x, y, width, height, fileWidth, fileHeight);
    }
}
