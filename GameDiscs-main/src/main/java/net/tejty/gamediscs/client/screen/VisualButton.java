package net.tejty.gamediscs.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.systems.RenderSystem;

public record VisualButton(
        ResourceLocation image,
        int imageWidth,
        int imageHeight,
        int x,
        int y,
        int width,
        int height,
        int sourceX,
        int sourceY,
        int shift
) {
    public void render(PoseStack poseStack, int x, int y, boolean pressed) {
        RenderSystem.setShaderTexture(0, image);
        GuiComponent.blit(poseStack, x + this.x, y + this.y, 0, sourceX, pressed ? this.shift + sourceY : sourceY, width, height, imageWidth, imageHeight);
    }
}
