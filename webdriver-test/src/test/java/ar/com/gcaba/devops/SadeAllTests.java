package ar.com.gcaba.devops;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.browserlaunchers.Sleeper;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SadeAllTests
{
	private static final String TEST_TEXT = "Test performance. Carece de motivación administrativa.";
	private static WebDriver driver;
	private static WebDriverWait wait;
	private static final String baseUrl = "http://eu.hml.gcba.gob.ar/";	// Inyectar por Spring
	private static Logger log = LoggerFactory.getLogger(SadeAllTests.class);
	private static final String SADE_TITLE = "Sistema de Administración de Documentos Electrónicos";
	private static final String EU_TITLE = "Escritorio Único";
	private static final String CCOO_TITLE = "Comunicaciones Oficiales";
	private static final String GEDO_TITLE = "Sistema GEDO";
	private static final String EE_TITLE = "Sistema Expediente Electrónico";
	private static final String TRACK_TITLE = "SADE";
	private static final String PF_TITLE = "Sistema PORTA FIRMA";
	private static final String LOYS_TITLE = "LOyS";

	private static long startTime;

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		log.info("Setup before class...");
		DesiredCapabilities cp = DesiredCapabilities.firefox();
		cp.setCapability("version", "7");
		cp.setCapability("platform", Platform.LINUX);
		cp.setCapability("selenium-version", "2.18.0");
		
		driver = new FirefoxDriver(cp);
	    wait  =  new WebDriverWait(driver, 30);
	    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	    driver.get(baseUrl);
		log.info("Get base URL");
		assertEquals(SADE_TITLE, driver.getTitle());
		waitForCss("input#username").sendKeys("ggomez");		// Ingresa usuario
		findByCss("input#password").sendKeys("1234");		// Ingresa password
		clickAt(findByCss("input[class=\"egovBoton\"]"), "Login");	// Login
		waitForTitle(EU_TITLE);
//        modules = driver.findElements(By.cssSelector("img[title=\"Ir a la aplicación\"]"));

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		log.info("Quit driver...");
		driver.quit();
		driver = null;
		log.info("Tear down after class completed.");
	}

	@Before
	public void setUp() throws Exception
	{
		waitForTitle(EU_TITLE);
		assertEquals(EU_TITLE, driver.getTitle());
		log.debug("------------------------------------------------------------");
		log.debug("Setup before test - Escritorio OK");
	}

	@After
	public void tearDown() throws Exception
	{
		// Algunas aplicaciones vuelven solas al escritorio!
		if (!EU_TITLE.equals(driver.getTitle().trim()))
		{
			clickAt(findByXpath("//span[text()=\"Ir a Escritorio\"]"), "Salir a escritorio");
			log.debug("Teardown after test - Salir a escritorio");
		}
	}

	@Test
	public void testGEDO()
	{
        clickAt(findByXpath("//tr[1]/td[2]/div/table/tbody/tr/td/table/tbody/tr/td/img"), "Selecciona GEDO");
        waitForTitle(GEDO_TITLE);
        assertEquals( GEDO_TITLE, driver.getTitle() );

      // Nuevo documento texto
  	  clickAt(waitForXpath("//div[6]/div/div/div/div"),"Iniciar Documento");
      
  	  typeAndBlur("//td[3]/i/input", "IF");
      clickAt(findByCss("img[src=\"/gedo-web/imagenes/ProducirloYoMismo.png\"]"), "Producirlo yo mismo");
      
      typeAndBlur("//td[2]/input", TEST_TEXT);
      
      // Contenido en el Gecko editor
      driver.switchTo().frame(1);
      driver.switchTo().activeElement().sendKeys(TEST_TEXT);	// Editor embebido
      driver.switchTo().defaultContent();
      
      clickAt(findByCss("img[src=\"/gedo-web/imagenes/FirmarYoMismoElDocumento.png\"]"), "Firmar yo mismo el documento");
      clickAt(waitForXpath("//table[2]/tbody/tr/td/table/tbody/tr/td/span/table/tbody/tr[2]/td[2]"),"Se ha enviado al Porta Firma");
      log.debug("Documento (texto) enviado a Porta Firma");
 
      // Nuevo documento con Gráfico
      clickAt(waitForXpath("//div[6]/div/div/div/div"),"Iniciar Documento");

  	  typeAndBlur("//td[3]/i/input", "IFGRA");
      clickAt(findByCss("img[src=\"/gedo-web/imagenes/ProducirloYoMismo.png\"]"), "Producirlo yo mismo");
      
      typeAndBlur("//td[2]/input", TEST_TEXT);

      driver.findElement(By.name("file")).sendKeys("/home/scarabetta/Pictures/ibm-various-virtualization.jpg");

      clickAt(findByCss("img[src=\"/gedo-web/imagenes/FirmarYoMismoElDocumento.png\"]"), "Firmar yo mismo el documento");
      clickAt(waitForXpath("//table[2]/tbody/tr/td/table/tbody/tr/td/span/table/tbody/tr[2]/td[2]"),"Se ha enviado al Porta Firma");
      log.debug("Documento (gráfico) enviado a Porta Firma");
      
      
      // Nuevo documento SIN enviar a Porta Firma
      // Desactivar Portafirma
      clickAt(waitForXpath("//li[5]/div/div/div/span"), "Tab Perfil");
      clickAt(findByXpath("//span/input"), "Toggle PF");
      clickAt(findByXpath("//div/span/table/tbody/tr[2]/td[2]"), "Guardar");
      clickAt(waitForXpath("//table[2]/tbody/tr/td/table/tbody/tr/td/span/table/tbody/tr[2]/td[2]"),"Se han guardado las preferencias");
      
      clickAt(waitForXpath("//li/div/div/div/span"), "Tab Mis Tareas");
      clickAt(waitForXpath("//div[5]"), "???");
      
	  clickAt(waitForXpath("//div[6]/div/div/div/div"),"Iniciar Documento Texto SIN Porta Firma");
  	  typeAndBlur("//td[3]/i/input", "IF");
      clickAt(findByCss("img[src=\"/gedo-web/imagenes/ProducirloYoMismo.png\"]"), "Producirlo yo mismo");

      findByXpath("//td[2]/input").clear();
      findByXpath("//td[2]/input").sendKeys(TEST_TEXT);
      
      
      // Contenido en el Gecko editor
      driver.switchTo().frame(1);
      driver.switchTo().activeElement().sendKeys(TEST_TEXT);
      driver.switchTo().defaultContent();
      clickAt(findByCss("img[src=\"/gedo-web/imagenes/FirmarYoMismoElDocumento.png\"]"), "Firmar yo mismo el documento");

      clickAt(findByCss("img[src=\"./imagenes/FirmarConCertificado.png\"]"), "Firmar con certificado");
      
      
      String expediente = 
      		waitForXpath("//div[2]/div/div/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr/td/span").getText();
      System.out.println( expediente );
      
      clickAt(waitForXpath("//td[2]/div/div/div/div/img"), "Volver al buzón");
      
      
      clickAt(waitForXpath("//li[5]/div/div/div/span"), "Tab Perfil");
      clickAt(findByXpath("//span/input"), "Toggle PF");
      clickAt(findByXpath("//div/span/table/tbody/tr[2]/td[2]"), "Guardar");
      clickAt(waitForXpath("//table[2]/tbody/tr/td/table/tbody/tr/td/span/table/tbody/tr[2]/td[2]"),"Se han guardado las preferencias");
      
	}

	@Test
	public void testCCOO()
	{
        clickAt(findByXpath("//tr[2]/td[2]/div/table/tbody/tr/td/table/tbody/tr/td/img"), "Selecciona CCOO");
        waitForTitle(CCOO_TITLE);
        assertEquals( CCOO_TITLE, driver.getTitle() );
        driver.findElement(By.partialLinkText("InicioCO")).click();	// Mis tareas (default) => Inicio CO
        stopTimer("Seleccionar Mis Tareas");
        
        clickAt(waitForXpath("//tbody[@id='body:dataTipoExpediente:tbody_element']/tr[2]/td[3]/table/tbody/tr/td/a/span"), "Seleccionar...");
        clickAt(waitForCss("button.x8x"), "Seleccionar...");
        clickAt(waitForXpath("//tbody[@id='body:dataActividad:tbody_element']/tr/td[7]/table/tbody/tr/td[2]/a/span"), "Seleccionar...");
        
        // Popup ventana de carga
        waitForId("body:usuarioDestino").sendKeys("ggomez");
        findById("body:comentario").sendKeys("Test performance. Carece de motivacion administrativa.");
        clickAt(findByCss("button.x8w"), "Cargar formulario");
        clickAt(waitForCss("button.x8x"), "Continuar");
        clickAt(waitForXpath("//tbody[@id='body:dataActividad:tbody_element']/tr/td[7]/table/tbody/tr/td[2]/a/span"), "Mostrar x");

        driver.switchTo().frame(1);
        ((JavascriptExecutor)driver).executeScript("tinyMCE.activeEditor.setContent('Test performance. Carece de motivacion administrativa.')");
        driver.switchTo().defaultContent();

        clickAt(waitForId("body:enviar"), "Cargar nota");
        
        waitForId("body:destinatario").sendKeys("ggomez");
        findById("body:referencia").sendKeys(TEST_TEXT);
        findById("body:descripcionTarea").sendKeys(TEST_TEXT);
        clickAt(findByXpath("(//button[@type='button'])[6]"), "Firmar nota");
        
        
        new WebDriverWait(driver, 5).until(ExpectedConditions.alertIsPresent());
        // Alert = Está seguro que desea firmar?
        Alert alert = driver.switchTo().alert();
        alert.accept();
        
        driver.switchTo().defaultContent();
        
        driver.findElement(By.linkText("Consulta de Comunicaciones Oficiales")).click();
        
        new Select(findById("body:tipoActuacion")).selectByValue("NO");
        findById("body:anio").sendKeys("2014");
        findById("body:numeroActuacion").sendKeys("01079756");
        findById("body:reparticion").sendKeys("mgeya");
        findById("body:reparticion").click();
        findByXpath("//li[5]").click();
        
        clickAt(findByCss("button.x8w"), "Consultar");
	}

	@Test
	public void testEE()
	{
        clickAt(findByXpath("//tr[3]/td[2]/div/table/tbody/tr/td/table/tbody/tr/td/img"), "Selecciona EE");
        waitForTitle(EE_TITLE);
        assertEquals( EE_TITLE, driver.getTitle() );
        
        driver.findElement(By.xpath("//div[4]/div[2]/div/div/div")).click(); // caratular interno
        
        typeAndBlur("//textarea", TEST_TEXT);
        typeAndBlur("//td/div/div/table/tbody/tr[2]/td[2]/div/textarea", TEST_TEXT);
        
        driver.findElement(By.xpath("//div/i/input")).clear();
        driver.findElement(By.xpath("//div/i/input")).sendKeys("urses");
//        driver.switchTo().activeElement().sendKeys(Keys.TAB);
        Sleeper.sleepTightInSeconds(2);
        driver.findElement(By.xpath("//td/div/div[2]/table/tbody[2]/tr/td/div")).click();
        driver.findElement(By.xpath("//td/div/div/div/img")).click();	//Caratular
        
        String exp = driver.findElement(By.xpath("//td[3]/div/span")).getText();	// Pop-up
        System.out.println(exp);
        driver.findElement(By.xpath("//table[2]/tbody/tr/td/table/tbody/tr/td/span/table/tbody/tr[2]/td[2]")).click();
        
        driver.findElement(By.xpath("//td/div/span")).click();	// Select el que acaba de crear
        driver.findElement(By.xpath("//td[9]/div/table/tbody/tr/td/table/tbody/tr/td/img")).click();	// Revisar
        
        driver.findElement(By.xpath("//div/div/div/table/tbody/tr/td/table/tbody/tr/td[3]/div/div/div/img")).click();	// Seleccionar el único
        // Editor embebido
        driver.switchTo().frame(1);
        driver.switchTo().activeElement().sendKeys("Hola Mundo!");
        driver.switchTo().defaultContent();
        
         driver.findElement(By.xpath("//div[2]/div/table/tbody/tr/td[2]/div/i/i")).click();
        
        driver.findElement(By.xpath("//div[7]/table/tbody/tr[4]/td[2]")).click();
        driver.findElement(By.xpath("//div[6]/div/div/img")).click();
        driver.findElement(By.xpath("//div[3]/div/div/div/table[2]/tbody/tr/td/table/tbody/tr/td/span/table/tbody/tr[2]/td[2]")).click();
        driver.findElement(By.xpath("//table[2]/tbody/tr/td/table/tbody/tr/td/span/table/tbody/tr[2]/td[2]")).click();	}

	@Test
	public void testTRACK()
	{
        clickAt(findByXpath("//tr[4]/td[2]/div/table/tbody/tr/td/table/tbody/tr/td/img"), "Selecciona TRACK");
        waitForTitle(TRACK_TITLE);
        assertEquals( TRACK_TITLE, driver.getTitle() );
		clickAt(waitForXpath("//table[2]/tbody/tr/td/table/tbody/tr[1]/td[1]/table/tbody/tr/td/table/tbody/tr/td[1]/a"),
				"Inicio");	// Redundante, pero que las hay las hay...

		clickAt(waitForXpath("//table[2]/tbody/tr/td/table/tbody/tr[1]/td[1]/table/tbody/tr/td/table/tbody/tr/td[3]/a"),
					"Caratulación");

		assertEquals("Búsqueda de Carátulas",
				findByXpath("//*[@id='pbPrincipal']/table[1]/tbody/tr/td[2]/span").getText().trim());
		clickAt(waitForXpath("//td[4]/button"),"Nuevo Interno");
		assertEquals("Caratulación de Expedientes",
				findByXpath("//*[@id='pbPrincipal']/table[1]/tbody/tr/td[2]/span").getText().trim());

  	typeAndBlur("//*[@id=\"body:ExtractoCaratulacionNuevo\"]", "SADE100");
//      driver.findElement(By.xpath("//td/a")).click(); // Seleccionar codigo???

      typeAndBlur("//textarea", TEST_TEXT);
      
      findById("body:idDestino:_1").click(); // Seleccionar Interno
      
    	typeAndBlur("//*[@id=\"body:SectorInternoDestino_CaratulacionNuevo\"]", "ACSADE");
  	typeAndBlur("//*[@id=\"body:Fojas\"]", "2");

  	typeAndBlur("//*[@id=\"body:_idJsp191\"]", TEST_TEXT);
      clickAt(findByXpath("//button"), "Grabar");
      wait.until(ExpectedConditions.alertIsPresent()).accept();
		assertEquals("Expediente Generado",
				findByXpath("//*[@id='pbPrincipal']/table[1]/tbody/tr/td[2]/span").getText().trim());
     String expediente = findByXpath("//td[2]/table/tbody/tr[1]/td/span").getText();
      System.out.println(expediente);
      clickAt(findByXpath("//td[3]/table/tbody/tr/td[3]/a/span"), "Volver a la lista");
	}

	@Test
	public void testPF()
	{
        clickAt(findByXpath("//tr[5]/td[2]/div/table/tbody/tr/td/table/tbody/tr/td/img"), "Selecciona PF");
        waitForTitle(PF_TITLE);
        assertEquals( PF_TITLE, driver.getTitle() );
        
        clickAt( waitForXpath("//tbody[2]/tr/td/div"),"XX");
        clickAt( waitForXpath("//div[3]/div/div/div/div"),"XX");
        clickAt( waitForXpath("//td/span/table/tbody/tr[2]/td[2]"),"XX");
        int exitos = Integer.valueOf( findByXpath("//tr[3]/td[3]/div/span").getText()).intValue();
        assertTrue( exitos > 0 );
        clickAt( waitForXpath("//div[3]/span/table/tbody/tr[2]/td[2]"),"XX");
        assertEquals("Firma exitosa", findByXpath("//div[3]/div[2]/table/tbody[2]/tr/td[5]/div").getText());
        clickAt( waitForXpath("//td[7]/span/table/tbody/tr[2]/td[2]"),"XX");	
     }

	@Test
	public void testLOYS()
	{
        clickAt(findByXpath("//tr[11]/td[2]/div/table/tbody/tr/td/table/tbody/tr/td/img"), "Selecciona LOyS");
        waitForTitle(LOYS_TITLE);
        assertEquals( LOYS_TITLE, driver.getTitle() );
        driver.findElement(By.xpath("//td[2]/a")).click();
        driver.findElement(By.xpath("//input")).clear();
        driver.findElement(By.xpath("//input")).sendKeys("Test performance. Carece de motivación administrativa");
        driver.findElement(By.xpath("//tr[4]/td[2]/a/img")).click();
        String expediente = driver.findElement(By.xpath("//div/span")).getText();
        System.out.println(expediente);
        waitForXpath("//button").click();
	}

	protected static WebElement waitForXpath(String xpath) 
	  {
	  	startTime = System.currentTimeMillis();
	  	By locator = By.xpath(xpath);
	      wait.until(
	      		ExpectedConditions.presenceOfElementLocated(locator));
	      return driver.findElement(locator);
		}

	protected static WebElement findByXpath(String xpath) {
	      return driver.findElement(By.xpath(xpath));
	  }

	protected static WebElement waitForId(String id) 
	  {
	  	startTime = System.currentTimeMillis();
	  	By locator = By.id(id);
	      wait.until(
	      		ExpectedConditions.presenceOfElementLocated(locator));
	      return driver.findElement(locator);
		}

	protected static WebElement findById(String id) {
	      return driver.findElement(By.id(id));
	  }

	protected static WebElement waitForCss(String selector) 
	  {
	  	startTime = System.currentTimeMillis();
	  	By locator = By.cssSelector(selector);
	      wait.until(
	      		ExpectedConditions.presenceOfElementLocated(locator));
	      return driver.findElement(locator);
		}

	protected static WebElement findByCss(String locator) {
	      return driver.findElement(By.cssSelector(locator));
	  }

	protected static void waitForTitle(String title) {
	  	startTime = System.currentTimeMillis();
	      wait.until(ExpectedConditions.titleIs(title));
	      stopTimer("Wait " + title);
	  }

	protected static void typeAndBlur(String xpath, String text)
	{
		WebElement field = waitForXpath(xpath);
		if (field.isEnabled())
		{
			field.clear();
			field.sendKeys(text);
			field.click();  // Fire all events
			field.sendKeys(Keys.TAB);	// Fire change event (just in case)
		}
		else
		{
			log.debug("Field " + xpath + " is NOT enabled. Ok?");
		}
	}


	protected static void stopTimer(String msg) 
	  {
	  	System.out.println( msg + ": " + (System.currentTimeMillis()-startTime) + " mS.");
		}

	protected static void clickAt( WebElement element, String msg )
	{
	startTime = System.currentTimeMillis();
		element.click();
	System.out.println( msg + ": " + (System.currentTimeMillis()-startTime) + " mS.");
	}

	protected static void startTimer() 
	  {
	  	startTime = System.currentTimeMillis();
		}

	protected boolean isElementPresent(By by)
	{
	    try {
	      driver.findElement(by);
	      return true;
	    } catch (NoSuchElementException e) {
	      return false;
	    }
	  }

	protected boolean isAlertPresent()
	{
	    try {
	      driver.switchTo().alert();
	      return true;
	    } catch (NoAlertPresentException e) {
	      return false;
	    }
	  }

}
