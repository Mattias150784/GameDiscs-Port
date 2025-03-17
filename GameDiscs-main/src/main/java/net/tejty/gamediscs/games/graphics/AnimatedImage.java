package net.tejty.gamediscs.games.graphics;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class AnimatedImage extends MultiImage {
    private final int duration;
    private int timer = 0;


    public AnimatedImage(List<Image> images, int duration) {
        super(images);
        this.duration = duration;
    }

    public AnimatedImage(ResourceLocation file, int fileWidth, int fileHeight, List<Rect2i> rects, int duration) {
        super(file, fileWidth, fileHeight, rects);
        this.duration = duration;
    }

    public AnimatedImage(ResourceLocation file, int fileWidth, int fileHeight, int frames, int duration) {
        super(file, fileWidth, fileHeight, generateRects(fileWidth, fileHeight, frames));
        this.duration = duration;
    }

    public AnimatedImage tick() {
        timer++;
        if (timer >= duration) {
            if (current() >= count() - 1) {
                setImage(0);
            } else {
                setImage(current() + 1);
            }
            timer = 0;
        }
        return this;
    }
}
