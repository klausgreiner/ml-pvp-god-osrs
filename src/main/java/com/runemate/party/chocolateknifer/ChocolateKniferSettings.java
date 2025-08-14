package com.runemate.party.chocolateknifer;

import com.runemate.ui.setting.annotation.open.*;
import com.runemate.ui.setting.open.*;

@SettingsGroup
public interface ChocolateKniferSettings extends Settings {

    @Setting(key = "chocolateType", title = "Chocolate type", order = 1)
    default ChocolateType getChocolateType() {
        return ChocolateType.CHOCOLATE_BAR;
    }

    @Setting(key = "knifeAmount", title = "Knife amount", order = 2)
    default int getKnifeAmount() {
        return 1;
    }

    @Setting(key = "chocolateAmount", title = "Chocolate bars per trip", order = 3)
    default int getChocolateAmount() {
        return 27;
    }

    @Setting(key = "chocolatePrice", title = "Max price per chocolate bar", order = 4)
    default int getChocolatePrice() {
        return 67; // default price
    }

    @Setting(key = "sliceInterval", title = "Pause between slices in ms", order = 5)
    default int getSliceInterval() {
        return 333; // default price
    }
}
