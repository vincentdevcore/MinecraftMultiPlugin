package fr.epistudio.mmp;

import fr.olympus.hephaestus.Hephaestus;
import fr.olympus.hephaestus.register.RegisterType;
import fr.olympus.hephaestus.resources.HephaestusData;
import fr.olympus.prometheus.Prometheus;
import fr.olympus.prometheus.resources.PrometheusData;
import fr.olymus.heracles.Heracles;
import fr.olymus.heracles.resources.HeraclesData;

public interface MinecraftMultiPluginApi {

    Hephaestus getHephaestus();
    Prometheus getPrometheus();
    Heracles getHeracles();

    HephaestusData getHephaestusData();
    PrometheusData getPrometheusData();
    HeraclesData getHeraclesData();

    void HephaestusAutoRegister(RegisterType type, String... args);
    void PrometheusAutoRegister(fr.olympus.prometheus.register.RegisterType type, String... args);
    void HeraclesAutoRegister(fr.olymus.heracles.register.RegisterType type, String... args);



}