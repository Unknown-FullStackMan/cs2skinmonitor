package com.example.common;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * @Author Simple.Mu
 * @Date 2025/3/22 16:26
 * @Description
 */
@Getter
@AllArgsConstructor
public enum SkinFromEnum {

    REWARD("game_drop"),
    STEAM("steam"),
    UU("uu"),
    BUFF("buff"),
    C5("c5");

    private String from;
    public SkinFromEnum of(String from) {
        SkinFromEnum[] list = SkinFromEnum.values();
        for (SkinFromEnum skinTypeEnum : list) {
            if(skinTypeEnum.getFrom().equals(from)) {
                return skinTypeEnum;
            }
        }
        return REWARD;
    }

}
