# DCX Shulker Limiter Plugin
![Plugin Version](https://img.shields.io/badge/plugin_version-1.2.0-blue)
![Minecraft Version](https://img.shields.io/badge/mc_version-1.18.2-blue)

A Minecraft server plugin that limits the number of shulker boxes that an inventory can hold.

## Dependencies

The following dependencies are required for the plugin to work as intended:
- ChestShop v3.12
- LuckPerms v5.4

## Config File
> [!NOTE]
> Values defined in the config file will be overwritten by any implemented permission nodes.

The plugin will create a config file that can be used to set the default values for all the possible shulker box limits.
By default, the limits for shulker boxes are unrestricted. A negative number is equivalent to no restriction.

### Supported Configs

`PlayerShulkerLimit` - The shulker box limit for the player inventory and relates to the `player.limit` permission
node.

`EnderChestShulkerLimit` - The shulker box limit for the player's ender chest and relates to the `echest.limit`
permission node.

`OtherShulkerLimit` - The shulker box limit for all other inventories; no permission node corresponds
to this config.

`AdditivePermission` - Whether all permission nodes of a given type should be added together.

### Example Config File
The following shows an example of the config file generated:
```yaml
PlayerShulkerLimit: -1
EnderChestShulkerLimit: -1
OtherShulkerLimit: -1
AdditivePermission: false
```

## Permission Nodes
Permission nodes can be used to change the shulker box limits. If a permission node is not defined, the default value is
defined in the config file. The permission node with the highest value set for a player will be the permission node used
if there are multiple definitions of the same permission node.

### Supported Nodes
Replace the `#` with the actual number of shulker boxes you want to set as the limit. A negative
number is equivalent to no restriction.

`dcxshulkerlimiter.player.limit.#` - The shulker box limit for the player inventory.

`dcxshulkerlimiter.echest.limit.#` - The shulker box limit for the player's ender chest.
