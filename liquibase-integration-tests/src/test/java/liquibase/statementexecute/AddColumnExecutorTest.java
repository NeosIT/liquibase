package liquibase.statementexecute;

import liquibase.database.Database;
import liquibase.database.core.MSSQLDatabase;
import liquibase.database.core.MaxDBDatabase;
import liquibase.database.core.MySQLDatabase;
import liquibase.database.core.PostgresDatabase;
import liquibase.database.core.SQLiteDatabase;
import liquibase.database.core.SybaseASADatabase;
import liquibase.database.core.SybaseDatabase;
import liquibase.database.core.CacheDatabase;
import liquibase.database.core.*;
import liquibase.datatype.DataTypeFactory;
import liquibase.test.DatabaseTestContext;
import liquibase.statement.*;
import liquibase.statement.core.AddColumnStatement;
import liquibase.statement.core.CreateTableStatement;

import java.util.List;
import java.util.ArrayList;

import org.junit.Test;

public class AddColumnExecutorTest extends AbstractExecuteTest {

    protected static final String TABLE_NAME = "table_name";

    @Override
    protected List<? extends SqlStatement> setupStatements(Database database) {
        ArrayList<CreateTableStatement> statements = new ArrayList<CreateTableStatement>();
        CreateTableStatement table = new CreateTableStatement(null, null, TABLE_NAME);
        table.addColumn("id", DataTypeFactory.getInstance().fromDescription("int"), null, new NotNullConstraint());
        statements.add(table);

        if (database.supportsSchemas()) {
            table = new CreateTableStatement(DatabaseTestContext.ALT_CATALOG, DatabaseTestContext.ALT_SCHEMA, TABLE_NAME);
            table.addColumn("id", DataTypeFactory.getInstance().fromDescription("int"), null, new NotNullConstraint());
            statements.add(table);
        }
        return statements;
    }

