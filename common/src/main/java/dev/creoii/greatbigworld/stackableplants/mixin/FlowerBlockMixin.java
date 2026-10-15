package dev.creoii.greatbigworld.stackableplants.mixin;

import dev.creoii.greatbigworld.stackableplants.util.StackablePlantsProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowerBlock.class)
public abstract class FlowerBlockMixin extends VegetationBlock implements SuspiciousEffectHolder {
    @Unique private static final VoxelShape LARGE_SHAPE = Block.box(3.5d, 0d, 3.5d, 12.5d, 10d, 12.5d);

    protected FlowerBlockMixin(Properties settings) {
        super(settings);
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/item/component/SuspiciousStewEffects;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", at = @At("TAIL"))
    private void gbw$setDefaultState(SuspiciousStewEffects stewEffects, Properties settings, CallbackInfo ci) {
        BlockState defaultState = getStateDefinition().any();
        if (defaultState.hasProperty(StackablePlantsProperties.FLOWERS))
            defaultState = defaultState.setValue(StackablePlantsProperties.FLOWERS, 1);
        registerDefaultState(defaultState);
    }

    @Inject(method = "getShape", at = @At("RETURN"), cancellable = true)
    private void gbw$stackedShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(Shapes.or(state.getValue(StackablePlantsProperties.FLOWERS) > 2 ? LARGE_SHAPE.move(state.getOffset(pos)) : cir.getReturnValue(), Shapes.empty()));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(StackablePlantsProperties.FLOWERS);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (state.is(this)) {
            return state.setValue(StackablePlantsProperties.FLOWERS, Math.min(4, state.getValue(StackablePlantsProperties.FLOWERS) + 1));
        }
        return super.getStateForPlacement(ctx);
    }

    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return !context.isSecondaryUseActive() && ((context.getItemInHand().is(asItem()) && state.getValue(StackablePlantsProperties.FLOWERS) < 4) || super.canBeReplaced(state, context));
    }
}
