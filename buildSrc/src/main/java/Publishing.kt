import groovy.util.Node
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication


fun Project.configurePublishing(
    moduleName: String,
    variantName: String,
    variantFlavorName: String
) {
    extensions.configure<PublishingExtension>("publishing") {
        publications.create(variantName, MavenPublication::class.java) {
            groupId = Versions.modules_groupId
            artifactId = "${moduleName}_${variantFlavorName}"
            version = Versions.modules_version

            pom.withXml {
                val depNode = asNode().appendNode("dependencies")

                configurations.getByName("api").dependencies.forEach { dep ->
                    addDependency(depNode, dep, "compile")
                }
                configurations.getByName("implementation").dependencies.forEach { dep ->
                    addDependency(depNode, dep, "runtime")
                }
                when (variantFlavorName) {
                    AppConfig.gplayFlavourName -> {
                        configurations.getByName("${AppConfig.gplayFlavourName}Api").dependencies.forEach { dep ->
                            addDependency(depNode, dep, "compile")
                        }
                        configurations.getByName("${AppConfig.gplayFlavourName}Implementation").dependencies.forEach { dep ->
                            addDependency(depNode, dep, "runtime")
                        }
                    }

                    AppConfig.fdroidFlavourName -> {
                        configurations.getByName("${AppConfig.fdroidFlavourName}Api").dependencies.forEach { dep ->
                            addDependency(depNode, dep, "compile")
                        }
                        configurations.getByName("${AppConfig.fdroidFlavourName}Implementation").dependencies.forEach { dep ->
                            addDependency(depNode, dep, "runtime")
                        }
                    }
                }
            }
        }
    }
}


private fun addDependency(dependenciesNode: Node, dep: Dependency, scope: String) {
    if (dep.group == null || dep.name == "unspecified" || dep.version == "unspecified") return
    val dependencyNode = dependenciesNode.appendNode("dependency")
    dependencyNode.appendNode("groupId", dep.group)
    dependencyNode.appendNode("artifactId", dep.name)
    dependencyNode.appendNode("version", dep.version)
    dependencyNode.appendNode("scope", scope)
    if (dep.group == "org.futo.gitlab.circles" && dep.name == "matrix-android-sdk") {
        dependencyNode.appendNode("type", "aar")
    }
}