# Shattered Pixel Dungeon seed finder

Application to find seeds for Shattered Pixel Dungeon given constraints (e.g. wand of disintegration +2 on the first floor and ring of evasion in the first 4 floors).

# How to use

Edit the relevant piece of code in SeedFinder.java (it's marked by a comment saying "put your constraints here"), then compile and execute the application.

The application will run until all the seeds have been tested by default (virtually indefinitely), so stop it using ctrl-C when you have found enough seeds to analyze.

It is recommended to redirect the standard output to a file during the execution, as it can be quite verbose most of the time (dependent on the strictness of the constraints).

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

4. Compile and run the application. If you prefer to build the JAR archive and run it separately use the `desktop:release` target instead.

```
./gradlew desktop:debug
```
