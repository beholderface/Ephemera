package net.beholderface.ephemera.forge;

import at.petrak.hexcasting.forge.ForgeHexClientInitializer;
import dev.architectury.platform.forge.EventBuses;
import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.forge.ForgePacketHandler;
import net.beholderface.ephemera.registry.EphemeraAttributes;
import net.beholderface.ephemera.registry.EphemeraIotaTypeRegistry;
import net.beholderface.ephemera.registry.EphemeraPatternRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

/**
 * This is your loading entrypoint on forge, in case you need to initialize
 * something platform-specific.
 */
@Mod(Ephemera.MOD_ID)
public class EphemeraForge {
    public EphemeraForge() {
        // Submit our event bus to let architectury register our content on the right time
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Ephemera.MOD_ID, bus);
        bus.addListener(EphemeraClientForge::init);
        bus.addListener(EphemeraClientForge::registerRenderers);
        Ephemera.init();
        bus.addListener((RegisterEvent event) -> {
            EphemeraIotaTypeRegistry.init();
            EphemeraPatternRegistry.init();
        });
        EphemeraAttributes.register((attribute, id)->{
            bus.addListener((RegisterEvent event) -> {
                event.register(RegistryKeys.ATTRIBUTE, id, ()->attribute);
            });
        });
        bus.addListener((FMLCommonSetupEvent evt)-> evt.enqueueWork(ForgePacketHandler::init));
        bus.addListener((EntityAttributeModificationEvent e) ->{
            e.add(EntityType.PLAYER, EphemeraAttributes.BREAK_TIER_BOOST);
        });
    }
}
