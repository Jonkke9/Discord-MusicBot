package com.github.jonkke9.musicbot;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMD.StatusCode;
import net.sourceforge.pmd.PMDConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

final class TestPMD {

	@Test
	void testJava() {
		final PMDConfiguration config = new PMDConfiguration();

		config.setInputPaths(List.of(
				"src/main/java",
				"src/main/test"
		));

		config.setRuleSets(List.of(
				"src/main/test/resources/pmd-java-ruleset.xml"
		));

		config.setReportFormat("html");
		config.setReportFile("pmd-report.html");

		final StatusCode code = PMD.runPmd(config);
		if (code == StatusCode.OK) {
			try {
				Files.delete(Path.of(config.getReportFile()));
			} catch (final IOException e) {
				Assertions.fail("Failed to delete pmd-report.html file");
			}
		}

		Assertions.assertEquals(StatusCode.OK, code, "PDM Java ok");
	}

}
