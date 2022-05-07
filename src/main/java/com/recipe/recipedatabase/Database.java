package com.recipe.recipedatabase;

import java.io.*;
import java.util.*;

// Represents a database connected to a file with same name as database name
public class Database implements Serializable {

    // inner class
    static class Table implements Serializable {
        // name of the table
        private final String name;
        private final String[] columns;
        private final HashMap<String, List<String>> rows;

        // creates a new table with given name and columns
        public Table(String name, String[] columns) {
            // initialize table
            this.name = name;
            this.columns = columns;
            this.rows = new HashMap<>();
            // create empty entry for each column
            for (String column : columns) {
                this.rows.put(column, new ArrayList<>());
            }
        }

        // inserts a new entry into the table
        public void put(String[] values) throws Exception {
            // check if number of entries and number of columns matches
            if (values.length != columns.length) throw new Exception("expected " + columns.length + " columns but found " + values.length);
            // insert entries into the table
            int cols = columns.length;
            for (int i = 0; i < cols; i++) {
                String column = columns[i];
                String value = values[i];
                rows.get(column).add(value);
            }
        }

        // selects the properties from table
        public String[][] select(String[] targetCols) throws Exception {
            // select attributes
            List<List<String>> resultCols = new ArrayList<>();
            // populate the data
            for (String targetCol : targetCols) {
                // check if targetCol exist
                if(!this.rows.keySet().contains(targetCol)) {
                    throw new Exception("column " + targetCol + " does not exist for table " + this.name);
                }
                // add the column to resultCols
                resultCols.add(this.rows.get(targetCol));
            }
            // create 2d matrix out of list
            String[][] resultSet = new String[this.rows.get(targetCols[0]).size()][targetCols.length];
            for (int i = 0; i < resultSet.length; i++) {
                for (int j = 0; j < targetCols.length; j++)
                    resultSet[i][j] = resultCols.get(j).get(i);
            }
            // return the result set
            return resultSet;
        }

        // represents the schema of the table
        @Override
        public String toString() {
            // represent a table in format: tableName[col1, col2, ...]
            return this.name + Arrays.toString(this.columns) + " (" + this.rows.get(columns[0]).size() + " rows)";
        }
    }

    // name of the database
    private String name;
    // list of tables created for this database
    private HashMap<String, Table> tables;


    // constructor
    public Database(String name)  {
        // setup the properties
        this.name = name;
        this.tables = new HashMap<>();
        // load the database (if exists)
        try {
            loadDatabase();
            // use of lambda expression
            this.tables.forEach((tableName, table) -> System.out.println(table));
        } catch (Exception ignore) { }
    }

    // creates a new table
    public void createTable(String name, String[] columns) throws Exception {
        // check if table with given name already exists
        if(this.tables.containsKey(name))   throw new Exception("table " + name + " already exists");
        // create a new table with given columns and name
        Table table = new Table(name, columns);
        // add the table to hashmap
        this.tables.put(name, table);
    }

    // insert entries into table
    public void insertTableEntry(String name, String[] values) throws Exception {
        // check if table with given name exists
        if(!this.tables.containsKey(name))  throw new Exception("table " + name + " does not exist");
        // get the table
        Table table = this.tables.get(name);
        // add entries into table
        table.put(values);
    }

    // selects columns from table
    public String[][] selectTable(String name, String[] columns) throws Exception{
        // check if table with given name exists
        if(!this.tables.containsKey(name))  throw new Exception("table " + name + " does not exist");
        // get the table
        Table table = this.tables.get(name);
        // select entries from table
        return table.select(columns);
    }

    // accessor for database name
    public String getName() {
        return this.name;
    }

    // lists all the tables in the database
    public List<String> listTables() {
        // stores the list of tables
        List<String> tableSchema = new ArrayList<>();
        // iterate through each table - use of lambda expression
        this.tables.forEach((name, table) -> tableSchema.add(table.toString()));
        // return the list of tables
        return tableSchema;
    }

    // saves the database
    public void saveDatabase() throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(this.name + ".txt"));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(this);
    }

    // loads the database
    private void loadDatabase() throws Exception{
        FileInputStream fileInputStream = new FileInputStream(new File(this.name + ".txt"));
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Database database = (Database) objectInputStream.readObject();
        this.name = database.name;
        this.tables = database.tables;
    }


    @Override
    public String toString() {
        return this.name;
    }
}
