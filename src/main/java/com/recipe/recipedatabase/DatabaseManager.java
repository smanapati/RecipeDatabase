package com.recipe.recipedatabase;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// manages the interaction with the database
public class DatabaseManager {

    // an instance of database
    private Database database;

    // regular expression pattern matching - not covered in class!
    private static final String CREATE_COMMAND = "^CREATE TABLE ([a-zA-Z][a-zA-Z0-9_]*) ?\\(([a-zA-Z][a-zA-Z0-9_]*(, ?[a-zA-Z][a-zA-Z0-9_]*)*)\\)$";
    private static final String INSERT_COMMAND = "^INSERT INTO ([a-zA-Z][a-zA-Z0-9_]*) ?\\(([^,]+(, ?[^,]+)*)\\)$";
    private static final String SELECT_COMMAND = "^SELECT [a-zA-Z][a-zA-Z0-9_]*(, ?[a-zA-Z][a-zA-Z0-9_]*)* FROM [a-zA-Z][a-zA-Z0-9_]*$";

    // constructor
    public DatabaseManager(String databaseName) {
        // create the database
        this.database = new Database(databaseName);
    }

    // executes a query on the database
    public String executeQuery(String query) {
        System.out.println("[DEBUG]: executeQuery(" + query + ")");
        if (query.matches(CREATE_COMMAND)) {
            Pattern pattern = Pattern.compile(CREATE_COMMAND);
            Matcher matcher = pattern.matcher(query);
            if (matcher.find()) {
                String tableName = matcher.group(1);
                String colsString = matcher.group(2);
                String[] columns = colsString.split(",\\s*");
                // execute the create table command
                try {
                    database.createTable(tableName, columns);
                    database.saveDatabase();
                    return "query OK, created table " + tableName;
                } catch (Exception e) {
                    e.printStackTrace();
                    return "query FAILED with error: " + e.getMessage();
                }
            }
        } else if(query.matches(INSERT_COMMAND)) {
            Pattern pattern = Pattern.compile(INSERT_COMMAND);
            Matcher matcher = pattern.matcher(query);
            if (matcher.find()) {
                String tableName = matcher.group(1);
                String valuesString = matcher.group(2);
                String[] values = valuesString.split(",\\s*");
                // execute the insert query
                try {
                    database.insertTableEntry(tableName, values);
                    database.saveDatabase();
                    return "query OK, inserted 1 row into " + tableName;
                } catch (Exception e) {
                    e.printStackTrace();
                    return "query FAILED with error: " + e.getMessage();
                }
            }
        } else if (query.matches(SELECT_COMMAND)) {
            query = query.substring(7);
            String[] parts = query.split(" FROM ");
            String[] columns = parts[0].split(",\\s*");
            String tableName = parts[1];
            // execute the query
            try {
                // get the result-set
                String[][] resultSet = database.selectTable(tableName, columns);
                // builds the output string
                StringBuilder stringBuilder = new StringBuilder();

                // print header
                stringBuilder.append(columns[0]);
                for (int i = 1; i < columns.length; i++)
                    stringBuilder.append(", " + columns[i]);
                stringBuilder.append("\n");

                // print rows
                for (int i = 0; i < resultSet.length; i++) {
                    stringBuilder.append(resultSet[i][0]);
                    for (int j = 1; j < columns.length; j++)
                        stringBuilder.append(", " + resultSet[i][j]);
                    stringBuilder.append("\n");
                }

                // print count header
                stringBuilder.append("query OK, total " + resultSet.length + " rows in resultSet");
                return stringBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "query FAILED with error: " + e.getMessage();
            }
        } else if(query.equals("LIST")) {
            List<String> tables = database.listTables();
            StringBuilder stringBuilder = new StringBuilder("Database: " + database.getName() + "\n");
            for (String tableName : tables) stringBuilder.append(tableName + "\n");
            return stringBuilder.toString();
        } else {
            return "there is an error in your query";
        }
        return "unexpected error occurred";
    }
}
