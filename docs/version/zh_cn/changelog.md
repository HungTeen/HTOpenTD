## 1.20.x
### 1.20.x-1.3.2
#### 新内容
* 伤害效果组件添加 specific_source 字段（可选），指定伤害类型。
#### Bug修复
* 子弹实体的朝向不对且频繁抖动。
* 激光 cooldown 字段无效。
* 投掷子弹不能横向偏移。
* 子弹缩放进行了两次。
* 粒子效果组件无效。
* 植物设置中 extra_nbt 字段在指令召唤时无效。
#### 其他改变
* 支持 KubeJs 自定义三层动画。
* 冒险模式可以使用召唤卡。
* 子弹不再瞄着头顶攻击，而是眼睛。
### 1.20.x-1.3.1
#### 新内容
* 增加豌豆投手、钻石射手、向日果、激光葵、飞行射手以及一批简单示例。
* 物品设置的物品描述支持国际化。
* 增加 `laser_color` 选项，可固定激光颜色。
* 添加 `can_breath_in_water` 选项，可使实体在水中呼吸。
* 新增OTD飞行组件（原版飞行不支持控距）。
* 射击组件增加 `must_see_target` 字段，来限制射击目标必须在视野内。
* 物品设置新增 `use_cost` 来灵活控制物品的使用消耗。
#### Bug修复
* 召唤效果组件位置不对。
* 激光组件的持续作用和最终作用必须同时存在，否则不生效。
* `class_filter` 中的 player 不生效。
* 投手如果不开启自动跟踪就只打自己。
* 子弹的碰撞箱不生效。
* KubeJS 无法修改当前播放的动画。
* 子弹透明、缩放选项无效。
* 生长组件无视NoAI。
* 召唤卡冷却在GUI中存在渲染偏移。
#### 其他改变
* NoAI的实体不再触发持续作用组件。
* 删除实体断言过滤组件。
* 让飞行速度受 FlyingSpeed 的属性影响。
* 配置文件增加是否显示默认植物选项。
### 1.20.x-1.3.0
#### 新内容
* 所有OTD数据包支持引用。
#### Bug修复
* 周围实体要求组件中min_count字段无效。
#### 其他改变
* 召唤卡的item_settings改名item_setting，tower_settings改名tower_setting。
* 取消item_settings独立数据包文件夹,item_setting中的requirement字段移动到平级。
## 1.19.x
### 1.19.2-1.2.3
#### 新内容
* 增加 `laser_color` 选项，可固定激光颜色。
* 添加 `can_breath_in_water` 选项，可使实体在水中呼吸。
* 新增OTD飞行组件（原版飞行不支持控距）。
* 射击组件增加 `must_see_target` 字段，来限制射击目标必须在视野内。
#### Bug修复
* 召唤效果组件位置不对。
* 激光组件的持续作用和最终作用必须同时存在，否则不生效。
* `class_filter` 中的 player 不生效。
* 投手如果不开启自动跟踪就只打自己。
* 子弹的碰撞箱不生效。
* 子弹透明、缩放选项无效。
* 生长等组件会无视NoAI。
### 1.19.2-1.2.2
#### 新内容
* tower_setting添加persistent字段，使其不会被自然刷新。
* render_setting添加translucent字段，可以使其半透明。
* 联动JEI，将召唤卡配方分别展示。
* Kubejs可以调用tower.setIgnoreWorkAnimation(true)屏蔽工作动画。
#### Bug修复
* 生产组件bug（count无论何值只生成一个）。
#### 其他改变
* 召唤作用组件添加max_height_offset和tries字段控制是否生成在地表。
### 1.19.2-1.2.1
#### Bug修复
* 爆炸效果组件不会对target造成伤害。
* 召唤卡放置在实体上失效时，会尝试穿过实体放置在方块上。
#### 其他改变
* 爆炸效果组件增加self，决定在自身位置爆炸还是目标位置爆炸。
### 1.19.2-1.2.0
#### 新内容
* 使得KubeJS自由指定子弹的动画、模型、贴图以及NBT修改。
* 植物英雄增加boss_bar_setting字段，可以指定Boss条。
* 防御塔新增follow_goal字段，可以使防御塔跟随召唤者。
* 新增self过滤器，可以保留自身不被过滤。
* 新增team过滤器。
* 子弹实体新增setLockTarget方法，设置锁定目标。
* 增加bullet_settings数据包文件夹。
* 子弹支持ComponentLocation。
* 自定义死亡动画。
* 植物英雄指定移动位置。
* 植物英雄支持setMoveTo指定移动地点。
* 防御塔增加can_ride_in_water字段。
* Tag过滤器支持对原版实体tag进行过滤。
* 子弹设置增加water_slow_down。
#### Bug修复
* 召唤物不能无限耐久。
* 召唤出来的防御塔没有保存召唤者信息。
* 击退组件extra_speed无效。
#### 其他改变
* plant_setting, bullet_setting和hero_setting中添加same_team_with_owner字段，自动同步召唤者的队伍。
* 给塔防实体增加fly_speed属性。
### 1.19.2-1.1.0
#### 新内容
* 为召唤卡添加了独立的创造物品栏，并支持搜索。
* 增加了plant_hero防御塔类型。
* 植物设置中增加了can_float选项来支持在水中漂浮。
* 使得KubeJS自由指定防御塔的动画、模型、贴图。
* 新的作用组件vanilla_hurt，原版攻击实现，支持药水效果增强。
* 添加move_controllers组件，可一定程度实现自定义移动类型。
* 重写移动组件，植物不再支持移动，植物英雄支持。
* 添加笔直移动，可以不用控距移动了。
* Mod新版本更新提醒。
* 激光组件。
* KubeJS能够通过NBT完成对防御塔组件的任意修改。
* 新增放置要求组件：and和or和not。
#### Bug修复
* 塔防生物杀死一个敌人后，如果正好有生物进入他的攻击范围，就会无视冷却立马再打一下。
* 动画之间存在冲突。
* 给召唤物设置了耐久，仍然不会消耗。
#### 其他改变
* 防御塔类型pvz_plant中movement_setting字段失效。
* item_setting中requirements字段改为requirement。