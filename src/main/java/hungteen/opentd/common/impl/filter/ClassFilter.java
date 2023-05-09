package hungteen.opentd.common.impl.filter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.IEntityClassifier;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import hungteen.opentd.common.entity.PlantEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.raid.Raider;

import java.util.Arrays;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-18 17:30
 **/
public record ClassFilter(IEntityClassifier entityClassifier) implements ITargetFilter{

    private static final HTSimpleRegistry<IEntityClassifier> ENTITY_CLASSIFIERS = HTRegistryManager.create(OpenTD.prefix("entity_classifier"));
    public static final Codec<ClassFilter> CODEC = RecordCodecBuilder.<ClassFilter>mapCodec(instance -> instance.group(
            ENTITY_CLASSIFIERS.byNameCodec().fieldOf("class").forGetter(ClassFilter::entityClassifier)
    ).apply(instance, ClassFilter::new)).codec();

    public static final IEntityClassifier LIVING = new DefaultEntityClassifier("living", LivingEntity.class);
    public static final IEntityClassifier PLAYER = new DefaultEntityClassifier("player", PlantEntity.class);
    public static final IEntityClassifier VILLAGER = new DefaultEntityClassifier("villager", AbstractVillager.class);

    public static final IEntityClassifier ENEMY = new DefaultEntityClassifier("monster", Enemy.class);
    public static final IEntityClassifier RAIDER = new DefaultEntityClassifier("raider", Raider.class);
    public static final IEntityClassifier ANIMAL = new DefaultEntityClassifier("animal", Animal.class);
    public static final IEntityClassifier WATER_ANIMAL = new DefaultEntityClassifier("water_animal", WaterAnimal.class);


    /**
     * {@link OpenTD#OpenTD()} ()}
     */
    public static void registerClassifiers(){
        Arrays.asList(LIVING, PLAYER, VILLAGER, ENEMY, RAIDER, ANIMAL, WATER_ANIMAL).forEach(ClassFilter::registerClassifier);
    }

    public static void registerClassifier(IEntityClassifier type){
        ENTITY_CLASSIFIERS.register(type);
    }

    @Override
    public boolean match(ServerLevel level, Entity owner, Entity target) {
        return entityClassifier().getEntityClass().isAssignableFrom(target.getClass());
    }

    @Override
    public ITargetFilterType<?> getType() {
        return HTTargetFilters.CLASS_FILTER;
    }

    protected record DefaultEntityClassifier(String name, Class<?> classifier) implements IEntityClassifier {

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
