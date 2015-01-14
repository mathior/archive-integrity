package de.cbraeutigam.archint;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.cbraeutigam.archint.hashforest.HashForestTest;
import de.cbraeutigam.archint.util.OrderingTest;


@RunWith(Suite.class)
@Suite.SuiteClasses({OrderingTest.class, HashForestTest.class})
public class TestSuite {}
