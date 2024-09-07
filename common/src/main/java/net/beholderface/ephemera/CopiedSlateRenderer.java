package net.beholderface.ephemera;

import at.petrak.hexcasting.client.RenderLib;
import at.petrak.hexcasting.client.be.BlockEntitySlateRenderer;
import at.petrak.hexcasting.common.blocks.circles.BlockEntitySlate;
import at.petrak.hexcasting.common.blocks.circles.BlockSlate;
import com.mojang.blaze3d.systems.RenderSystem;
import net.beholderface.ephemera.blocks.blockentity.ExtraConnectedSlateBlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3f;

public class CopiedSlateRenderer implements BlockEntityRenderer<ExtraConnectedSlateBlockEntity> {
    public CopiedSlateRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    //just yoinked from base hex
    @Override
    public void render(ExtraConnectedSlateBlockEntity tile, float pPartialTick, MatrixStack ps,
                       VertexConsumerProvider buffer, int light, int overlay) {
        if (tile.pattern == null) {
            return;
        }

        var bs = tile.getCachedState();

        var oldShader = RenderSystem.getShader();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();

        ps.push();

        ps.translate(0.5, 0.5, 0.5);
        var attchFace = bs.get(BlockSlate.ATTACH_FACE);
        if (attchFace == WallMountLocation.WALL) {
            var quarters = (-bs.get(BlockSlate.FACING).getHorizontal()) % 4;
            ps.multiply(new Quaternion(Vec3f.POSITIVE_Y, MathHelper.HALF_PI * quarters, false));
            ps.multiply(new Quaternion(Vec3f.POSITIVE_Z, MathHelper.PI, false));
        } else {
            var neg = attchFace == WallMountLocation.FLOOR ? -1 : 1;
            ps.multiply(new Quaternion(
                    Vec3f.POSITIVE_X,
                    neg * MathHelper.HALF_PI,
                    false));
            var quarters = (bs.get(BlockSlate.FACING).getHorizontal() + 2) % 4;
            ps.multiply(new Quaternion(Vec3f.POSITIVE_Z, neg * MathHelper.HALF_PI * quarters, false));
        }

        // and now Z is out?
        ps.translate(0, 0, -0.5);
        ps.scale(1 / 16f, 1 / 16f, 1 / 16f);
        ps.translate(0, 0, 1.01);

        // yoink code from the pattern greeble
        // Do two passes: one with a random size to find a good COM and one with the real calculation
        var com1 = tile.pattern.getCenter(1);
        var lines1 = tile.pattern.toLines(1, Vec2f.ZERO);

        var maxDx = -1f;
        var maxDy = -1f;
        for (var dot : lines1) {
            var dx = MathHelper.abs(dot.x - com1.x);
            if (dx > maxDx) {
                maxDx = dx;
            }
            var dy = MathHelper.abs(dot.y - com1.y);
            if (dy > maxDy) {
                maxDy = dy;
            }
        }
        var scale = Math.min(3.8f, Math.min(16 / 2.5f / maxDx, 16 / 2.5f / maxDy));

        var com2 = tile.pattern.getCenter(scale);
        var lines2 = tile.pattern.toLines(scale, com2.negate());
        // For some reason it is mirrored left to right and i can't seem to posestack-fu it into shape
        for (int i = 0; i < lines2.size(); i++) {
            var v = lines2.get(i);
            lines2.set(i, new Vec2f(-v.x, v.y));
        }

        var isLit = bs.get(BlockSlate.ENERGIZED);
        var variance = isLit ? 2.5f : 0.5f;
        var speed = isLit ? 0.1f : 0f;
        var stupidHash = tile.getPos().hashCode();
        var zappy = RenderLib.makeZappy(lines2, RenderLib.findDupIndices(tile.pattern.positions()),
                10, variance, speed, 0.2f, 0f, 1f, stupidHash);

        int outer = isLit ? 0xff_64c8ff : 0xff_d2c8c8;
        int inner = isLit ? RenderLib.screenCol(outer) : 0xc8_322b33;
        RenderLib.drawLineSeq(ps.peek().getPositionMatrix(), zappy, 1f, 0f, outer, outer);
        RenderLib.drawLineSeq(ps.peek().getPositionMatrix(), zappy, 0.4f, 0.01f, inner, inner);

        ps.pop();
        RenderSystem.setShader(() -> oldShader);
    }
}
