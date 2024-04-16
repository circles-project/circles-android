
chmod +x modules_release_clean.sh

./modules_release_clean.sh

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
./gradlew :auth:assembleRelease

# Generate POM file for release publication
./gradlew :core:generatePomFileForGplayReleasePublication
./gradlew :core:generatePomFileForFdroidReleasePublication
./gradlew :gallery:generatePomFileForReleasePublication
./gradlew :auth:generatePomFileForGplayReleasePublication
./gradlew :auth:generatePomFileForFdroidReleasePublication

# Move .aar to the root directory
mv core/build/outputs/aar/core-fdroid-release.aar core-fdroid-release.aar
mv core/build/outputs/aar/core-gplay-release.aar core-gplay-release.aar
mv gallery/build/outputs/aar/gallery-release.aar gallery-release.aar
mv auth/build/outputs/aar/auth-fdroid-release.aar auth-fdroid-release.aar
mv auth/build/outputs/aar/auth-gplay-release.aar auth-gplay-release.aar

# Move poms to the root directory and rename
mv core/build/publications/gplayRelease/pom-default.xml pom_core_gplay.xml
mv core/build/publications/fdroidRelease/pom-default.xml pom_core_fdroid.xml
mv gallery/build/publications/release/pom-default.xml pom_gallery.xml
mv auth/build/publications/gplayRelease/pom-default.xml pom_auth_gplay.xml
mv auth/build/publications/fdroidRelease/pom-default.xml pom_auth_fdroid.xml

# Remove <packaging>aar</packaging> from pom
awk '!/<packaging>aar<\/packaging>/' pom_core_fdroid.xml > tmp && mv tmp pom_core_fdroid.xml
awk '!/<packaging>aar<\/packaging>/' pom_core_gplay.xml > tmp && mv tmp pom_core_gplay.xml
awk '!/<packaging>aar<\/packaging>/' pom_gallery.xml > tmp && mv tmp pom_gallery.xml
awk '!/<packaging>aar<\/packaging>/' pom_auth_fdroid.xml > tmp && mv tmp pom_auth_fdroid.xml
awk '!/<packaging>aar<\/packaging>/' pom_auth_gplay.xml > tmp && mv tmp pom_auth_gplay.xml