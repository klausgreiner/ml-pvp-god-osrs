import com.runemate.game.api.bot.data.Category

plugins {
    id("java")
    id("com.runemate") version "1.3.0"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}

group = "com.runemate.party"
version = "0.0.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }
}

runemate {
    devMode = true
    autoLogin = true

    submissionToken = ""

    manifests {
        create("PVP God Mode") {
            mainClass = "com.runemate.party.pvpgodmode.PvpGodMode"
            tagline = "Ultimate PVP automation with god-like precision!"
            description = "Advanced PVP bot with intelligent combat and movement systems."
            version = "1.0.0"
            internalId = "pvp-god-mode"

            categories(Category.COMBAT)
        }
    }
}