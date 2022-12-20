package hungteen.opentd.client;

import hungteen.htlib.client.render.entity.HTBoatRender;
import hungteen.htlib.common.entity.HTEntities;
import hungteen.opentd.client.render.entity.PlantEntityRender;
import hungteen.opentd.common.entity.OpenTDEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 11:27
 **/
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientHandler {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(OpenTDEntities.PLANT_ENTITY.get(), PlantEntityRender::new);
    }
}
