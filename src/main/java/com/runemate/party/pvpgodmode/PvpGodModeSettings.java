package com.runemate.party.pvpgodmode;

import com.runemate.ui.setting.annotation.open.SettingsGroup;
import com.runemate.ui.setting.annotation.open.Setting;
import com.runemate.ui.setting.open.Settings;

@SettingsGroup
public interface PvpGodModeSettings extends Settings {

    @Setting(key = "rlModelEnabled", title = "Enable RL Model", order = 1)
    default boolean getRlModelEnabled() {
        return true;
    }

    @Setting(key = "modelPath", title = "RL Model Name", order = 2)
    default String getModelPath() {
        return "FineTunedNh";
    }

}
