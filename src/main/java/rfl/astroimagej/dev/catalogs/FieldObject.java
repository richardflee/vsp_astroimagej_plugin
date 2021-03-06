package rfl.astroimagej.dev.catalogs;

/**
 * Query result from on-line catalog database, instances of this class encapsulate coordinate and magnitude data for 
 * a single Comparison or Reference star.
 */
public class FieldObject  {	
	private String objectId = null;
	private Double raHr = null;
	private Double decDeg = null;	
	private Double mag = null;
	private Double magErr = null;
	
	/**
	 * Default parameters for Sirius, eg.g for unit test
	 */
	public FieldObject() {
		objectId = "sirius";
		raHr = 6.75248;
		decDeg = -16.71612;
		mag = -1.46;
		magErr = 0.02;		
	}
	
	// autogenerated getters, setters and toString methods
	
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public Double getRaHr() {
		return raHr;
	}

	public void setRaHr(Double raHr) {
		this.raHr = raHr;
	}

	public Double getDecDeg() {
		return decDeg;
	}

	public void setDecDeg(Double decDeg) {
		this.decDeg = decDeg;
	}

	public Double getMag() {
		return mag;
	}

	public void setMag(Double mag) {
		this.mag = mag;
	}

	public Double getMagErr() {
		return magErr;
	}

	public void setMagErr(Double magErr) {
		this.magErr = magErr;
	}

	@Override
	public String toString() {
		return "FieldObject [objectId=" + objectId + ", raHr=" + raHr + ", decDeg=" + decDeg + ", mag=" + mag
				+ ", magErr=" + magErr + "]";
	}
	
}

