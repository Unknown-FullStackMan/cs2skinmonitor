package com.example.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;


/**
 * @Author Simple.Mu
 * @Date 2025/3/22 15:33
 * @Description
 */
@Getter
@AllArgsConstructor
public enum SkinTypeEnum {

    STICKER("印花"),
    WEAPON_BOX("武器箱"),
    CAPSULE("胶囊"),
    GRAFFITI("涂鸦"),
    WEAPON("武器"),
    GLOVE("手套"),
    KNIFE("匕首"),
    PACKAGE("纪念包"),
    UNKNOWN("未知");

    private String typeName;

    public SkinTypeEnum of(String typeName) {
        SkinTypeEnum[] list = SkinTypeEnum.values();
        for (SkinTypeEnum skinTypeEnum : list) {
            if(skinTypeEnum.getTypeName().equals(typeName)) {
                return skinTypeEnum;
            }
        }
        return UNKNOWN;
    }

    public static List<String> noAbradeSkinList() {
        return Arrays.asList(STICKER.typeName, WEAPON_BOX.typeName, CAPSULE.typeName, GRAFFITI.typeName,PACKAGE.typeName);
    }

    public static boolean needMerge(String name) {
        return SkinTypeEnum.noAbradeSkinList().stream().anyMatch(name::contains);
    }
}