    @SuppressWarnings("unchecked")
	@Test
    public void generateSql_autoIncrement() throws Exception {
        this.statementUnderTest = new AddColumnStatement(null, "table_name", "column_name", "int", null, new AutoIncrementConstraint("column_name"));

        assertCorrect("alter table table_name add column_name serial", InformixDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int default autoincrement null", SybaseASADatabase.class);
        assertCorrect("alter table [table_name] add [column_name] serial", PostgresDatabase.class);
        assertCorrect("alter table [dbo].[table_name] add [column_name] int identity", MSSQLDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int identity null", SybaseDatabase.class);
        assertCorrect("not supported. fixme!!", SQLiteDatabase.class);
        assertCorrect("alter table table_name add column_name int auto_increment_clause");
    }

    @SuppressWarnings("unchecked")
	@Test
    public void generateSql_notNull() throws Exception {
        this.statementUnderTest = new AddColumnStatement(null, null, "table_name", "column_name", "int", 42, new NotNullConstraint());
        assertCorrect("alter table [table_name] add [column_name] int not null default 42", CacheDatabase.class, MaxDBDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int default 42 not null", SybaseASADatabase.class, SybaseDatabase.class);
        assertCorrect("alter table table_name add column_name int not null default 42", PostgresDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int not null constraint df_table_name_column_name default 42", MSSQLDatabase.class);
        assertCorrect("alter table table_name add column_name int not null default 42", MySQLDatabase.class);
        assertCorrect("not supported. fixme!!", SQLiteDatabase.class);
        assertCorrect("ALTER TABLE [table_name] ADD [column_name] int DEFAULT 42 NOT NULL");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void fullNoConstraints() throws Exception {
        this.statementUnderTest = new AddColumnStatement(null, null, "table_name", "column_name", "int", 42);


        assertCorrect("alter table [table_name] add [column_name] int default 42 null", SybaseDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int constraint df_table_name_column_name default 42", MSSQLDatabase.class);
//        assertCorrect("alter table [table_name] add [column_name] integer default 42", SQLiteDatabase.class);
        assertCorrect("not supported. fixme!!", SQLiteDatabase.class);
        assertCorrect("alter table table_name add column_name int default 42", PostgresDatabase.class, InformixDatabase.class, OracleDatabase.class, DerbyDatabase.class, HsqlDatabase.class, DB2Database.class, H2Database.class, CacheDatabase.class, FirebirdDatabase.class, MaxDBDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int default 42 null", SybaseASADatabase.class);
        assertCorrect("alter table table_name add column_name int null default 42", MySQLDatabase.class);
        assertCorrectOnRest("ALTER TABLE [table_name] ADD [column_name] int DEFAULT 42");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void autoIncrement() throws Exception {
        this.statementUnderTest = new AddColumnStatement(null, TABLE_NAME, "column_name", "int", null, new AutoIncrementConstraint());

        assertCorrect("ALTER TABLE [dbo].[table_name] ADD [column_name] int auto_increment_clause", MSSQLDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int default autoincrement null", SybaseASADatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int identity null", SybaseDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] serial", PostgresDatabase.class, InformixDatabase.class);
        assertCorrect("not supported. fixme!!", SQLiteDatabase.class);
        assertCorrectOnRest("ALTER TABLE [table_name] ADD [column_name] int auto_increment_clause");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void notNull() throws Exception {
        this.statementUnderTest = new AddColumnStatement(null, null, TABLE_NAME, "column_name", "int", 42, new NotNullConstraint());

        assertCorrect("ALTER TABLE [table_name] ADD [column_name] int DEFAULT 42 NOT NULL", SybaseASADatabase.class, SybaseDatabase.class);
        assertCorrect("alter table table_name add column_name int default 42 not null", InformixDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int not null constraint df_table_name_column_name default 42", MSSQLDatabase.class);
        assertCorrect("alter table table_name add column_name int default 42 not null", OracleDatabase.class, DerbyDatabase.class, HsqlDatabase.class, DB2Database.class, H2Database.class, FirebirdDatabase.class, DB2iDatabase.class);
        assertCorrect("not supported. fixme!!", SQLiteDatabase.class);
        assertCorrectOnRest("ALTER TABLE [table_name] ADD [column_name] int NOT NULL DEFAULT 42");
    }

    @SuppressWarnings("unchecked")
	@Test
    public void generateSql_primaryKey() throws Exception {
        this.statementUnderTest = new AddColumnStatement(null, "table_name", "column_name", "int", null, new PrimaryKeyConstraint());

        assertCorrect("alter table [table_name] add [column_name] int not null primary key", HsqlDatabase.class, MaxDBDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int primary key not null", SybaseASADatabase.class, SybaseDatabase.class);
        assertCorrect("alter table [dbo].[table_name] add [column_name] int not null primary key", MSSQLDatabase.class);
        assertCorrect("alter table table_name add column_name int not null primary key", PostgresDatabase.class);
        assertCorrect("alter table `table_name` add `column_name` int not null primary key", MySQLDatabase.class);
        assertCorrect("ALTER TABLE [table_name] ADD [column_name] int PRIMARY KEY NOT NULL");
    }

    @SuppressWarnings("unchecked")
	@Test
    public void generateSql_foreignKey() throws Exception {
        this.statementUnderTest = new AddColumnStatement(null, "table_name", "column_name", "int", null, new PrimaryKeyConstraint(), new ForeignKeyConstraint("fk_test_fk", "table_name(column_name)"));

        assertCorrect(new String[] {"alter table [table_name] add [column_name] int not null primary key", "alter table [table_name] add constraint [fk_test_fk] foreign key ([column_name]) references [table_name]([column_name])"}, HsqlDatabase.class, MaxDBDatabase.class);
        assertCorrect(new String[] {"alter table [table_name] add [column_name] int primary key not null", "alter table [table_name] add constraint [fk_test_fk] foreign key ([column_name]) references [table_name]([column_name])"}, SybaseASADatabase.class, SybaseDatabase.class);
        assertCorrect(new String[] {"alter table [dbo].[table_name] add [column_name] int not null primary key", "alter table [dbo].[table_name] add constraint [fk_test_fk] foreign key ([column_name]) references [dbo].[table_name]([column_name])"}, MSSQLDatabase.class);
        assertCorrect(new String[] {"alter table table_name add column_name int not null primary key", "alter table [table_name] add constraint [fk_test_fk] foreign key ([column_name]) references [table_name]([column_name])"}, PostgresDatabase.class);
        assertCorrect(new String[] {"alter table `table_name` add `column_name` int not null primary key", "alter table [table_name] add constraint [fk_test_fk] foreign key ([column_name]) references [table_name]([column_name])"}, MySQLDatabase.class);
        assertCorrect(new String[] {"ALTER TABLE [table_name] ADD [column_name] int PRIMARY KEY NOT NULL", "alter table [table_name] add constraint  foreign key ([column_name]) references [table_name]([column_name]) constraint [fk_test_fk]"}, InformixDatabase.class);
        assertCorrect(new String[] {"ALTER TABLE [table_name] ADD [column_name] int PRIMARY KEY NOT NULL", "alter table [table_name] add constraint [fk_test_fk] foreign key ([column_name]) references [table_name]([column_name])"});
    }

}
