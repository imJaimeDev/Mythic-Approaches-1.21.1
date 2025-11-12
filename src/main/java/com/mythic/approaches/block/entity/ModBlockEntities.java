package com.mythic.approaches.block.entity;

import com.mythic.approaches.MythicApproachesMod;
import com.mythic.approaches.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MythicApproachesMod.MOD_ID);

    public static final Supplier<BlockEntityType<CauldronBlockEntity>> CAULDRON_ENTITY =
            BLOCK_ENTITIES.register("cauldron_entity", () -> BlockEntityType.Builder.of(
                    CauldronBlockEntity::new, ModBlocks.CAULDRON.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
