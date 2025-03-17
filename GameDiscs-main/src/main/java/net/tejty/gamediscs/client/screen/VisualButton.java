package net.tejty.gamediscs.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;

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
        Minecraft.getInstance().getTextureManager().bindForSetup(image);
        GuiComponent.blit(poseStack, x + this.x, y + this.y, 0, sourceX, pressed ? this.shift + sourceY : sourceY, width, height, imageWidth, imageHeight);
    }
}
