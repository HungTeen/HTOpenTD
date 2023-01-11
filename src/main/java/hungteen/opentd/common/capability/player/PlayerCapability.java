package hungteen.opentd.common.capability.player;

import hungteen.htlib.common.capability.player.HTPlayerCapability;
import net.minecraft.world.entity.player.Player;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-08 12:39
 **/
public class PlayerCapability extends HTPlayerCapability<PlayerDataManager> {

    @Override
    public void init(Player player) {
        if(this.dataManager == null){
            this.dataManager = new PlayerDataManager(player);
        }
    }
}
