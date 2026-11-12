package com.daqem.grieflogger.neoforge.events;

import com.daqem.grieflogger.GriefLogger;
import com.daqem.grieflogger.model.action.BlockAction;
import com.daqem.grieflogger.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionEvent;

@EventBusSubscriber(modid = GriefLogger.MOD_ID)
public class NeoBlockEvents {

    @SubscribeEvent
    public static void explosionEvent(ExplosionEvent.Detonate event) {
        // actor = Who to blame (Player or the Mob itself)
        Entity actor = event.getExplosion().getIndirectSourceEntity();
        if (actor == null) actor = event.getExplosion().getDirectSourceEntity();

        // original = The tool used (TNT, Creeper, etc.)
        Entity original = event.getExplosion().getDirectSourceEntity();

        if (actor == null || original == null) return;

        for (BlockPos pos : event.getAffectedBlocks()) {
            BlockState state = event.getLevel().getBlockState(pos);
            if (!state.isAir()) {
                // Pass both to our new utility
                EntityUtils.logBlockAction(event.getLevel(), pos, state, actor, original, BlockAction.BREAK_BLOCK);
            }
        }
    }


}
