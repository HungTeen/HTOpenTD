package hungteen.opentd.common.impl;

import hungteen.htlib.api.interfaces.IHTSimpleRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.IPathNavigationType;
import hungteen.opentd.util.Util;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.level.Level;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/12 10:31
 */
public interface OTDPathNavigations {

    HTSimpleRegistry<IPathNavigationType> PATH_NAVIGATION_TYPES = HTRegistryManager.createSimple(Util.prefix("path_navigation_type"));

    IPathNavigationType GROUND = register(new PathNavigationType("ground"){

        @Override
        public PathNavigation create(Level level, Mob mob) {
            return new GroundPathNavigation(mob, level);
        }
    });

    IPathNavigationType WATER = register(new PathNavigationType("water"){

        @Override
        public PathNavigation create(Level level, Mob mob) {
            return new WaterBoundPathNavigation(mob, level);
        }
    });

    IPathNavigationType FLY = register(new PathNavigationType("fly"){

        @Override
        public PathNavigation create(Level level, Mob mob) {
            return new FlyingPathNavigation(mob, level);
        }
    });

    IPathNavigationType AMPHIBIOUS = register(new PathNavigationType("amphibious"){

        @Override
        public PathNavigation create(Level level, Mob mob) {
            return new AmphibiousPathNavigation(mob, level);
        }
    });

    static IHTSimpleRegistry<IPathNavigationType> registry(){
        return PATH_NAVIGATION_TYPES;
    }

    static IPathNavigationType register(IPathNavigationType type) {
        return registry().register(type);
    }

    abstract class PathNavigationType implements IPathNavigationType {

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
