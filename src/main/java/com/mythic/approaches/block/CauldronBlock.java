package com.mythic.approaches.block;

import com.mojang.serialization.MapCodec;
import com.mythic.approaches.block.entity.CauldronBlockEntity;
import com.mythic.approaches.recipes.CauldronInput;
import com.mythic.approaches.recipes.CauldronRecipe;
import com.mythic.approaches.recipes.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CauldronBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 12, 14);
    public static final MapCodec<CauldronBlock> CODEC = simpleCodec(CauldronBlock::new);

    public CauldronBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CauldronBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldron) {
                cauldron.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(
            @NotNull ItemStack stack, @NotNull BlockState state, Level level, @NotNull BlockPos pos,
            @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult
    ) {
        if (level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldron) {
            //CROUCHING => LOOK FOR RECIPE
            if (player.isCrouching() && stack.isEmpty()) {
                // if we are on server side
                if (!level.isClientSide) {
                    Optional<CauldronRecipe> recipe = findMatchingRecipe(cauldron, level);

                    if (recipe.isPresent()) {
                        CauldronInput input = new CauldronInput(cauldron.getInventory());
                        ItemStack result = recipe.get().assemble(input, level.registryAccess());

                        cauldron.clearContents();

                        if (!player.getInventory().add(result)) {
                            player.drop(result, false);
                        }

                        return ItemInteractionResult.SUCCESS;
                    }
                }

            }
            // NOT CROUCHING => ADD / REMOVE ITEM
            else {
                // Check if empty-handed => retrieve last item
                if (stack.isEmpty()) {
                    int slot = getFirstFullSlot(cauldron);
                    if (slot != -1) {
                        ItemStack retrievedItem = cauldron.inventory.extractItem(slot, 1, false);
                        player.addItem(retrievedItem);
                        cauldron.inventory.setStackInSlot(slot, ItemStack.EMPTY);
                        level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1, 1);

                        return ItemInteractionResult.SUCCESS;
                    }
                } else {
                    // Add item to first empty slot
                    int slot = getFirstEmptySlot(cauldron);
                    if (slot != -1 && !stack.isEmpty()) {
                        cauldron.inventory.insertItem(slot, stack.copy(), false);
                        stack.shrink(1);
                        level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1, 2);

                        return ItemInteractionResult.SUCCESS;
                    }
                }
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /**
     * Searches for a cauldron recipe
     *
     * @param cauldron CauldronBlockEntity instance
     * @param level    Level instance
     * @return first matching recipe containing the items in the cauldron's inventory
     */
    private Optional<CauldronRecipe> findMatchingRecipe(CauldronBlockEntity cauldron, Level level) {
        CauldronInput input = new CauldronInput(cauldron.getInventory());

        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.CAULDRON_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .filter(recipe -> recipe.matches(input, level))
                .findFirst();
    }

    /**
     * Searches for an empty slot
     *
     * @param entity CaldronBlockEntity instance
     * @return The index of the first empty slot or -1 if there are no empty slots
     */
    private int getFirstEmptySlot(CauldronBlockEntity entity) {
        for (int i = 0; i < CauldronBlockEntity.SLOTS_AMOUNT; i++) {
            if (entity.inventory.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Searches for a full slot
     *
     * @param entity CauldronBlockEntity instance
     * @return The index of the first full slot or -1 if there are no full slots
     */
    private int getFirstFullSlot(CauldronBlockEntity entity) {
        for (int i = CauldronBlockEntity.SLOTS_AMOUNT - 1; i >= 0; i--) {
            if (!entity.inventory.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }
}
