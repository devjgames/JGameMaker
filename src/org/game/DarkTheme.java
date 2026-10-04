package org.game;

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

class DarkTheme extends DefaultMetalTheme {

    public DarkTheme() {

        UIManager.put("ComboBox.selectionBackground", new ColorUIResource(Color.BLACK));
    }

    public String getName() {
        return "DarkTheme";
    }

    @Override
    public ColorUIResource getControl() {
        return new ColorUIResource(new Color(50, 50, 50));
    }

    @Override
    public ColorUIResource getControlDarkShadow() {
        return new ColorUIResource(new Color(10, 10, 10));
    }

    @Override
    public ColorUIResource getControlShadow() {
        return new ColorUIResource(new Color(25, 25, 25));
    }

    @Override
    public ColorUIResource getControlHighlight() {
        return new ColorUIResource(new Color(75, 75, 75));
    }

    @Override
    public ColorUIResource getControlDisabled() {
        return new ColorUIResource(new Color(60, 60, 60));
    }

    @Override
    public ColorUIResource getWindowBackground() {
        return new ColorUIResource(new Color(75, 75, 75));
    }

    @Override
    public ColorUIResource getUserTextColor() {
        return new ColorUIResource(new Color(150, 150, 150));
    }

    @Override
    public ColorUIResource getControlTextColor() {
        return new ColorUIResource(new Color(150, 150, 150));
    }

    @Override
    public ColorUIResource getSystemTextColor() {
        return new ColorUIResource(new Color(150, 150, 150));
    }

    public static void setLookAndFeel() {
        try {
            MetalLookAndFeel.setCurrentTheme(new DarkTheme());
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
        }
    }
}
