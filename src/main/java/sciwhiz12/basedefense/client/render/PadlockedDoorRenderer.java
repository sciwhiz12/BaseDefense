package sciwhiz12.basedefense.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import sciwhiz12.basedefense.block.PadlockedDoorBlock;
import sciwhiz12.basedefense.block.PadlockedDoorBlock.DoorSide;
import sciwhiz12.basedefense.tileentity.PadlockedDoorTile;

public class PadlockedDoorRenderer extends TileEntityRenderer<PadlockedDoorTile> {
    private final ItemRenderer itemRenderer;

    public PadlockedDoorRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
        itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(PadlockedDoorTile tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer,
            int combinedLightIn, int combinedOverlayIn) {
        matrixStack.push();
        BlockState state = tileEntity.getBlockState();
        ItemStack itemstack = tileEntity.getLockStack();
        Direction dir = state.get(PadlockedDoorBlock.FACING);
        DoorHingeSide hinge = state.get(PadlockedDoorBlock.HINGE);
        double mult = (hinge == DoorHingeSide.RIGHT ? 1D : -1D) * (dir.getAxis() == Direction.Axis.Z ? 1D : -1D);
        matrixStack.translate(dir.getXOffset() * -0.51D + 0.5D, 1D, dir.getZOffset() * -0.51D + 0.5D);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(dir.getHorizontalAngle()));
        matrixStack.translate(mult * 0.275D, 0D, 0D);
        if (state.get(PadlockedDoorBlock.SIDE) == DoorSide.INSIDE) {
            matrixStack.translate(0D, 0D, dir.getAxis() == Direction.Axis.X ? -0.205D : 0.205D);
        }
        matrixStack.scale(0.5F, 0.5F, 0.5F);
        this.itemRenderer.renderItem(null, itemstack, ItemCameraTransforms.TransformType.FIXED, false, matrixStack, buffer,
                tileEntity.getWorld(), combinedLightIn, combinedOverlayIn);
        matrixStack.pop();
    }
}
