package studySheetPdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.Table;

public class StudySheetPdf {

	private Color FONT_COLOR = new DeviceRgb(20, 20, 20);
	private Color FONT_COLOR2 = new DeviceRgb(0, 0, 255);
	private Color TABLE_HEADER_CELL_COLOR = new DeviceRgb(220, 220, 220);
	private String FONT_FILE_ENGLISH = "src/main/resources/fonts/english/TidyHand.ttf";
	private String FONT_FILE_CHINESE = "src/main/resources/fonts/chinese/arphic/bkai00mp.ttf";
	private MpsSplitCharacters MPS_SPLIT_CHARACTERS = new MpsSplitCharacters();
	private MultiSplitCharacters MULTI_SPLIT_CHARACTERS = new MultiSplitCharacters();

	public static Comparator<VocabEntry> MultiComparator = new Comparator<VocabEntry>() {
		public int compare(VocabEntry entry1, VocabEntry entry2) {
			return entry1.getChinese().compareTo(entry2.getChinese());
		}
	};

	public Map<String, List<VocabEntry>> loadSingleCharacterVocabFile(File file) throws Exception {
		Map<String, List<VocabEntry>> entries = new HashMap<String, List<VocabEntry>>();

		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		for (; (line = br.readLine()) != null;) {

			line = line.trim();

			// split the line into the three components
			String tokens[] = line.split("\\|");
			String glyph = tokens[0];
			String mps = tokens[1];
			String english = tokens[2];

			// create a new vocab entry
			VocabEntry entry = new VocabEntry(glyph, mps, english);

			// look up the single character
			List<VocabEntry> list = entries.get(glyph);

			if (list == null) {
				// first time we've seen this character
				list = new ArrayList<VocabEntry>();
				list.add(entry);
				entries.put(glyph, list);
			} else {
				// we already have an entry for this character, so add this additional entry
				list.add(entry);
			}
		}

		// close the file
		br.close();

		return entries;
	}

	public List<VocabEntry> loadMultiCharacterVocabFile(File file) throws Exception {
		List<VocabEntry> entries = new ArrayList<VocabEntry>();

		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		for (; (line = br.readLine()) != null;) {

			line = line.trim();

			// split the line into the three components
			String tokens[] = line.split("\\|");
			String glyph = tokens[0];
			String mps = tokens[1];
			String english = tokens[2];

			// create a new vocab entry and add it to the list
			VocabEntry entry = new VocabEntry(glyph, mps, english);
			entries.add(entry);
		}

		// close the file
		br.close();

		return entries;
	}

