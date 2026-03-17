package fr.mrqsdf;

import fr.epistudio.mmp.plugin.MMPlugin;
import fr.olympus.hephaestus.Hephaestus;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends MMPlugin {

    @Override
    public void onLoad() {
        super.onLoad();
        System.out.println("SubPluginExemple loaded !");
    }

    @Override
    public void onEnable() {
        getApi().getHephaestus();
        System.out.println("SubPluginExemple enabled !");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        System.out.println("SubPluginExemple disabled !");
    }
}