import com.phonecompany.billing.BillCalculator;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void test1() {
        String csvContent = null;

        try {
            csvContent = new String(Files.readAllBytes(Paths.get("src/zdroje/test1.csv")));
        } catch (IOException e) {
            fail("Invalid fixture file");
        }

        BillCalculator calculator = new BillCalculator();
        BigDecimal result = calculator.calculate(csvContent);

        BigDecimal expectedResult = new BigDecimal("11.7");

        assertEquals(result, expectedResult);
    }
}