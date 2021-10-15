import edu.brown.cs.student.main.Database;
import edu.brown.cs.student.main.Rent;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

public class ORMTester {

  @Test
  public void testInsertOne() throws SQLException, ClassNotFoundException, IllegalAccessException {
    Database d = new Database("data/emptyEditable");
    d.insert(new Rent("1", "2", "3", "4", "5", "6", "7", "8"));
  }

}
