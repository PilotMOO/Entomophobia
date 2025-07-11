package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.entity.celestial.CarrioniteEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CarrioniteOrbitWand extends Item {
    public CarrioniteOrbitWand(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer sPlayer){
            CarrioniteEntity.createOrbitingCarrioniteOverPlayer(sPlayer);
        }
        player.getCooldowns().addCooldown(this, 20);
        return super.use(level, player, hand);
    }
}
