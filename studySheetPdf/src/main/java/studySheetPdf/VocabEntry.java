package studySheetPdf;

public class VocabEntry {
	private String english;
	private String mps;
	private String chinese;
	
	public VocabEntry (String chinese, String mps, String english) {
		this.chinese = chinese;
		this.mps = mps;
		this.english = english;
	}
	
	public String getEnglish() {
		return english;
	}
	public void setEnglish(String english) {
		this.english = english;
	}
	public String getMps() {
		return mps;
	}
	public void setMps(String mps) {
		this.mps = mps;
	}
	public String getChinese() {
		return chinese;
	}
	public void setChinese(String chinese) {
		this.chinese = chinese;
	}
}