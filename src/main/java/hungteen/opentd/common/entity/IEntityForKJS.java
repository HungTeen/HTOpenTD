package hungteen.opentd.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/6/7 10:50
 */
public interface IEntityForKJS {

    /**
     * KubeJs support.
     */
    void updateTowerComponent(CompoundTag tag);

    CompoundTag getComponentTag();

    ClientEntityResource getClientResource();

    void setClientResource(ClientEntityResource clientTowerResource);

    default Optional<ResourceLocation> getTowerModel() {
        return Optional.ofNullable(getClientResource().getTowerModel());
    }

    default Optional<ResourceLocation> getTowerTexture() {
        return Optional.ofNullable(getClientResource().getTowerTexture());
    }

    default Optional<ResourceLocation> getTowerAnimation() {
        return Optional.ofNullable(getClientResource().getTowerAnimation());
    }

    default Optional<String> getCurrentAnimation() {
        return getCurrentAnimation(0);
    }

    default Optional<String> getCurrentAnimation(int idx) {
        return Optional.ofNullable(getClientResource().getCurrentAnimation(idx));
    }

    default void setTowerModel(ResourceLocation towerModel) {
        this.setClientResource(new ClientEntityResource().from(this.getClientResource()).setTowerModel(towerModel));
    }

    default void setTowerTexture(ResourceLocation towerTexture) {
        this.setClientResource(new ClientEntityResource().from(this.getClientResource()).setTowerTexture(towerTexture));
    }

    default void setTowerAnimation(ResourceLocation towerAnimation) {
        this.setClientResource(new ClientEntityResource().from(this.getClientResource()).setTowerAnimation(towerAnimation));
    }

    default void setCurrentAnimation(String currentAnimation) {
        setCurrentAnimation(currentAnimation, 0);
    }

    default void setCurrentAnimation(String currentAnimation, int idx) {
        this.setClientResource(new ClientEntityResource().from(this.getClientResource()).setCurrentAnimation(currentAnimation, idx));
    }

    class ClientEntityResource {
        private ResourceLocation towerTexture = null;
        private ResourceLocation towerModel = null;
        private ResourceLocation towerAnimation = null;
        private String[] currentAnimations = null;

        public ClientEntityResource() {
            this.currentAnimations = new String[1];
        }

        public ClientEntityResource(int length) {
            this.currentAnimations = new String[length];
        }

        public ClientEntityResource from(ClientEntityResource resource) {
            this.setTowerTexture(resource.getTowerTexture());
            this.setTowerModel(resource.getTowerModel());
            this.setTowerAnimation(resource.getTowerAnimation());
            this.setCurrentAnimations(resource.getCurrentAnimations());
            return this;
        }

        public CompoundTag saveTo(CompoundTag tag) {
            if (this.getTowerTexture() != null) {
                tag.putString("TowerTexture", this.getTowerTexture().toString());
            }
            if (this.getTowerModel() != null) {
                tag.putString("TowerModel", this.getTowerModel().toString());
            }
            if (this.getTowerAnimation() != null) {
                tag.putString("TowerAnimation", this.getTowerAnimation().toString());
            }
            if (this.getCurrentAnimations() != null) {
                CompoundTag listTag = new CompoundTag();
                listTag.putInt("AnimationLength", currentAnimations.length);
                for(int i = 0; i < currentAnimations.length; i++) {
                    if(getCurrentAnimation(i) != null) {
                        listTag.putString("Animation_" + i, getCurrentAnimation(i));
                    }
                }
                tag.put("CurrentAnimations", listTag);
            }
            return tag;
        }

        public ClientEntityResource readFrom(CompoundTag tag) {
            if (tag.contains("TowerTexture")) {
                this.setTowerTexture(new ResourceLocation(tag.getString("TowerTexture")));
            }
            if (tag.contains("TowerModel")) {
                this.setTowerModel(new ResourceLocation(tag.getString("TowerModel")));
            }
            if (tag.contains("TowerAnimation")) {
                this.setTowerAnimation(new ResourceLocation(tag.getString("TowerAnimation")));
            }
            if (tag.contains("CurrentAnimations")) {
                CompoundTag listTag = tag.getCompound("CurrentAnimations");
                int length = listTag.getInt("AnimationLength");
                String[] currentAnimations = new String[length];
                for(int i = 0; i < length; i++) {
                    if(listTag.contains("Animation_" + i)) {
                        currentAnimations[i] = listTag.getString("Animation_" + i);
                    }
                }
                this.setCurrentAnimations(currentAnimations);
            }
            return this;
        }

        public String getCurrentAnimation(int idx) {
            assert idx >= 0 && idx < currentAnimations.length;
            return currentAnimations[idx];
        }

        public String[] getCurrentAnimations() {
            return currentAnimations;
        }

        public void setCurrentAnimations(String[] currentAnimations) {
            this.currentAnimations = currentAnimations;
        }

        public ClientEntityResource setCurrentAnimation(String currentAnimation) {
            return setCurrentAnimation(currentAnimation, 0);
        }

        public ClientEntityResource setCurrentAnimation(String currentAnimation, int idx) {
            assert idx >= 0 && idx < currentAnimations.length;
            this.currentAnimations[idx] = currentAnimation;
            return this;
        }

        public ResourceLocation getTowerAnimation() {
            return towerAnimation;
        }

        public ClientEntityResource setTowerAnimation(ResourceLocation towerAnimation) {
            this.towerAnimation = towerAnimation;
            return this;
        }

        public ResourceLocation getTowerModel() {
            return towerModel;
        }

        public ClientEntityResource setTowerModel(ResourceLocation towerModel) {
            this.towerModel = towerModel;
            return this;
        }

        public ResourceLocation getTowerTexture() {
            return towerTexture;
        }

        public ClientEntityResource setTowerTexture(ResourceLocation towerTexture) {
            this.towerTexture = towerTexture;
            return this;
        }
    }

}
