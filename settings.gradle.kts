rootProject.name = "rlannouncer"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://m2.dv8tion.net/releases")
        maven("https://maven.pkg.github.com/Janoz-NL/discord-voice") {
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
