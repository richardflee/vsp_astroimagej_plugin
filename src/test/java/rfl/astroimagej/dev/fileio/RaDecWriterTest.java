package rfl.astroimagej.dev.fileio;

// cross-check with on-line web-based vsp queries for wasp-12 and vega May-2021

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import rfl.astroimagej.dev.catalogs.CatalogQuery;
import rfl.astroimagej.dev.catalogs.QueryResult;
import rfl.astroimagej.dev.catalogs.VspCatalog;
import rfl.astroimagej.dev.enums.CatalogType;

class RaDecWriterTest {
	// wasp & custom vega catalog, query & result
	private static VspCatalog catalog;
	private static CatalogQuery waspQuery;
	private static CatalogQuery vegaQuery;
	private static QueryResult waspResult;
	private static QueryResult vegaResult;
	private static RaDecFileWriter radecWriter;

	// filenames and paths
	private static File waspFile = null;
	private static File vegaFile = null;

	// imported radec data into string arrrays
	private List<String> waspLines;
	private List<String> vegaLines;

	// setup & write wasp & vega radec files
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		// vsp catalog
		catalog = new VspCatalog();
		radecWriter = new RaDecFileWriter();

		// default wasp12 & custom vega queries
		waspQuery = new CatalogQuery();
		waspQuery.setCatalogType(CatalogType.VSP);
		
		vegaQuery = new CatalogQuery();
		vegaQuery.setObjectId("vega");
		vegaQuery.setRaHr(18.61565);
		vegaQuery.setDecDeg(38.78369);
		vegaQuery.setFovAmin(50.0);
		vegaQuery.setMagLimit(16.0);
		vegaQuery.setCatalogType(CatalogType.VSP);
		vegaQuery.setMagBand("B");

		waspResult = catalog.runQuery(waspQuery);
		vegaResult = catalog.runQuery(vegaQuery);

		waspFile =  RaDecFileWriter.getFile(waspQuery, "radec.txt");
		vegaFile = RaDecFileWriter.getFile(vegaQuery, "radec.txt");
		
		RaDecFileWriter.getFile(waspQuery, "fits").delete();
		RaDecFileWriter.getFile(vegaQuery, "fits").delete();
		
		System.out.println("wasp :" + waspFile.delete());
		System.out.println("vega: " + vegaFile.delete());

		// write radec files
		System.out.println(radecWriter.writeRaDecFile(waspQuery, waspResult));
		System.out.println(radecWriter.writeRaDecFile(vegaQuery, vegaResult));
	}
	
	// true if daat matches any line in waspLines list
	private boolean waspFinder(String data) {
		for (String line : waspLines) {
			if (line.equalsIgnoreCase(data)) {
				return true;
			}
		}
		return false;
	}

	private boolean vegaFinder(String data) {
		for (String line : vegaLines) {
			if (line.equalsIgnoreCase(data)) {
				return true;
			}
		}
		return false;
	}

	@DisplayName("Confirm compiled wasp12 & vega radec filenames are correct")
	@Test
	void compileFilenames_ConfirmCorrect() {
		Path waspPath = Paths.get(waspFile.toString());
		Path vegaPath = Paths.get(vegaFile.toString());
		
		assertEquals("wasp_12.V.060.radec.txt".toLowerCase(), waspPath.getFileName().toString().toLowerCase());
		assertEquals("vega.B.050.radec.txt".toLowerCase(), vegaPath.getFileName().toString().toLowerCase());
	}

	@DisplayName("Read wasp12 radec file confirm selected data & comment lines are correct")
	@Test
	void readWasp12_SelectedLines_AreCorrect() throws IOException {
		// java 8 streams import wasp12 radec into list
		try (Stream<String> lines = Files.lines(Paths.get(waspFile.toString()))) {
			waspLines = lines.collect(Collectors.toList());
		}

		// target data and comment lines
		assertTrue(waspFinder("06:30:32.80, +29:40:20.28, 0, 1, 99.999"));
		assertTrue(waspFinder("#T1, wasp 12, 06:30:32.80, +29:40:20.28, 0, 1, 99.999"));

		// first ref star lines
		assertTrue(waspFinder("06:30:47.77, +29:35:30.40, 1, 1, 9.453"));
		assertTrue(waspFinder("#C2, 000-BKG-164, 06:30:47.77, +29:35:30.40, 1, 1, 9.453"));

		// mid table ref star
		assertTrue(waspFinder("06:30:22.63, +29:44:42.10, 1, 1, 11.728"));
		assertTrue(waspFinder("#C6, 000-BMX-310, 06:30:22.63, +29:44:42.10, 1, 1, 11.728"));

		// last table ref star
		assertTrue(waspFinder("06:31:08.10, +29:41:52.80, 1, 1, 12.748"));
		assertTrue(waspFinder("#C8, 000-BKG-168, 06:31:08.10, +29:41:52.80, 1, 1, 12.748"));
		
		System.out.println("\nWasp query");
		System.out.println(waspQuery.toString());
	}
	
	@DisplayName("Read vega radec file confirm selected data & comment lines are correct")
	@Test
	void readVega_SelectedLines_AreCorrect() throws IOException {
		// java 8 streams import wasp12 radec into list
		try (Stream<String> lines = Files.lines(Paths.get(vegaFile.toString()))) {
			vegaLines = lines.collect(Collectors.toList());
		}

		// target data and comment lines
		assertTrue(vegaFinder("18:36:56.34, +38:47:01.28, 0, 1, 99.999"));
		assertTrue(vegaFinder("#T1, vega, 18:36:56.34, +38:47:01.28, 0, 1, 99.999"));

		// first ref star lines
		assertTrue(vegaFinder("18:36:56.34, +38:47:01.30, 1, 1, 0.03"));
		assertTrue(vegaFinder("#C2, 000-BCC-827, 18:36:56.34, +38:47:01.30, 1, 1, 0.03"));

		// mid table ref star
		assertTrue(vegaFinder("18:35:27.59, +38:26:03.10, 1, 1, 13.677"));
		assertTrue(vegaFinder("#C4, 000-BCC-765, 18:35:27.59, +38:26:03.10, 1, 1, 13.677"));

		// last table ref star
		assertTrue(vegaFinder("18:35:20.77, +38:22:05.50, 1, 1, 15.492"));
		assertTrue(vegaFinder("#C5, 000-BMW-492, 18:35:20.77, +38:22:05.50, 1, 1, 15.492"));
		
		System.out.println("\nvega query");
		System.out.println(vegaQuery.toString());
	}
}
