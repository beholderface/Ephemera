package net.beholderface.ephemera.registry;

import at.petrak.hexcasting.common.lib.HexBlocks;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.blocks.ExtraConnectedSlateBlock;
import net.beholderface.ephemera.blocks.RelayIndexBlock;
import net.beholderface.ephemera.blocks.RelayTPDetectorBlock;
import net.beholderface.ephemera.blocks.blockentity.ExtraConnectedSlateBlockEntity;
import net.beholderface.ephemera.blocks.blockentity.RelayIndexBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlimeBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class EphemeraBlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Ephemera.MOD_ID, Registry.BLOCK_KEY);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Ephemera.MOD_ID, Registry.BLOCK_ENTITY_TYPE_KEY);

    public static void init() {
        BLOCKS.register();
        BLOCK_ENTITIES.register();
    }

    public static final RegistrySupplier<RelayTPDetectorBlock> TP_DETECTOR = BLOCKS.register("relay_tp_detector", ()-> new RelayTPDetectorBlock(AbstractBlock.Settings.copy(HexBlocks.SLATE_BLOCK)));
    public static final RegistrySupplier<RelayIndexBlock> RELAY_INDEX = BLOCKS.register("relay_index", ()-> new RelayIndexBlock(AbstractBlock.Settings.copy(HexBlocks.SLATE_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<RelayIndexBlockEntity>> RELAY_INDEX_ENTITY = BLOCK_ENTITIES.register("relay_index_entity", () -> BlockEntityType.Builder.create(RelayIndexBlockEntity::new, RELAY_INDEX.get()).build(null));
    /*@Nullable
    public static final RegistrySupplier<SlimeBlock> GUMMY_BLOCK = Platform.isModLoaded("hexgloop") ? BLOCKS.register("thought_gummy_block", ()->new SlimeBlock(AbstractBlock.Settings.copy(Blocks.SLIME_BLOCK))) : null;*/

    public static final RegistrySupplier<ExtraConnectedSlateBlock> SNEAKY_SLATE = BLOCKS.register("sneakyslate", ()-> new ExtraConnectedSlateBlock(AbstractBlock.Settings.copy(HexBlocks.SLATE)));
    public static final RegistrySupplier<BlockEntityType<ExtraConnectedSlateBlockEntity>> SNEAKY_SLATE_ENTITY = BLOCK_ENTITIES.register("sneakyslate_entity", ()->BlockEntityType.Builder.create(ExtraConnectedSlateBlockEntity::new, SNEAKY_SLATE.get()).build(null));

    //public static final RegistrySupplier<Block> FAKE_SLATE = BLOCKS.register("fakeslate", ()->new Block(AbstractBlock.Settings.copy(HexBlocks.SLATE)));
}
