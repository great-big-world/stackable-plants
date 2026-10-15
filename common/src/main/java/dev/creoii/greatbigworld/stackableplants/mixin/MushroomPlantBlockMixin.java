package dev.creoii.greatbigworld.stackableplants.mixin;

import dev.creoii.greatbigworld.stackableplants.util.StackablePlantsProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.levelgen.feature.Feature;
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

@Mixin(MushroomBlock.class)
public abstract class MushroomPlantBlockMixin extends VegetationBlock implements BonemealableBlock {
    @Unique
    private static final VoxelShape LARGE_SHAPE = Block.box(3.5d, 0d, 3.5d, 12.5d, 10d, 12.5d);

    protected MushroomPlantBlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void gbw$setDefaultState(final ResourceKey<Feature> feature, Properties settings, CallbackInfo ci) {
        BlockState defaultState = getStateDefinition().any();
        if (defaultState.hasProperty(StackablePlantsProperties.MUSHROOMS))
            defaultState = defaultState.setValue(StackablePlantsProperties.MUSHROOMS, 1);
        registerDefaultState(defaultState);
    }

    @Inject(method = "getShape", at = @At("RETURN"), cancellable = true)
    private void gbw$stackedShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(Shapes.or(state.getValue(StackablePlantsProperties.MUSHROOMS) > 2 ? LARGE_SHAPE : cir.getReturnValue(), Shapes.empty()));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(StackablePlantsProperties.MUSHROOMS);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (state.is(this)) {
            return state.setValue(StackablePlantsProperties.MUSHROOMS, Math.min(4, state.getValue(StackablePlantsProperties.MUSHROOMS) + 1));
        }
        return super.getStateForPlacement(ctx);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return !context.isSecondaryUseActive() && (context.getItemInHand().is(asItem()) && state.getValue(StackablePlantsProperties.MUSHROOMS) < 4) || super.canBeReplaced(state, context);
    }
}
