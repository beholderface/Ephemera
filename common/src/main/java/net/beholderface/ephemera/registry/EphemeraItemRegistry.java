package net.beholderface.ephemera.registry;

import at.petrak.hexcasting.common.lib.HexBlocks;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.items.ConjuredArmorItem;
import net.beholderface.ephemera.items.ConjuredArmorMaterial;
import net.beholderface.ephemera.items.ExtraConnectedSlateItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.registry.Registry;

import static net.beholderface.ephemera.Ephemera.id;

public class EphemeraItemRegistry {
    // Register items through this
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Ephemera.MOD_ID, Registry.ITEM_KEY);

    public static void init() {
        ITEMS.register();
    }

    public static final ArmorMaterial MEDIA_ARMOR = new ConjuredArmorMaterial();

    // A new creative tab. Notice how it is one of the few things that are not deferred
    public static final ItemGroup EPHEMERA_STUFF = CreativeTabRegistry.create(id("ephemera"), () -> new ItemStack(EphemeraItemRegistry.TP_DETECTOR_ITEM.get()));

    private static final Item.Settings EPHEMERA_STACKABLE64 = new Item.Settings().group(EPHEMERA_STUFF).maxCount(64);
    private static final Item.Settings EPHEMERA_STACKABLE16 = new Item.Settings().group(EPHEMERA_STUFF).maxCount(16);
    private static final Item.Settings EPHEMERA_UNSTACKABLE = new Item.Settings().group(EPHEMERA_STUFF).maxCount(1);

    public static final RegistrySupplier<ArmorItem> MEDIA_HELMET = ITEMS.register("media_helmet", ()-> new ConjuredArmorItem(MEDIA_ARMOR, EquipmentSlot.HEAD, new Item.Settings()));
    public static final RegistrySupplier<ArmorItem> MEDIA_CHESTPLATE = ITEMS.register("media_chestplate", ()-> new ConjuredArmorItem(MEDIA_ARMOR, EquipmentSlot.CHEST, new Item.Settings()));
    public static final RegistrySupplier<ArmorItem> MEDIA_LEGGINGS = ITEMS.register("media_leggings", ()-> new ConjuredArmorItem(MEDIA_ARMOR, EquipmentSlot.LEGS, new Item.Settings()));
    public static final RegistrySupplier<ArmorItem> MEDIA_BOOTS = ITEMS.register("media_boots", ()-> new ConjuredArmorItem(MEDIA_ARMOR, EquipmentSlot.FEET, new Item.Settings()));
    public static final RegistrySupplier<BlockItem> RELAY_INDEX_ITEM = ITEMS.register("relay_index", ()->new BlockItem(EphemeraBlockRegistry.RELAY_INDEX.get(), EPHEMERA_STACKABLE64));
    public static final RegistrySupplier<BlockItem> TP_DETECTOR_ITEM = ITEMS.register("relay_tp_detector", ()->new BlockItem(EphemeraBlockRegistry.TP_DETECTOR.get(), EPHEMERA_STACKABLE64));

    public static final RegistrySupplier<ExtraConnectedSlateItem> SNEAKY_SLATE = ITEMS.register("sneakyslate", ()->new ExtraConnectedSlateItem(EphemeraBlockRegistry.SNEAKY_SLATE.get(), EPHEMERA_STACKABLE64));
}
