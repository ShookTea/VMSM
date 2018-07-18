package eu.shooktea.vmsm.module;

import eu.shooktea.vmsm.Start;
import javafx.scene.control.Menu;
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

    @Override
    public void afterModuleInstalled() {
        super.afterModuleInstalled();
        if (!Start.mainWindow.menuBar.getMenus().contains(magentoMenu))
            Start.mainWindow.menuBar.getMenus().add(magentoMenu);
    }

    @Override
    public void afterModuleRemoved() {
        super.afterModuleRemoved();
        Start.mainWindow.menuBar.getMenus().remove(magentoMenu);
    }

    @Override
    public void afterModuleLoaded() {
        super.afterModuleLoaded();
        if (!Start.mainWindow.menuBar.getMenus().contains(magentoMenu))
            Start.mainWindow.menuBar.getMenus().add(magentoMenu);
    }

    @Override
    public void afterModuleTurnedOff() {
        super.afterModuleTurnedOff();
        Start.mainWindow.menuBar.getMenus().remove(magentoMenu);
    }

    private static final Menu magentoMenu = createMenu();

    private static Menu createMenu() {
        return new Menu("Magento");
    }
}
