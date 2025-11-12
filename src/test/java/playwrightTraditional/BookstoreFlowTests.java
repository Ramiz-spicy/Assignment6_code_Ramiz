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
                .launch(new BrowserType.LaunchOptions().setHeadless(true));


        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/"))
                .setRecordVideoSize(1280, 720));

        page = context.newPage();
    }

    void createFreshContext() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/"))
                .setRecordVideoSize(1280, 720));
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

    // TEST CASE 5
    @Test
    @Order(5)
    public void testPickupInformationPage() {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Continue")).click();
    }

    // TEST CASE 6
    @Test
    @Order(6)
    public void testPaymentInformationPage() {
        assertThat(page.getByText("Order Subtotal").nth(1)).isVisible();
        assertThat(page.getByText("$164.98").nth(2)).isVisible();
        assertThat(page.getByText("Handling To support the").nth(1)).isVisible();
        assertThat(page.getByText("$3.00").nth(3)).isVisible();
        assertThat(page.getByText("Tax").nth(1)).isVisible();
        assertThat(page.getByText("$17.22").nth(1)).isVisible();
        assertThat(page.getByText("$185.20").nth(1)).isVisible();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Back to cart")).click();
    }

    // TEST CASE 7
    @Test
    @Order(7)
    public void testYourShoppingCart() {
        page.getByLabel("Remove product JBL Quantum").click();
        assertThat(page.getByRole(AriaRole.ALERT)).isVisible();
        assertThat(page.getByText("Your cart is empty")).isVisible();
        page.close();
    }

    @AfterAll
    static void teardown() {
        context.close();
        browser.close();
        playwright.close();
    }
}
