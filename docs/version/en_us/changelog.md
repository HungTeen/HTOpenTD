## 1.19.x
### 1.19.2-1.2.2
#### New Features
* Add 'persistent' in tower_setting, which can disable natural despawn.
* Add 'translucent' in render_setting to enable translucent texture rendering.
* Separate recipe display in JEI.
* Kubejs can call tower.setIgnoreWorkAnimation(true) to ignore work animations.
#### Bug Fix
* 'count' in gen_setting not working.
#### Other Changes
* Add 'max_height_offset' and 'tries' in summon_effect to handle ground summon.
### 1.19.2-1.2.1
#### Bug Fix
* Explosion effect can not deal damage to target.
* When Summon card placement on entity fails, it tries to pass through the entity to place it on block.
#### Other Changes
* Add 'self' in explosion effect to decide where to explode.
### 1.19.2-1.2.0
#### New Features:
* Enable modification of bullet animation, texture, model, nbt by KubeJS.
* Add boss_bar_setting to plant hero setting, which can customize boss bar. 
* Add follow_goal to tower defence, which can make them follow owner. 
* Add Self filter, Team filter. 
* Add setLockTarget method for KubeJs to lock target. 
* New bullet_settings datapack folder. 
* Bullet support ComponentLocation nbt to summon by command. 
* Death animation support. 
* KubeJs can point out the move destination of plant hero by setMoveTo method. 
* Add can_ride_in_water for tower entity. 
* Tag filer support entity tag for vanilla command /tag. 
* Add water_slow_down for bullet.
#### Bug Fix
* Summon card can not be unbreakable. 
* Tower has no information of its owner. 
* extra_speed in KnockBackEffect has no effect. 
#### Other Changes. 
* Add same_team_with_owner in plant_setting, bullet_setting and hero_setting to auto sync team with its owner. 
* Add fly_speed for tower entity.
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
#### Bug Fix
* After the tower kills an enemy, if there still exists enemy in its attack range, it will ignore the cooldown and immediately attack again.
* There are conflicts between different animations.
* The duration setting didn't work.
#### Other Changes
* Disable movement_setting of pvz_plant.
* Change requirements to requirement in item_setting.