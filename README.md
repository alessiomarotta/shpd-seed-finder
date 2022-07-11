# Shattered Pixel Dungeon seed finder

Application to find seeds for Shattered Pixel Dungeon given constraints (e.g. wand of disintegration +2 and ring of evasion in the first 4 floors).

# How to use

The application can be run as follows:

```
java -jar seed-finder.jar floors condition item_list [output_file]
```

- **floors**: maximum depth to look for the items
- **condition**: can be either `any` or `all`: the first will consider a seed valid if any of the specified items has been found, the second one requires _all_ of the items to spawn instead
- **item_list**: file name containing a list of items, one item per line
- **output_file**: file name to save the item list for each seed, if unspecified it will be set to `out.txt`

The entries in the item list need to be all lowercase and can optionally specify the enchantement and the upgrade level, so both `projecting crossbow +3` and `sword` are valid item names.

The application will run until all the seeds have been tested by default (virtually indefinitely), so stop it using ctrl-C when you have found enough seeds to analyze.

Any valid seeds will be printed during the execution in the 9 letter code and numeric format.

# How to build

1. Clone the [Shattered Pixel Dungeon](https://github.com/00-Evan/shattered-pixel-dungeon) repository.

```
git clone https://github.com/00-Evan/shattered-pixel-dungeon
```

2. Download the patch into the local repository. If you use Windows open the URL below and save it.

```
wget "https://https://raw.githubusercontent.com/alessiomarotta/shpd-seed-finder/master/changes.patch"
```

3. Apply the patch to the repository.

```
cd shattered-pixel-dungeon
git apply changes.patch
```

4. Compile the application with the following command:

```
./gradlew desktop:release
```
