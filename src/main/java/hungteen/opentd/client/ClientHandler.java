package hungteen.opentd.client;

import hungteen.opentd.client.model.item.SummonTowerModel;
import hungteen.opentd.client.render.entity.BulletEntityRender;
import hungteen.opentd.client.render.entity.PlantEntityRender;
import hungteen.opentd.client.render.item.CoolDownDecorator;
import hungteen.opentd.common.entity.OpenTDEntities;
import hungteen.opentd.common.item.OpenTDItems;
import hungteen.opentd.impl.HTItemSettings;
import hungteen.opentd.impl.HTSummonItems;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
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
        event.registerEntityRenderer(OpenTDEntities.BULLET_ENTITY.get(), BulletEntityRender::new);
        event.registerEntityRenderer(OpenTDEntities.PLANT_ENTITY.get(), PlantEntityRender::new);
    }

    @SubscribeEvent
    public static void bakeModel(ModelEvent.BakingCompleted event) {
        final ModelResourceLocation key = new ModelResourceLocation(OpenTDItems.SUMMON_TOWER_ITEM.getId(), "inventory");
        final BakedModel oldModel = event.getModels().get(key);
        if (oldModel != null) {
            event.getModels().put(key, new SummonTowerModel(oldModel, event.getModelBakery()));
        }
    }

    @SubscribeEvent
    public static void bakeModel(ModelEvent.RegisterAdditional event) {
        HTSummonItems.SUMMON_ITEMS.getValues().stream()
                .map(HTSummonItems.SummonEntry::itemSettings)
                .map(HTItemSettings.ItemSetting::model)
                .map(model -> new ModelResourceLocation(model, "inventory"))
                .forEach(event::register);
    }

    @SubscribeEvent
    public static void registerItemRender(RegisterItemDecorationsEvent event) {
        event.register(OpenTDItems.SUMMON_TOWER_ITEM.get(), new CoolDownDecorator());
    }

}
