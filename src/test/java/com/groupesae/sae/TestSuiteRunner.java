package com.groupesae.sae;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        GrilleTest.class,
        DijkstraTest.class,
        AstarTest.class,
        MoutonTest.class,
        LoupTest.class,
        IntegrationTest.class
})
public class TestSuiteRunner {

}