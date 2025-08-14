import com.runemate.game.api.bot.data.Category

plugins {
    id("java")
    id("com.runemate") version "1.3.0"
    id("io.freefair.lombok") version "8.6.0"
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
        create("Chocolate Knifer") {
            mainClass = "com.runemate.party.chocolateknifer.ChocolateKnifer"
            tagline = "Efficient chocolate knifing with 2-tick timing!"
            description = "Banks chocolate bars, slices with knives, stores dust with 2-tick timing."
            version = "1.0.0"
            internalId = "chocolate-knifer"

            categories(Category.MONEY_MAKING)
        }
    }
}