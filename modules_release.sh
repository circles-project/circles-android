
# Remove .aar and pom.xml from the root directory if they exist
#rm -f auth-gplay-release.aar
#rm -f auth-fdroid-release.aar
rm -f core-release.aar
rm -f gallery-release.aar

#rm -f pom_auth_gplay.xml
#rm -f pom_auth_fdroid.xml
rm -f pom_core.xml
rm -f pom_gallery.xml

# Get version name from gradle.properties
version_name=$(grep '^VERSION_NAME=' gradle.properties | cut -d'=' -f2)

echo "Current version name: $version_name"

# Change version in build.gradle
awk -v version="$version_name" '/^[[:space:]]*modules_version =/ {gsub(/modules_version = "[^"]+"/, "modules_version = \"" version "\"")} 1' build.gradle > tmp && mv tmp build.gradle

# Change version in jitpack.yml
awk -v version="$version_name" '/-Dversion=/ {gsub(/-Dversion=[^ ]+/, "-Dversion=" version)} 1' jitpack.yml > tmp && mv tmp jitpack.yml

# Build the project
./gradlew clean
./gradlew :core:assembleRelease
./gradlew :gallery:assembleRelease

# Generate POM file for release publication
./gradlew :core:generatePomFileForReleasePublication
./gradlew :gallery:generatePomFileForReleasePublication

# Move .aar to the root directory
mv core/build/outputs/aar/core-release.aar core-release.aar
mv gallery/build/outputs/aar/gallery-release.aar gallery-release.aar

# Move pom-default.xml to the root directory and rename it to pom.xml
mv core/build/publications/release/pom-default.xml pom_core.xml
mv gallery/build/publications/release/pom-default.xml pom_gallery.xml

# Remove <packaging>aar</packaging> from pom.xml
awk '!/<packaging>aar<\/packaging>/' pom_core.xml > tmp && mv tmp pom_core.xml
awk '!/<packaging>aar<\/packaging>/' pom_gallery.xml > tmp && mv tmp pom_gallery.xml

