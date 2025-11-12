package playwrightTraditional;

import com.microsoft.playwright.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookstoreFlowTests {

    static Playwright playwright;
    static Browser browser;
    static BrowserContext context;
    static Page page;

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }

    // TEST CASE 1
    @Test
    @Order(1)
    public void testBookstoreScenario() {
        page.navigate("https://depaul.bncollege.com/");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        page.getByPlaceholder("Enter your search details (product title, ISBN, keyword, etc.)")
                .fill("earbuds");
        page.keyboard().press("Enter");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Brand")).click();
        page.locator("label").filter(new Locator.FilterOptions().setHasText("JBL"))
                .locator("svg").first().click();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color")).click();
        page.locator("label").filter(new Locator.FilterOptions().setHasText("Black"))
                .locator("svg").first().click();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).click();
        page.getByText("Over $50").click();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("JBL Quantum True Wireless")).click();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("JBL Quantum True Wireless"))).isVisible();

        page.getByLabel("Add to cart").click();
        page.waitForTimeout(3000);

        assertThat(page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Cart 1 items"))).isVisible();
    }

    // TEST CASE 2
    @Test
    @Order(2)
    public void testCartPageScenario() {
        page.navigate("https://depaul.bncollege.com/cart");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Your Shopping Cart(1 Item)"))).isVisible();
        assertThat(page.getByText("Subtotal $164.98")).isVisible();

        page.getByLabel("Proceed To Checkout").first().click();
        page.waitForURL("**/login/checkout", new Page.WaitForURLOptions().setTimeout(30000));
    }

    // TEST CASE 3
    @Test
    @Order(3)
    public void testCreateAccountPage() {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Create Account"))).isVisible();

        page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Proceed As Guest")).click();

        page.waitForURL("**/checkout/multi/delivery-address/add",
                new Page.WaitForURLOptions().setTimeout(20000));
    }

    // TEST CASE 4
    @Test
    @Order(4)
    public void testContactInformationPage() {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Contact Information"))).isVisible();

        page.getByPlaceholder("Please enter your first name").fill("Ramiz");
        page.getByPlaceholder("Please enter your last name").fill("Imtiaz");
        page.getByPlaceholder("Please enter a valid email").fill("ramizimtiaz268@gmail.com");
        page.getByPlaceholder("Please enter a valid phone").fill("2174190164");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("CONTINUE")).click();
        page.waitForTimeout(3000);
    }


    @Test
    @Order(5)
    public void testPickupInformationPage() {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.waitForTimeout(3000);

        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Contact Information"))).isVisible();
        assertThat(page.getByText("Ramiz", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.getByText("ramizimtiaz268@gmail.com")).isVisible();
        assertThat(page.getByText("12174190164")).isVisible();

        assertThat(page.getByText("I'll pick them up")).isVisible();
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Pickup Person"))).isVisible();
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Pickup Location"))).isVisible();
        assertThat(page.getByText("DePaul University Loop Campus & SAIC")).isVisible();

        assertThat(page.getByText("$164.98")).isVisible();
        assertThat(page.getByText("$3.00")).isVisible();
        assertThat(page.getByText("$17.22")).isVisible();
        assertThat(page.getByText("$185.20")).isVisible();

        assertThat(page.getByText("JBL Quantum True Wireless")).isVisible();
        assertThat(page.getByText("$164.98")).isVisible();

        page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("CONTINUE")).click();

        page.waitForTimeout(4000);
    }






    @AfterAll
    static void teardown() {
        context.close();
        browser.close();
        playwright.close();
    }
}
