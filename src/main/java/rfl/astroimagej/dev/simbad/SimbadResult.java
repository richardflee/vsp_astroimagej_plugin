package rfl.astroimagej.dev.simbad;

/**
 * Encapsulates the results of a objectId query on the SIMBAD database.
 * <p>
 * Overwrites default wasp 12 data with object data if the objectId is found, 
 * otherwise leaves default data unchanged
 * </p>
 */
public class SimbadResult {
	// Readonly objectId to query simbad database
	private String objectId = null;

	// Simbad query params
	private String simbadId = null;
	private Double simbadRaHr = null;
	private Double simbadDecDeg = null;
	private Double magB = null;
	private Double magV = null;
	private Double magR = null;
	private Double magI = null;

	public SimbadResult(String objectId) {
		this.objectId = objectId;

		this.simbadId = "WASP-12";
		this.simbadRaHr = 6.50911;
		this.simbadDecDeg = 29.67230;
		magB = 12.14;
		magV = 11.57;
		magR = null;
		magI = null;
	}

	public String getObjectId() {
		return objectId;
	}

// autogenerated 
	public String getSimbadId() {
		return simbadId;
	}

	public void setSimbadId(String simbadId) {
		this.simbadId = simbadId;
	}

	public Double getSimbadRaHr() {
		return simbadRaHr;
	}

	public void setSimbadRaHr(Double simbadRaHr) {
		this.simbadRaHr = simbadRaHr;
	}

	public Double getSimbadDecDeg() {
		return simbadDecDeg;
	}

	public void setSimbadDecDeg(Double simbadDecDeg) {
		this.simbadDecDeg = simbadDecDeg;
	}

	public Double getMagB() {
		return magB;
	}

	public void setMagB(Double magB) {
		this.magB = magB;
	}

	public Double getMagV() {
		return magV;
	}

	public void setMagV(Double magV) {
		this.magV = magV;
	}

	public Double getMagR() {
		return magR;
	}

	public void setMagR(Double magR) {
		this.magR = magR;
	}

	public Double getMagI() {
		return magI;
	}

	public void setMagI(Double magI) {
		this.magI = magI;
	}

	@Override
	public String toString() {
		return "SimbadResult [objectId=" + objectId + ", simbadId=" + simbadId + ", simbadRaHr=" + simbadRaHr
				+ ", simbadDecDeg=" + simbadDecDeg + ", magB=" + magB + ", magV=" + magV + ", magR=" + magR + ", magI="
				+ magI + "]";
	}

}
