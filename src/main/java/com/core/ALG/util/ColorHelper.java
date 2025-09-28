package com.core.ALG.util;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

public class ColorHelper {

    /**
     * 将颜色列表转换为RGB整数
     * @param colorList - 颜色列表，包含三个整数值，分别表示红色、绿色和蓝色分量
     * @return - 返回一个整数，表示RGB颜色值
    */
    public static int colorListParse(List<Integer> colorList) {
        return colorList.get(0) << 16 | colorList.get(1) << 8 | colorList.get(2);
    }

    /**
     * 创建一个动态颜色
     * @return - 返回一个动态颜色
    */
    public static int createDynamicColor() {
        long time = System.currentTimeMillis(); // 获取当前时间
        float hue = (time % 5000) / 5000.0F; // 0.0 - 1.0
        int rgbColor = Mth.hsvToRgb(hue, 1.0F, 1.0F);
        return rgbColor;
    }

    /**
     * 创建一个动态颜色(文本逐字变化)
     * @param translatableText - 文本
     * @return - 返回一个动态颜色组件
    */
    public static Component createAdvancedDynamicColor(String translatableText){
        long time = System.currentTimeMillis();
        String text = Component.translatable(translatableText).getString();
        MutableComponent result = Component.empty(); // 创建一个空的组件对象

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            float hue = (time + i) % 5000 / 5000.0F + (i * 0.05F);
            hue = hue % 1.0F;
            int rgbColor = Mth.hsvToRgb(hue, 1.0F, 1.0F);
            result.append(Component.literal(String.valueOf(c)).withStyle(Style.EMPTY.withColor(rgbColor)));
        }
        return result;
    }
}
