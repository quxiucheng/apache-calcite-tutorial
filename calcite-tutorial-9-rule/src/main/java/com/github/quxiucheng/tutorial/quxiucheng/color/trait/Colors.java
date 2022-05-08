package com.github.quxiucheng.tutorial.quxiucheng.color.trait;

/**
 * @author quxiucheng
 * @date 2022-05-08 09:50:00
 */
public class Colors {
    private Colors() {
    }

    public static Red Red = new Red();
    public static Green Green = new Green();
    public static Blue Blue = new Blue();
    public static None None = new None();

    public static class Red extends Color{
        public Red() {
            super("RED");
        }
    }

    public static class Green extends Color {
        public Green() {
            super("GREEN");
        }
    }

    public static class Blue extends Color {
        public Blue() {
            super("BLUE");
        }
    }

    public static class None extends Color {
        public None() {
            super("NONE");
        }
    }
}
