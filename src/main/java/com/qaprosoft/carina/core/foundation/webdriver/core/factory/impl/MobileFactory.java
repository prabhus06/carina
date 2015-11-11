package com.qaprosoft.carina.core.foundation.webdriver.core.factory.impl;


import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.SpecialKeywords;
import com.qaprosoft.carina.core.foundation.webdriver.core.capability.CapabilitiesLoder;
import com.qaprosoft.carina.core.foundation.webdriver.core.capability.impl.mobile.MobileGridCapabilities;
import com.qaprosoft.carina.core.foundation.webdriver.core.capability.impl.mobile.MobileNativeCapabilities;
import com.qaprosoft.carina.core.foundation.webdriver.core.capability.impl.mobile.MobileWebCapabilities;
import com.qaprosoft.carina.core.foundation.webdriver.core.factory.AbstractFactory;
import com.qaprosoft.carina.core.foundation.webdriver.device.Device;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

public class MobileFactory extends AbstractFactory {


    public static final String MOBILE_GRID = SpecialKeywords.MOBILE_GRID;

    public static final String MOBILE_POOL = SpecialKeywords.MOBILE_POOL;

    public static final String MOBILE = SpecialKeywords.MOBILE;


    @Override
    public WebDriver create(String testName, Device device) {

        String selenium = Configuration.get(Configuration.Parameter.SELENIUM_HOST);
        String browser = Configuration.get(Configuration.Parameter.BROWSER);
        String mobile_platform_name = Configuration.get(Configuration.Parameter.MOBILE_PLATFORM_NAME);

        RemoteWebDriver driver = null;
        DesiredCapabilities capabilities = getCapabilities(testName, browser);
        try {

            if (MOBILE_GRID.equalsIgnoreCase(browser)) {

                driver = new RemoteWebDriver(new URL(selenium), capabilities);
            } else if (MOBILE_POOL.equalsIgnoreCase(browser) || browser.equalsIgnoreCase(MOBILE)) {
                if (mobile_platform_name.toLowerCase().equalsIgnoreCase(SpecialKeywords.ANDROID))
                    driver = new AndroidDriver(new URL(selenium), capabilities);
                else if (mobile_platform_name.toLowerCase().equalsIgnoreCase(SpecialKeywords.IOS)) {
                    driver = new IOSDriver(new URL(selenium), capabilities);
                }


            } else if (browser.equalsIgnoreCase(SpecialKeywords.CUSTOM)) {
                driver = new RemoteWebDriver(new URL(selenium), capabilities);
            } else {
                throw new RuntimeException("Unsupported browser");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        return driver;
    }

    public DesiredCapabilities getCapabilities(String testName, String browser) {
        if (MOBILE_GRID.equalsIgnoreCase(browser)) {
            return new MobileGridCapabilities().getCapability(browser, testName);
        } else if ((MOBILE_POOL.equalsIgnoreCase(browser) || MOBILE.equalsIgnoreCase(browser) && !Configuration.get(Configuration.Parameter.MOBILE_BROWSER_NAME).isEmpty())) {
            return new MobileWebCapabilities().getCapability(testName, browser);
        } else if ((MOBILE_POOL.equalsIgnoreCase(browser) || MOBILE.equalsIgnoreCase(browser) && Configuration.get(Configuration.Parameter.MOBILE_BROWSER_NAME).isEmpty())) {
            return new MobileNativeCapabilities().getCapability(testName, browser);
        } else if (SpecialKeywords.CUSTOM.equalsIgnoreCase(browser)) {
            try {
                return new CapabilitiesLoder(new FileInputStream(Configuration.get(Configuration.Parameter.CUSTOM_CAPABILITIES))).loadCapabilities();
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Unable read custom capabilities");
            }
        }
        {
            throw new RuntimeException("Unsupported mobile browser");
        }

    }


}
