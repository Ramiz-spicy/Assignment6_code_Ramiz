package playwrightLLM;

import com.microsoft.playwright.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.*;
import java.nio.file.Paths;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookstoreLLMTests {

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
        page.setDefaultTimeout(45000);
    }

    // TEST CASE 1 – Search for a JanSport backpack and add to cart
    @Test
    @Order(1)
    public void testBookstoreScenario() {
        page.navigate("https://depaul.bncollege.com/");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        page.getByPlaceholder("Enter your search details (").click();
        page.getByPlaceholder("Enter your search details (").fill("jansport");
        page.getByPlaceholder("Enter your search details (").press("Enter");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        page.getByTitle("Main Campus JanSport Backpacks").first().click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        page.getByLabel("Add to cart").click();
        page.waitForTimeout(2000);

        assertThat(page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Cart 1 items"))).isVisible();
    }

    // TEST CASE 2 – Cart page and proceed to checkout
    @Test
    @Order(2)
    public void testCartPageScenario() {
        page.navigate("https://depaul.bncollege.com/cart");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // ✅ use role-based locator to avoid strict-mode duplication
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Your Shopping Cart(1 Item)"))).isVisible();

        page.getByLabel("Proceed To Checkout").first().click();
        page.waitForURL("**/login/checkout",
                new Page.WaitForURLOptions().setTimeout(30000));
    }

    // TEST CASE 3 – Guest checkout page
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

    // TEST CASE 4 – Fill contact information
    @Test
    @Order(4)
    public void testContactInformationPage() {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Contact Information"))).isVisible();

        page.getByPlaceholder("Please enter your first name").fill("Bob");
        page.getByPlaceholder("Please enter your last name").fill("Smith");
        page.getByPlaceholder("Please enter a valid email").fill("ramizimtiaz268@gmail.com");
        page.getByPlaceholder("Please enter a valid phone").fill("2174190164");

        page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Continue")).click();
        page.waitForTimeout(2000);

        page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Continue")).click();
        page.waitForTimeout(2000);
    }

    // TEST CASE 5 – Back to cart and remove item
    @Test
    @Order(5)
    public void testReturnToCartAndRemove() {
        page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Back to cart")).click();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // precise role again
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Your Shopping Cart(1 Item)"))).isVisible();

        page.getByLabel("Remove product Main Campus").click();
        page.waitForTimeout(1500);
        assertThat(page.getByText("Your cart is empty")).isVisible();
    }

    @AfterAll
    static void teardown() {
        context.close();
        browser.close();
        playwright.close();
    }
}
