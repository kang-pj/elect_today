import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.List;

public class TestJavaCrawler {
    public static void main(String[] args) {
        // 로그 레벨 설정
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");
        
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver-144");
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36");
        
        WebDriver driver = new ChromeDriver(options);
        
        try {
            System.out.println("사이트 접속 중...");
            driver.get("https://ev.or.kr/nportal/buySupprt/initSubsidyPaymentCheckAction.do");
            Thread.sleep(5000);
            
            List<WebElement> tables = driver.findElements(By.tagName("table"));
            System.out.println("테이블 수: " + tables.size());
            
            if (tables.size() >= 2) {
                WebElement dataTable = tables.get(1);
                List<WebElement> rows = dataTable.findElements(By.tagName("tr"));
                System.out.println("데이터 행 수: " + rows.size());
                
                for (int i = 1; i <= Math.min(5, rows.size() - 1); i++) {
                    List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));
                    if (cells.size() >= 7) {
                        System.out.println(cells.get(0).getText() + " - " + cells.get(1).getText());
                    }
                }
            }
            
            System.out.println("크롤링 성공!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
