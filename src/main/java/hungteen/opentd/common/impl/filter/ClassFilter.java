package hungteen.opentd.common.impl.filter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.interfaces.IHTSimpleRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.IEntityClassifier;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import hungteen.opentd.util.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-18 17:30
 **/
public record ClassFilter(IEntityClassifier entityClassifier) implements ITargetFilter{

    private static final HTSimpleRegistry<IEntityClassifier> ENTITY_CLASSIFIERS = HTRegistryManager.createSimple(Util.prefix("entity_classifier"));
    public static final Codec<ClassFilter> CODEC = RecordCodecBuilder.<ClassFilter>mapCodec(instance -> instance.group(
            registry().byNameCodec().fieldOf("class").forGetter(ClassFilter::entityClassifier)
    ).apply(instance, ClassFilter::new)).codec();

    public static final IEntityClassifier LIVING = create("living", LivingEntity.class);
    public static final IEntityClassifier PLAYER = create("player", Player.class);
    public static final IEntityClassifier VILLAGER = create("villager", AbstractVillager.class);

    public static final IEntityClassifier ENEMY = create("enemy", Enemy.class);
    public static final IEntityClassifier RAIDER = create("raider", Raider.class);
    public static final IEntityClassifier ANIMAL = create("animal", Animal.class);
    public static final IEntityClassifier WATER_ANIMAL = create("water_animal", WaterAnimal.class);

    public static IHTSimpleRegistry<IEntityClassifier> registry(){
        return ENTITY_CLASSIFIERS;
    }

    public static IEntityClassifier create(String name, Class<?> classifier){
        return registry().register(new DefaultEntityClassifier(name, classifier));
    }

    @Override
    public boolean match(ServerLevel level, Entity owner, Entity target) {
        return entityClassifier().getEntityClass().isAssignableFrom(target.getClass());
    }

    @Override
    public ITargetFilterType<?> getType() {
        return OTDTargetFilterTypes.CLASS_FILTER;
    }

    record DefaultEntityClassifier(String name, Class<?> classifier) implements IEntityClassifier {

        @Override
        public Class<?> getEntityClass() {
            return classifier();
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getModID() {
            return OpenTD.MOD_ID;
        }
    }

}
