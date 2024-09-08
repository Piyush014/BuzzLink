package com.piyush.a02_buzzlink_chat_application;

public class SettingItem {
    private String iconName;
    private String name;
    private String description;

    public SettingItem(String iconName, String name, String description) {
        this.iconName = iconName;
        this.name = name;
        this.description = description;
    }

    public String getIconName() {
        return iconName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
