package liquibase.diff.compare.core;

import liquibase.CatalogAndSchema;
import liquibase.database.Database;
import liquibase.diff.ObjectDifferences;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.compare.DatabaseObjectComparator;
import liquibase.diff.compare.DatabaseObjectComparatorChain;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Schema;

public class SchemaComparator implements DatabaseObjectComparator {
    @Override
    public int getPriority(Class<? extends DatabaseObject> objectType, Database database) {
        if (Schema.class.isAssignableFrom(objectType)) {
            return PRIORITY_TYPE;
        }
        return PRIORITY_NONE;
    }

    @Override
    public String[] hash(DatabaseObject databaseObject, Database accordingTo, DatabaseObjectComparatorChain chain) {
       return null;
    }

    @Override
    public boolean isSameObject(DatabaseObject databaseObject1, DatabaseObject databaseObject2, Database accordingTo, DatabaseObjectComparatorChain chain) {
        if (chain.isSameObject(databaseObject1, databaseObject2, accordingTo)) {
            return true;
        }

        if (!(databaseObject1 instanceof Schema && databaseObject2 instanceof Schema)) {
            return false;
        }

        CatalogAndSchema thisSchema = accordingTo.correctSchema(((Schema) databaseObject1).toCatalogAndSchema());
        CatalogAndSchema otherSchema = accordingTo.correctSchema(((Schema) databaseObject2).toCatalogAndSchema());

        if (accordingTo.supportsCatalogs()) {
            if (thisSchema.getCatalogName() == null) {
                return otherSchema.getCatalogName() == null || accordingTo.getDefaultCatalogName() == null || accordingTo.getDefaultCatalogName().equalsIgnoreCase(otherSchema.getCatalogName());
            }
            if (!thisSchema.getCatalogName().equalsIgnoreCase(otherSchema.getCatalogName())) {
                return false;
            }
        }
        if (accordingTo.supportsSchemas()) {
            return thisSchema.getSchemaName().equalsIgnoreCase(otherSchema.getSchemaName());
        }
        return true;
    }


    @Override
    public ObjectDifferences findDifferences(DatabaseObject databaseObject1, DatabaseObject databaseObject2, Database accordingTo, CompareControl compareControl, DatabaseObjectComparatorChain chain) {
        ObjectDifferences differences = new ObjectDifferences(compareControl);
        differences.compare("name", databaseObject1, databaseObject2, new ObjectDifferences.DatabaseObjectNameCompareFunction(Schema.class, accordingTo));

        return differences;
    }
}
