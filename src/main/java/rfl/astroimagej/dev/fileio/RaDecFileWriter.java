package rfl.astroimagej.dev.fileio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import rfl.astroimagej.dev.catalogs.AstroCatalog;
import rfl.astroimagej.dev.catalogs.CatalogFactory;
import rfl.astroimagej.dev.catalogs.CatalogQuery;
import rfl.astroimagej.dev.catalogs.FieldObject;
import rfl.astroimagej.dev.catalogs.QueryResult;
import rfl.astroimagej.dev.utils.AstroCoords;

/**
 * This class is an observer of FileWriter events, running catalog queries and saving results
 * to radec and fits files.
 * 
 * <p>The event fires when user clicks the Catalog button in the catalog dialog.</p>
 */
public class RaDecFileWriter implements FileWriterListener {

	/**
	 * Runs catalog and dss queries. Saves results in radec and fits files respectively
	 * and returns message whether operation was successful
	 */
	@Override
	public String writeFile(CatalogQuery query) {		
		String message = "";
		QueryResult result = null;
		
		// run online catalog query
		AstroCatalog catalog = CatalogFactory.createCatalog(query.getCatalogType());		
		if (catalog != null) {
			result = catalog.runQuery(query);
			message = writeRaDecFile(query, result);
			
			// run DSS query to download fits file & append dss message
			message += DssWriter.downloadDssFits(query);			
		}
		return message;	
	}

	/**
	 * Writes radec file with filename format
	 * [user_input_objectid].[magband].radec.txt
	 * <p>
	 * The result of a query on an on-line catalog is to import an array of ref star attributes 
	 * matching query criteria. This method writes imported data to an radec format file. 
	 * </p>
	 * <p> After importing the radec file into astroimagej, the software draws
	 *  photometry apertures centred on target and comparison star images.
	 *  </p>
	 * <p> Ref: https://www.astro.louisville.edu/software/astroimagej/</p>
	 * 
	 * @param query  catalog query object, contains target star data
	 * 
	 * @param result query result object, contains list of FieldObjects containing
	 *               ref star data for current filter / mag band
	 *               
	 * @return message whether writing radec file was successful
	 */
	public String writeRaDecFile(CatalogQuery query, QueryResult result) {
		// exit if no query results
		String message = "";
		if (result.getFieldObjects().size() == 0) {
			message = String.format("No records found in %s catalog for specified parameters", 
					query.getCatalogType());
			return message;
		}

		// converts query data to string list to write to radec file
		List<String> lines = compileRaDecList(query, result);
		
		// write new radec file and update message
		File file = getFile(query, "radec.txt");
		String filePath = file.toString();
		message = String.format("Radec file: %s already exists", filePath);
		if (!file.exists()) {
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
				for (String line : lines) {
					bw.append(line);
				}
				message = String.format("Saved radec file: %s", filePath);
			} catch (IOException e) {
				message = String.format("Error in writing file: %s", filePath);
			}
		}
		return message +"\n";
	}

	// compiles rade or fits filename,
	// format <objectid>.<magband>.<fov_amin>.fits or .radec.txt
	// replace spaces with "_"
	protected static String compileFilename(CatalogQuery query, String fileSuffix) {
		String filename = String.join(".", Arrays.asList(query.getObjectId(), query.getMagBand(),
				String.format("%03d", query.getFovAmin().intValue()), fileSuffix));
		return filename.replace(" ", "_");
	}

	// compile path and filename from query fields
	// filename format <objectid>.<filter>.radec.txt
	// dir format ..\AstroImageJ\radec
	protected static File getFile(CatalogQuery query, String suffix) {
		// path to radec file, create new folder if necessary
		File dir = new File(System.getProperty("user.dir"), "radec");
		dir.mkdirs();
		File file = new File(dir, compileFilename(query, suffix));
		return file;
	}


	/*
	 * Compiles a string list of target and ref_star parms to write to radec file
	 * Data format: RA (HMS), DEC (DMS), RefStar (flag), Centroid (flag), Mag
	 * 
	 * First section contains radec imported into astroimagej, followed by a 
	 * comment section with the same data plus object name and magnitude uncertainty
	 * 
	 * Note that astroimagej treats a line with leading char '#' as comment
	 * 
	 * @param query catalog query object
	 * 
	 * @param result catalog query result object
	 * 
	 * @return string list formatted to write radec file
	 */
	private List<String> compileRaDecList(CatalogQuery query, QueryResult result) {
		// string list for file write, extract FieldObjects from QueryREsult object
		List<String> lines = new ArrayList<>();
		List<FieldObject> fieldObjects = result.getFieldObjects();

		// target object data
		FieldObject to = new FieldObject();
		to.setObjectId(query.getObjectId());
		to.setRaHr(query.getRaHr());
		to.setDecDeg(query.getDecDeg());
		to.setMag(99.999);

		// data block format RA (HMS), Dec (DMS), RefStar, Centroid, Mag
		// header
		lines.add("#RA, Dec, RefStar, Centroid, Mag\n");
		
		// target data
		lines.add(getFieldLine(to, 0, true));
		
		// ref star data
		for (FieldObject fo : fieldObjects) {
			lines.add(getFieldLine(fo, 0, false));
		}
		// end of data
		lines.add("#\n");

		// comment block, as data prepend aperture & object id
		lines.add("#Ap, Auid, RA, Dec, RefStar, Centroid, Mag\n");
		// target comment
		lines.add(getFieldLine(to, 1, true));
		// ref star comments
		int index = 2;
		for (FieldObject fo : fieldObjects) {
			lines.add(getFieldLine(fo, index++, false));
		}
		
		// append filter / mag band
		lines.add(String.format("#%s", query.getMagBand()));
		return lines;
	}

	/*
	 * compile one line of radec data or a comment line
	 * 
	 * @param fo current data object *
	 * 
	 * @param index index = 0 => write data line, otherwise write comment line *
	 * 
	 * @param isTarget => true for target line (top data or comment line) otherwise
	 * false
	 * 
	 * @return compiled radec write string
	 */
	private String getFieldLine(FieldObject fo, int index, boolean isTarget) {
		// sets RefStar, Centroid flags + catalog mag for selected band (mag = 99.999
		// target line)
		String lineEnd = (isTarget) ? ", 0, " : ", 1, ";
		lineEnd += String.format("1, %s", String.valueOf(fo.getMag()));
		lineEnd += "\n";

		// Concatenate ra and dec sexagesimal values & append lineEnd string
		List<String> terms = Arrays.asList(AstroCoords.raHr_To_raHms(fo.getRaHr()),
				AstroCoords.decDeg_To_decDms(fo.getDecDeg()));
		String line = String.join(", ", terms) + lineEnd;

		// target or RefStar data line
		if (index == 0) {
			return line;
			// target comment line
		} else if (index == 1) {
			return String.format("#T%s, %s, " + line, String.valueOf(index), fo.getObjectId());
			// RefStar comment line
		} else {
			return String.format("#C%s, %s, " + line, String.valueOf(index), fo.getObjectId());
		}
	}
}
