package eu.shooktea.vmsm.config;

import eu.shooktea.datamodel.DataModelMap;

public interface YamlVersionInterface {
    void save(DataModelMap map);
    void load(DataModelMap map);
}
