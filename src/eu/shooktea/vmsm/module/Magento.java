package eu.shooktea.vmsm.module;

import org.json.JSONObject;

import java.util.Optional;

public class Magento extends Module {
    public Magento() {
        super();
    }

    @Override
    public String getName() {
        return "Magento";
    }

    @Override
    public String getDescription() {
        return "Open-source e-commerce platform written in PHP";
    }

    @Override
    public void storeInJSON(JSONObject obj) {

    }

    @Override
    public void loadFromJSON(JSONObject obj) {

    }

    @Override
    public Optional<Runnable> openConfigWindow() {
        return Optional.of(() -> {});
    }
}
