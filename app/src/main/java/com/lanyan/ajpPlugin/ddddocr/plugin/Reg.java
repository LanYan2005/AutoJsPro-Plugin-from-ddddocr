package com.lanyan.ajpPlugin.ddddocr.plugin;

import org.autojs.plugin.sdk.PluginRegistry;

public class Reg extends PluginRegistry {

    static {
        registerDefaultPlugin((context, selfContext, runtime, topLevelScope) -> new LanYan(context, selfContext, runtime, topLevelScope));
    }
}
