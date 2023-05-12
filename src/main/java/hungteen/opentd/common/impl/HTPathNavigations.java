package hungteen.opentd.common.impl;

import hungteen.htlib.api.interfaces.IHTSimpleRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.IPathNavigationType;
import hungteen.opentd.api.interfaces.ITowerComponentType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.level.Level;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/12 10:31
 */
public class HTPathNavigations {

    public static final HTSimpleRegistry<IPathNavigationType> PATH_NAVIGATION_TYPES = HTRegistryManager.create(OpenTD.prefix("path_navigation_type"));

    public static final IPathNavigationType GROUND = register(new PathNavigationType("ground"){

        @Override
        public PathNavigation create(Level level, PathfinderMob mob) {
            return new GroundPathNavigation(mob, level);
        }
    });

    public static final IPathNavigationType WATER = register(new PathNavigationType("water"){

        @Override
        public PathNavigation create(Level level, PathfinderMob mob) {
            return new WaterBoundPathNavigation(mob, level);
        }
    });

    public static final IPathNavigationType FLY = register(new PathNavigationType("fly"){

        @Override
        public PathNavigation create(Level level, PathfinderMob mob) {
            return new FlyingPathNavigation(mob, level);
        }
    });

    public static final IPathNavigationType AMPHIBIOUS = register(new PathNavigationType("amphibious"){

        @Override
        public PathNavigation create(Level level, PathfinderMob mob) {
            return new AmphibiousPathNavigation(mob, level);
        }
    });

    public static IHTSimpleRegistry<IPathNavigationType> registry(){
        return PATH_NAVIGATION_TYPES;
    }

    public static IPathNavigationType register(IPathNavigationType type) {
        registry().register(type);
        return type;
    }

    public static void register(){
    }

    protected static abstract class PathNavigationType implements IPathNavigationType {

        private final String name;

        protected PathNavigationType(String name) {
            this.name = name;
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
