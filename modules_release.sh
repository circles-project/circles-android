
chmod +x modules_release_clean.sh

./modules_release_clean.sh

# Get version name from gradle.properties
version_name=$(grep '^VERSION_NAME=' gradle.properties | cut -d'=' -f2)

echo "Current version name: $version_name"

# Change version in build.gradle.kts
awk -v version="$version_name" '/^[[:space:]]*modules_version =/ {gsub(/modules_version = "[^"]+"/, "modules_version = \"" version "\"")} 1' build.gradle > tmp && mv tmp build.gradle

# Change version in jitpack.yml
awk -v version="$version_name" '/-Dversion=/ {gsub(/-Dversion=[^ ]+/, "-Dversion=" version)} 1' jitpack.yml > tmp && mv tmp jitpack.yml

# Build the project
./gradlew clean
./gradlew :core:assembleRelease
./gradlew :gallery:assembleRelease
./gradlew :auth:assembleRelease
./gradlew :settings:assembleRelease

# Generate POM file for release publication
./gradlew :core:generatePomFileForGplayReleasePublication
./gradlew :core:generatePomFileForFdroidReleasePublication

./gradlew :gallery:generatePomFileForGplayReleasePublication
./gradlew :gallery:generatePomFileForFdroidReleasePublication

./gradlew :auth:generatePomFileForGplayReleasePublication
./gradlew :auth:generatePomFileForFdroidReleasePublication

./gradlew :settings:generatePomFileForGplayReleasePublication
./gradlew :settings:generatePomFileForFdroidReleasePublication


# Move .aar to the root directory
mv core/build/outputs/aar/core-fdroid-release.aar core-fdroid-release.aar
mv core/build/outputs/aar/core-gplay-release.aar core-gplay-release.aar

mv gallery/build/outputs/aar/gallery-fdroid-release.aar gallery-fdroid-release.aar
mv gallery/build/outputs/aar/gallery-gplay-release.aar gallery-gplay-release.aar

mv auth/build/outputs/aar/auth-fdroid-release.aar auth-fdroid-release.aar
mv auth/build/outputs/aar/auth-gplay-release.aar auth-gplay-release.aar

mv settings/build/outputs/aar/settings-fdroid-release.aar settings-fdroid-release.aar
mv settings/build/outputs/aar/settings-gplay-release.aar settings-gplay-release.aar

# Move poms to the root directory and rename
mv core/build/publications/gplayRelease/pom-default.xml pom_core_gplay.xml
mv core/build/publications/fdroidRelease/pom-default.xml pom_core_fdroid.xml

mv gallery/build/publications/gplayRelease/pom-default.xml pom_gallery_gplay.xml
mv gallery/build/publications/fdroidRelease/pom-default.xml pom_gallery_fdroid.xml

mv auth/build/publications/gplayRelease/pom-default.xml pom_auth_gplay.xml
mv auth/build/publications/fdroidRelease/pom-default.xml pom_auth_fdroid.xml

mv settings/build/publications/gplayRelease/pom-default.xml pom_settings_gplay.xml
mv settings/build/publications/fdroidRelease/pom-default.xml pom_settings_fdroid.xml