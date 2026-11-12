package com.daqem.grieflogger.neoforge.events;

import com.daqem.grieflogger.GriefLogger;
import com.daqem.grieflogger.event.item.BreakItemEvent;
import com.daqem.grieflogger.event.item.PickupItemEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerDestroyItemEvent;

@EventBusSubscriber(modid = GriefLogger.MOD_ID)
public class NeoItemEvents {

    @SubscribeEvent
    public static void onBreakItem(PlayerDestroyItemEvent event) {
        if (event.getEntity().isCreative()) {
            return;
        }
        BreakItemEvent.breakItem(event.getEntity(), event.getOriginal());
    }

    @SubscribeEvent
    public static void pickUpItem(ItemEntityPickupEvent.Post event) {
        PickupItemEvent.onPickupItem(event.getPlayer(), event.getItemEntity(), event.getOriginalStack());
    }

}
