## 1.20.x
### 1.20.x-1.2.0
## 1.19.x
### 1.19.2-1.1.0
#### New Features
* Add custom creative tab for summon cards, and support searching.
* Add plant_hero tower type.
* Add can_float in plant setting to enable them float in water.
* Enable KubeJS to set animation, model and texture of tower entities.
* New effect component named vanilla_hurt, which affected by potion effects.
* Add move_controllers in tower setting, it makes movement more flexible.
* Disable movement setting of pvz_plant, and enable in plant_hero tower type.
* Add straightforward move type.
* Add mod update notification.
* Add laser component.
* KubeJS can modify tower setting by change its nbt.
* Add and, not, or requirement components.
#### Bug fix
* After the tower kills an enemy, if there still exists enemy in its attack range, it will ignore the cooldown and immediately attack again.
* There are conflicts between different animations.
* The duration setting didn't work.
#### Other changes
* Disable movement_setting of pvz_plant.
* Change requirements to requirement in item_setting.