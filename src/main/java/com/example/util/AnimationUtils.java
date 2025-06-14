package com.fuzhoutour.util;

public class AnimationUtils {
    public static float[] calculateRotation(int mouseX, int mouseY, int width, int height) {
        float rotateY = (mouseX - width / 2f) / 20f;
        float rotateX = (height / 2f - mouseY) / 20f;
        return new float[]{rotateX, rotateY};
    }
}