package hungteen.opentd.common.capability;

import hungteen.opentd.OpenTD;
import hungteen.opentd.common.capability.player.PlayerCapProvider;
import hungteen.opentd.common.capability.player.PlayerCapability;
import net.minecraft.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-08 12:37
 **/
public class OpenTDCapabilities {

    public static Capability<PlayerCapability> PLAYER_CAP = CapabilityManager.get(new CapabilityToken<PlayerCapability>() {});

    /**
     * {@link OpenTD#OpenTD()}
     */
    public static void registerCapabilities(RegisterCapabilitiesEvent event){
        event.register(PlayerCapability.class);
    }

    /**
     * {@link OpenTD#OpenTD()}
     */
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof Player){
            event.addCapability(OpenTD.prefix("player_data"), new PlayerCapProvider((Player) event.getObject()));
        }
    }

    public static LazyOptional<PlayerCapability> getPlayerCapability(Player player){
        return player.getCapability(PLAYER_CAP);
    }

}
