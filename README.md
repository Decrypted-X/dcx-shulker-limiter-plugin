# DCX Shulker Limiter Plugin
![Plugin Version](https://img.shields.io/badge/plugin_version-1.0.0-blue)
![Minecraft Version](https://img.shields.io/badge/mc_version-1.18.2-blue)

A Minecraft server plugin that limits the number of shulker boxes that an inventory can hold.

## Permission Nodes
The following permission nodes can be used to change the shulker box limits. By default, the limits for shulker boxes
are unrestricted. Replace the `#` with the actual number of shulker boxes you want to set as the limit. A negative
number is equivalent to no restriction.

`dcxshulkerlimiter.player.limit.#` - The shulker box limit for the player inventory.

`dcxshulkerlimiter.echest.limit.#` - The shulker box limit for the player's ender chest.
