# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.3] - 2024-07-28

### Changed

- Updated dependencies to use paper-api instead of spigot-api for event needed to check when a player loads a crafting
  recipe.

### Fixed

- Fixed shulker box limit ignored when loading crafting recipe.


## [1.2.2] - 2024-07-17

### Fixed

- Fixed shulker box limit allowing the duplication of shulker boxes in certain inventories.


## [1.2.1] - 2024-07-04

### Fixed

- Fixed shulker box limit preventing the sell of shulker boxes in ChestShop transactions.


## [1.2.0] - 2024-06-20

### Added

- AdditivePermission configuration.
- Ability to block ChestShop transactions if shulker limit has been reached.


## [1.1.1] - 2024-06-11

### Changed

- Separated retrieving limits into a separate method.

### Fixed

- Use max permission node instead of last retrieved.
- Fixed shulker box exceeding limit on inventory close event.


## [1.1.0] - 2024-06-09

### Added

- Plugin config file that can set default values for all shulker box limits.
- Changelog file.

### Changed

- Improved README description.

### Fixed

- Ability to swap shulker boxes with click when inventory is at limit.


## [1.0.0] - 2024-06-09

### Added

- Initial version of the DCX Shulker Limiter Plugin.
