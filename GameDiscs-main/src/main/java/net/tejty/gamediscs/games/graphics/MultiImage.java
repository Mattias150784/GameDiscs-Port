package net.tejty.gamediscs.games.graphics;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MultiImage extends Renderer {
    private List<Image> images;
    private int current = 0;

    public MultiImage(List<Image> images) {
        this.images = images != null ? images : new ArrayList<>();
    }

    public MultiImage(ResourceLocation file, int fileWidth, int fileHeight, List<Rect2i> rects) {
        images = new ArrayList<>();
        for (Rect2i rect : rects) {
            images.add(new Image(file, fileWidth, fileHeight, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));
        }
    }

    public MultiImage(ResourceLocation file, int fileWidth, int fileHeight, int count) {
        this(file, fileWidth, fileHeight, generateRects(fileWidth, fileHeight, count));
    }

    public static List<Rect2i> generateRects(int fileWidth, int fileHeight, int count) {
        List<Rect2i> rects = new ArrayList<>();
        int rectHeight = fileHeight / count;
        for (int i = 0; i < count; i++) {
            rects.add(new Rect2i(0, rectHeight * i, fileWidth, rectHeight));
        }
        return rects;
    }

    public MultiImage setImage(int index) {
        current = Math.min(Math.max(index, 0), count() - 1);
        return this;
    }

    public int current() {
        return current;
    }

    public int count() {
        return images.size();
    }

    @Override
    public void render(PoseStack graphics, int posX, int posY) {
        if (!images.isEmpty()) {
            images.get(current).render(graphics, posX, posY);
        }
    }
}