	public void createSingleCharMinimalistPdf(File pdfFile, String pageTitle, Map<String, List<VocabEntry>> entries, boolean sorted) {

		System.out.println("PDF File: " + pdfFile.getAbsolutePath());

		// create the three fonts we want in our Study Sheet
		boolean embedded = true;
		try {
			PdfFont pdfFontEnglish = PdfFontFactory.createFont(this.FONT_FILE_ENGLISH, embedded);
			PdfFont pdfFontChinese = PdfFontFactory.createFont(this.FONT_FILE_CHINESE, PdfEncodings.IDENTITY_H, embedded);

			// create the PDF document
			PdfDocument pdfDocument = new PdfDocument(new PdfWriter(pdfFile));
			Document document = new Document(pdfDocument, PageSize.LETTER.rotate());

			String sortedTitle = sorted ? "(sorted order)" : "(random order)";

			// add page title
			document.add(new Paragraph(pageTitle + ": " + entries.size() + " " + sortedTitle).setFont(pdfFontEnglish).setFontSize(22).setFontColor(FONT_COLOR));

			// add Chinese characters, each separated with a TAB
			Paragraph p = new Paragraph().setFont(pdfFontChinese).setFontSize(28).setFontColor(FONT_COLOR);

			Set<String> glyphKeySet = entries.keySet();
			List<String> glyphKeyList = new ArrayList<String>(glyphKeySet);

			// sort or shuffle the Chinese characters
			if (sorted)
				Collections.sort(glyphKeyList);
			else
				Collections.shuffle(glyphKeyList);

			for (String singleChar : glyphKeyList) {
				p.add(singleChar).add(new Tab());
			}

			document.add(p);

			// close the PDF document
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createSingleCharFullPdf(File pdfFile, String pageTitle, Map<String, List<VocabEntry>> entries,
			boolean sorted) {

		System.out.println("PDF File: " + pdfFile.getAbsolutePath());

		// create the three fonts we want in our Study Sheet
		boolean embedded = true;
		try {
			PdfFont pdfFontEnglish = PdfFontFactory.createFont(this.FONT_FILE_ENGLISH, embedded);
			PdfFont pdfFontChinese = PdfFontFactory.createFont(this.FONT_FILE_CHINESE, PdfEncodings.IDENTITY_H, embedded);

			// create the PDF document
			PdfDocument pdfDocument = new PdfDocument(new PdfWriter(pdfFile));
			Document document = new Document(pdfDocument, PageSize.LETTER.rotate());

			String sortedTitle = sorted ? "(sorted order)" : "(random order)";

			// add page title
			document.add(new Paragraph(pageTitle + ": " + entries.size() + " " + sortedTitle).setFont(pdfFontEnglish).setFontSize(22).setFontColor(FONT_COLOR));

			Table table = new Table(3);
			Cell cell = null;

			// Chinese
			cell = new Cell();
			cell.setKeepTogether(true);
			cell.setPadding(4.0f);
			cell.setFont(pdfFontEnglish);
			cell.setFontSize(10);
			cell.setBackgroundColor(TABLE_HEADER_CELL_COLOR);
			cell.add(new Paragraph("Single Glyph"));
			table.addHeaderCell(cell);

			// MPS
			cell = new Cell();
			cell.setKeepTogether(true);
			cell.setPadding(4.0f);
			cell.setFont(pdfFontEnglish);
			cell.setFontSize(10);
			cell.setBackgroundColor(TABLE_HEADER_CELL_COLOR);
			cell.add(new Paragraph("MPS"));
			table.addHeaderCell(cell);

			// English
			cell = new Cell();
			cell.setKeepTogether(true);
			cell.setPadding(4.0f);
			cell.setFont(pdfFontEnglish);
			cell.setFontSize(10);
			cell.setBackgroundColor(TABLE_HEADER_CELL_COLOR);
			cell.add(new Paragraph("English"));
			table.addHeaderCell(cell);

			Set<String> glyphKeySet = entries.keySet();
			List<String> glyphKeyList = new ArrayList<String>(glyphKeySet);

			// sort or shuffle the Chinese characters
			if (sorted)
				Collections.sort(glyphKeyList);
			else
				Collections.shuffle(glyphKeyList);

			for (String singleChar : glyphKeyList) {

				// reminder that an individual character can have multiple entries due to
				// pronunciation differences
				for (VocabEntry vocabEntry : entries.get(singleChar)) {

					// Chinese
					cell = new Cell();
					cell.setKeepTogether(true);
					cell.setPadding(4.0f);
					cell.setFont(pdfFontChinese);
					cell.setFontSize(16);
					cell.setSplitCharacters(MULTI_SPLIT_CHARACTERS);
					cell.add(new Paragraph(vocabEntry.getChinese()));
					table.addCell(cell);

					// MPS
					cell = new Cell();
					cell.setKeepTogether(true);
					cell.setPadding(4.0f);
					cell.setFont(pdfFontChinese);
					cell.setFontSize(10);
					cell.setSplitCharacters(MPS_SPLIT_CHARACTERS);
					cell.add(new Paragraph(vocabEntry.getMps()));
					table.addCell(cell);

					// English
					cell = new Cell();
					cell.setKeepTogether(true);
					cell.setPadding(4.0f);
					cell.setFont(pdfFontEnglish);
					cell.setFontSize(10);
					cell.add(new Paragraph(vocabEntry.getEnglish()));
					table.addCell(cell);
				}
			}

			document.add(table);

			// close the PDF document
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createMultiCharMinimalistPdf(File pdfFile, String pageTitle, List<VocabEntry> entries, boolean sorted) {

		System.out.println("PDF File: " + pdfFile.getAbsolutePath());

		// create the three fonts we want in our Study Sheet
		boolean embedded = true;
		try {
			PdfFont pdfFontEnglish = PdfFontFactory.createFont(this.FONT_FILE_ENGLISH, embedded);
			PdfFont pdfFontChinese = PdfFontFactory.createFont(this.FONT_FILE_CHINESE, PdfEncodings.IDENTITY_H, embedded);

			// create the PDF document
			PdfDocument pdfDocument = new PdfDocument(new PdfWriter(pdfFile));
			Document document = new Document(pdfDocument, PageSize.LETTER.rotate());

			String sortedTitle = sorted ? "(sorted order)" : "(random order)";

			// add page title
			document.add(new Paragraph(pageTitle + ": " + entries.size() + " " + sortedTitle).setFont(pdfFontEnglish).setFontSize(22).setFontColor(FONT_COLOR));

			// group the vocabulary words by number of characters in the words
			Paragraph p2 = new Paragraph().setFont(pdfFontChinese).setFontSize(28).setFontColor(FONT_COLOR);
			Paragraph p3 = new Paragraph().setFont(pdfFontChinese).setFontSize(28).setFontColor(FONT_COLOR);
			Paragraph p4 = new Paragraph().setFont(pdfFontChinese).setFontSize(28).setFontColor(FONT_COLOR);
			Paragraph pOther = new Paragraph().setFont(pdfFontChinese).setFontSize(28).setFontColor(FONT_COLOR);

			boolean found2 = false;
			boolean found3 = false;
			boolean found4 = false;
			boolean foundOther = false;

			// sort or shuffle the Chinese characters
			if (sorted)
				Collections.sort(entries, MultiComparator);
			else
				Collections.shuffle(entries);

			// loop through each vocabulary entry, adding it to the appropriate paragraph
			for (VocabEntry vocabEntry : entries) {
				String chinese = vocabEntry.getChinese();
				int numChars = chinese.length();

				if (numChars == 2) {
					p2.add(chinese).add(new Tab());
					found2 = true;
				} else if (numChars == 3) {
					p3.add(chinese).add(new Tab());
					found3 = true;
				} else if (numChars == 4) {
					p4.add(chinese).add(new Tab());
					found4 = true;
				} else {
					pOther.add(chinese).add(new Tab());
					foundOther = true;
				}
			}

			if (found2) {
				document.add(new Paragraph());
				document.add(new Paragraph().setFont(pdfFontEnglish).setFontSize(18).setFontColor(FONT_COLOR2).add("Two-Character Words:"));
				document.add(p2);
			}

			if (found3) {
				document.add(new Paragraph());
				document.add(new Paragraph().setFont(pdfFontEnglish).setFontSize(18).setFontColor(FONT_COLOR2).add("Three-Character Words:"));
				document.add(p3);
			}

			if (found4) {
				document.add(new Paragraph());
				document.add(new Paragraph().setFont(pdfFontEnglish).setFontSize(18).setFontColor(FONT_COLOR2).add("Four-Character Words:"));
				document.add(p4);
			}

			if (foundOther) {
				document.add(new Paragraph());
				document.add(new Paragraph().setFont(pdfFontEnglish).setFontSize(18).setFontColor(FONT_COLOR2).add("Other Words:"));
				document.add(pOther);
			}

			// close the PDF document
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createMultiCharFullPdf(File pdfFile, String pageTitle, List<VocabEntry> entries, boolean sorted) {

		System.out.println("PDF File: " + pdfFile.getAbsolutePath());

		// create the three fonts we want in our Study Sheet
		boolean embedded = true;
		try {
			PdfFont pdfFontEnglish = PdfFontFactory.createFont(this.FONT_FILE_ENGLISH, embedded);
			PdfFont pdfFontChinese = PdfFontFactory.createFont(this.FONT_FILE_CHINESE, PdfEncodings.IDENTITY_H, embedded);

			// create the PDF document
			PdfDocument pdfDocument = new PdfDocument(new PdfWriter(pdfFile));
			Document document = new Document(pdfDocument, PageSize.LETTER.rotate());

			String sortedTitle = sorted ? "(sorted order)" : "(random order)";

			// add page title
			document.add(new Paragraph(pageTitle + ": " + entries.size() + " " + sortedTitle).setFont(pdfFontEnglish).setFontSize(22).setFontColor(FONT_COLOR));

			Table table = new Table(3);
			Cell cell = null;

			// Chinese
			cell = new Cell();
			cell.setKeepTogether(true);
			cell.setPadding(4.0f);
			cell.setFont(pdfFontEnglish);
			cell.setFontSize(10);
			cell.setBackgroundColor(TABLE_HEADER_CELL_COLOR);
			cell.add(new Paragraph("Single Glyph"));
			table.addHeaderCell(cell);

			// MPS
			cell = new Cell();
			cell.setKeepTogether(true);
			cell.setPadding(4.0f);
			cell.setFont(pdfFontEnglish);
			cell.setFontSize(10);
			cell.setBackgroundColor(TABLE_HEADER_CELL_COLOR);
			cell.add(new Paragraph("MPS"));
			table.addHeaderCell(cell);

			// English
			cell = new Cell();
			cell.setKeepTogether(true);
			cell.setPadding(4.0f);
			cell.setFont(pdfFontEnglish);
			cell.setFontSize(10);
			cell.setBackgroundColor(TABLE_HEADER_CELL_COLOR);
			cell.add(new Paragraph("English"));
			table.addHeaderCell(cell);

			// sort or shuffle the Chinese characters
			if (sorted)
				Collections.sort(entries, MultiComparator);
			else
				Collections.shuffle(entries);

			for (VocabEntry vocabEntry : entries) {

				// Chinese
				cell = new Cell();
				cell.setKeepTogether(true);
				cell.setPadding(4.0f);
				cell.setFont(pdfFontChinese);
				cell.setFontSize(16);
				cell.setSplitCharacters(MULTI_SPLIT_CHARACTERS);
				cell.add(new Paragraph(vocabEntry.getChinese()));
				table.addCell(cell);

				// MPS
				cell = new Cell();
				cell.setKeepTogether(true);
				cell.setPadding(4.0f);
				cell.setFont(pdfFontChinese);
				cell.setFontSize(10);
				cell.setSplitCharacters(MPS_SPLIT_CHARACTERS);
				cell.add(new Paragraph(vocabEntry.getMps()));
				table.addCell(cell);

				// English
				cell = new Cell();
				cell.setKeepTogether(true);
				cell.setPadding(4.0f);
				cell.setFont(pdfFontEnglish);
				cell.setFontSize(10);
				cell.add(new Paragraph(vocabEntry.getEnglish()));
				table.addCell(cell);
			}

			document.add(table);

			// close the PDF document
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		File curDir = new File(".");
		System.out.println("Current (execution) directory is [" + curDir.getAbsolutePath() + "]");

		StudySheetPdf studySheet = new StudySheetPdf();

		// use a Map to store single-character vocabulary words because a single
		// character
		// can have multiple pronunciations and meanings
		File newSingleVocabListFile = new File("src/main/resources/vocab/newSingleVocabList.txt");
		System.out.println("New Single Vocab List File: " + newSingleVocabListFile.getAbsolutePath());
		Map<String, List<VocabEntry>> newSingleCharVocabEntries = studySheet.loadSingleCharacterVocabFile(newSingleVocabListFile);

		// use a List to store multi-character vocabulary words
		File newMultiVocabListFile = new File("src/main/resources/vocab/newMultiVocabList.txt");
		System.out.println("New Multi Vocab List File: " + newMultiVocabListFile.getAbsolutePath());
		List<VocabEntry> newMultiCharVocabEntries = studySheet.loadMultiCharacterVocabFile(newMultiVocabListFile);

		// ==============================================================================
		// STUDY SHEET #1: Single Characters (Minimalist)
		// ==============================================================================
		String pageTitle = "New Vocabulary";
		boolean sorted = true;
		studySheet.createSingleCharMinimalistPdf(new File("newSingleMinimalist.pdf"), pageTitle, newSingleCharVocabEntries, sorted);

		// ==============================================================================
		// STUDY SHEET #2: Multi Characters (Minimalist)
		// ==============================================================================
		pageTitle = "New Vocabulary";
		sorted = true;
		studySheet.createMultiCharMinimalistPdf(new File("newMultiMinimalist.pdf"), pageTitle, newMultiCharVocabEntries, sorted);

		// ==============================================================================
		// STUDY SHEET #3: Single Characters (Full)
		// ==============================================================================
		pageTitle = "New Vocabulary";
		sorted = true;
		studySheet.createSingleCharFullPdf(new File("newSingleFull.pdf"), pageTitle, newSingleCharVocabEntries, sorted);

		// ==============================================================================
		// STUDY SHEET #4: Multi Characters (Full)
		// ==============================================================================
		pageTitle = "New Vocabulary";
		sorted = true;
		studySheet.createMultiCharFullPdf(new File("newMultiFull.pdf"), pageTitle, newMultiCharVocabEntries, sorted);
	}
}