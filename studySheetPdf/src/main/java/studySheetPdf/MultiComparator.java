package studySheetPdf;

import java.util.Comparator;

public class MultiComparator {

	public static Comparator<VocabEntry> MultiComparator = new Comparator<VocabEntry>() {
		
		public int compare(VocabEntry entry1, VocabEntry entry2) {
			return entry1.getChinese().compareTo(entry2.getChinese());
		}
	};
	
	
}
