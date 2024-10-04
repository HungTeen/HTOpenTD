## 1.20.x
### 1.20.1-1.3.1
#### New Features
* Added Pea Shooter, Diamond Shooter, Sun Flower, Laser Sunflower, Flying Shooter, and a batch of simple examples.
* Item descriptions in item settings now support internationalization.
* Added `laser_color` option to fix the laser color.
* Added `can_breath_in_water` option to allow entities to breathe underwater.
* Introduced `OTDFlyingMoveComponent` (vanilla one does not support distance control).
* Added `must_see_target` field to the shooting component to restrict targets to those within the line of sight.
* Added `use_cost` to item settings for flexible control over item usage consumption.
#### Bug Fixes
* Incorrect position of the summon effect component.
* The laser component requires both continuous effect and final effect to be present, otherwise, it will not work.
* The player option in class_filter does not work.
* Pult only hits itself if auto-tracking is not enabled.
* Bullet hitbox do not work.
* KubeJS cannot modify the currently playing animation.
* Bullet transparency and scaling options are ineffective.
* The growth component ignores the NoAI setting.
* Render offset issue for summon card cooldown in the GUI.
#### Other Changes
* Entities with NoAI no longer trigger the continuous effect component.
* Removed entity assertion filtering component.
* Flight speed is now affected by the FlyingSpeed attribute.
* Added an option in the configuration file to show or hide default plants.
### 1.20.x-1.3.0
#### New Features
* All OTD data packs now support references.
#### Bug Fixes
* The `min_count` field in the surrounding entity requirement component is ineffective.
#### Other Changes
* Renamed `item_settings` to `item_setting` and `tower_settings` to `tower_setting`.
* Removed the independent data pack folder for `item_settings`, and moved the `requirement` field in `item_setting` to the same level.
## 1.19.x
### 1.19.2-1.2.3
#### New Features
* Added `laser_color` option to modify the laser color.
* Added `can_breath_in_water` option to allow entities to breathe underwater.
* Introduced OTD flight component (vanilla flight does not support distance control).
* Added `must_see_target` field to the shooting component to restrict targets to those within the line of sight.
#### Bug Fixes
* Incorrect position of the summon effect component.
* The laser component requires both continuous effect and final effect to be present, otherwise, it will not work.
* The player option in class_filter does not work.
* Pult only hits itself if auto-tracking is not enabled.
* Bullet hitbox do not work. 
* Bullet transparency and scaling options are ineffective. 
* Components like growth ignore the NoAI setting.
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