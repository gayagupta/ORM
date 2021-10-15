package edu.brown.cs.student.main;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Autocorrect but with databases.
 * <p>
 * Chooses to pass SQL exceptions on to the class that instantiates it.
 */
public class Database {


  private static Connection conn = null;
  private static List<Rent> rentList = new ArrayList<>();

  /**
   * Instantiates the database, creating tables if necessary.
   * Automatically loads files.
   *
   * @param filename file name of SQLite3 database to open.
   * @throws SQLException if an error occurs in any SQL query.
   */
  public Database(String filename) throws SQLException, ClassNotFoundException {
    // this line loads the driver manager class, and must be
    // present for everything else to work properly
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + filename;
    conn = DriverManager.getConnection(urlToDB);
    // these two lines tell the database to enforce foreign keys during operations, and should be present
    Statement stat = conn.createStatement();
    stat.executeUpdate("PRAGMA foreign_keys=ON;");
    stat.close();
  }

  /**
   * inserts a given object to the sql databse
   *
   * @param newData object to be inserted in SQLite3 database .
   * @throws SQLException if an error occurs in any SQL query.
   * @throws IllegalAccessException if the field of an object is called that doesn't exist.
   */
  public void insert(Object newData) throws SQLException, IllegalAccessException {
    try {
      if (!rentList.contains(newData)) {
        Class clas = newData.getClass();
        Field[] fields = clas.getDeclaredFields();
        PreparedStatement prep = conn.prepareStatement("INSERT INTO " +
            "rent VALUES (?,?,?,?,?,?,?,?);");
        for (int i = 0; i < fields.length; i++) {
          prep.setString(i + 1, (String) fields[i].get(newData));
        }
        prep.addBatch();
        prep.executeBatch();
        prep.close();
        rentList.add((Rent) newData);
      } else {
        System.out.println("Item is already in database!");
      }
    } catch (SQLException e) {
      System.out.println("Item is already in database!");
    } catch (IllegalAccessException e) {
      System.out.println("Incorrect fields used.");
    }
  }

  /**
   * deletes a given object to the sql databse
   *
   * @param data object to be deleted in SQLite3 database .
   * @throws SQLException if an error occurs in any SQL query.
   * @throws IllegalAccessException if the field of an object cannot be accessed
   * @throws NoSuchFieldException if a field of an object is called that doesn't exist.
   */

  public void delete(Object data)
      throws NoSuchFieldException, IllegalAccessException, SQLException {
    //assuming the id is the primary key, we can delete from the table by specifying the id we want to delete
    //in order to make this more general, we'd just replace "id" with whatever the primary key is
    Class clas = data.getClass();
    String deletedField = clas.getDeclaredField("id").get(data).toString();
    PreparedStatement prep = conn.prepareStatement("DELETE FROM rent WHERE id=?");
    prep.setString(1, deletedField);
    prep.execute();
    prep.close();
    rentList.remove(data);
  }

  /**
   * selects the data in a table where a given predicate is true. Returns a table
   * of the selected data
   *
   * @param pred predicate of sql select operation, i.e. the field we're looking at specifically.
   * @param predEquals what the predicate should equal, i.e. the qualifier we're specifying.
   * @throws SQLException if an error occurs in any SQL query.
   */

  public void where(String pred, String predEquals) throws SQLException {
    List<Rent> selectList = new ArrayList<>();
    PreparedStatement prep = conn.prepareStatement("SELECT * FROM rent where " + pred + "=" + predEquals);
    ResultSet rs = prep.executeQuery();
    //adds selected data to a list
    while (rs.next()) {
      selectList.add(makeNewRent(rs));
    }
    rs.close();
    prep.close();
    //displays selected data
    printRentHeader();
    for (int i = 0; i < selectList.size();  i++) {
      printRent(selectList.get(i));
    }
  }

  //prints a header with fields of rent
  public static void printRentHeader() {
    System.out.println(String.format("%10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s", "fit", "|", "user_id", "|", "item_id", "|", "rating", "|", "rented_for", "|", "category", "|", "size", "|", "id" , "|"));
    System.out.println(String.format("%s", "-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"));
  }

  //prints a rent object with its field values
  public void printRent(Rent r) {
    System.out.println(String.format("%10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s", r.fit, "|", r.user_id, "|", r.item_id, "|", r.rating, "|", r.rented_for, "|", r.category, "|", r.size, "|", r.id, "|"));
  }

  //helper function to make a new rent object. this can be modified to make any type of class object
  public Rent makeNewRent(ResultSet rs) throws SQLException {
    return new Rent(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
        rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8));
  }

  /**
   * selects the data in a table where a given predicate is true. Returns a table
   * of the selected data
   *
   * @param datum datum to be updated; we use the primary key from this object to update th necessary object
   * @param field field thta we will be updating
   * @param newVal value of the field that we are updating
   * @throws SQLException if an error occurs in any SQL query.
   * @throws IllegalAccessException if the field of an object cannot be accessed
   * @throws NoSuchFieldException if a field of an object is called that doesn't exist.
   */
  public void update(Object datum, String field, String newVal)
      throws NoSuchFieldException, SQLException, IllegalAccessException {
    Class clas = datum.getClass();
    Field fieldToUpdate = clas.getDeclaredField("id"); //gets simple name of field to update using the primary key
    String oldVal = fieldToUpdate.get(datum).toString();
    PreparedStatement prep = conn.prepareStatement("UPDATE rent SET "
        + field + " = '" +
        newVal + "' WHERE id=" + oldVal);
    prep.executeUpdate();
    prep.close();
  }

  /**
   * allows us to execute any explicit sql query from the given statement
   *
   * @param rawSQLQuery sql statement to be executed
   * @throws SQLException if an error occurs in any SQL query.
   */
  public void sql(String rawSQLQuery) throws SQLException {
    PreparedStatement prep = conn.prepareStatement(rawSQLQuery);
    prep.execute();
    prep.close();
  }


}

